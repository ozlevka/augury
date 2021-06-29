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


