package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.content.Context;
import android.view.WindowManager;

import com.avos.avoscloud.AVException;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

/**
 * Created by Administrator on 2017/7/29.
 */

public class LeanCloundUtil {

    public static boolean handleLeanResult(Context context, AVException e) {
        if (e == null) {
            return true;
        } else {
            try {
                MangaDialog errorDialog = new MangaDialog(context);
                errorDialog.show();
                errorDialog.setTitle("出错了");
                errorDialog.setMessage(e.getMessage());
            } catch (WindowManager.BadTokenException exception) {

            }
            return false;
        }
    }
}
