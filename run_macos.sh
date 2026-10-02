#!/usr/bin/env bash
set -e

# Direktori lokasi script ini berada (misal ~/Downloads)
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Cari file JAR Lyreon
JAR_PATH="$(find "$DIR" -maxdepth 1 -name "Lyreon-macos-arm64-*.jar" | head -n 1)"

if [ -z "$JAR_PATH" ] || [ ! -f "$JAR_PATH" ]; then
    if [ -f "$DIR/Lyreon-macos-arm64-3.5.0.jar" ]; then
        JAR_PATH="$DIR/Lyreon-macos-arm64-3.5.0.jar"
    else
        echo "[ERROR] File JAR Lyreon tidak ditemukan di: $DIR"
        echo "Pastikan 'Lyreon-macos-arm64-3.5.0.jar' ada di folder yang sama."
        exit 1
    fi
fi

# Fungsi deteksi executable Java yang valid di macOS
find_java() {
    # 1. Cek JAVA_HOME jika disetel
    if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        if "$JAVA_HOME/bin/java" -version >/dev/null 2>&1; then
            echo "$JAVA_HOME/bin/java"
            return 0
        fi
    fi

    # 2. Cek utilitas resmi macOS java_home
    if [ -x "/usr/libexec/java_home" ]; then
        local jh
        jh="$(/usr/libexec/java_home 2>/dev/null || true)"
        if [ -n "$jh" ] && [ -x "$jh/bin/java" ]; then
            if "$jh/bin/java" -version >/dev/null 2>&1; then
                echo "$jh/bin/java"
                return 0
            fi
        fi
    fi

    # 3. Cek Homebrew OpenJDK di Apple Silicon (/opt/homebrew)
    local brew_paths=(
        "/opt/homebrew/opt/openjdk@21/bin/java"
        "/opt/homebrew/opt/openjdk/bin/java"
        "/opt/homebrew/bin/java"
        "/usr/local/opt/openjdk@21/bin/java"
        "/usr/local/opt/openjdk/bin/java"
    )
    for p in "${brew_paths[@]}"; do
        if [ -x "$p" ] && "$p" -version >/dev/null 2>&1; then
            echo "$p"
            return 0
        fi
    done

    # 4. Cek direktori JVM standar macOS
    for jvm in /Library/Java/JavaVirtualMachines/*/Contents/Home/bin/java; do
        if [ -x "$jvm" ] && "$jvm" -version >/dev/null 2>&1; then
            echo "$jvm"
            return 0
        fi
    done

    # 5. Cek SDKMAN / ASDF jika ada
    if [ -x "$HOME/.sdkman/candidates/java/current/bin/java" ]; then
        if "$HOME/.sdkman/candidates/java/current/bin/java" -version >/dev/null 2>&1; then
            echo "$HOME/.sdkman/candidates/java/current/bin/java"
            return 0
        fi
    fi

    # 6. Fallback command java
    if command -v java >/dev/null 2>&1; then
        if java -version >/dev/null 2>&1; then
            echo "java"
            return 0
        fi
    fi

    return 1
}

JAVA_BIN="$(find_java || true)"

if [ -z "$JAVA_BIN" ]; then
    echo "================================================================="
    echo "[ERROR] Java Runtime (JDK 21) belum terpasang di Mac Anda."
    echo "Penyebab: macOS membutuhkan JRE/JDK native Apple Silicon (ARM64)."
    echo "================================================================="
    echo ""
    echo "CARA PASANG (Pilih salah satu):"
    echo ""
    echo "1. CARA PALING MUDAH (Installer PKG tanpa terminal):"
    echo "   Unduh installer resmi Temurin JDK 21 (ARM64) langsung:"
    echo "   https://api.adoptium.net/v3/installer/latest/21/ga/mac/aarch64/jdk/hotspot/normal/eclipse"
    echo "   (Buka file .pkg hasil download dan klik Next sampai selesai)"
    echo ""
    echo "2. VIA HOMEBREW (di Terminal Mac):"
    echo "   brew install openjdk@21"
    echo "   sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk"
    echo "================================================================="
    exit 1
fi

ICON_ARGS=()
if [ -f "$DIR/ic_launcher.png" ]; then
    ICON_ARGS+=("-Xdock:icon=$DIR/ic_launcher.png")
fi

# Bersihkan cache OpenJFX lama jika ada dylib x86_64 yang tertinggal
rm -rf "$HOME/.openjfx/cache"

echo "[LYREON] Menggunakan Java: $($JAVA_BIN -version 2>&1 | head -n 1)"
echo "[LYREON] Menjalankan $(basename "$JAR_PATH") di Apple Silicon (ARM64)..."

exec "$JAVA_BIN" \
    -Xdock:name="Lyreon" \
    "${ICON_ARGS[@]}" \
    -Dfile.encoding=UTF-8 \
    -jar "$JAR_PATH"
