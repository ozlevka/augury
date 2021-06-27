
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

resource "digitalocean_droplet" "jenkins_server" {
  image    = "docker-20-04"
  name     = "jenkins"
  region   = "ams3"
  size     = "s-1vcpu-1gb"
  ssh_keys = [30371814, 30684983] 
}