package com.warrior.hangsu.administrator.strangerbookreader.base;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.utils.AudioMgr;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.Logger;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.VolumeUtil;
import com.youdao.sdk.common.YouDaoLog;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/12.
 */

public abstract class TTSActivity extends BaseActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, this); // 参数Context,TextToSpeech.OnInitListener
    }

    protected void text2Speech(String text) {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (this, ShareKeys.CLOSE_TTS_KEY, false)) {
            return;
        }
        if (tts != null && !tts.isSpeaking()) {
            tts.setPitch(SharedPreferencesUtils.getFloatSharedPreferencesData(this, ShareKeys.TTS_PITCH_KEY));// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            tts.setSpeechRate(SharedPreferencesUtils.getFloatSharedPreferencesData(this, ShareKeys.TTS_PITCH_KEY));
            HashMap<String, String> myHashAlarm = new HashMap();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_VOLUME,
                    VolumeUtil.getMusicVolumeRate(this) + "");
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);

            if (VolumeUtil.getHeadPhoneStatus(this)) {
                AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                mAudioManager.startBluetoothSco();
            }
            tts.speak(text,
                    TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
    }

    protected synchronized void playVoice(String speakUrl) {
        playVoice(speakUrl, null);
    }

    protected synchronized void playVoice(String speakUrl, AudioMgr.SuccessListener listener) {
        YouDaoLog.e(AudioMgr.PLAY_LOG + "TranslateDetailActivity click to playVoice speakUrl = " + speakUrl);
        if (!TextUtils.isEmpty(speakUrl) && speakUrl.startsWith("http")) {
            AudioMgr.startPlayVoice(speakUrl, listener);
        }
    }

    protected void stopSpeak() {
        if (null != tts) {
            tts.stop();
        }
    }

    @Override
    public void onBackPressed() {
        if (tts.isSpeaking()) {
            tts.stop();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop(); // 不管是否正在朗读TTS都被打断
        tts.shutdown(); // 关闭，释放资源
    }

    /**
     * 用来初始化TextToSpeech引擎
     * status:SUCCESS或ERROR这2个值
     * setLanguage设置语言，帮助文档里面写了有22种
     * TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失。
     * TextToSpeech.LANG_NOT_SUPPORTED:不支持
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Logger.d("onStart:" + utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {
                    Logger.d("onDone:" + utteranceId);
                    onTTSFinish(utteranceId);
                }

                @Override
                public void onError(String utteranceId) {
                    Logger.d("onError:" + utteranceId);
                    onTTSError(utteranceId);
                }
            });
            int result = tts.setLanguage(Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                ToastUtils.showSingleToast("数据丢失或不支持");
            }
        }
    }

    protected void onTTSFinish(String text) {

    }

    protected void onTTSError(String text) {
    }
}
