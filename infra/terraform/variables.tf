# infra/terraform/variables.tf

# ─────────────────────────────────────────────────────────────
# PROJETO E AMBIENTE
# ─────────────────────────────────────────────────────────────

variable "project_id" {
  description = "ID do projeto GCP"
  type        = string
}

variable "billing_account_id" {
  description = "ID da Billing Account GCP para aplicação dos CUDs"
  type        = string
  sensitive   = true
}

variable "region" {
  description = "Região principal GCP (Iowa)"
  type        = string
  default     = "us-central1"

  validation {
    condition     = contains(["us-central1", "us-east1", "us-east4"], var.region)
    error_message = "Região deve ser uma das regiões GCP aprovadas para o projeto."
  }
}

variable "environment" {
  description = "Ambiente de execução (production, staging, development)"
  type        = string
  default     = "production"

  validation {
    condition     = contains(["production", "staging", "development"], var.environment)
    error_message = "Ambiente deve ser production, staging ou development."
  }
}

# ─────────────────────────────────────────────────────────────
# CLOUD SPANNER
# ─────────────────────────────────────────────────────────────

variable "spanner_processing_units" {
  description = <<-EOT
    Processing units da instância Spanner.
    100 PU  = 0.1 node  (desenvolvimento/baixa carga)
    1000 PU = 1 node    (produção padrão)
  EOT
  type        = number
  default     = 100

  validation {
    condition     = var.spanner_processing_units >= 100 && var.spanner_processing_units % 100 == 0
    error_message = "Processing units deve ser múltiplo de 100 e no mínimo 100."
  }
}

variable "spanner_storage_limit_gib" {
  description = "Limite de armazenamento do Spanner em GiB (SSD nativo)"
  type        = number
  default     = 300

  validation {
    condition     = var.spanner_storage_limit_gib >= 100
    error_message = "O storage do Spanner deve ser de no mínimo 100 GiB."
  }
}

variable "spanner_backup_retention_days" {
  description = "Período de retenção dos backups do Spanner em dias"
  type        = number
  default     = 7

  validation {
    condition     = var.spanner_backup_retention_days >= 1 && var.spanner_backup_retention_days <= 365
    error_message = "Retenção de backup deve ser entre 1 e 365 dias."
  }
}

variable "spanner_cud_years" {
  description = <<-EOT
    Duração do Committed Use Discount para Cloud Spanner.
    1 = ONE_YEAR     (~30% desconto)
    3 = THIRTY_SIX_MONTH (~65% desconto)
  EOT
  type        = number
  default     = 3

  validation {
    condition     = contains([1, 3], var.spanner_cud_years)
    error_message = "O CUD do Spanner deve ser de 1 ou 3 anos."
  }
}

variable "spanner_version_retention_period" {
  description = "Período de retenção de versões para PITR (Point-in-Time Recovery)"
  type        = string
  default     = "3d"
}

variable "spanner_deletion_protection" {
  description = "Habilita proteção contra deleção acidental do banco via Terraform"
  type        = bool
  default     = true
}

variable "spanner_autoscaling_min_pu" {
  description = "Mínimo de Processing Units para autoscaling do Spanner"
  type        = number
  default     = 100

  validation {
    condition     = var.spanner_autoscaling_min_pu >= 100 && var.spanner_autoscaling_min_pu % 100 == 0
    error_message = "Mínimo de PU deve ser múltiplo de 100 e no mínimo 100."
  }
}

variable "spanner_autoscaling_max_pu" {
  description = "Máximo de Processing Units para autoscaling do Spanner"
  type        = number
  default     = 1000

  validation {
    condition     = var.spanner_autoscaling_max_pu >= 100 && var.spanner_autoscaling_max_pu % 100 == 0
    error_message = "Máximo de PU deve ser múltiplo de 100 e no mínimo 100."
  }
}

variable "spanner_cpu_autoscaling_target" {
  description = "Percentual de CPU para disparo do autoscaling do Spanner"
  type        = number
  default     = 75

  validation {
    condition     = var.spanner_cpu_autoscaling_target >= 10 && var.spanner_cpu_autoscaling_target <= 90
    error_message = "Target de CPU deve ser entre 10% e 90%."
  }
}

variable "spanner_storage_autoscaling_target" {
  description = "Percentual de storage para disparo do autoscaling do Spanner"
  type        = number
  default     = 85

  validation {
    condition     = var.spanner_storage_autoscaling_target >= 10 && var.spanner_storage_autoscaling_target <= 95
    error_message = "Target de storage deve ser entre 10% e 95%."
  }
}

# ─────────────────────────────────────────────────────────────
# GKE CLUSTER
# ─────────────────────────────────────────────────────────────

variable "gke_node_count" {
  description = <<-EOT
    Número total de nodes no pool regional.
    Distribuídos automaticamente entre as zonas da região.
    Ex.: 4 nodes em us-central1 = ~1-2 nodes por zona.
  EOT
  type        = number
  default     = 4

  validation {
    condition     = var.gke_node_count >= 1 && var.gke_node_count <= 100
    error_message = "Número de nodes deve ser entre 1 e 100."
  }
}

variable "gke_machine_type" {
  description = <<-EOT
    Machine type dos nodes GKE.
    c2-standard-4 = 4 vCPU | 16 GB RAM (compute-optimized)
    Deve ser família c2 para compatibilidade com o CUD configurado.
  EOT
  type        = string
  default     = "c2-standard-4"

  validation {
    condition     = can(regex("^c2-", var.gke_machine_type))
    error_message = "Machine type deve ser da família c2 para compatibilidade com o CUD configurado."
  }
}

