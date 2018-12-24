# Domato - main generator script
# -------------------------------
#
# Written and maintained by Ivan Fratric <ifratric@google.com>
#
# Copyright 2017 Google Inc. All Rights Reserved.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
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


from __future__ import print_function
import os
import sys
import re
import random

from grammar import Grammar

_N_MAIN_LINES = 1000
_N_EVENTHANDLER_LINES = 500

_N_ADDITIONAL_HTMLVARS = 5

# A map from tag name to corresponding type for HTML tags
_HTML_TYPES = {
    'a': 'HTMLAnchorElement',
    'abbr': 'HTMLUnknownElement',
    'acronym': 'HTMLUnknownElement',
    'address': 'HTMLUnknownElement',
    'applet': 'HTMLUnknownElement',
    'area': 'HTMLAreaElement',
    'article': 'HTMLUnknownElement',
    'aside': 'HTMLUnknownElement',
    'audio': 'HTMLAudioElement',
    'b': 'HTMLUnknownElement',
    'base': 'HTMLBaseElement',
    'basefont': 'HTMLUnknownElement',
    'bdi': 'HTMLUnknownElement',
    'bdo': 'HTMLUnknownElement',
    'bgsound': 'HTMLUnknownElement',
    'big': 'HTMLUnknownElement',
    'blockquote': 'HTMLUnknownElement',
    'br': 'HTMLBRElement',
    'button': 'HTMLButtonElement',
    'canvas': 'HTMLCanvasElement',
    'caption': 'HTMLTableCaptionElement',
    'center': 'HTMLUnknownElement',
    'cite': 'HTMLUnknownElement',
    'code': 'HTMLUnknownElement',
    'col': 'HTMLTableColElement',
    'colgroup': 'HTMLUnknownElement',
    'command': 'HTMLUnknownElement',
    'content': 'HTMLContentElement',
    'data': 'HTMLDataElement',
    'datalist': 'HTMLDataListElement',
    'dd': 'HTMLUnknownElement',
    'del': 'HTMLModElement',
    'details': 'HTMLDetailsElement',
    'dfn': 'HTMLUnknownElement',
    'dialog': 'HTMLDialogElement',
    'dir': 'HTMLDirectoryElement',
    'div': 'HTMLDivElement',
    'dl': 'HTMLDListElement',
    'dt': 'HTMLUnknownElement',
    'em': 'HTMLUnknownElement',
    'embed': 'HTMLEmbedElement',
    'fieldset': 'HTMLFieldSetElement',
    'figcaption': 'HTMLUnknownElement',
    'figure': 'HTMLUnknownElement',
    'font': 'HTMLFontElement',
    'footer': 'HTMLUnknownElement',
    'form': 'HTMLFormElement',
    'frame': 'HTMLFrameElement',
    'frameset': 'HTMLFrameSetElement',
    'h1': 'HTMLHeadingElement',
    'h2': 'HTMLHeadingElement',
    'h3': 'HTMLHeadingElement',
    'h4': 'HTMLHeadingElement',
    'h5': 'HTMLHeadingElement',
    'h6': 'HTMLHeadingElement',
    'header': 'HTMLUnknownElement',
    'hgroup': 'HTMLUnknownElement',
    'hr': 'HTMLHRElement',
    'i': 'HTMLUnknownElement',
    'iframe': 'HTMLIFrameElement',
    'image': 'HTMLImageElement',
    'img': 'HTMLImageElement',
    'input': 'HTMLInputElement',
    'ins': 'HTMLModElement',
    'isindex': 'HTMLUnknownElement',
    'kbd': 'HTMLUnknownElement',
    'keygen': 'HTMLKeygenElement',
    'label': 'HTMLLabelElement',
    'layer': 'HTMLUnknownElement',
    'legend': 'HTMLLegendElement',
    'li': 'HTMLLIElement',
    'link': 'HTMLLinkElement',
    'listing': 'HTMLUnknownElement',
    'main': 'HTMLUnknownElement',
    'map': 'HTMLMapElement',
    'mark': 'HTMLUnknownElement',
    'marquee': 'HTMLMarqueeElement',
    'menu': 'HTMLMenuElement',
    'menuitem': 'HTMLMenuItemElement',
    'meta': 'HTMLMetaElement',
    'meter': 'HTMLMeterElement',
    'nav': 'HTMLUnknownElement',
    'nobr': 'HTMLUnknownElement',
    'noembed': 'HTMLUnknownElement',
    'noframes': 'HTMLUnknownElement',
    'nolayer': 'HTMLUnknownElement',
    'noscript': 'HTMLUnknownElement',
    'object': 'HTMLObjectElement',
    'ol': 'HTMLOListElement',
    'optgroup': 'HTMLOptGroupElement',
    'option': 'HTMLOptionElement',
    'output': 'HTMLOutputElement',
    'p': 'HTMLParagraphElement',
    'param': 'HTMLParamElement',
    'picture': 'HTMLPictureElement',
    'plaintext': 'HTMLUnknownElement',
    'pre': 'HTMLPreElement',
    'progress': 'HTMLProgressElement',
    'q': 'HTMLQuoteElement',
    'rp': 'HTMLUnknownElement',
    'rt': 'HTMLUnknownElement',
    'ruby': 'HTMLUnknownElement',
    's': 'HTMLUnknownElement',
    'samp': 'HTMLUnknownElement',
    'section': 'HTMLUnknownElement',
    'select': 'HTMLSelectElement',
    'shadow': 'HTMLShadowElement',
    'small': 'HTMLUnknownElement',
    'source': 'HTMLSourceElement',
    'span': 'HTMLSpanElement',
    'strike': 'HTMLUnknownElement',
    'strong': 'HTMLUnknownElement',
    'style': 'HTMLStyleElement',
    'sub': 'HTMLUnknownElement',
    'summary': 'HTMLUnknownElement',
    'sup': 'HTMLUnknownElement',
    'table': 'HTMLTableElement',
    'tbody': 'HTMLTableSectionElement',
    'td': 'HTMLUnknownElement',
    'template': 'HTMLTemplateElement',
    'textarea': 'HTMLTextAreaElement',
    'tfoot': 'HTMLTableSectionElement',
    'th': 'HTMLTableCellElement',
    'thead': 'HTMLTableSectionElement',
    'time': 'HTMLTimeElement',
    'title': 'HTMLTitleElement',
    'tr': 'HTMLTableRowElement',
    'track': 'HTMLTrackElement',
    'tt': 'HTMLUnknownElement',
    'u': 'HTMLUnknownElement',
    'ul': 'HTMLUListElement',
    'var': 'HTMLUnknownElement',
    'video': 'HTMLVideoElement',
    'wbr': 'HTMLUnknownElement',
    'xmp': 'HTMLUnknownElement'
}

