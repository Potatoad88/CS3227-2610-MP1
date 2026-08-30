#!/usr/bin/env bash
# Checks CS3227-2610-MP1 repo against submission requirements.
# Usage: ./check_mp1_structure.sh [path-to-repo]

REPO="${1:-.}"
FAIL=0

check_dir() {
    if [ -d "$REPO/$1" ] && [ "$(ls -A "$REPO/$1" 2>/dev/null)" ]; then
        echo "[OK]   $1/ exists and is non-empty"
    else
        echo "[FAIL] $1/ missing or empty"
        FAIL=1
    fi
}

check_file() {
    if [ -f "$REPO/$1" ]; then
        echo "[OK]   $1 exists"
    else
        echo "[FAIL] $1 missing"
        FAIL=1
    fi
}

check_jar() {
    if compgen -G "$REPO/release/*.jar" > /dev/null; then
        echo "[OK]   release/ contains a .jar file"
    else
        echo "[FAIL] release/ has no .jar file"
        FAIL=1
    fi
}

check_repo_name() {
    NAME=$(basename "$(git -C "$REPO" rev-parse --show-toplevel 2>/dev/null)")
    if [ "$NAME" == "CS3227-2610-MP1" ]; then
        echo "[OK]   repo folder named CS3227-2610-MP1"
    else
        echo "[WARN] repo folder is '$NAME', expected CS3227-2610-MP1 (check GitHub repo name too)"
    fi
}

echo "Checking MP1 submission structure in: $REPO"
echo "----------------------------------------"

check_repo_name
check_dir "src"
check_dir "release"
check_jar
check_dir "docs"
check_file "docs/UserGuide.md"
check_file "docs/DeveloperGuide.md"
check_file "docs/Reflections.md"
check_dir "logs"

echo "----------------------------------------"
if [ "$FAIL" -eq 0 ]; then
    echo "All required items present."
else
    echo "Missing items found above, fix it before submitting."
fi

exit $FAIL
