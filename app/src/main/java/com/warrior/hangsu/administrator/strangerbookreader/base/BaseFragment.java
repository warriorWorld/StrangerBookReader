package com.warrior.hangsu.administrator.strangerbookreader.base;/**
 * Created by Administrator on 2016/10/26.
 */

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;

import java.util.Locale;

/**
 * 作者：苏航 on 2016/10/26 14:46
 * 邮箱：772192594@qq.com
 */
public class BaseFragment extends Fragment implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(getActivity(), this); // 参数Context,TextToSpeech.OnInitListener
    }

    protected void text2Speech(String text) {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (getActivity(), ShareKeys.CLOSE_TTS_KEY, false)) {
            return;
        }
        if (tts != null && !tts.isSpeaking()) {
            tts.setPitch(SharedPreferencesUtils.getFloatSharedPreferencesData(getActivity(), ShareKeys.TTS_PITCH_KEY));// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            tts.setSpeechRate(SharedPreferencesUtils.getFloatSharedPreferencesData(getActivity(), ShareKeys.TTS_PITCH_KEY));
            tts.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
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
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                ToastUtils.showSingleToast("数据丢失或不支持");
            }
        }
    }
}
