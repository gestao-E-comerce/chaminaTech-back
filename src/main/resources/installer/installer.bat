@echo off
setlocal enableextensions

net session >nul 2>&1
if %errorlevel% neq 0 (
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

set "INSTALL_DIR=C:\MeuServicoInstaller"
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
set "TASK_NAME=MeuServicoTask"
schtasks /query /tn "%TASK_NAME%" >nul 2>&1
if %errorlevel%==0 (
    schtasks /end /tn "%TASK_NAME%" >nul 2>&1
    schtasks /delete /tn "%TASK_NAME%" /f >nul 2>&1
)

set "LOG_DIR=C:\MeuServicoLogs"
if exist "%LOG_DIR%" (
    rmdir /s /q "%LOG_DIR%"
)

if exist "%INSTALL_DIR%\start_app.bat" (
    del /q "%INSTALL_DIR%\start_app.bat"
)

set MATRIZ_ID=2
set PORT=8080
:PORT_CHECK
netstat -ano | findstr :%PORT% >nul 2>&1
if %errorlevel%==0 (
    set /a PORT+=1
    goto PORT_CHECK
)

set "JAVA_EXEC="
for /f "delims=" %%i in ('where javaw 2^>nul') do (
    "%%i" -version 2>&1 | findstr /i "17\." >nul
    if not errorlevel 1 (
        set "JAVA_EXEC=%%i"
        goto JAVA_FOUND
    )
)

if not exist "%INSTALL_DIR%\jdk64.msi" (
    echo [ERRO] Arquivo jdk64.msi nao encontrado em %INSTALL_DIR%.
    pause
    exit /b
)
msiexec /i "%INSTALL_DIR%\jdk64.msi" /quiet /norestart
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao instalar o JDK. Verifique o instalador.
    pause
    exit /b
)

for /f "delims=" %%i in ('where javaw 2^>nul') do (
    "%%i" -version 2>&1 | findstr /i "17\." >nul
    if not errorlevel 1 (
        set "JAVA_EXEC=%%i"
        goto JAVA_FOUND
    )
)
echo [ERRO] JDK nao foi instalado corretamente. Abandonando o instalador.
pause
exit /b

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
    echo start "" /B "%JAVA_EXEC%" -jar "%APP_PATH%" --server.port=%PORT% --matriz.id=%MATRIZ_ID% ^> "%LOG_DIR%\stdout.log" 2^> "%LOG_DIR%\stderr.log"
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