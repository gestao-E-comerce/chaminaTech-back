@echo off
setlocal enableextensions

net session >nul 2>&1
if %errorlevel% neq 0 (
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

set "INSTALL_DIR=C:\ChaminaTechApp"
if not exist "%INSTALL_DIR%" (
    mkdir "%INSTALL_DIR%"
    xcopy "%~dp0*" "%INSTALL_DIR%\" /E /H /C /I >nul
    if %errorlevel% neq 0 (
        echo [ERRO] Erro ao copiar arquivos para %INSTALL_DIR%. Verifique o espaco em disco ou permissao.
        pause
        exit /b
    )
)

cd /d "%INSTALL_DIR%"
set "TASK_NAME=ChaminaTechTask"
schtasks /query /tn "%TASK_NAME%" >nul 2>&1
if %errorlevel%==0 (
    schtasks /end /tn "%TASK_NAME%" >nul 2>&1
    schtasks /delete /tn "%TASK_NAME%" /f >nul 2>&1
)

set "LOG_DIR=C:\ChaminaTechLogs"
if exist "%LOG_DIR%" (
    rmdir /s /q "%LOG_DIR%"
)

if exist "%INSTALL_DIR%\start_app.bat" (
    del /q "%INSTALL_DIR%\start_app.bat"
)

set MATRIZ_ID=${MATRIZ_ID}
set TOKEN=${TOKEN}
set PORT=8080
:PORT_CHECK
netstat -ano | findstr :%PORT% >nul 2>&1
if %errorlevel%==0 (
    set /a PORT+=1
    goto PORT_CHECK
)

:: ---------- LOCALIZA / INSTALA JDK 17 ----------
set "JAVA_EXEC="
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
    if exist "%%D\bin\java.exe" set "JAVA_EXEC=%%D\bin\java.exe"
)

if not defined JAVA_EXEC (
    if not exist "%INSTALL_DIR%\jdk64.msi" (
        echo [ERRO] Java 17 nao encontrado e jdk64.msi ausente.
        pause & exit /b
    )
    echo [INFO] Instalando Java 17…
    msiexec /i "%INSTALL_DIR%\jdk64.msi" /quiet /norestart
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
        if exist "%%D\bin\java.exe" set "JAVA_EXEC=%%D\bin\java.exe"
    )
)
if not defined JAVA_EXEC (
    echo [ERRO] Java 17 ainda nao localizado. Abortando.
    pause & exit /b
)

:JAVA_FOUND
for %%a in ("%JAVA_EXEC%") do set "JAVA_HOME=%%~dpa.."
set "APP_PATH=%INSTALL_DIR%\app.jar"
if not exist "%APP_PATH%" (
    echo [ERRO] O arquivo app.jar nao foi encontrado em %INSTALL_DIR%.
    pause
    exit /b
)

mkdir "%LOG_DIR%"
(
    echo @echo off
    echo start "" /B "%JAVA_EXEC%" -jar "%APP_PATH%" --server.port=%PORT% --matriz.id=%MATRIZ_ID% --token=%TOKEN% ^> "%LOG_DIR%\stdout.log" 2^> "%LOG_DIR%\stderr.log"
) > "%INSTALL_DIR%\start_app.bat"

schtasks /create /tn "%TASK_NAME%" ^
    /tr "cmd.exe /c \"%INSTALL_DIR%\start_app.bat\"" ^
    /sc ONSTART /ru SYSTEM /rl HIGHEST /F
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao criar a tarefa agendada.
    pause
    exit /b
)

schtasks /run /tn "%TASK_NAME%"
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao iniciar a tarefa agendada.
    pause
    exit /b
)