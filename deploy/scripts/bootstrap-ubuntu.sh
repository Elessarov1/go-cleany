#!/usr/bin/env bash
set -Eeuo pipefail

if [[ ${EUID} -ne 0 ]]; then
  echo "Run this script as root: sudo ./deploy/scripts/bootstrap-ubuntu.sh [deploy-user]" >&2
  exit 1
fi

if [[ ! -r /etc/os-release ]]; then
  echo "Cannot identify the operating system." >&2
  exit 1
fi

# shellcheck disable=SC1091
source /etc/os-release
if [[ ${ID:-} != "ubuntu" ]]; then
  echo "This bootstrap script supports Ubuntu only. Detected: ${ID:-unknown}." >&2
  exit 1
fi

deploy_user=${1:-${SUDO_USER:-}}

apt-get update
apt-get install -y ca-certificates curl git openssl util-linux

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

architecture=$(dpkg --print-architecture)
printf '%s\n' \
  "deb [arch=${architecture} signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu ${VERSION_CODENAME} stable" \
  > /etc/apt/sources.list.d/docker.list

apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
systemctl enable --now docker

if [[ -n ${deploy_user} ]]; then
  if ! id "${deploy_user}" >/dev/null 2>&1; then
    echo "Deploy user does not exist: ${deploy_user}" >&2
    exit 1
  fi
  usermod -aG docker "${deploy_user}"
  echo "Added ${deploy_user} to the docker group. Reconnect the SSH session before deploying."
fi

docker version --format 'Docker server: {{.Server.Version}}'
docker compose version
echo "VPS bootstrap completed. Configure the firewall before the first deployment."
