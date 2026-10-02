# Lyreon

Aplikasi pemutar musik lintas-platform (Android, Windows, macOS, iOS) audio-only untuk YouTube Music. Dilengkapi fitur **Impor Playlist Spotify** instan dan **100% Bebas Iklan (Tanpa Iklan)**. Mendukung pencarian, playlist, unduhan offline lokal, dan pemutaran latar belakang (background playback).

Versi rilis aktif: **3.5.0**

---

## Keunggulan Utama

- **100% Bebas Iklan (Tanpa Iklan)**: Aliran audio murni langsung tanpa interupsi iklan komersial, banner pengganggu, atau jeda promosi di awal, tengah, maupun akhir lagu.
- **Impor Playlist Spotify Instan**: Tempel tautan playlist publik Spotify apa saja, Lyreon otomatis mengekstrak judul trek, artis, dan durasi, lalu mencocokkannya ke database streaming berkecepatan tinggi tanpa perlu akun berbayar.
- **Background Playback Penuh**: Musik tetap berjalan lancar saat layar mati, berganti aplikasi, atau terkunci pada Android, iOS, Windows, dan macOS.
- **Hemat Kuota & Penyimpanan**: Hanya mengalirkan data audio (AAC/M4A dan Opus/WebM) tanpa memuat beban video.

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

## Fitur Lengkap

1. **Impor Playlist Spotify**: Mendukung URL embed dan publik (`https://open.spotify.com/playlist/...`). Algoritma matcher cerdas memetakan setiap trek ke audio berbitrate tinggi secara otomatis.
2. **Tanpa Iklan (Ad-Free)**: Bebas gangguan sponsor, iklan audio, dan pelacak analitik pihak ketiga.
3. **Pemutaran Latar Belakang (Background Playback)**:
   - Android: `MediaSessionService` + notifikasi pemutar sistem, lockscreen, dan kendali Bluetooth/headset.
   - iOS: Native Apple `AVAudioSessionCategoryPlayback` + `MPNowPlayingInfoCenter` dan `MPRemoteCommandCenter`.
   - Desktop (Windows & macOS): Engine background terintegrasi.
4. **Unduhan Offline Lokal**: Unduh lagu favorit dalam format audio lokal berkualitas tinggi untuk didengarkan tanpa koneksi internet (0 kuota).
5. **Pencarian Terpadu & Anonim**: Jelajahi lagu, album, artis, dan playlist tanpa memerlukan login akun Google/YouTube.
6. **Tangga Klien InnerTube Otomatis**: Algoritma fallback berlapis (Player Client Ladder) yang menjaga keandalan stream ketika CDN utama membatasi akses.
7. **Riwayat & Koleksi Lokal**: Kelola playlist kustom, riwayat putar, dan lagu favorit yang tersimpan aman di database lokal perangkat.

---

## Panduan Impor Playlist Spotify

1. Salin tautan playlist publik dari aplikasi atau web Spotify (contoh: `https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M`).
2. Buka Lyreon di perangkat Anda, masuk ke menu **Koleksi / Library**.
3. Pilih tombol **Import Spotify**.
4. Tempel tautan ke dalam kolom input dan klik **Muat / Fetch**.
5. Sistem akan menampilkan pratinjau daftar lagu. Klik **Simpan ke Playlist** untuk menyimpannya ke koleksi lokal atau **Putar Sekarang** untuk langsung mendengarkan tanpa jeda iklan.

---

## Panduan Instalasi Per Platform

### 1. Android (.apk)
1. Unduh [Lyreon-3.5.0.apk](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.apk).
2. Buka berkas APK melalui file manager Android.
3. Berikan izin instalasi dari sumber tidak dikenal (Unknown Sources) jika diminta.
4. Selesaikan instalasi dan buka aplikasi.

### 2. Windows (.exe)
1. Unduh [Lyreon-3.5.0.exe](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.exe).
2. Jalankan berkas installer `.exe`.
3. Ikuti langkah wizard di layar hingga selesai.
4. Shortcut aplikasi akan tersedia di Desktop dan Start Menu.

### 3. macOS (.dmg)
1. Unduh [Lyreon-3.5.0.dmg](https://github.com/ucuk048/lyreon/raw/main/Lyreon-3.5.0.dmg).
2. Buka berkas `.dmg`, lalu seret `Lyreon.app` ke direktori `Applications`.
3. Buka `Lyreon.app`. Jika macOS Gatekeeper memberi notifikasi pengembang belum terverifikasi:
   - Masuk ke System Settings -> Privacy & Security -> klik "Open Anyway", ATAU
   - Jalankan perintah terminal:
     ```bash
     xattr -cr /Applications/Lyreon.app
     ```

### 4. iOS / iPadOS (.ipa)
Sideload berkas `Lyreon-3.5.0.ipa` menggunakan salah satu metode berikut:
- **TrollStore** (iOS 14.0 - 16.6.1 / 17.0): Unduh langsung via Safari dan pasang melalui TrollStore untuk instalasi permanen.
- **AltStore / SideStore**: Sambungkan perangkat ke komputer, pilih `Lyreon-3.5.0.ipa`, lalu sign menggunakan Apple ID Anda.
- **Sideloadly**: Jalankan Sideloadly di PC/Mac, seret berkas `Lyreon-3.5.0.ipa`, masukkan Apple ID, dan klik Start.

---

## Arsitektur Teknis

- **Mobile (Android)**: Kotlin, Jetpack Compose Material 3, AndroidX Media3 (ExoPlayer), Room Database, OkHttp.
- **Desktop (Windows & macOS)**: Kotlin, Compose Multiplatform for Desktop, JavaFX Media 21 (ARM64 Apple Silicon & x64 Windows), fallback CoreAudio `/usr/bin/afplay`.
- **Mobile (iOS)**: Kotlin Multiplatform (KMP), Compose Multiplatform for iOS, UIKit bridge via `ComposeUIViewController`, Apple AVFoundation `AVPlayer`, `MPNowPlayingInfoCenter`.

---

## Ketentuan & Lisensi

Aplikasi ini ditujukan untuk keperluan pembelajaran dan penggunaan pribadi. Seluruh audio dialirkan secara publik dan anonim tanpa melibatkan API resmi berbayar.
