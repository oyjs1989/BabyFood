#!/usr/bin/env python3
"""
Android 应用图标生成脚本 - 使用 PIL/Pillow
将单个图标文件转换为 Android 所需的各种尺寸
"""

from PIL import Image
import os
from pathlib import Path

# 图标尺寸配置（对应 Android 的不同密度）
ICON_SIZES = {
    "mipmap-mdpi": 48,      # 48x48
    "mipmap-hdpi": 72,      # 72x72
    "mipmap-xhdpi": 96,     # 96x96
    "mipmap-xxhdpi": 144,   # 144x144
    "mipmap-xxxhdpi": 192,   # 192x192
}

# 源图标路径
SOURCE_ICON = Path("图标.png")

# 输出目录
OUTPUT_BASE = Path("app/src/main/res")

def generate_icons():
    """生成各种尺寸的图标"""
    if not SOURCE_ICON.exists():
        print(f"❌ 错误：找不到源图标文件 {SOURCE_ICON}")
        return False

    print("✅ 开始生成 Android 图标...")
    print(f"📁 源文件：{SOURCE_ICON.absolute()}")
    print(f"📂 输出目录：{OUTPUT_BASE.absolute()}\n")

    # 打开原始图标
    try:
        img = Image.open(SOURCE_ICON)
        # 确保是 RGBA 模式（支持透明度）
        if img.mode != 'RGBA':
            img = img.convert('RGBA')

        print(f"📐 原始尺寸：{img.size[0]}x{img.size[1]}")
        print(f"🎨 模式：{img.mode}\n")
    except Exception as e:
        print(f"❌ 错误：无法读取图标文件 - {e}")
        return False

    # 为每个密度生成图标
    success_count = 0
    for folder, size in ICON_SIZES.items():
        output_folder = OUTPUT_BASE / folder
        output_file = output_folder / "ic_launcher.png"
        output_round_file = output_folder / "ic_launcher_round.png"

        # 创建目录
        output_folder.mkdir(parents=True, exist_ok=True)

        # 调整图标大小
        resized = img.resize((size, size), Image.Resampling.LANCZOS)

        # 保存普通图标
        try:
            resized.save(output_file, 'PNG', optimize=True)
            print(f"✓ {folder}: {size}x{size} -> {output_file.name}")
            success_count += 1
        except Exception as e:
            print(f"✗ {folder}: 保存失败 - {e}")
            continue

        # 保存圆形图标
        try:
            # 创建圆形遮罩
            mask = Image.new('L', (size, size), 0)
            from PIL import ImageDraw
            draw = ImageDraw.Draw(mask)
            draw.ellipse([(0, 0), (size-1, size-1)], fill=255)

            # 应用圆形遮罩
            output = Image.new('RGBA', (size, size), (0, 0, 0, 0))
            output.paste(resized, (0, 0))
            output.putalpha(mask)

            output.save(output_round_file, 'PNG', optimize=True)
            print(f"  └─ {output_round_file.name} (圆形)")
        except Exception as e:
            print(f"  └─ 圆形图标生成失败 - {e}")

    print(f"\n✅ 图标生成完成！成功生成 {success_count}/{len(ICON_SIZES)} 个尺寸")
    print(f"\n下一步：")
    print(f"1. 重新构建应用：./gradlew clean assembleDebug")
    print(f"2. 在设备上安装并查看图标效果")

    return True

def create_adaptive_icon_xml():
    """创建自适应图标 XML 配置"""
    print("\n📝 创建自适应图标配置...")

    # 更新 mipmap-anydpi-v26/ic_launcher.xml
    xml_dir = OUTPUT_BASE / "mipmap-anydpi-v26"
    xml_dir.mkdir(parents=True, exist_ok=True)

    # 普通图标
    ic_launcher_xml = xml_dir / "ic_launcher.xml"
    with open(ic_launcher_xml, 'w', encoding='utf-8') as f:
        f.write('''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
''')
    print(f"  ✓ {ic_launcher_xml.name}")

    # 圆形图标
    ic_launcher_round_xml = xml_dir / "ic_launcher_round.xml"
    with open(ic_launcher_round_xml, 'w', encoding='utf-8') as f:
        f.write('''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
''')
    print(f"  ✓ {ic_launcher_round_xml.name}")

if __name__ == "__main__":
    print("🎨 BabyFood Android 图标生成器 (PIL)")
    print("=" * 50)
    print()

    success = generate_icons()

    if success:
        create_adaptive_icon_xml()
        print("\n" + "=" * 50)
        print("🎉 所有图标已生成！")
        print("=" * 50)
    else:
        print("\n❌ 图标生成失败，请检查错误信息")
        exit(1)
