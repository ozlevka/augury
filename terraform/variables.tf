variable do_token {
  type        = string
  description = "description"
}

variable github_token {
  type        = string
  description = "Github token for login to ghcr.io"
}

variable pvt_key {
  type        = string
  default     = "/home/vscode/.ssh/do"
  description = "Pathio to private key"
}

variable current_ip {
  type        = string
  description = "Current IP address"
}

variable droplet_size {
  type        = string
  default     = "s-2vcpu-4gb"
  description = "Droplet size into nodegroup"
}

variable droplet_count {
  type        = number
  default     = 2
  description = "Amount of droplets per default node group"
}





