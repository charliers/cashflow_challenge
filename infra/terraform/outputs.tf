output "spanner_instance_name" {
  description = "Nome da instância Cloud Spanner"
  value       = google_spanner_instance.cashflow.name
}

output "spanner_database_lancamentos" {
  description = "Nome do banco de lançamentos"
  value       = google_spanner_database.lancamentos.name
}

output "spanner_processing_units" {
  description = "Processing units configurados"
  value       = google_spanner_instance.cashflow.processing_units
}

output "spanner_cud_commitment_name" {
  description = "Nome do Committed Use Discount ativo"
  value       = google_compute_commitment.spanner_cud_3yr.name
}

output "spanner_cud_plan" {
  description = "Plano de commitment do CUD"
  value       = google_compute_commitment.spanner_cud_3yr.plan
}

output "pubsub_topic_lancamentos" {
  description = "Tópico Pub/Sub de lançamentos"
  value       = google_pubsub_topic.lancamentos.id
}

output "redis_host" {
  description = "Host do Cloud Memorystore"
  value       = google_redis_instance.cache.host
  sensitive   = true
}

output "gke_cluster_name" {
  description = "Nome do cluster GKE"
  value       = google_container_cluster.cashflow.name
}