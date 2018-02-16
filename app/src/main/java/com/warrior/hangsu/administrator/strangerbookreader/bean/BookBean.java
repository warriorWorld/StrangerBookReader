package com.warrior.hangsu.administrator.strangerbookreader.bean;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/4/3.
 */
public class BookBean {
    private String path;
    private String bpPath;
    private String name;
    private int progress;

    public String getBpPath() {
        if (TextUtils.isEmpty(bpPath)) {
            return "";
        } else {
            return "file://" + bpPath;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
