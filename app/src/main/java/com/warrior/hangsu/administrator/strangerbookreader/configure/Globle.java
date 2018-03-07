package com.warrior.hangsu.administrator.strangerbookreader.configure;

import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.warrior.hangsu.administrator.strangerbookreader.R;

public class Globle {
    public static final boolean isTest=false;
    //数据库版本号
    public static final int DB_VERSION = 1;
    //版本号
    public static final String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=" +
            "strangerbookreader&key=1903646178&type=data&doctype=json&version=1.1&q=";
    final public static String DOWNLOAD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/" + "english_book_reader";
    final public static DisplayImageOptions normalImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .build();
}
