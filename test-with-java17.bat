@echo off
echo 正在切换到Java 17环境...

REM 保存当前JAVA_HOME
set OLD_JAVA_HOME=%JAVA_HOME%

REM 设置Java 17路径（请根据你的实际安装路径调整）
set JAVA_HOME=C:\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo 当前Java版本：
java -version

echo.
echo 开始编译测试...
mvn clean compile -q

if %ERRORLEVEL% EQU 0 (
    echo 编译成功！
    echo 测试Lombok功能...
    mvn test -Dtest=HealthModuleFunctionalityTest -q
) else (
    echo 编译失败，请检查错误信息
)

echo.
echo 恢复原来的Java环境...
set JAVA_HOME=%OLD_JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%

echo 完成！
pause