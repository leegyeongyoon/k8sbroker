#!/bin/sh

cd ${path}

#terraform init -plugin-dir=/Users/kimjaejung/.terraform/providers
terraform init
terraform apply -auto-approve
