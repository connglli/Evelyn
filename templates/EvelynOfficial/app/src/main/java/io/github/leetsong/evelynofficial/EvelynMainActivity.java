package io.github.leetsong.evelynofficial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class EvelynMainActivity extends AppCompatActivity {

    // path to websettings.properties
    private static final String EVELYN_WEB_SETTINGS_PROPERTIES = "websettings.properties";

    WebView mWebView;
    WebSettings mWebSettings;
    EvelynWebViewBridge mBridge;
    EvelynWebSettingsLoader mLoader;

    FrameLayout mWebViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = new WebView(this);
        mWebSettings = mWebView.getSettings();
        mBridge = new EvelynWebViewBridgeImpl(this, mWebView);
        mLoader = new EvelynWebSettingsLoaderImpl(this, mWebSettings);
        mWebViewContainer = findViewById(R.id.fl_webview_container);

        // load settings
        mLoader.load(this.getSettingsFile());
        // override JavaScriptEnabled setting
        mWebSettings.setJavaScriptEnabled(true);
        // connect host and client
        mBridge.connect();

        // listen for incoming messages
        mBridge.registerOnHostEndOnMessageListener(new EvelynWebViewBridge.OnMessageListener() {

            private void handleMessage(EvelynProtocol.EvelynMessage message) {
                Toast.makeText(EvelynMainActivity.this,
                        message.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessage(EvelynProtocol.EvelynMessage message) {
                // firstly flush the queue to fetch all previously unhandled messages
                List<EvelynProtocol.EvelynMessage> messages = mBridge.flushMessageQueue();
                for (EvelynProtocol.EvelynMessage m : messages) {
                    handleMessage(m);
                }
                // secondly handle current message
                handleMessage(message);
            }
        });

        // attach to parent
        mWebViewContainer.addView(mWebView);

        mWebView.loadUrl("https://www.baidu.com");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mBridge.disconnect();
        this.mWebView.destroy();
        this.mWebView = null;
    }

    private File getSettingsFile() {
        // we manually put the settings file in ExternalFilesDir
        return new File(getExternalFilesDir(null), EVELYN_WEB_SETTINGS_PROPERTIES);
    }
}
