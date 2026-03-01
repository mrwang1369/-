@echo off
chcp 65001 >nul
echo 🎨 正在创建图标目录...
mkdir images\tab 2>nul

echo ✅ 目录创建完成！
echo.
echo 📝 接下来请按以下步骤操作：
echo.
echo 1. 打开浏览器访问以下任意一个网站下载图标：
echo    - https://www.iconfont.cn/ (推荐，中文)
echo    - https://www.flaticon.com/
echo    - https://www.iconfinder.com/
echo.
echo 2. 搜索关键词：home, pet, health, alarm, map
echo.
echo 3. 下载PNG格式图标，尺寸设为 81x81 像素
echo.
echo 4. 将图标重命名并保存到 images\tab 目录：
echo    - home.png / home-active.png
echo    - pets.png / pets-active.png
echo    - health.png / health-active.png
echo    - reminder.png / reminder-active.png
echo    - map.png / map-active.png
echo.
echo 💡 提示：每个图标需要两个版本
echo    - 普通版本：灰色 (#CCCCCC)
echo    - 选中版本：粉色 (#FF6B9D)
echo.
echo 或者双击打开 generate-icons.html 使用在线工具生成图标
echo.
pause
