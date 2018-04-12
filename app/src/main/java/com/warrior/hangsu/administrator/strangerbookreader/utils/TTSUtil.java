//package com.warrior.hangsu.administrator.strangerbookreader.utils;
//
//import android.content.Context;
//import android.speech.tts.TextToSpeech;
//
//import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
//
///**
// * Created by Administrator on 2018/4/12.
// */
//
//public class TTSUtil {
//    public static void text2Speech(Context context, TextToSpeech tts, String text) {
//        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
//                (context, ShareKeys.CLOSE_TTS_KEY, false)) {
//            return;
//        }
//        if (tts != null && !tts.isSpeaking()) {
//            tts.setPitch(SharedPreferencesUtils.getFloatSharedPreferencesData(context, ShareKeys.TTS_PITCH_KEY));// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
//            tts.speak(text,
//                    TextToSpeech.QUEUE_FLUSH, null);
//        }
//    }
//}
