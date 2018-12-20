package io.github.leetsong.evelynofficial;

import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public class EvelynWebViewBridgeHostEndImpl implements EvelynWebViewBridge.HostEnd {

    private EvelynWebViewBridge mBridge;
    private WebView mWebView;

    public EvelynWebViewBridgeHostEndImpl(EvelynWebViewBridge bridge, WebView webView) {
        this.mBridge = bridge;
        this.mWebView = webView;
    }

    @Override
    public void executeJavaScript(String code) {
        // there are multiple methods to evaluate JavaScript, we randomly use them
        if (Build.VERSION.SDK_INT < 19 || EvelynUtility.randomBoolean()) {
            this.mWebView.loadUrl("javascript: " + code);
        } else {
            this.mWebView.evaluateJavascript(code, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // stub
                }
            });
        }
    }

    @Override
    public void post(String message) {
        // TODO
    }

    @Override
    public EvelynWebViewBridge getBridge() {
        return this.mBridge;
    }
}
