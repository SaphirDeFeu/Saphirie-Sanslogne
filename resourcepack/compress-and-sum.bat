@echo off

REM Use the 'where' command to check for 7z
where 7z >nul 2>&1

REM Check the errorlevel to determine if 7z is available
if %errorlevel%==0 (
	echo.
) else (
	echo 7z n'est pas disponible. Annulation...
	pause
	exit /b
)

set /p DUMMY=Continuer ce script implique lancer le script compress-and-sum.ps1 et executer ses commandes. Veuillez vous assurer que c'est bien ce que vous voulez faire et que le script n'a pas ete modifie en quelque maniere.

powershell -NoProfile -ExecutionPolicy Bypass -File "compress-and-sum.ps1"

pause