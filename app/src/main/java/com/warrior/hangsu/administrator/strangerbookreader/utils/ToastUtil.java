package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast
 */
public class ToastUtil {

	public static void tipLong(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static void tipLong(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	public static void tipShort(Context context, String conten) {
		Toast.makeText(context, conten, Toast.LENGTH_SHORT).show();
	}

	public static void tipShort(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
}
