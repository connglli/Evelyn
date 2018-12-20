// MIT License
// 
// Copyright (c) 2018 S. Lee
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


(function (window, location, document, $$) {
  const EVELYN_IFRAME_BRIDGE_ID = 'EvelynIframeBridge';

  const registerDOMBridges = () => {
    // iframe bridges
    if (!$$(`#${EVELYN_IFRAME_BRIDGE_ID}`)) {
        const iframeBridge = document.createElement('iframe');
        $$.id(iframeBridge, EVELYN_IFRAME_BRIDGE_ID);
        $$.attr(iframeBridge, 'display', 'none');
        $$.append(document.body, iframeBridge);
    }
  };

  const registerBridgePostFunctions = (EvelynHost) => {
    if (!!!EvelynHost) {
      // crash Evelyn because EvelynHost should be presented by far
      throw new Error('Error: Evelyn failed to inject EvelynHost');
    }

    // directly return if already registered
    if (EvelynHost.post) {
      return;
    }

    const msg2url = {
      viaEvelyn: (bridge, msg) => `evelyn://evelyn.com/tohost/${bridge}/${encodeURI(msg)}`,
      viaHttp: (bridge, msg) => `http://localhost:31115/evelyn/tohost/${bridge}/${encodeURI(msg)}`,
      viaHttps: (bridge, msg) => `https://localhost:31115/evelyn/tohost/${bridge}/${encodeURI(msg)}`,
      random: function (bridge, msg) {
        const v = EvelynHost.randomDouble();
        if (v <= 0.333333) {
          return this.viaEvelyn(bridge, msg);
        } else if (v < 0.666666) {
          return this.viaHttp(bridge, msg);
        } else {
          return this.viaHttps(bridge, msg);
        }
      }
    };

    EvelynHost.post$WebView$addJavaScriptInterface = msg => {
      EvelynHost.postMessage(msg2url.random('webviewaddjavascriptinterface', msg))
    };

    EvelynHost.post$WebChromeClient$onConsoleMessage = msg => {
      console.log(msg2url.random('webchromeclientonconsolemessage', msg));
    };

    EvelynHost.post$WebChromeClient$onJsAlert = msg => {
      alert(msg2url.random('webchromeclientonjsalert', msg));
    };

    EvelynHost.post$WebChromeClient$onJsConfirm = msg => {
      confirm(msg2url.random('webchromeclientonjsconfirm', msg));
    };

    EvelynHost.post$WebChromeClient$onJsPrompt = msg => {
      prompt(msg2url.random('webchromeclientonjsprompt', msg), msg2url.random('bridge, Evelyn prompt...'));
    };

    EvelynHost.post$WebViewClient$shouldInterceptRequest = msg => {
      if (!location.protocol.startsWith('https')) {
        const iframeBridge = $$(`#${EVELYN_IFRAME_BRIDGE_ID}`);
        $$.attr(iframeBridge, 'src', msg2url.viaEvelyn('webviewclientshouldinterceptrequest', msg));
      }
    };

    EvelynHost.post$WebViewClient$onReceivedError = msg => {
      if (!location.protocol.startsWith('https') && location.protocol.startsWith('http')) {
        const iframeBridge = $$(`#${EVELYN_IFRAME_BRIDGE_ID}`);
        $$.attr(iframeBridge, 'src', msg2url.viaHttp('webviewclientonreceivederror', msg));
      }
    };

    EvelynHost.post$WebViewClient$onReceivedSslError = msg => {
      const iframeBridge = $$(`#${EVELYN_IFRAME_BRIDGE_ID}`);
      $$.attr(iframeBridge, 'src', msg2url.viaHttps('webviewclientonreceivedsslerror', msg));
    };

    EvelynHost.post = msg => {
      
    };

    return EvelynHost;
  };

  registerDOMBridges();
  window.EvelynHost = registerBridgePostFunctions(window.EvelynHost);
})(window, location, document, (() => {
  const $$ = document.querySelector.bind(document);

  $$.id = (e, v) => {
    if (!!e && !!v) {
      e.id = v;
    } else {
      return (!!e && e.id) || '';
    }
  };

  $$.attr = (e, a, v) => {
    if (!!v) {
      return !!e && !!a && e.setAttribute(a, v);
    } else {
      return !!e && !!a && e.getAttribute(a);
    }
  };

  $$.append = (p, c) => {
    return !!p && !!c && p.appendChild(c);
  };

  return $$;
})());