# A map from tag name to corresponding type for SVG tags
_SVG_TYPES = {
    'a': 'SVGAElement',
    'altGlyph': 'SVGElement',
    'altGlyphDef': 'SVGElement',
    'altGlyphItem': 'SVGElement',
    'animate': 'SVGAnimateElement',
    'animateColor': 'SVGElement',
    'animateMotion': 'SVGAnimateMotionElement',
    'animateTransform': 'SVGAnimateTransformElement',
    'circle': 'SVGCircleElement',
    'clipPath': 'SVGClipPathElement',
    'color-profile': 'SVGElement',
    'cursor': 'SVGCursorElement',
    'defs': 'SVGDefsElement',
    'desc': 'SVGDescElement',
    'discard': 'SVGDiscardElement',
    'ellipse': 'SVGEllipseElement',
    'feBlend': 'SVGFEBlendElement',
    'feColorMatrix': 'SVGFEColorMatrixElement',
    'feComponentTransfer': 'SVGFEComponentTransferElement',
    'feComposite': 'SVGFECompositeElement',
    'feConvolveMatrix': 'SVGFEConvolveMatrixElement',
    'feDiffuseLighting': 'SVGFEDiffuseLightingElement',
    'feDisplacementMap': 'SVGFEDisplacementMapElement',
    'feDistantLight': 'SVGFEDistantLightElement',
    'feDropShadow': 'SVGFEDropShadowElement',
    'feFlood': 'SVGFEFloodElement',
    'feFuncA': 'SVGFEFuncAElement',
    'feFuncB': 'SVGFEFuncBElement',
    'feFuncG': 'SVGFEFuncGElement',
    'feFuncR': 'SVGFEFuncRElement',
    'feGaussianBlur': 'SVGFEGaussianBlurElement',
    'feImage': 'SVGFEImageElement',
    'feMerge': 'SVGFEMergeElement',
    'feMergeNode': 'SVGFEMergeNodeElement',
    'feMorphology': 'SVGFEMorphologyElement',
    'feOffset': 'SVGFEOffsetElement',
    'fePointLight': 'SVGFEPointLightElement',
    'feSpecularLighting': 'SVGFESpecularLightingElement',
    'feSpotLight': 'SVGFESpotLightElement',
    'feTile': 'SVGFETileElement',
    'feTurbulence': 'SVGFETurbulenceElement',
    'filter': 'SVGFilterElement',
    'font': 'SVGElement',
    'font-face': 'SVGElement',
    'font-face-format': 'SVGElement',
    'font-face-name': 'SVGElement',
    'font-face-src': 'SVGElement',
    'font-face-uri': 'SVGElement',
    'foreignObject': 'SVGForeignObjectElement',
    'g': 'SVGGElement',
    'glyph': 'SVGElement',
    'glyphRef': 'SVGElement',
    'hatch': 'SVGElement',
    'hatchpath': 'SVGElement',
    'hkern': 'SVGElement',
    'image': 'SVGImageElement',
    'line': 'SVGLineElement',
    'linearGradient': 'SVGLinearGradientElement',
    'marker': 'SVGMarkerElement',
    'mask': 'SVGMaskElement',
    'mesh': 'SVGElement',
    'meshgradient': 'SVGElement',
    'meshpatch': 'SVGElement',
    'meshrow': 'SVGElement',
    'metadata': 'SVGMetadataElement',
    'missing-glyph': 'SVGElement',
    'mpath': 'SVGMPathElement',
    'path': 'SVGPathElement',
    'pattern': 'SVGPatternElement',
    'polygon': 'SVGPolygonElement',
    'polyline': 'SVGPolylineElement',
    'radialGradient': 'SVGRadialGradientElement',
    'rect': 'SVGRectElement',
    'set': 'SVGSetElement',
    'svg': 'SVGSVGElement',
    'solidcolor': 'SVGElement',
    'stop': 'SVGStopElement',
    'switch': 'SVGSwitchElement',
    'symbol': 'SVGSymbolElement',
    'text': 'SVGTextElement',
    'textPath': 'SVGTextPathElement',
    'title': 'SVGTitleElement',
    'tref': 'SVGElement',
    'tspan': 'SVGTSpanElement',
    'unknown': 'SVGElement',
    'use': 'SVGUseElement',
    'view': 'SVGViewElement',
    'vkern': 'SVGElement'
}


