#!/usr/bin/env bash

# Navigasi ke direktori tempat file .command berada
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Pastikan run_macos.sh memiliki izin eksekusi
if [ -f "$DIR/run_macos.sh" ]; then
    chmod +x "$DIR/run_macos.sh"
    exec "$DIR/run_macos.sh" "$@"
else
    echo "[ERROR] File run_macos.sh tidak ditemukan di $DIR"
    read -p "Tekan ENTER untuk keluar..."
    exit 1
fi
