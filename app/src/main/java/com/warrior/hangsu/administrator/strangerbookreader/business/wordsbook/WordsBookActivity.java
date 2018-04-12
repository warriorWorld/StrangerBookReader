package com.warrior.hangsu.administrator.strangerbookreader.business.wordsbook;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.VibratorUtil;
import com.warrior.hangsu.administrator.strangerbookreader.voice.TipVoiceManager;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class WordsBookActivity extends BaseActivity implements OnClickListener, TextToSpeech.OnInitListener {
    private WordsBookAdapter adapter;
    private View emptyView;
    private TextView topBarRight, topBarLeft;
    private ViewPager vp;
    private DbAdapter db;//数据库
    private ArrayList<WordsBookBean> wordsList = new ArrayList<WordsBookBean>();
    private int nowPosition = 0;
    private ClipboardManager clip;//复制文本用
    private Button killBtn;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        refresh();
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, this); // 参数Context,TextToSpeech.OnInitListener
    }

    @Override
    protected void onResume() {
        super.onResume();
        text2Speech(wordsList.get(nowPosition).getWord());
    }

    private void refresh() {
        wordsList = db.queryAllWordsBook();
        if (null == wordsList || wordsList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            killBtn.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            killBtn.setVisibility(View.VISIBLE);
        }
        try {
            WordsBookBean item = wordsList.get(nowPosition);
            topBarRight.setText("查询次数:" + item.getTime());
            topBarLeft.setText("总计:" + wordsList.size() + "个生词,当前位置:" + (nowPosition + 1));
        } catch (IndexOutOfBoundsException e) {

        }
        initViewPager();
    }


    private void initUI() {
        vp = (ViewPager) findViewById(R.id.words_viewpager);
        emptyView = findViewById(R.id.empty_view);
        killBtn = (Button) findViewById(R.id.kill_btn);
        topBarLeft = (TextView) findViewById(R.id.top_bar_left);
        topBarRight = (TextView) findViewById(R.id.top_bar_right);
        hideBaseTopBar();
        killBtn.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_words_book;
    }


    private void initViewPager() {
        if (null == adapter) {
            adapter = new WordsBookAdapter(WordsBookActivity.this);
            adapter.setOnWordsBookViewListener(new WordsBookView.OnWordsBookViewListener() {
                @Override
                public void onWordClick(String word) {

                }

                @Override
                public void queryWord(String word) {
                    translation(word);
                }

                @Override
                public void onWordLongClick(String word) {
                    //长按才调用这个
                    clip.setText(word);
                }
            });
            adapter.setList(wordsList);
            vp.setAdapter(adapter);
            recoverState();
            vp.setPageTransformer(true, new DepthPageTransformer());
            vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    nowPosition = position;
                    WordsBookBean item = wordsList.get(nowPosition);
                    topBarRight.setText("查询次数:" + item.getTime());
                    topBarLeft.setText("总计:" + wordsList.size() + "个生词,当前位置:" + (position + 1));
                    text2Speech(wordsList.get(position).getWord());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
//            mangaPager.setCurrentItem(2);
        } else {
            adapter.setList(wordsList);
            adapter.notifyDataSetChanged();
        }
    }

    private void translation(final String word) {
        String url = Globle.YOUDAO + word;
        text2Speech(word);
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }
                        adapter.getNowView().setTranslate(result.getQuery() + " [" + item.getPhonetic() +
                                "]: " + "\n" + t);
                    } else {
                        adapter.getNowView().setTranslate("没查到该词");
                    }
                } else {
                    adapter.getNowView().setTranslate("网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(WordsBookActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                WordsBookActivity.this, url, params,
                YoudaoResponse.class, callback);

    }

    private void text2Speech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.setPitch(0.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            tts.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
        tts.stop(); // 不管是否正在朗读TTS都被打断
        tts.shutdown(); // 关闭，释放资源
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverState();
    }

    private void saveState() {
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.WORDS_BOOK_PROGRESS_KEY,
                nowPosition);
    }

    private void recoverState() {
        int p = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                ShareKeys.WORDS_BOOK_PROGRESS_KEY);
        if (p >= 0) {
            nowPosition = p;
            vp.setCurrentItem(p);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kill_btn:
                //太吵
//                TipVoiceManager.getInstance().voiceTip(0);
                try {
                    VibratorUtil.Vibrate(WordsBookActivity.this, 10);
                    WordsBookBean item = wordsList.get(nowPosition);
                    db.deleteWordByWord(item.getWord());
                    wordsList.remove(nowPosition);
                    initViewPager();
                } catch (IndexOutOfBoundsException e) {
                    WordsBookActivity.this.finish();
                }
                break;
        }
    }

}