def generate_html_elements(ctx, n):
    for i in range(n):
        tag = random.choice(list(_HTML_TYPES))
        tagtype = _HTML_TYPES[tag]
        ctx['htmlvarctr'] += 1
        varname = 'htmlvar%05d' % ctx['htmlvarctr']
        ctx['htmlvars'].append({'name': varname, 'type': tagtype})
        ctx[
            'htmlvargen'] += '/* newvar{' + varname + ':' + tagtype + '} */ var ' + varname + ' = document.createElement(\"' + tag + '\"); //' + tagtype + '\n'


def add_html_ids(matchobj, ctx):
    tagname = matchobj.group(0)[1:-1]
    if tagname in _HTML_TYPES:
        ctx['htmlvarctr'] += 1
        varname = 'htmlvar%05d' % ctx['htmlvarctr']
        ctx['htmlvars'].append({'name': varname, 'type': _HTML_TYPES[tagname]})
        ctx['htmlvargen'] += '/* newvar{' + varname + ':' + _HTML_TYPES[
            tagname] + '} */ var ' + varname + ' = document.getElementById(\"' + varname + '\"); //' + _HTML_TYPES[
                                 tagname] + '\n'
        return matchobj.group(0) + 'id=\"' + varname + '\" '
    elif tagname in _SVG_TYPES:
        ctx['svgvarctr'] += 1
        varname = 'svgvar%05d' % ctx['svgvarctr']
        ctx['htmlvars'].append({'name': varname, 'type': _SVG_TYPES[tagname]})
        ctx['htmlvargen'] += '/* newvar{' + varname + ':' + _SVG_TYPES[
            tagname] + '} */ var ' + varname + ' = document.getElementById(\"' + varname + '\"); //' + _SVG_TYPES[
                                 tagname] + '\n'
        return matchobj.group(0) + 'id=\"' + varname + '\" '
    else:
        return matchobj.group(0)


def generate_function_body(js_grammar, html_ctx, num_lines):
    js = ''
    js += 'var fuzzervars = {};\n\n'
    js += "SetVariable(fuzzervars, window, 'Window');\nSetVariable(fuzzervars, document, 'Document');\nSetVariable(fuzzervars, document.body.firstChild, 'Element');\n\n"
    js += '//beginjs\n'
    js += html_ctx['htmlvargen']
    js += js_grammar._generate_code(num_lines, html_ctx['htmlvars'])
    js += '\n//endjs\n'
    js += 'var fuzzervars = {};\nfreememory()\n'
    return js


def check_grammar(grammar):
    """Checks if grammar has errors and if so outputs them.

    Args:
      grammar: The grammar to check.
    """

    for rule in grammar._all_rules:
        for part in rule['parts']:
            if part['type'] == 'text':
                continue
            tagname = part['tagname']
            # print tagname
            if tagname not in grammar._creators:
                print('No creators for type ' + tagname)


