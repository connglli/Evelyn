package io.github.leetsong.evelynofficial;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EvelynWebViewClient extends WebViewClient {

    private EvelynWebViewBridgeImpl mBridge;

    public EvelynWebViewClient(EvelynWebViewBridgeImpl bridge) {
        super();
        this.mBridge = bridge;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(url);

        if (message != null) {
            this.mBridge.dispatchMessage(message);
            return true;
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    // WebResourceRequest.getUrl is introduced in 21, however,
    // shouldOverrideUrlLoading(WebView view, WebResourceRequest request) is introduced in 24
    @SuppressLint("NewApi")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(
                request.getUrl().toString());

        if (message != null) {
            this.mBridge.dispatchMessage(message);
            return true;
        }

        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // inject rest EvelynHost
        this.mBridge.injectEvelynHost(-1);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(url);
        if (message != null) {
            this.mBridge.dispatchMessage(message);
            return new WebResourceResponse(null, null, null);
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(
                request.getUrl().toString());
        if (message != null) {
            this.mBridge.dispatchMessage(message);
            return new WebResourceResponse(null, null, null);
        } else {
            return super.shouldInterceptRequest(view, request);
        }
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        super.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(failingUrl);
        if (message != null) {
            this.mBridge.dispatchMessage(message);
        } else {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(
                request.getUrl().toString());
        if (message != null) {
            this.mBridge.dispatchMessage(message);
        } else {
            super.onReceivedError(view, request, error);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(
                request.getUrl().toString());
        if (message != null) {
            this.mBridge.dispatchMessage(message);
        } else {
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        EvelynProtocol.EvelynMessage message = EvelynProtocol.EvelynMessage.fromClientUrl(
                error.getUrl());
        if (message != null) {
            this.mBridge.dispatchMessage(message);
        } else {
            super.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        super.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        return super.onRenderProcessGone(view, detail);
    }

    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        super.onSafeBrowsingHit(view, request, threatType, callback);
    }
}
