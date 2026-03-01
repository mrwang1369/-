"""
快速生成微信小程序TabBar图标
"""
import os
from PIL import Image, ImageDraw, ImageFont

def create_icon(output_path, symbol, text_color, bg_color):
    """创建图标"""
    # 创建画布 (81x81 像素)
    img = Image.new('RGBA', (81, 81), bg_color)
    draw = ImageDraw.Draw(img)

    # 使用文本代替复杂图标
    font_size = 48
    try:
        # 尝试使用系统字体
        font = ImageFont.truetype("arial.ttf", font_size)
    except:
        # 如果找不到字体，使用默认字体
        font = ImageFont.load_default()

    # 绘制文本
    text_width, text_height = draw.textsize(symbol, font=font)
    position = ((81 - text_width) // 2, (81 - text_height) // 2 - 5)
    draw.text(position, symbol, fill=text_color, font=font)

    # 保存图片
    img.save(output_path, 'PNG')
    print(f"✅ 已生成: {output_path}")

def create_simple_icon(output_path, color):
    """创建简单的纯色图标"""
    # 创建画布
    img = Image.new('RGBA', (81, 81), color)
    
    # 保存
    img.save(output_path, 'PNG')
    print(f"✅ 已生成: {output_path}")

def main():
    # 图标配置
    icons = [
        {'name': 'home', 'symbol': 'H', 'title': '首页'},
        {'name': 'pets', 'symbol': 'P', 'title': '宠物'},
        {'name': 'health', 'symbol': 'H', 'title': '健康'},
        {'name': 'reminder', 'symbol': 'R', 'title': '提醒'},
        {'name': 'map', 'symbol': 'M', 'title': '服务'}
    ]

    # 输出目录
    output_dir = os.path.join(os.path.dirname(__file__), 'images', 'tab')
    os.makedirs(output_dir, exist_ok=True)

    print("🎨 开始生成图标...\n")

    # 生成每个图标
    for icon in icons:
        # 未选中状态（灰色）
        normal_path = os.path.join(output_dir, f"{icon['name']}.png")
        create_simple_icon(normal_path, '#CCCCCC')

        # 已选中状态（粉色）
        active_path = os.path.join(output_dir, f"{icon['name']}-active.png")
        create_simple_icon(active_path, '#FF6B9D')

    print("\n✅ 所有图标生成完成！")
    print(f"📁 保存位置: {output_dir}")
    print("\n现在可以在微信开发者工具中运行项目了！🎉")

if __name__ == '__main__':
    main()
