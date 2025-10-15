#!/usr/bin/env bash
set -euo pipefail

# Tail Logcat for a single app process, launching it if not running.
# Usage: ./scripts/logcat_app.sh [package]
# Default package: com.bluecodeltd.chw.ecap

PKG=${1:-com.bluecodeltd.chw.ecap}

ADB=${ADB:-adb}

start_app_if_needed() {
  if ! $ADB shell pidof -s "$PKG" >/dev/null 2>&1; then
    echo "[logcat_app] Starting $PKG via monkey..."
    $ADB shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 || true
    # give it a moment to start
    sleep 2
  fi
}

ensure_pid() {
  local pid
  pid=$($ADB shell pidof -s "$PKG" | tr -d '\r') || true
  if [[ -z "$pid" ]]; then
    # fallback for older images
    pid=$($ADB shell ps -A | awk -v pkg="$PKG" '$0 ~ pkg {print $2; exit}' | tr -d '\r') || true
  fi
  echo "$pid"
}

start_app_if_needed
PID=$(ensure_pid)
if [[ -z "$PID" ]]; then
  echo "[logcat_app] Could not find PID for $PKG. Is the app installed?"
  exit 1
fi

echo "[logcat_app] Attaching to $PKG (pid=$PID). Press Ctrl+C to exit."
exec $ADB logcat --pid="$PID" -v time

