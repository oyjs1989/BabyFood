# Gradle Sync Shutdown Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Gradle Sync Shutdown Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Monitoring Gradle processes..." -ForegroundColor Yellow
Write-Host "Press Ctrl+C to cancel" -ForegroundColor Gray
Write-Host ""

while ($true) {
    $gradleRunning = $false
    
    try {
        $gradleProcess = Get-Process -Name "gradle" -ErrorAction SilentlyContinue
        if ($gradleProcess) { $gradleRunning = $true }
        
        $gradlewProcess = Get-Process -Name "gradlew" -ErrorAction SilentlyContinue
        if ($gradlewProcess) { $gradleRunning = $true }
        
        $javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue
        if ($javaProcesses) {
            foreach ($javaProc in $javaProcesses) {
                try {
                    $cmdLine = (Get-WmiObject Win32_Process -Filter "ProcessId=$($javaProc.Id)").CommandLine
                    if ($cmdLine -and ($cmdLine -match "gradle|org.gradle.launcher")) {
                        $gradleRunning = $true
                        break
                    }
                } catch {}
            }
        }
    } catch {}

    if ($gradleRunning) {
        Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Gradle is running..." -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "  Gradle Sync Completed!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Shutting down in 10 seconds..." -ForegroundColor Yellow
        Write-Host "Press any key to cancel..." -ForegroundColor Red
        Write-Host ""

        $timeout = 10
        for ($i = $timeout; $i -gt 0; $i--) {
            Write-Host "`rShutdown in: $i seconds " -NoNewline -ForegroundColor Yellow
            if ($host.UI.RawUI.KeyAvailable) {
                $key = $host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
                Write-Host ""
                Write-Host "Shutdown cancelled!" -ForegroundColor Red
                exit
            }
            Start-Sleep -Seconds 1
        }

        Write-Host ""
        Write-Host "Shutting down..." -ForegroundColor Red
        shutdown /s /t 0 /f
        exit
    }

    Start-Sleep -Seconds 5
}
