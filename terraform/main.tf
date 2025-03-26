provider "local" {
  # No configuration needed for the local provider
}

resource "local_file" "example" {
  content  = "This is a test file created by Terraform!"
  filename = "${path.module}/example.txt"
}