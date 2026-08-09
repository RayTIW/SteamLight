#!/bin/bash

set -e

BASE="$HOME/steamlight"

PYTHON="$BASE/tools/python/bin/python"
FIRMWARE_DIR="$BASE/firmware"
PORT="/dev/ttyACM0"

BOOTLOADER="$FIRMWARE_DIR/bootloader.bin"
PARTITIONS="$FIRMWARE_DIR/partitions.bin"
BOOT_APP="$FIRMWARE_DIR/boot_app0.bin"
FIRMWARE="$FIRMWARE_DIR/firmware.bin"

if [[ ! -x "$PYTHON" ]]; then
    echo "Fehler: Python-Umgebung nicht gefunden: $PYTHON"
    exit 1
fi

if [[ ! -e "$PORT" ]]; then
    echo "Fehler: SteamLight nicht gefunden: $PORT"
    exit 1
fi

for file in \
    "$BOOTLOADER" \
    "$PARTITIONS" \
    "$BOOT_APP" \
    "$FIRMWARE"
do
    if [[ ! -f "$file" ]]; then
        echo "Fehler: Firmware-Datei fehlt: $file"
        exit 1
    fi
done

if ! "$PYTHON" -m esptool version >/dev/null 2>&1; then
    echo "Fehler: esptool ist nicht installiert."
    exit 1
fi

echo "Flashe SteamLight über $PORT ..."

"$PYTHON" -m esptool \
    --chip esp32c3 \
    --port "$PORT" \
    --baud 460800 \
    write-flash \
    0x00000000 "$BOOTLOADER" \
    0x00008000 "$PARTITIONS" \
    0x0000e000 "$BOOT_APP" \
    0x00010000 "$FIRMWARE"

echo "Firmware erfolgreich aktualisiert."