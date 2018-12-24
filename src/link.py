# MIT License
#
# Copyright (c) 2018 S. Lee
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.


import os
import sys
from random import random as next_float

PAGE_TAIL = u"""</body>
</html>
"""

PAGE_NAME_TEMPLATE = u"%s%d.html"
PAGE_LINK_TEMPLATE = u"<a href=\"%s%d.html\">enter new page</a>"


def generate_random_graph(proba: float, nr_node: int):
    """
    Args:
      p: probability to generate an edge
      nr_node: number of nodes to generate

    Returns:
      A list of pairs [(x, y)+], each indicate a link from x to y
    """
    for i in range(nr_node):
        for j in range(nr_node):
            if next_float() <= proba:
                yield (i, j)


def link_page(from_page, to_page, page_dir, page_name, page_url):
    """
    Args:
      from_page: page linked from
      to_page: page linked to
      page_name: prefix of the page name
      page_url: url in the link
    """
    from_page_fd = os.open(os.path.join(
        os.path.curdir, PAGE_NAME_TEMPLATE % (page_dir + "/" + page_name, from_page)),
        os.O_RDWR)
    os.lseek(from_page_fd, -len(PAGE_TAIL), os.SEEK_END)
    os.write(from_page_fd, bytes(PAGE_LINK_TEMPLATE % (page_url + "/" + page_name, to_page), encoding='utf-8'))
    os.write(from_page_fd, bytes(PAGE_TAIL, encoding='utf-8'))
    os.close(from_page_fd)


def generate_link(proba: float, nr_page: int, page_dir: str, page_name: str, page_url: str):
    """
    Args:
      proba: probability to generate a link
      nr_page: number of pages to generate
      page_name: prefix of the page name
      page_url: url in the link
    """
    for (from_page, to_page) in generate_random_graph(proba, nr_page):
        link_page(from_page, to_page, page_dir, page_name, page_url)


if __name__ == '__main__':
    def usage():
        print('python link.py <probability> <nr_page> <page_dir> <page_name> <page_url>')
        exit(1)
        
    if len(sys.argv) < 6:
        usage()
    
    proba = sys.argv[1]
    nr_page = sys.argv[2]
    page_dir = sys.argv[3]
    page_name = sys.argv[4]
    page_url = sys.argv[5]

    generate_link(proba, nr_page, page_dir, page_name, page_url)
