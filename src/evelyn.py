#! /usr/bin/python

# MIT License
#
# Copyright (c) 2018 S. Lee
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the 'Software'), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

# This script will create a webapp for testing in
# the folder generated-webapp, with index index.html
# usage: python evelyn.py


import os

from page import generate_page_from_file_to
from link import generate_link


# templates for index.html
INDEX_PAGE_TEMPLATE = """
<html>
  <body>
    <ul>
%s
    </ul>
  </body>
</html>
"""

LINK_TEMPLATE = '      <li><a href="%s%d.html">new page</a></li>\n'

# some configurations
CURDIR = os.path.curdir
TEMPLATE = os.path.join(CURDIR, 'template.html')
GRAMMAR_HTML = os.path.join(CURDIR, 'html.txt')
GRAMMAR_CSS = os.path.join(CURDIR, 'css.txt')
GRAMMAR_JS = os.path.join(CURDIR, 'js.txt')
GRAMMAR_BRIDGE = os.path.join(CURDIR, 'bridge.txt')

NR_PAGE = 512
OUTPUT_DIR = os.path.join(CURDIR, '..', 'generates-webapp')
PAGE_NAME = 'page-'
PAGE_URL = 'http://localhost:8080'
INDEX_NAME = 'index.html'

PROBABILITY = 0.5

# clear and create output directory
if os.path.isdir(OUTPUT_DIR):
    for page in os.listdir(OUTPUT_DIR):
        page_path = os.path.join(OUTPUT_DIR, page)
        try:
            os.unlink(page_path)
        except Exception as e:
            print(e)
else:
    os.mkdir(OUTPUT_DIR)

# generate each page
for i in range(NR_PAGE):
    generate_page_from_file_to(template_file=TEMPLATE,
                               html_file=GRAMMAR_HTML,
                               css_file=GRAMMAR_CSS,
                               js_file=GRAMMAR_JS,
                               bridge_file=GRAMMAR_BRIDGE,
                               output_file=os.path.join(OUTPUT_DIR, PAGE_NAME + str(i) + '.html'))

# generate links between pages
generate_link(proba=PROBABILITY,
              nr_page=NR_PAGE,
              page_dir=os.path.abspath(OUTPUT_DIR),
              page_name=PAGE_NAME,
              page_url=PAGE_URL)

# generate index page
all_links = ""
for i in range(NR_PAGE):
    all_links += LINK_TEMPLATE % (PAGE_URL + "/" + PAGE_NAME, i)
index_page_content = INDEX_PAGE_TEMPLATE % all_links
with open(os.path.join(OUTPUT_DIR, INDEX_NAME), 'w') as index_file:
    index_file.write(index_page_content)
