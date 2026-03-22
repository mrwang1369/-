@echo off

echo 正在启动 DOTA2标注工具...
echo.

if not exist "target\DOTA2-1.0-SNAPSHOT.jar" (
    echo 错误: 找不到核心jar文件！
    echo 请确保已经成功构建项目。
    pause
    exit /b 1
)

java -jar target\DOTA2-1.0-SNAPSHOT.jar

if %errorlevel% neq 0 (
    echo.
    echo 程序启动失败！
    echo 请检查是否已安装Java 8或更高版本。
    echo 下载地址: https://java.com
)
pause