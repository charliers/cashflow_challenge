# infra/terraform/gke.tf

# ─── GKE Cluster Standard ─────────────────────────────────────
resource "google_container_cluster" "cashflow" {
  name     = "cashflow-cluster"
  location = var.region  # us-central1 — regional cluster (1 região, 3 zonas)

  # Remove o node pool default para usar node pool customizado
  remove_default_node_pool = true
  initial_node_count       = 1

  # Kubernetes Edition: GKE Standard
  enable_autopilot = false

  # Rede e segurança
  network    = "default"
  subnetwork = "default"

  # Confidential GKE Nodes: false
  confidential_nodes {
    enabled = false
  }

  # Configurações de release channel
  release_channel {
    channel = "REGULAR"
  }

  # Addons
  addons_config {
    http_load_balancing {
      disabled = false
    }

    horizontal_pod_autoscaling {
      disabled = false
    }

    gce_persistent_disk_csi_driver_config {
      enabled = true  # necessário para SSD persistent disk
    }
  }

  # Logging e monitoramento nativos GCP
  logging_service    = "logging.googleapis.com/kubernetes"
  monitoring_service = "monitoring.googleapis.com/kubernetes"

  # Workload Identity para acesso seguro ao Spanner e Pub/Sub
  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  labels = {
    environment   = var.environment
    team          = "cashflow"
    region        = var.region
    managed_by    = "terraform"
    discount_type = "resource-cud-3yr"
  }
}

# ─── Node Pool Principal ───────────────────────────────────────
resource "google_container_node_pool" "cashflow_nodes" {
  name       = "cashflow-node-pool"
  cluster    = google_container_cluster.cashflow.name
  location   = var.region  # regional: distribui os 4 nodes entre as zonas

  # Number of Nodes: 4 (total regional)
  node_count = var.gke_node_count  # 4 nodes distribuídos entre zonas

  # Provisioning model: Regular (não Spot/Preemptible)
  node_config {
    # Machine type: c2-standard-4
    # 4 vCPU | 16 GB RAM — compute-optimized
    machine_type = var.gke_machine_type  # "c2-standard-4"

    # Operating System: Container-Optimized OS (free)
    image_type = "COS_CONTAINERD"  # Free: Container-optimized

    # Boot disk type: SSD persistent disk
    disk_type = "pd-ssd"

    # Boot disk size: 10 GiB
    disk_size_gb = var.gke_boot_disk_size  # 10

    # Add GPUs: false
    # (sem bloco guest_accelerator)

    # Local SSD: None
    local_ssd_count = 0

    # Confidential GKE Nodes: false
    # (já definido no cluster, reforçado aqui)
    enable_confidential_storage = false

    # Sustained use discounts: false
    # (SUD não se aplica com CUD ativo)

    # Workload Identity no node pool
    workload_metadata_config {
      mode = "GKE_METADATA"
    }

    # Service account dedicada ao node pool
    service_account = google_service_account.gke_node_sa.email

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]

    # Metadata de segurança
    metadata = {
      disable-legacy-endpoints = "true"
    }

    # Shielded Instance (recomendado sem Confidential Nodes)
    shielded_instance_config {
      enable_secure_boot          = true
      enable_integrity_monitoring = true
    }

    labels = {
      environment = var.environment
      team        = "cashflow"
      node_pool   = "cashflow-node-pool"
    }

    tags = [
      "cashflow-node",
      var.environment
    ]
  }

  # Autoscaling desabilitado — node count fixo para CUD
  autoscaling {
    min_node_count = var.gke_node_count
    max_node_count = var.gke_node_count
  }

  # Gestão de upgrades
  management {
    auto_repair  = true
    auto_upgrade = true
  }

  # Upgrade policy: surge upgrade para zero downtime
  upgrade_settings {
    strategy        = "SURGE"
    max_surge       = 1
    max_unavailable = 0
  }

  depends_on = [
    google_container_cluster.cashflow
  ]
}

# ─── Service Account para Node Pool ───────────────────────────
resource "google_service_account" "gke_node_sa" {
  account_id   = "cashflow-gke-node-sa"
  display_name = "CashFlow GKE Node Service Account"
  description  = "SA dedicada aos nodes do cluster GKE CashFlow"
}

# Permissões mínimas para os nodes (princípio do least privilege)
resource "google_project_iam_member" "gke_node_log_writer" {
  project = var.project_id
  role    = "roles/logging.logWriter"
  member  = "serviceAccount:${google_service_account.gke_node_sa.email}"
}

resource "google_project_iam_member" "gke_node_metric_writer" {
  project = var.project_id
  role    = "roles/monitoring.metricWriter"
  member  = "serviceAccount:${google_service_account.gke_node_sa.email}"
}

resource "google_project_iam_member" "gke_node_monitoring_viewer" {
  project = var.project_id
  role    = "roles/monitoring.viewer"
  member  = "serviceAccount:${google_service_account.gke_node_sa.email}"
}

resource "google_project_iam_member" "gke_node_artifact_registry" {
  project = var.project_id
  role    = "roles/artifactregistry.reader"
  member  = "serviceAccount:${google_service_account.gke_node_sa.email}"
}

# ─── Committed Use Discount — Resource-based 3 anos ───────────
resource "google_compute_commitment" "gke_cud_3yr" {
  name        = "cashflow-gke-cud-3yr"
  description = "Resource-based CUD 3 anos para GKE c2-standard-4 x4 nodes — CashFlow"
  region      = var.region
  plan        = "THIRTY_SIX_MONTH"  # 3 anos
  type        = "GENERAL_PURPOSE_C2" # família c2 (compute-optimized)

  # c2-standard-4 = 4 vCPU + 16GB RAM por node
  # 4 nodes = 16 vCPU + 64 GB RAM total
  resources {
    type   = "VCPU"
    amount = "16"  # 4 vCPU × 4 nodes
  }

  resources {
    type   = "MEMORY"
    amount = "65536"  # 16 GB × 4 nodes em MB (64 GB total)
  }

  auto_renew = false  # não renova automaticamente
}