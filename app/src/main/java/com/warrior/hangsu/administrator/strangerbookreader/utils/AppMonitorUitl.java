package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.view.textservice.TextServicesManager;

import java.util.List;

/**
 * Created by Administrator on 2016-07-28.
 */
public class AppMonitorUitl {
    private ActivityManager mActivityManager = null;

    public AppMonitorUitl(Context context) {
        // 获得ActivityManager服务的对象
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public boolean isThisAppRun(String app) {
//这东西从Android5.1之后 Google因为安全问题给关闭了
        //解决办法见http://stackoverflow.com/questions/30619349/android-5-1-1-and-above-getrunningappprocesses-returns-my-application-packag
        List<ActivityManager.RunningAppProcessInfo> list = mActivityManager.getRunningAppProcesses();
        if (list != null) {
            Logger.d("长度" + list.size());
            for (int i = 0; i < list.size(); i++) {
//                if ("com.android.email".matches(list.get(i).processName)) {
//                    int pid = android.os.Process.getUidForName("com.android.email");
//                    android.os.Process.killProcess(pid);
//                } else {
//                    mTextVIew.append(list.get(i).processName + "\n");
//                }
                Logger.d(list.get(i).processName);
            }
        }
        return false;
    }
}
