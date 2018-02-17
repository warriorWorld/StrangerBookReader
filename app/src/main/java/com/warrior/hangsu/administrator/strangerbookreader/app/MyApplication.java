package com.warrior.hangsu.administrator.strangerbookreader.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.utils.AppUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;

/**
 * Created by Administrator on 2016/4/3.
 */
public class MyApplication extends Application {
    private static Context context;
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sInstance = this;
        String s = SharedPreferencesUtils.getSharedPreferencesData(getApplicationContext(), "closeQueryWord");
        if ("true".equals(s)) {
            Globle.closeQueryWord = true;
        } else {
            Globle.closeQueryWord = false;
        }
        initImageLoader(this);

        AppUtils.init(this);
        initPrefs();

        dealFileUriExposedException();
    }

    private void dealFileUriExposedException() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static MyApplication getsInstance() {
        return sInstance;
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        ImageLoader.getInstance().init(config.build());
    }

    public static Context getContext() {
        return context;
    }
}
