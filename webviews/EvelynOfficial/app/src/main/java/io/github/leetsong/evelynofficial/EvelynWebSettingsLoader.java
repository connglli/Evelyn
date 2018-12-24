package io.github.leetsong.evelynofficial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;

import java.io.File;

public interface EvelynWebSettingsLoader {

    /**
     * Get current Context
     * @return current context instance
     */
    @NonNull
    Context getContext();

    /**
     * Get WebSettings
     * @return WebSettings instance
     */
    @NonNull
    WebSettings getSettings();

    /**
     * Load settings from a configuration
     * @param settingsFile settings configuration file
     */
    void load(@NonNull File settingsFile);
}
