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
echo UAC.ShellExecute "cmd.exe", "/c ""%~s0"" %params:"=""%", "", "runas", 1 >> "%temp%\getadmin.vbs"

"%temp%\getadmin.vbs"
del "%temp%\getadmin.vbs"
exit /B

:receivedAdminPermission
pushd "%cd%"
cd /D "%~dp0"

echo NextLabs Control Center starting.
sc start CompliantEnterpriseServer
sc start controlcenterpolicyvalidator.exe
pause