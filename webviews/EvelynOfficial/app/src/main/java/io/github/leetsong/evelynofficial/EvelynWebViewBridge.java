package io.github.leetsong.evelynofficial;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

public interface EvelynWebViewBridge {

    interface ClientEnd {

        /**
         * This return the EvelynWebViewBridge instance
         * @return the EvelynWebViewBridge instance
         */
        EvelynWebViewBridge getBridge();

        /**
         * This post a message to hostEnd
         * @param message message to be posted
         */
        @JavascriptInterface
        void postMessage(String message);

        /**
         * This returns a random boolean value,
         * because we are fuzzing WebView, so we make all
         * these computations natively
         * @return a random boolean value
         */
        @JavascriptInterface
        boolean randomBoolean();

        /**
         * This returns a random boolean value,
         * because we are fuzzing WebView, so we make all
         * these computations natively
         * @param pTrue probability for returning true
         * @return      a random boolean value
         */
        @JavascriptInterface
        boolean randomBooleanWithProba(double pTrue);

        /**
         * This returns a random double value,
         * because we are fuzzing WebView, so we make all
         * these computations natively
         * @return a random double value
         */
        @JavascriptInterface
        double randomDouble();
    }

    interface HostEnd {

        /**
         * This return the EvelynWebViewBridge instance
         * @return the EvelynWebViewBridge instance
         */
        EvelynWebViewBridge getBridge();

        /**
         * Execute a JavaScript code
         * @param code the JavaScript code
         */
        void executeJavaScript(String code);

        /**
         * Post a message to Client
         * @param message
         */
        void post(String message);
    }

    interface OnMessageListener {

        /**
         * Handle message received from Client
         * @param message
         */
        void onMessage(EvelynProtocol.EvelynMessage message);
    }

    /**
     * This returns the bridge instance used by HostEnd (the Native end)
     * @return a HostEnd instance
     */
    HostEnd hostEnd();

    /**
     * This returns the bridge instance used by ClientEnd (the Web end)
     * @return a ClientEnd instance
     */
    ClientEnd clientEnd();

    /**
     * Connect Native (Host) with Web (Client)
     */
    void connect();

    /**
     * Disconnect Native (Host) with Web (Client)
     */
    void disconnect();

    /**
     * This dispatches message from Client to UI thread,
     * if not in UI thread, queued this message
     * @param message message to be dispatched
     */
    void dispatchMessage(EvelynProtocol.EvelynMessage message);

    /**
     * Flushes the message queue
     * @return a list of flushed messages
     */
    List<EvelynProtocol.EvelynMessage> flushMessageQueue();

    /**
     * Register listener to receive messages from Client,
     * all listeners are handled in the UI thread
     * @param listener Listener to be registered
     * @return         ID of this listener, use it to unregister
     */
    int registerOnHostEndOnMessageListener(OnMessageListener listener);

    /**
     * Unregister listener
     * @param id Listener id
     */
    void unregisterOnHostEndOnMessageListener(int id);

    /**
     * This return the Context instance
     * @return the Context instance
     */
    Context getContext();

    /**
     * This return the WebView instance
     * @return the WebView instance
     */
    WebView getWebView();

    /**
     * This return the WebViewClient instance
     * @return the WebViewClient instance
     */

    WebViewClient getWebViewClient();

    /**
     * This return the WebChromeClient instance
     * @return the WebChromeClient instance
     */
    WebChromeClient getWebChromeClient();
}
