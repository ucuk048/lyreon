#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$DIR/.." && pwd)"

echo "================================================================="
echo "[LYREON] Memulai proses pembuatan paket iOS (.ipa)"
echo "================================================================="

cd "$ROOT_DIR"

echo "[LYREON] 1. Kompilasi KMP iOS Framework (iosArm64)..."
./gradlew :ios:linkReleaseFrameworkIosArm64 --no-daemon

FRAMEWORK_DIR="$(find "$ROOT_DIR/ios/build" -type d -name "LyreonApp.framework" 2>/dev/null | head -n 1)"
if [ -z "$FRAMEWORK_DIR" ] || [ ! -d "$FRAMEWORK_DIR" ]; then
    echo "[ERROR] Framework LyreonApp.framework tidak ditemukan di $ROOT_DIR/ios/build"
    echo "Isi direktori $ROOT_DIR/ios/build:"
    find "$ROOT_DIR/ios/build" -maxdepth 5 2>/dev/null || true
    exit 1
fi

echo "[LYREON] Menemukan framework di: $FRAMEWORK_DIR"
FRAMEWORK_PARENT="$(dirname "$FRAMEWORK_DIR")"
OUTPUT_DIR="$ROOT_DIR/build/ipa"
PAYLOAD_DIR="$OUTPUT_DIR/Payload"
APP_DIR="$PAYLOAD_DIR/Lyreon.app"

echo "[LYREON] 2. Menyiapkan struktur aplikasi Apple iOS..."
rm -rf "$OUTPUT_DIR"
mkdir -p "$APP_DIR/Frameworks"

# Salin framework ke dalam bundle
cp -R "$FRAMEWORK_DIR" "$APP_DIR/Frameworks/"

echo "[LYREON] 3. Kompilasi launcher Swift native..."
SDK_PATH="$(xcrun --sdk iphoneos --show-sdk-path)"

swiftc "$DIR/iOSApp.swift" \
    -parse-as-library \
    -target arm64-apple-ios16.0 \
    -sdk "$SDK_PATH" \
    -F "$FRAMEWORK_PARENT" \
    -framework LyreonApp \
    -framework UIKit \
    -framework AVFoundation \
    -framework MediaPlayer \
    -Xlinker -rpath -Xlinker @executable_path/Frameworks \
    -o "$APP_DIR/Lyreon"

cp "$DIR/Info.plist" "$APP_DIR/Info.plist"
echo "APPL????" > "$APP_DIR/PkgInfo"

echo "[LYREON] 4. Signing ad-hoc untuk sideloading..."
codesign --force --deep --sign - "$APP_DIR"

echo "[LYREON] 5. Mengompresi Payload menjadi Lyreon-3.5.0.ipa..."
cd "$OUTPUT_DIR"
zip -qry "$ROOT_DIR/Lyreon-3.5.0.ipa" Payload

echo "================================================================="
echo "[LYREON] Selesai! Berkas IPA berhasil dibuat:"
ls -lh "$ROOT_DIR/Lyreon-3.5.0.ipa"
echo "================================================================="
