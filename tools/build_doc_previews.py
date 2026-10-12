#!/usr/bin/env python3
"""Frame real Vision Lab screenshots for documentation (requires Pillow)."""
import argparse
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--captures', type=Path, required=True, help='Directory containing android-before.png, android-after.png, ios-after.png, camera.png, picker.png')
parser.add_argument('--output', type=Path, default=Path('docs/images'))
parser.add_argument('--font', type=Path, required=True, help='Path to a sans-serif TrueType font')
args = parser.parse_args()
args.output.mkdir(parents=True, exist_ok=True)
font = lambda size: ImageFont.truetype(str(args.font), size)

def fitted(name, height):
    im = Image.open(args.captures / name).convert('RGB')
    return im.resize((round(im.width * height / im.height), height), Image.Resampling.LANCZOS)

# Preserve screenshots intact; only resize and place them on a presentation canvas.
canvas = Image.new('RGB', (1440, 1100), '#0d1823')
draw = ImageDraw.Draw(canvas)
draw.text((70, 62), 'ONE SHARED SCREEN', fill='#74f0c0', font=font(19))
draw.text((70, 110), 'Built in Kotlin.\nRunning natively.', fill='white', font=font(42), spacing=12)
draw.text((70, 260), 'Camera + OCR\n+ visual overlays', fill='#a7bdcc', font=font(24), spacing=10)
draw.text((70, 890), 'VISION LAB', fill='#74f0c0', font=font(20))
draw.text((70, 930), 'Runnable tutorial\nRunning app captures', fill='#a7bdcc', font=font(19), spacing=8)
for x, name, label in [(520, 'android-after.png', 'ANDROID / ML KIT'), (970, 'ios-after.png', 'iOS / APPLE VISION')]:
    im = fitted(name, 900)
    draw.text((x, 60), label, fill='#74f0c0', font=font(18))
    canvas.paste(im, (x, 112))
draw.text((70, 1040), 'Android emulator and iOS Simulator. Same bundled input; platform recognition results may differ.', fill='#a7bdcc', font=font(16))
canvas.save(args.output / 'tutorial-platforms.png', optimize=True)
for source, target in [('android-after.png', 'tutorial-ocr.png'), ('camera.png', 'camera.png'), ('picker.png', 'picker.png')]:
    fitted(source, 1400).save(args.output / target, optimize=True)
frames = [fitted(name, 1100) for name in ['android-before.png', 'android-after.png']]
frames[0].save(args.output / 'tutorial-ocr.gif', save_all=True, append_images=frames[1:], duration=[1800, 3200], loop=0, optimize=True)
# Picking is shown as a sequence of real states, not a fabricated cursor animation.
frames = [fitted(name, 1100) for name in ['android-after.png', 'picker.png']]
frames[0].save(args.output / 'tutorial-picker.gif', save_all=True, append_images=frames[1:], duration=[1400, 2600], loop=0, optimize=True)
print('Generated comparison, three static previews, and two animated walkthroughs.')
