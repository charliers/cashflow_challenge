# ─── Cloud Pub/Sub ───────────────────────────────────────────
resource "google_pubsub_topic" "financialrecords" {
  name                       = "financialrecords-events"
  message_ordering           = true
  message_retention_duration = "864000s"
}

resource "google_pubsub_topic" "financialrecords_dlq" {
  name = "financialrecords-events-dlq"
}

resource "google_pubsub_subscription" "dailyConsolidator" {
  name  = "dailyconsolidator-sub"
  topic = google_pubsub_topic.financialrecords.id

  enable_message_ordering = true
  ack_deadline_seconds    = 60

  retry_policy {
    minimum_backoff = "10s"
    maximum_backoff = "300s"
  }

  dead_letter_policy {
    dead_letter_topic     = google_pubsub_topic.financialrecords_dlq.id
    max_delivery_attempts = 5
  }
}