def generate_page_from_grammar(template, html_grammar, css_grammar, js_grammar, bridge_grammar):
    """Parses grammar rules from string.

    Args:
      template: A template string.
      html_grammar: Grammar for generating HTML code.
      css_grammar: Grammar for generating CSS code.
      js_grammar: Grammar for generating JS code.
      bridge_grammar: Grammar for generating bridge code.

    Returns:
      A string containing page data.
    """

    result = template

    css = css_grammar.generate_symbol('rules')
    html = html_grammar.generate_symbol('bodyelements')
    bridge = bridge_grammar.generate_root()

    html_ctx = {
        'htmlvars': [],
        'htmlvarctr': 0,
        'svgvarctr': 0,
        'htmlvargen': ''
    }
    html = re.sub(r'<[a-zA-Z0-9_-]+ ',
                  lambda match: add_html_ids(match, html_ctx), html)
    generate_html_elements(html_ctx, _N_ADDITIONAL_HTMLVARS)

    result = result.replace('<cssfuzzer>', css)
    result = result.replace('<htmlfuzzer>', html)
    result = result.replace('<bridgefuzzer>', bridge)

    handlers = False
    while '<jsfuzzer>' in result:
        numlines = _N_MAIN_LINES
        if handlers:
            numlines = _N_EVENTHANDLER_LINES
        else:
            handlers = True
        result = result.replace('<jsfuzzer>',
                                generate_function_body(js_grammar, html_ctx, numlines), 1)

    return result


def generate_page_from_file(template_file, html_file, css_file, js_file, bridge_file):
    """Generates a set of pages and writes them to the output files.

    Args:
      template_file: path to template.html
      html_file: path to html grammar file
      css_file: path to css grammar file
      js_file: path to js grammar file
      bridge_file: path to bridge grammar file
    """

    f = open(template_file)
    template = f.read()
    f.close()

    html_grammar = Grammar()
    err = html_grammar.parse_from_file(html_file)
    # CheckGrammar(html_grammar)
    if err > 0:
        print('Error parsing html grammar')
        return

    css_grammar = Grammar()
    err = css_grammar.parse_from_file(css_file)
    # CheckGrammar(css_grammar)
    if err > 0:
        print('Error parsing css grammar')
        return

    js_grammar = Grammar()
    err = js_grammar.parse_from_file(js_file)
    # CheckGrammar(js_grammar)
    if err > 0:
        print('Error parsing js grammar')
        return

    bridge_grammar = Grammar()
    err = bridge_grammar.parse_from_file(bridge_file)
    # CheckGrammar(bridge_grammar)
    if err > 0:
        print('Error parsing bridge grammar')
        return

    # JS and HTML grammar need access to CSS grammar.
    # Add it as import
    html_grammar.add_import('cssgrammar', css_grammar)
    js_grammar.add_import('cssgrammar', css_grammar)

    return generate_page_from_grammar(template,
                                      html_grammar, css_grammar, js_grammar, bridge_grammar)


def generate_page_from_file_to(template_file, html_file, css_file, js_file, bridge_file, output_file):
    """Generates a set of pages and writes them to the output files.

    Args:
      template_file: path to template.html
      html_file: path to html grammar file
      css_file: path to css grammar file
      js_file: path to js grammar file
      bridge_file: path to bridge grammar file
      output_file: path to the output file
    """
    result = generate_page_from_file(template_file,
                                     html_file, css_file, js_file, bridge_file)
    with open(output_file, 'w') as file:
        file.write(result)


if __name__ == '__main__':
    def usage():
        print('Usage: python page.py <template_path> <html_path> <css_path> <js_path> <bridge_path> <output_path>')
        exit(1)

    if len(sys.argv) < 7:
        usage()

    curdir = os.path.curdir
    template_path = os.path.join(curdir, sys.argv[1])
    html_path = os.path.join(curdir, sys.argv[2])
    css_path = os.path.join(curdir, sys.argv[3])
    js_path = os.path.join(curdir, sys.argv[4])
    bridge_path = os.path.join(curdir, sys.argv[5])
    output_path = os.path.join(curdir, sys.argv[6])

    result = generate_page_from_file(template_path,
                                     html_path, css_path, js_path, bridge_path)
    if result is None:
        print("Error generating pages, check your template,html,css,js files")
        exit(1)

    print('Writing a sample to %s' % (output_path))
    try:
        generate_page_from_file(template_path,
                                html_path, css_path, js_path, bridge_path)
    except IOError:
        print('Error writing to %s' % (output_path))
