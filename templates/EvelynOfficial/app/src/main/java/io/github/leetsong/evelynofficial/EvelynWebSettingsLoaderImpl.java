package io.github.leetsong.evelynofficial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class EvelynWebSettingsLoaderImpl implements EvelynWebSettingsLoader {

    private static Set<String> METHOD_SET_PARAMED_BOOLEAN = new HashSet<>(Arrays.asList(
            "AllowContentAccess",
            "AllowFileAccess",
            "AllowFileAccessFromFileURLs",
            "AllowUniversalAccessFromFileURLs",
            "AppCacheEnabled",
            "BlockNetworkImage",
            "BlockNetworkLoads",
            "BuiltInZoomControls",
            "DatabaseEnabled",
            "DisplayZoomControls",
            "DomStorageEnabled",
            "EnableSmoothTransition",
            "GeolocationEnabled",
            "JavaScriptCanOpenWindowsAutomatically",
            "JavaScriptEnabled",
            "LightTouchEnabled",
            "LoadWithOverviewMode",
            "LoadsImagesAutomatically",
            "MediaPlaybackRequiresUserGesture",
            "NeedInitialFocus",
            "OffscreenPreRaster",
            "SafeBrowsingEnabled",
            "SaveFormData",
            "SavePassword",
            "SupportMultipleWindows",
            "SupportZoom",
            "UseWideViewPort"
    ));

    private static Set<String> METHOD_SET_PARAMED_STRING = new HashSet<>(Arrays.asList(
            "AppCachePath",
            "CursiveFontFamily",
            "DatabasePath",
            "DefaultTextEncodingName",
            "FantasyFontFamily",
            "FixedFontFamily",
            "GeolocationDatabasePath",
            "SansSerifFontFamily",
            "SerifFontFamily",
            "StandardFontFamily",
            "UserAgentString"
    ));

    private static Set<String> METHOD_SET_PARAMED_INTEGER = new HashSet<>(Arrays.asList(
            "CacheMode",
            "DefaultFixedFontSize",
            "DefaultFontSize",
            "DisabledActionModeMenuItems",
            "MinimumFontSize",
            "MinimumLogicalFontSize",
            "MixedContentMode",
            "TextZoom"
    ));

    private static Set<String> METHOD_SET_PARAMED_SPECIAL = new HashSet<>(Arrays.asList(
            "AppCacheMaxSize",
            "DefaultZoom",
            "LayoutAlgorithm",
            "PluginState",
            "RenderPriority",
            "TextSize"
    ));

    private Context mContext;
    private WebSettings mWebSettings;

    public EvelynWebSettingsLoaderImpl(@NonNull Context context, @NonNull WebSettings webSettings) {
        this.mContext = context;
        this.mWebSettings = webSettings;
    }

    @NonNull
    @Override
    public Context getContext() {
        return this.mContext;
    }

    @NonNull
    @Override
    public WebSettings getSettings() {
        return this.mWebSettings;
    }

    @Override
    public void load(@NonNull File settingsFile) {
        FileReader fileReader = null;
        try {
            Properties properties = new Properties();
            fileReader = new FileReader(settingsFile);
            properties.load(fileReader);
            setSettings(properties);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSettings(@NonNull Properties properties) {
        for (String propertyName : properties.stringPropertyNames()) {
            String value = properties.getProperty(propertyName);
            try {
                switch (propertyName) {
                case "DefaultZoom":
                    mWebSettings.setDefaultZoom(
                            WebSettings.ZoomDensity.valueOf(value));
                    break;

                case "LayoutAlgorithm":
                    mWebSettings.setLayoutAlgorithm(
                            WebSettings.LayoutAlgorithm.valueOf(value));
                    break;

                case "PluginState":
                    mWebSettings.setPluginState(
                            WebSettings.PluginState.valueOf(value));
                    break;

                case "RenderPriority":
                    mWebSettings.setRenderPriority(
                            WebSettings.RenderPriority.valueOf(value));
                    break;

                case "TextSize":
                    mWebSettings.setTextSize(
                            WebSettings.TextSize.valueOf(value));
                    break;

                case "AppCacheEnabled":
                    boolean appCacheEnabled = Boolean.valueOf(value);
                    mWebSettings.setAppCacheEnabled(appCacheEnabled);
                    // we don't directly set the path and max size, but with 1/2 probability
                    if (appCacheEnabled && EvelynUtility.randomBoolean()) {
                        mWebSettings.setAppCacheMaxSize(Long.valueOf(
                                properties.getProperty("AppCacheMaxSize")));
                    }
                    if (appCacheEnabled && EvelynUtility.randomBoolean()) {
                        mWebSettings.setAppCachePath(
                                this.mContext.getDir("cache", Context.MODE_PRIVATE).getPath());
                    }
                    break;

                case "DatabaseEnabled":
                    boolean databaseEnabled = Boolean.valueOf(value);
                    mWebSettings.setDatabaseEnabled(databaseEnabled);
                    // we don't directly set the path, but with 1/2 probability
                    if (databaseEnabled && EvelynUtility.randomBoolean()) {
                        mWebSettings.setDatabasePath(
                                this.mContext.getDir("database", Context.MODE_PRIVATE).getPath());
                    }
                    break;

                case "GeolocationEnabled":
                    boolean geolocationEnabled = Boolean.valueOf(value);
                    mWebSettings.setGeolocationEnabled(geolocationEnabled);
                    // we don't directly set the path, but with 1/2 probability
                    if (geolocationEnabled && EvelynUtility.randomBoolean()) {
                        mWebSettings.setGeolocationDatabasePath(
                                this.mContext.getDir("geolocationdatabase", Context.MODE_PRIVATE).getPath());
                    }
                    break;

                case "AppCacheMaxSize": case "AppCachePath":
                case "DatabasePath": case "GeolocationDatabasePath":
                    // skip
                    break;

                default:
                    Method method;
                    Object objValue;
                    if (METHOD_SET_PARAMED_INTEGER.contains(propertyName)) {
                        method = WebSettings.class.getMethod(
                                "set" + propertyName, Integer.TYPE);
                        objValue = Integer.valueOf(value);
                    } else if (METHOD_SET_PARAMED_BOOLEAN.contains(propertyName)) {
                        method = WebSettings.class.getMethod(
                                "set" + propertyName, Boolean.TYPE);
                        objValue = Boolean.valueOf(value);
                    } else {
                        method = WebSettings.class.getMethod(
                                "set" + propertyName, String.class);
                        objValue = value;
                    }
                    method.invoke(mWebSettings, objValue);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
