package io.github.leetsong.evelynofficial;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class EvelynWebChromeClient extends WebChromeClient {

    private EvelynWebViewBridgeImpl mBridge;

    public EvelynWebChromeClient(EvelynWebViewBridgeImpl bridge) {
        super();
        this.mBridge = bridge;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        EvelynProtocol.EvelynMessage m = EvelynProtocol.EvelynMessage.fromClientUrl(message);
        if (m != null && bridgeName("onJsAlert").equalsIgnoreCase(m.getBridge())) {
            this.mBridge.dispatchMessage(m);
            return false;
        }
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        EvelynProtocol.EvelynMessage m = EvelynProtocol.EvelynMessage.fromClientUrl(message);
        if (m != null && bridgeName("onJsConfirm").equalsIgnoreCase(m.getBridge())) {
            this.mBridge.dispatchMessage(m);
            return false;
        }
        return super.onJsConfirm(view, url, message, result);

    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        EvelynProtocol.EvelynMessage m = EvelynProtocol.EvelynMessage.fromClientUrl(message);
        if (m != null && bridgeName("onJsPrompt").equalsIgnoreCase(m.getBridge())) {
            this.mBridge.dispatchMessage(m);
            return false;
        }
        return super.onJsPrompt(view, url, message,defaultValue,  result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        // we do not check origin, just randomly accept or refuse the permission,
        // and always retain
        callback.invoke(origin, EvelynUtility.randomBoolean(), true);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        super.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        super.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onJsTimeout() {
        return super.onJsTimeout();
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        EvelynProtocol.EvelynMessage m = EvelynProtocol.EvelynMessage.fromClientUrl(message);
        if (m != null && bridgeName("onConsoleMessage").equalsIgnoreCase(m.getBridge())) {
            this.mBridge.dispatchMessage(m);
            return;
        }
        super.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        EvelynProtocol.EvelynMessage m = EvelynProtocol.EvelynMessage.fromClientUrl(
                consoleMessage.message());
        if (m != null && bridgeName("onConsoleMessage").equalsIgnoreCase(m.getBridge())) {
            this.mBridge.dispatchMessage(m);
            return false;
        }
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        super.getVisitedHistory(callback);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    private String bridgeName(@NonNull String m) {
        return "WebChromeClient" + m;
    }
}
