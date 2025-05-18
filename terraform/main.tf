# Cloud Storage
resource "google_storage_bucket" "db" {
  name     = "colormaster-db"
  location = "asia-northeast1"
}

# Artifact Registry
resource "google_project_service" "artifact_registry" {
  service = "artifactregistry.googleapis.com"
}

resource "google_artifact_registry_repository" "main" {
  depends_on = [google_project_service.artifact_registry]

  location      = "asia-northeast1"
  repository_id = "colormaster-backend"
  format        = "DOCKER"
}
