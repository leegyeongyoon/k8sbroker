#!/bin/sh

cd ${path}

terraform init -plugin-dir=/Users/kimjaejung/.terraform/providers
terraform apply -auto-approve
