@echo off

if "%PROCESSOR_ARCHITECTURE%" == "amd64" (
>nul 2>&1 "%SYSTEMROOT%\SysWOW64\cacls.exe" "%SYSTEMROOT%\SysWOW64\config\system"
) else (
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
)

if not '%errorlevel%' == '0' (
    goto requestAdminPermission
) else ( goto receivedAdminPermission )

:requestAdminPermission
echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
set params= %*
echo UAC.ShellExecute "cmd.exe", "/c ""%~f0"" %params:"=""%", "", "runas", 1 >> "%temp%\getadmin.vbs"

"%temp%\getadmin.vbs"
del "%temp%\getadmin.vbs"
exit /b

:receivedAdminPermission
pushd "%cd%"
cd /d "%~dp0"

set /p NEXTLABS_CC_UNINSTALL_CONFIRMATION="Do you want to uninstall NextLabs Control Center (y - Yes / n - No)? "
if /i "%NEXTLABS_CC_UNINSTALL_CONFIRMATION%" == "y" goto uninstall
if /i "%NEXTLABS_CC_UNINSTALL_CONFIRMATION%" == "Y" goto uninstall
if /i "%NEXTLABS_CC_UNINSTALL_CONFIRMATION%" == "yes" goto uninstall
goto finish
:uninstall
start /b /wait cmd /c call "tools\control-center.bat" -uninstall
if exist "action-cc-delete.txt" goto confirmDeleteCcDirectory
goto finish

:confirmDeleteCcDirectory
set /p NEXTLABS_CC_DIRECTORY=<"action-cc-delete.txt"
if [NEXTLABS_CC_DIRECTORY] == [] goto finish
echo(
echo Before deleting the Control Center directory, verify the path to ensure that the directory is not shared with another instance of Control Center.
del "action-cc-delete.txt" >nul 2>&1
echo(
set /p NEXTLABS_CC_UNINSTALL_DELETE_CONFIRMATION="Do you want to delete the directory %NEXTLABS_CC_DIRECTORY% (y - Yes / n - No)? "
if /i "%NEXTLABS_CC_UNINSTALL_DELETE_CONFIRMATION%" == "y" goto deleteCcDirectory
if /i "%NEXTLABS_CC_UNINSTALL_DELETE_CONFIRMATION%" == "Y" goto deleteCcDirectory
if /i "%NEXTLABS_CC_UNINSTALL_DELETE_CONFIRMATION%" == "yes" goto deleteCcDirectory
goto finish

:deleteCcDirectory
cd "%NEXTLABS_CC_DIRECTORY%\.."
start /b /wait cmd /c call rmdir /q /s "%NEXTLABS_CC_DIRECTORY%"
echo Control Center directory has deleted.

:finish
pause
