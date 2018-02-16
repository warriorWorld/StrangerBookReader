package com.warrior.hangsu.administrator.strangerbookreader.configure;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.warrior.hangsu.administrator.strangerbookreader.R;

public class Globle {
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnLoading(R.drawable.on_loading)
            .showImageOnFail(R.drawable.loading_fail)
            .build();
    //数据库版本号
    public static final int DB_VERSION = 1;
    //版本号
    public static String versionName;
    public static String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=" +
            "strangerbookreader&key=1903646178&type=data&doctype=json&version=1.1&q=";

    public static String nowBookName;
    public static String nowBookPath;
    public static int nowBookPosition;

    public static boolean closeQueryWord;
}
