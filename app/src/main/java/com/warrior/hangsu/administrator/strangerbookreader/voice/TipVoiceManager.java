package com.warrior.hangsu.administrator.strangerbookreader.voice;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.app.MyApplication;


/**
 * Created by Administrator on 2016-06-28.
 */
public class TipVoiceManager {
    private Context context;
    // 声音
    public SoundPool mBipPool;// 声音管理器
    public int ca;

    private TipVoiceManager() {
        context = MyApplication.getContext();
        initVoice();
    }

    private static volatile TipVoiceManager instance = null;

    public static TipVoiceManager getInstance() {
        if (instance == null) {
            synchronized (TipVoiceManager.class) {
                if (instance == null) {
                    instance = new TipVoiceManager();
                }
            }
        }
        return instance;
    }

    private void initVoice() {
        // 初始化声音管理器
        mBipPool = new SoundPool(3, AudioManager.STREAM_NOTIFICATION, 2);
        // 读取声音
        ca = mBipPool.load(context, R.raw.ca, 1);
    }

    /**
     * 声音提示
     *
     * @param Code
     */
    public void voiceTip(int Code) {
        switch (Code) {
            case 0:
                //登陆成功
                mBipPool.play(ca, 1, 1, 0, 0, 1);
                break;

        }
    }
}
