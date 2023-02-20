#!/bin/sh

# move temporary directory
cd ${path}

# execute terraform
terraform destroy -auto-approve
