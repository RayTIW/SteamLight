#!/bin/bash

set -e

SERVICE_SOURCE="$(cd "$(dirname "$0")" && pwd)/steamlight.service"
SERVICE_TARGET="$HOME/.config/systemd/user/steamlight.service"

mkdir -p "$HOME/.config/systemd/user"

cp "$SERVICE_SOURCE" "$SERVICE_TARGET"

systemctl --user daemon-reload
systemctl --user enable steamlight.service
systemctl --user restart steamlight.service

echo "SteamLight-Service installiert und gestartet."