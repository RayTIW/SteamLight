#!/bin/bash

set -e

SERVICE_TARGET="$HOME/.config/systemd/user/steamlight.service"

systemctl --user stop steamlight.service 2>/dev/null || true
systemctl --user disable steamlight.service 2>/dev/null || true

rm -f "$SERVICE_TARGET"

systemctl --user daemon-reload

echo "SteamLight-Service entfernt."