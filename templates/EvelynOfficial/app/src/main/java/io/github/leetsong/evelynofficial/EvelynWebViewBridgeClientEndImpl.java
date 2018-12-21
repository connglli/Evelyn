package io.github.leetsong.evelynofficial;

import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;

public class EvelynWebViewBridgeClientEndImpl implements EvelynWebViewBridge.ClientEnd {

    private EvelynWebViewBridge mBridge;

    EvelynWebViewBridgeClientEndImpl(EvelynWebViewBridge bridge) {
        this.mBridge = bridge;
    }

    @Override
    public EvelynWebViewBridge getBridge() {
        return this.mBridge;
    }

    @JavascriptInterface
    @Override
    public void postMessage(String message) {
        EvelynProtocol.EvelynMessage m = EvelynProtocol.EvelynMessage.fromClientUrl(message);
        if (m != null && bridgeName("addJavaScriptInterface").equalsIgnoreCase(m.getBridge())) {
            this.mBridge.dispatchMessage(m);
        }
    }

    @JavascriptInterface
    @Override
    public boolean randomBoolean() {
        return EvelynUtility.randomBoolean();
    }

    @JavascriptInterface
    @Override
    public boolean randomBooleanWithProba(double pTrue) {
        return EvelynUtility.randomBoolean(pTrue);
    }

    @JavascriptInterface
    @Override
    public double randomDouble() {
        return EvelynUtility.randomDouble();
    }

    private String bridgeName(@NonNull String m) {
        return "WebView" + m;
    }
}
