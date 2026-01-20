@echo off
chcp 65001 >nul
echo ========================================
echo   Gradle Sync 完成后自动关机脚本
echo ========================================
echo.
echo 正在启动脚本...
echo.

powershell -ExecutionPolicy Bypass -File "%~dp0shutdown-after-sync.ps1"

pause