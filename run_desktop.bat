@echo off
setlocal
cd /d "%~dp0"

set "EXE_PATH=desktop\build\compose\binaries\main\app\Lyreon\Lyreon.exe"

if exist "%EXE_PATH%" (
    echo [LYREON] Menjalankan Lyreon Desktop dari binary mandiri...
    start "" "%EXE_PATH%"
) else (
    echo [LYREON] Binary belum ada, menjalankan via Gradle...
    call gradlew.bat :desktop:run
)
