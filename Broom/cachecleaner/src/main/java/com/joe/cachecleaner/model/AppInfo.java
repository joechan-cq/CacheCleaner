package com.joe.cachecleaner.model;

import android.graphics.drawable.Drawable;

/**
 * Description
 * Created by chenqiao on 2015/12/2.
 */
public class AppInfo {

    private String appName;

    private Drawable appIcon;

    private long appCacheSize;

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public long getAppCacheSize() {
        return appCacheSize;
    }

    public void setAppCacheSize(long appCacheSize) {
        this.appCacheSize = appCacheSize;
    }
}