variable "gke_boot_disk_size" {
  description = "Tamanho do boot disk SSD dos nodes GKE em GiB"
  type        = number
  default     = 10

  validation {
    condition     = var.gke_boot_disk_size >= 10 && var.gke_boot_disk_size <= 2000
    error_message = "Boot disk deve ser entre 10 GiB e 2000 GiB."
  }
}

variable "gke_boot_disk_type" {
  description = "Tipo do boot disk dos nodes GKE"
  type        = string
  default     = "pd-ssd"

  validation {
    condition     = contains(["pd-ssd", "pd-standard", "pd-balanced"], var.gke_boot_disk_type)
    error_message = "Tipo de disco deve ser pd-ssd, pd-standard ou pd-balanced."
  }
}

variable "gke_image_type" {
  description = "Sistema operacional dos nodes GKE (Free: Container-Optimized OS)"
  type        = string
  default     = "COS_CONTAINERD"

  validation {
    condition     = contains(["COS_CONTAINERD", "UBUNTU_CONTAINERD"], var.gke_image_type)
    error_message = "Image type deve ser COS_CONTAINERD (free) ou UBUNTU_CONTAINERD."
  }
}

variable "gke_enable_confidential_nodes" {
  description = "Habilita Confidential GKE Nodes (AMD SEV)"
  type        = bool
  default     = false
}

variable "gke_enable_sustained_use_discount" {
  description = "Habilita Sustained Use Discounts (não aplicável com CUD ativo)"
  type        = bool
  default     = false
}

variable "gke_enable_gpu" {
  description = "Habilita GPUs nos nodes GKE"
  type        = bool
  default     = false
}

variable "gke_local_ssd_count" {
  description = "Quantidade de Local SSDs por node (0 = nenhum)"
  type        = number
  default     = 0

  validation {
    condition     = var.gke_local_ssd_count >= 0 && var.gke_local_ssd_count <= 8
    error_message = "Local SSD count deve ser entre 0 e 8."
  }
}

variable "gke_release_channel" {
  description = "Canal de release do GKE para atualizações automáticas"
  type        = string
  default     = "REGULAR"

  validation {
    condition     = contains(["RAPID", "REGULAR", "STABLE"], var.gke_release_channel)
    error_message = "Release channel deve ser RAPID, REGULAR ou STABLE."
  }
}

variable "gke_cud_years" {
  description = <<-EOT
    Duração do Resource-based CUD para GKE.
    1 = ONE_YEAR         (~37% desconto)
    3 = THIRTY_SIX_MONTH (~55% desconto)
  EOT
  type        = number
  default     = 3

  validation {
    condition     = contains([1, 3], var.gke_cud_years)
    error_message = "O CUD do GKE deve ser de 1 ou 3 anos."
  }
}

variable "gke_node_vcpu_total" {
  description = "Total de vCPUs para o commitment CUD (gke_node_count × vCPUs da machine type)"
  type        = number
  default     = 16  # 4 vCPU × 4 nodes
}

variable "gke_node_memory_total_mb" {
  description = "Total de memória em MB para o commitment CUD (gke_node_count × RAM da machine type)"
  type        = number
  default     = 65536  # 16 GB × 4 nodes em MB
}

variable "gke_max_surge" {
  description = "Máximo de nodes extras durante upgrade (surge strategy)"
  type        = number
  default     = 1
}

variable "gke_max_unavailable" {
  description = "Máximo de nodes indisponíveis durante upgrade"
  type        = number
  default     = 0
}

# ─────────────────────────────────────────────────────────────
# CLOUD PUB/SUB
# ─────────────────────────────────────────────────────────────

variable "pubsub_message_retention_seconds" {
  description = "Tempo de retenção de mensagens no tópico Pub/Sub em segundos"
  type        = string
  default     = "86600s"
}

variable "pubsub_ack_deadline_seconds" {
  description = "Prazo de acknowledgment das mensagens em segundos"
  type        = number
  default     = 60

  validation {
    condition     = var.pubsub_ack_deadline_seconds >= 10 && var.pubsub_ack_deadline_seconds <= 600
    error_message = "Ack deadline deve ser entre 10 e 600 segundos."
  }
}

variable "pubsub_max_delivery_attempts" {
  description = "Máximo de tentativas de entrega antes de enviar para Dead Letter Topic"
  type        = number
  default     = 5

  validation {
    condition     = var.pubsub_max_delivery_attempts >= 5 && var.pubsub_max_delivery_attempts <= 100
    error_message = "Máximo de tentativas deve ser entre 5 e 100."
  }
}

variable "pubsub_min_retry_backoff" {
  description = "Backoff mínimo entre tentativas de reentrega no Pub/Sub"
  type        = string
  default     = "10s"
}

variable "pubsub_max_retry_backoff" {
  description = "Backoff máximo entre tentativas de reentrega no Pub/Sub"
  type        = string
  default     = "300s"
}

# ─────────────────────────────────────────────────────────────
# MONITORAMENTO E ALERTAS
# ─────────────────────────────────────────────────────────────

variable "alert_email" {
  description = "Email para recebimento de alertas de monitoramento"
  type        = string
  default     = "cashflow-alerts@empresa.com"
}

variable "spanner_storage_alert_threshold_bytes" {
  description = "Threshold em bytes para alerta de storage do Spanner (default: 280 GiB)"
  type        = number
  default     = 300706963456  # 280 GiB em bytes
}

variable "budget_monthly_amount_usd" {
  description = "Budget mensal em USD para o Cloud Spanner com CUD aplicado"
  type        = string
  default     = "270"
}