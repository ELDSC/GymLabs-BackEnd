@echo off
setlocal

:: Configuracion de la ruta destino de los backups (proveida por el usuario)
set BACKUP_DIR=C:\Users\saran\OneDrive\Desktop\Backups

:: Validar si el directorio existe, si no, crearlo
if not exist "%BACKUP_DIR%" (
    mkdir "%BACKUP_DIR%"
    echo Carpeta de backups creada en %BACKUP_DIR%
)

:: Generar una estampa de tiempo (YYYYMMDD_HHMMSS) para el nombre del archivo
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%b%%a)
for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
set mytime=%mytime: =0%
set TIMESTAMP=%mydate%_%mytime%

:: Credenciales y conexion de la BD en produccion (Railway)
set DB_HOST=tokaido.proxy.rlwy.net
set DB_PORT=40899
set DB_USER=root
set DB_PASS=JHIpeUOmljsRJidQucjqJNWmCrPAkcEd
set DB_NAME=railway

set BACKUP_FILE=%BACKUP_DIR%\gymlabs_backup_%TIMESTAMP%.sql

echo ========================================================
echo Iniciando respaldo de la base de datos de GymLabs...
echo Host: %DB_HOST%
echo Destino: %BACKUP_FILE%
echo ========================================================

:: Ejecutar mysqldump
:: NOTA: Es necesario tener 'mysqldump' configurado en las Variables de Entorno (PATH) de Windows
mysqldump -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% > "%BACKUP_FILE%"

if %errorlevel% equ 0 (
    echo [EXITO] Respaldo completado satisfactoriamente.
) else (
    echo [ERROR] Hubo un problema al generar el respaldo. Verifique que 'mysqldump' este instalado y en el PATH.
)

endlocal
pause
