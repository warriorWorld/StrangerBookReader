package com.warrior.hangsu.administrator.strangerbookreader.bean;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/4/3.
 */
public class BookBean {
    private String path;
    private String bpPath;
    private String name;
    private float progress;
    private String format;

    public String getBpPath() {
        if (TextUtils.isEmpty(bpPath)) {
            return "";
        } else {
            return bpPath;
        }
    }

    public void setBpPath(String bpPath) {
        this.bpPath = bpPath;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
