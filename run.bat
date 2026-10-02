@echo off
cd /d "%~dp0"
if exist out rmdir /s /q out
javac --release 17 -d out src\com\group12\ripperdoc\*.java src\com\group12\ripperdoc\model\*.java src\com\group12\ripperdoc\decorator\*.java src\com\group12\ripperdoc\service\*.java src\com\group12\ripperdoc\api\*.java
if errorlevel 1 exit /b 1
java -cp out com.group12.ripperdoc.Main %*
