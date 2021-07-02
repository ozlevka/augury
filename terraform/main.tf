
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



resource "digitalocean_droplet" "jenkins_server" {
  image    = "docker-20-04"
  name     = "jenkins"
  region   = "ams3"
  size     = "s-1vcpu-1gb"
  ssh_keys = [30371814, 30684983]

  connection {
    host = self.ipv4_address
    user = "root"
    type = "ssh"
    private_key = file(var.pvt_key)
    timeout = "2m"
  }

  depends_on = [null_resource.doctl_apply_token]

  provisioner "remote-exec" {
    inline = [
      "ufw disable",
      "echo ${var.github_token} | docker login ghcr.io/ozlevka -u ozlevka --password-stdin",
      "docker run -d -v ~/.kube:/root/.kube --network host ghcr.io/ozlevka/jenkins-empty:v0.0.4"
    ]
  } 
}

resource "digitalocean_firewall" "jenkins_firewall" {
  name = "augury-test-terraform"
  droplet_ids = [digitalocean_droplet.jenkins_server.id]

  inbound_rule {
    protocol         = "tcp"
    port_range       = "8080"
    source_addresses = ["140.82.112.0/20", "143.55.64.0/20", "185.199.108.0/22", "192.30.252.0/22"]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "1-65535"
    source_addresses = ["10.133.0.0/24", var.current_ip]
  }

  outbound_rule {
    protocol              = "tcp"
    port_range            = "1-65535"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }

  outbound_rule {
    protocol              = "udp"
    port_range            = "1-65535"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }

  outbound_rule {
    protocol              = "icmp"
    port_range            = "1-65535"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }
}

resource "digitalocean_kubernetes_cluster" "augury_test" {
  name    = "augury-test"
  region  = "ams3"
  version = "1.19.12-do.0"

  node_pool {
    name       = "augury-worker-pool"
    size       = "s-2vcpu-2gb"
    node_count = 2
  }
  
  depends_on = [null_resource.doctl_apply_token]
}

resource "null_resource" "finish_cluster" {
  provisioner "local-exec" {
     command = "./complete_infra.py"
     interpreter = ["/usr/local/bin/python", "-u"]
     environment = {
       CURRENT_IP = var.current_ip
     }
  }

  depends_on = [digitalocean_kubernetes_cluster.augury_test]
}


