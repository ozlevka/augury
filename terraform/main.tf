
terraform {
  required_providers {
    digitalocean = {
      source = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}

provider "digitalocean" {
  token = var.do_token
}

resource null_resource "doctl_apply_token" {
  provisioner "local-exec" {
    command = "doctl auth init -t ${var.do_token}"
  }
}

resource "digitalocean_kubernetes_cluster" "my_test" {
  name    = "my-test"
  region  = "ams3"
  version = "1.19.12-do.0"

  node_pool {
    name       = "first-worker-pool"
    size       = var.droplet_size
    node_count = var.droplet_count
  }
  
  depends_on = [null_resource.doctl_apply_token]
}


