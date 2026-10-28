#!/bin/bash
# Paste this into the Jenkins EC2 instance's User data field only.
set -euo pipefail
. /etc/os-release
if [ "$ID" != 'amzn' ] || [ "$VERSION_ID" != '2023' ]; then
    echo 'This setup requires Amazon Linux 2023.' >&2
    exit 1
fi
dnf install -y git python3.11 python3.11-pip
python3.11 -m venv /opt/mirizoom-bootstrap
/opt/mirizoom-bootstrap/bin/pip install --no-cache-dir 'ansible-core==2.19.3'
chown -R ec2-user:ec2-user /opt/mirizoom-bootstrap
# Docker, swap, Jenkins and remote server setup are managed by bootstrap.yml.
