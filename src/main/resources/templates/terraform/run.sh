#!/bin/sh

cd ${path}

terraform init -plugin-dir=/home/.terraform/providers
#terraform init
terraform apply -auto-approve
