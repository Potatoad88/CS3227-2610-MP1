#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="8.10.2"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
GRADLE_HOME="$PROJECT_DIR/.gradle/local/gradle-$GRADLE_VERSION"
GRADLE_ZIP="$PROJECT_DIR/.gradle/local/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
export GRADLE_USER_HOME="$PROJECT_DIR/.gradle/user-home"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  mkdir -p "$PROJECT_DIR/.gradle/local"
  if [ ! -f "$GRADLE_ZIP" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    curl -L "$GRADLE_URL" -o "$GRADLE_ZIP"
  fi
  echo "Installing Gradle $GRADLE_VERSION locally..."
  if command -v unzip >/dev/null 2>&1; then
    unzip -q "$GRADLE_ZIP" -d "$PROJECT_DIR/.gradle/local"
  else
    ditto -x -k "$GRADLE_ZIP" "$PROJECT_DIR/.gradle/local"
  fi
fi

exec "$GRADLE_HOME/bin/gradle" --gradle-user-home "$GRADLE_USER_HOME" "$@"
