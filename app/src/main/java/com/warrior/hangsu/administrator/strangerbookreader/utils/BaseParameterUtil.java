package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;


/**
 * Created by Administrator on 2017/11/11.
 */

public class BaseParameterUtil {
    private PackageManager manager;
    private PackageInfo info;
    public String EMPTY = "empty";

    private BaseParameterUtil() {
    }

    private static volatile BaseParameterUtil instance = null;

    public static BaseParameterUtil getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (BaseParameterUtil.class) {
                //双重锁定
                if (instance == null) {
                    instance = new BaseParameterUtil();
                }
            }
        }
        return instance;
    }

    private void init(Activity context) {
        try {
            manager = context.getPackageManager();
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getAppVersionName(Activity context) {
        if (null == manager || null == info) {
            init(context);
        }
        if (TextUtils.isEmpty(info.versionName)) {
            return EMPTY;
        }
        return info.versionName;
    }

    public int getAppVersionCode(Activity context) {
        if (null == manager || null == info) {
            init(context);
        }
        return info.versionCode;
    }

    public String getDevice(Activity context) {
        String device = SharedPreferencesUtils.getSharedPreferencesData(context,
                ShareKeys.DEVICE_KEY);
        if (TextUtils.isEmpty(device)) {
            device = DeviceIDUtil.getDeviceID(context);
            if (TextUtils.isEmpty(device)) {
                return EMPTY;
            }
            SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.DEVICE_KEY,
                    device);
        }
        return device;
    }

//    public String getDeviceToken() {
//        String deviceToken = SharedPreferencesUtils.getSharedPreferencesData(context,
//                ShareKeys.DEVICE_TOKEN_KEY);
//        if (TextUtils.isEmpty(deviceToken)) {
//            deviceToken = PushAgent.getInstance(context).getRegistrationId();
//            if (TextUtils.isEmpty(deviceToken)) {
//                return EMPTY;
//            }
//            SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.DEVICE_TOKEN_KEY,
//                    deviceToken);
//        }
//        return deviceToken;
//    }

    public void saveDeviceToken(Activity context, String token) {
        SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.DEVICE_TOKEN_KEY,
                token);
    }

    public String getSystemVersion() {
        if (TextUtils.isEmpty(android.os.Build.VERSION.RELEASE)) {
            return EMPTY;
        }
        return android.os.Build.VERSION.RELEASE;
    }

    public String getPhoneModel() {
        if (TextUtils.isEmpty(android.os.Build.MODEL)) {
            return EMPTY;
        }
        return android.os.Build.MODEL;
    }

    public String getSource() {
        return "ANDROID";
    }

    public String handleActivityName(String str) {
        try {
            String res = str;
            int dotPosition = str.lastIndexOf(".");
            res = res.substring(dotPosition + 1, res.length());
            res = res.replaceAll("Activity", "");
            return res;
        } catch (Exception e) {
            return str;
        }
    }
}
