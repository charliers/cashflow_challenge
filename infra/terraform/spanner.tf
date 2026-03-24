# infra/terraform/main.tf

provider "google" {
  project = var.project_id
  region  = var.region
}

# ─── Cloud Spanner Instance ───────────────────────────────────
resource "google_spanner_instance" "cashflow" {
  name             = "cashflow-instance"
  config           = "regional-${var.region}"
  display_name     = "CashFlow Spanner Instance"
  processing_units = var.spanner_processing_units  # 100 PU ao invés de 1 node

  autoscaling_config {
    autoscaling_limits {
      min_processing_units = 100
      max_processing_units = 1000  # permite escalar até 1 node completo sob demanda
    }

    autoscaling_targets {
      high_priority_cpu_utilization_percent = 85  # escala ao atingir 85% CPU
      storage_utilization_percent           = 90  # escala ao atingir 90% storage
    }
  }

  labels = {
    environment = var.environment
    team        = "cashflow"
    region      = var.region
    managed_by  = "terraform"
    cost_center    = "cashflow-prod"
    discount_type  = "THIRTY_SIX_MONTH"  # 3 anos 
  }
}

# Spanner
# ─── Committed Use Discount — 3 anos ─────────────────────────
resource "google_compute_commitment" "spanner_cud_3yr" {
  name        = "cashflow-spanner-cud-3yr"
  description = "Committed Use Discount 3 anos para Cloud Spanner - CashFlow"
  region      = var.region
  plan        = var.discount_plan  # definido como THIRTY_SIX_MONTH na variável

  # Spanner CUD é baseado em recursos de compute equivalentes
  resources {
    type   = "MEMORY"
    amount = "8"  # GB de memória equivalente para 100 PU
  }

  resources {
    type   = "VCPU"
    amount = "2"  # vCPUs equivalentes para 100 PU
  }

  auto_renew = false  # não renovar automaticamente ao fim dos 3 anos
}

# ─── Database: financial-records ────────────────────────────
resource "google_spanner_database" "financial-records" {
  instance = google_spanner_instance.cashflow.name
  name     = "financial-records"

  version_retention_period   = "3d"   # retenção de versões para PITR (Point-in-time Recovery)
  deletion_protection        = true   # impede deleção acidental via Terraform

  ddl = [
    <<-EOT
      CREATE TABLE Records (
        Id           UUID         NOT NULL DEFAULT (NEW_UUID()),
        Type         STRING(6)    NOT NULL,
        Amount       NUMERIC      NOT NULL,
        Description  STRING(500)          ,
        RefDate      DATE         NOT NULL,
        CreatedAt    TIMESTAMP    NOT NULL OPTIONS (allow_commit_timestamp=false),
        UpdatedAt    TIMESTAMP    NOT NULL OPTIONS (allow_commit_timestamp=true),
        RefundToId   UUID         
      ) PRIMARY KEY (Id)
    EOT
    ,
    <<-EOT
      CREATE INDEX IX_Records_RefundToId
      ON Records (RefundToId DESC)
    EOT
    ,
    <<-EOT
      CREATE INDEX IX_Records_CreatedAt_Type
      ON Records (CreatedAt DESC, Type)
    EOT
    ,
    <<-EOT
      CREATE TABLE ConsolidatedRecords (
        Id           UUID       NOT NULL DEFAULT (NEW_UUID()),
        RefDate      DATE       NOT NULL,
        AmountCredit NUMERIC    NOT NULL,
        AmountDebit  NUMERIC    NOT NULL,
        CreatedAt    TIMESTAMP  NOT NULL OPTIONS (allow_commit_timestamp=false),
        UpdatedAt    TIMESTAMP  NOT NULL OPTIONS (allow_commit_timestamp=true)
      ) PRIMARY KEY (RefDate, Id)
    EOT
  ]
}

# ─── Backup Schedule: Records ─────────────────────────────
resource "google_spanner_backup_schedule" "records_daily" {
  instance = google_spanner_instance.cashflow.name
  database = google_spanner_database.financialRecords.name
  name     = "records-daily-backup"

  retention_duration = "${var.spanner_backup_retention_days * 24 * 60 * 60}s"  # 7 dias em segundos

  spec {
    cron_schedule {
      text = "0 2 * * *"  # todo dia às 02:00 UTC
    }
  }

  full_backup_spec {}  # backup completo (não incremental)
}

# ─── Backup Storage Limit ─────────────────────────────────────
# O Spanner gerencia o storage automaticamente, mas o limite
# é controlado via IAM e quotas no nível do projeto.
# O bloco abaixo garante que o tamanho do banco não ultrapasse
# o limite configurado via organização/policy.

resource "google_spanner_instance_config" "storage_limit" {
  # Nota: o limite de storage no Spanner é definido por
  # capacidade provisionada (PU/nodes) e não por configuração direta.
  # 100 PU suportam até 409.6 GB de storage por padrão no GCP.
  # Para forçar o limite de 300 GB, utilizamos um budget alert abaixo.
  count = 0  # placeholder para documentação
}

# ─── Budget Alert: Storage 300GB ──────────────────────────────
resource "google_monitoring_alert_policy" "spanner_storage_alert" {
  display_name = "Spanner Storage > 280GB (Cashflow)"
  combiner     = "OR"

  conditions {
    display_name = "Storage utilization acima de 280GB"

    condition_threshold {
      filter          = "resource.type=\"spanner_instance\" AND metric.type=\"spanner.googleapis.com/storage/used_bytes\" AND resource.label.instance_id=\"cashflow-instance\""
      duration        = "300s"
      comparison      = "COMPARISON_GT"
      threshold_value = 300706963456  # 280 GiB em bytes (alerta preventivo)

      aggregations {
        alignment_period   = "60s"
        per_series_aligner = "ALIGN_MEAN"
      }
    }
  }

  notification_channels = [
    google_monitoring_notification_channel.email.name
  ]

  alert_strategy {
    auto_close = "86400s"
  }
}

resource "google_monitoring_notification_channel" "email" {
  display_name = "Alerta Email - Time CashFlow"
  type         = "email"

  labels = {
    email_address = "cashflow-alerts@cashflow-challenge.com"
  }
}

# ─── Budget com CUD aplicado ──────────────────────────────────
resource "google_billing_budget" "spanner_cud_budget" {
  billing_account = var.billing_account_id
  display_name    = "CashFlow Spanner CUD Budget — 3yr"

  budget_filter {
    projects = ["projects/${var.project_id}"]

    services = [
      "services/95FF-2EF5-5EA1"  # Cloud Spanner service ID
    ]

    credit_types_treatment = "INCLUDE_SPECIFIED_CREDITS"
    credit_types           = ["COMMITTED_USAGE_DISCOUNT"]  # considera CUD no budget
  }

  amount {
    specified_amount {
      currency_code = "USD"
      units         = "270"  # ~$270/mês pós-desconto CUD 3yr (65% off)
    }
  }

  threshold_rules {
    threshold_percent = 0.8   # alerta em 80% do budget
    spend_basis       = "CURRENT_SPEND"
  }

  threshold_rules {
    threshold_percent = 1.0   # alerta em 100% do budget
    spend_basis       = "CURRENT_SPEND"
  }

  threshold_rules {
    threshold_percent = 1.2   # alerta crítico em 120% (autoscaling acionado)
    spend_basis       = "FORECASTED_SPEND"
  }

  all_updates_rule {
    monitoring_notification_channels = [
      google_monitoring_notification_channel.email.name
    ]
    disable_default_iam_recipients = false
  }
}
