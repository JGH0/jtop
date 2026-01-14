#!/usr/bin/env bash
# jtop launcher with self-update via GitHub + rebuild (requires sudo for update)

JTOP_DIR="/usr/local/lib/jtop"
JTOP_JAR="$JTOP_DIR/jtop.jar"
GIT_REPO="https://github.com/JGH0/jtop.git"

update_jtop() {
    # Check for sudo/root
    if [[ $EUID -ne 0 ]]; then
        echo "jtop --update requires root privileges. Please run with sudo."
        exit 1
    fi

    echo "Updating jtop from GitHub..."

    TMP_DIR=$(mktemp -d)
    echo "Cloning repository into $TMP_DIR..."
    if ! git clone --depth 1 "$GIT_REPO" "$TMP_DIR"; then
        echo "Failed to clone repository."
        rm -rf "$TMP_DIR"
        exit 1
    fi

    echo "Building jtop..."
    pushd "$TMP_DIR" >/dev/null
    if ! ./build.sh; then
        echo "Build failed."
        popd >/dev/null
        rm -rf "$TMP_DIR"
        exit 1
    fi

    echo "Installing new version..."
    if ! ./install.sh; then
        echo "Install failed."
        popd >/dev/null
        rm -rf "$TMP_DIR"
        exit 1
    fi

    popd >/dev/null
    rm -rf "$TMP_DIR"
    echo "Update completed successfully!"
    exit 0
}

# Handle --update argument
if [[ "$1" == "--update" ]]; then
    update_jtop
fi

# Run jtop normally
java -jar "$JTOP_JAR" "$@"