@echo off
setlocal
cd /d "%~dp0"

set "JAR_PATH=%~dp0Lyreon-windows-x64-3.5.0.jar"
set "EXE_PATH=%~dp0desktop\build\compose\binaries\main\app\Lyreon\Lyreon.exe"

if exist "%JAR_PATH%" (
    echo [LYREON] Menjalankan Lyreon Windows x64 (%JAR_PATH%)...
    start "" javaw -Dfile.encoding=UTF-8 -jar "%JAR_PATH%"
    if errorlevel 1 (
        java -Dfile.encoding=UTF-8 -jar "%JAR_PATH%"
    )
    exit /b 0
)

if exist "%EXE_PATH%" (
    echo [LYREON] Menjalankan Lyreon Desktop dari binary mandiri...
    start "" "%EXE_PATH%"
    exit /b 0
)

echo [LYREON] Binary belum ada, menjalankan via Gradle...
call gradlew.bat :desktop:run
