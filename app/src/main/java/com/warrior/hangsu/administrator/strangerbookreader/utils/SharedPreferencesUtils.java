package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地存储工具
 *
 * @author Yunlongx Luo
 */
public class SharedPreferencesUtils {

    /**
     * SharedPreferences
     */
    private static SharedPreferences mSharedPreferences;


    public static void setSharedPreferencesData(Context mContext, String mKey,
                                                String mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putString(mKey, mValue).commit();
    }

    public static void setSharedPreferencesData(Context mContext, String mKey, int mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putInt(mKey, mValue).commit();
    }

    public static void setSharedPreferencesData(Context mContext, String mKey, float mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putFloat(mKey, mValue).commit();
    }

    public static void setSharedPreferencesData(Context mContext, String mKey, boolean mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putBoolean(mKey, mValue).commit();
    }

    public static String getSharedPreferencesData(Context mContext, String mKey) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getString(mKey, "");
    }

    public static String getSharedPreferencesData(Context mContext, String mKey, String defaultValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getString(mKey, defaultValue);
    }

    public static Integer getIntSharedPreferencesData(Context mContext, String mKey) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getInt(mKey, 0);
    }

    public static Float getFloatSharedPreferencesData(Context mContext, String mKey) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getFloat(mKey, 0f);
    }

    public static boolean getBooleanSharedPreferencesData(Context mContext, String mKey, boolean defaultValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "english_bookreader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getBoolean(mKey, defaultValue);
    }
}
