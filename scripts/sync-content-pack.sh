#!/usr/bin/env bash
# Copies the content pack from the laptop pipeline into content/pack/.
# The source is `margin.packSource` in local.properties, or the first argument.
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd)"
destination="$repo_root/content/pack"

read_pack_source() {
  grep -E '^margin\.packSource=' "$repo_root/local.properties" 2>/dev/null | cut -d= -f2-
}

expand_home() {
  echo "${1/#\~/$HOME}"
}

source_dir="$(expand_home "${1:-$(read_pack_source)}")"

if [[ -z "$source_dir" || ! -f "$source_dir/manifest.json" ]]; then
  echo "No pack found at '${source_dir}'." >&2
  echo "Set margin.packSource in local.properties, or pass the pack directory as an argument." >&2
  exit 1
fi

mkdir -p "$destination"
rsync -a --delete --exclude .DS_Store "$source_dir/" "$destination/"
echo "Synced pack $(grep -m1 '"version"' "$destination/manifest.json" | tr -d ' ,') into content/pack"
