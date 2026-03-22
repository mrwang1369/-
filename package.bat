@echo off
set LAUNCH4J_HOME=C:\Program Files\Launch4j
if not exist "%LAUNCH4J_HOME%" (
    echo Launch4j未安装在默认路径，请先安装Launch4j
    echo 下载地址: https://sourceforge.net/projects/launch4j/files/launch4j-3/3.14/launch4j-3.14-win64.exe/download
    pause
    exit /b 1
)

echo 开始打包...
"%LAUNCH4J_HOME%\launch4jc.exe" launch4j.xml
if %errorlevel% equ 0 (
    echo 打包成功!
    echo exe文件已生成在target目录下
) else (
    echo 打包失败!
)
pause