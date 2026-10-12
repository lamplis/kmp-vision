#!/usr/bin/env python3
"""Fail the docs build on missing local links or preview assets, including raw HTML."""
import sys
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit

root = Path(sys.argv[1] if len(sys.argv) > 1 else 'site').resolve()
errors = []
count = 0

class Links(HTMLParser):
    def handle_starttag(self, tag, attrs):
        global count
        attrs = dict(attrs)
        value = attrs.get('src') if tag in ('img', 'script') else attrs.get('href') if tag in ('a', 'link') else None
        if not value:
            return
        url = urlsplit(value)
        if url.scheme or url.netloc or not url.path:
            return
        path = unquote(url.path)
        if path.startswith('/kmp-vision/'):
            target = root / path.removeprefix('/kmp-vision/')
        elif path.startswith('/'):
            return
        else:
            target = current.parent / path
        count += 1
        if not target.exists():
            errors.append(f'{current.relative_to(root)}: missing {value}')

for current in root.rglob('*.html'):
    Links().feed(current.read_text())
if errors:
    sys.exit('\n'.join(errors))
print(f'Checked {count} local links and assets: all exist.')
