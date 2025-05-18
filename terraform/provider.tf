terraform {
  required_providers {
    google = {
      source  = "hashicorp/google" # Specify the source of the Google provider
      version = "~> 6.0"          # Use a version of the Google provider that is compatible with version
    }
  }
}

provider "google" {
  project = var.project
  region  = var.region
}
