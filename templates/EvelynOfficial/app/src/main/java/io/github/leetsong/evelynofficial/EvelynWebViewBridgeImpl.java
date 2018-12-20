package io.github.leetsong.evelynofficial;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EvelynWebViewBridgeImpl implements EvelynWebViewBridge {

    private static final String EVELYN_HOST_JS_PATH = "EvelynHost.js";

    private Context mContext;
    private WebView mWebView;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;

    private EvelynWebViewBridgeHostEndImpl mHostEnd;
    private EvelynWebViewBridgeClientEndImpl mClientEnd;

    private List<OnMessageListener> mListeners;
    private Queue<EvelynProtocol.EvelynMessage> mMessageQueue;

    public EvelynWebViewBridgeImpl(Context context, WebView webView) {
        this.mContext = context;
        this.mWebView = webView;
        this.mClientEnd = new EvelynWebViewBridgeClientEndImpl(this);
        this.mHostEnd = new EvelynWebViewBridgeHostEndImpl(this, webView);
        this.mListeners = new LinkedList<>();
        this.mMessageQueue = new LinkedList<>();
    }

    @Override
    public HostEnd hostEnd() {
        return this.mHostEnd;
    }

    @Override
    public ClientEnd clientEnd() {
        return this.mClientEnd;
    }

    @Override
    public void connect() {
        // create the two clients
        this.mWebViewClient = new EvelynWebViewClient(this);
        this.mWebChromeClient = new EvelynWebChromeClient(this);

        // set them
        this.mWebView.setWebViewClient(this.mWebViewClient);
        this.mWebView.setWebChromeClient(this.mWebChromeClient);

        // initially inject EvelynHost
        this.injectEvelynHost(0);
    }

    @Override
    public void disconnect() {
        this.mWebView.setWebViewClient(null);
        this.mWebView.setWebChromeClient(null);
        // initially inject EvelynHost
        this.uninjectEvelynHost();
    }

    @Override
    public void dispatchMessage(final EvelynProtocol.EvelynMessage message) {
        if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
            for (OnMessageListener l : mListeners) {
                l.onMessage(message);
            }
        } else {
            this.mMessageQueue.offer(message);
        }
    }

    @Override
    public List<EvelynProtocol.EvelynMessage> flushMessageQueue() {
        List<EvelynProtocol.EvelynMessage> messages = new LinkedList<>(this.mMessageQueue);
        this.mMessageQueue.clear();
        return messages;
    }

    @Override
    public int registerOnHostEndOnMessageListener(OnMessageListener listener) {
        if (listener != null) {
            this.mListeners.add(listener);
            return this.mListeners.size() - 1;
        } else {
            return -1;
        }
    }

    @Override
    public void unregisterOnHostEndOnMessageListener(int id) {
        if (id >= 0 && id < this.mListeners.size()) {
            this.mListeners.remove(id);
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public WebViewClient getWebViewClient() {
        return mWebViewClient;
    }

    @Override
    public WebChromeClient getWebChromeClient() {
        return mWebChromeClient;
    }

    protected void injectEvelynHost(int stage) {
        // @JavaScriptInterface is only available when API level >= 17
        // but we ignore it because we always need EvelynHost
        // EvelynHost is injected separately via this line, and
        // this.injectEvelynHost in mWebViewClient when the page is loaded
        if (stage == 0) {
            mWebView.addJavascriptInterface(this.mClientEnd, "EvelynHost");
        } else {
            String initCode = EvelynUtility.assetJs2String(
                    this.mWebView.getContext(), EVELYN_HOST_JS_PATH);
            this.mHostEnd.executeJavaScript(initCode);
        }
    }

    protected void uninjectEvelynHost() {
        this.mHostEnd.executeJavaScript(
                "if ('EvelynHost' in window) { delete window.EvelynHost; }");
    }
}
