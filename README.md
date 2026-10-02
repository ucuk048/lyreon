# Lyreon

Aplikasi pemutar musik lintas-platform (Android, Windows, macOS, iOS) audio-only untuk YouTube Music. Mendukung pencarian, playlist, antrean lagu, unduhan offline lokal, dan pemutaran latar belakang tanpa henti (background playback).

Versi rilis aktif: **3.5.0**

---

## Berkas Rilis & Unduhan

| Platform | Format | Tautan Unduh | Keterangan |
|----------|--------|--------------|------------|
| Android | .apk | [Lyreon-3.5.0.apk](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.apk) | Kompatibel Android 8.0+ (ARM64 & x86_64) |
| Windows | .exe | [Lyreon-3.5.0.exe](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.exe) | Installer mandiri Windows 10/11 (x64) |
| macOS | .dmg | [Lyreon-3.5.0.dmg](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.dmg) | Apple Silicon (M1/M2/M3/M4) dengan native audio |
| iOS | .ipa | [Lyreon-3.5.0.ipa](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.ipa) | iOS 16.0+ untuk sideloading |

Informasi versi mentah: [version.txt](https://raw.githubusercontent.com/ucuk048/lyreon/main/version.txt)

---

## Fitur Utama

- **Audio-Only Streaming**: Memutar aliran audio murni (AAC/M4A dan Opus/WebM) hemat data tanpa decoding video yang membebani CPU/GPU.
- **Background Playback Penuh**:
  - Android: Foreground `MediaSessionService` + notifikasi sistem, lockscreen, dan kontrol Bluetooth.
  - iOS: Native `AVAudioSessionCategoryPlayback` + `MPNowPlayingInfoCenter` dan `MPRemoteCommandCenter`.
  - Desktop (Windows & macOS): Thread background media player mandiri.
- **Pencarian Terpadu & Jelajah**: Pencarian lagu, album, artis, dan playlist YouTube Music secara anonim tanpa login akun.
- **Penyimpanan Lokal & Unduhan Offline**: Mendukung unduhan chunked multi-part ke penyimpanan lokal untuk pemutaran tanpa kuota internet.
- **Riwayat & Koleksi Favorit**: Sinkronisasi database lokal untuk riwayat pemutaran dan daftar lagu favorit.
- **Klien InnerTube Otomatis**: Algoritma fallback tangga klien (Player Client Ladder) untuk memastikan streaming tidak terputus saat endpoint CDN kedaluwarsa.

---

## Panduan Instalasi Per Platform

### 1. Android (.apk)
1. Unduh [Lyreon-3.5.0.apk](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.apk).
2. Buka berkas APK di file manager perangkat Android Anda.
3. Izinkan instalasi dari sumber tidak dikenal (Unknown Sources) jika diminta oleh sistem.
4. Lanjutkan instalasi hingga selesai.

### 2. Windows (.exe)
1. Unduh [Lyreon-3.5.0.exe](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.exe).
2. Jalankan berkas installer `.exe`.
3. Ikuti petunjuk instalasi di layar.
4. Aplikasi akan membuat shortcut di Start Menu dan Desktop.

### 3. macOS (.dmg)
1. Unduh [Lyreon-3.5.0.dmg](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.dmg).
2. Buka berkas `.dmg`, lalu seret ikon `Lyreon.app` ke dalam folder `Applications`.
3. Buka `Lyreon.app` dari Launchpad atau Applications.
4. Jika macOS Gatekeeper menampilkan peringatan belum terverifikasi:
   - Buka System Settings -> Privacy & Security -> klik "Open Anyway", ATAU
   - Buka Terminal dan jalankan perintah:
     ```bash
     xattr -cr /Applications/Lyreon.app
     ```

### 4. iOS / iPadOS (.ipa)
Paket `.ipa` ditujukan untuk sideloading pada perangkat iPhone/iPad:
- **TrollStore** (iOS 14.0 - 16.6.1 / 17.0): Buka Safari, unduh `Lyreon-3.5.0.ipa`, lalu buka via TrollStore untuk instalasi permanen tanpa batas 7 hari.
- **AltStore / SideStore**: Hubungkan perangkat ke komputer, buka AltStore, klik tanda plus (+), pilih `Lyreon-3.5.0.ipa`, dan masukkan Apple ID.
- **Sideloadly**: Jalankan Sideloadly di PC/Mac, hubungkan iPhone via kabel USB/WiFi, seret `Lyreon-3.5.0.ipa`, masukkan Apple ID, lalu klik Start.

---

## Arsitektur Teknis

- **Mobile (Android)**: Kotlin, Jetpack Compose Material 3, AndroidX Media3 (ExoPlayer), Room Database, OkHttp.
- **Desktop (Windows & macOS)**: Kotlin, Compose Multiplatform for Desktop, JavaFX Media 21 (ARM64 native untuk macOS, x64 untuk Windows), CoreAudio `/usr/bin/afplay` fallback engine.
- **Mobile (iOS)**: Kotlin Multiplatform (KMP), Compose Multiplatform for iOS, UIKit bridge via `ComposeUIViewController`, Apple AVFoundation `AVPlayer`, `MPNowPlayingInfoCenter`.

---

## Ketentuan & Lisensi

Aplikasi ini ditujukan untuk keperluan pembelajaran dan penggunaan pribadi. Akses data audio dilakukan secara publik dan anonim tanpa melibatkan API resmi berbayar atau akun pengguna.
