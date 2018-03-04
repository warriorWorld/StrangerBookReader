package com.warrior.hangsu.administrator.strangerbookreader.business.epub;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyTool;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Created by Administrator on 2018/2/12.
 */

public class EpubActivity extends BaseActivity {
    private String bookPath;
    private int currentChapter = 0;
    private TranslateWebView epubWebView;
    private Book book;
    private boolean is_show_bottom = false;
    private DbAdapter db;//数据库
    private ClipboardManager clip;//复制文本用
    private MangaDialog dialog;
    private TextView topBarRight, topBarLeft;
    private String bookTitle;
    /**
     * 时间
     */
    private SimpleDateFormat sdf;
    private Date curDate;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        sdf = new SimpleDateFormat("HH:mm");
        curDate = new Date(System.currentTimeMillis());//获取当前时间
        date = sdf.format(curDate);

        Intent intent = getIntent();
        bookPath = intent.getStringExtra("bookPath");
        if (TextUtils.isEmpty(bookPath)) {
            finish();
        }
        initUI();
        initEpubLib();
        loadChapter();
        ToastUtils.showLongToast("epub格式仅支持长按翻译\nepub是上下翻页的\n可以用音量键跳转章节。");
        recoverProgress();
    }


    private void initUI() {
        epubWebView = (TranslateWebView) findViewById(R.id.epub_web_view);
        epubWebView.setTextSelectionListener(new TextSelectionListener() {
            @Override
            public void seletedWord(String word) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *为了取消复制弹窗
                         */
                        epubWebView.clearFocus();
                    }
                }, 150);//n秒后执行Runnable中的run方法
                translation(word);
            }
        });
        epubWebView.setOnCustomScroolChangeListener(new TranslateWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
                if (oldt - t > 50) {
                    refreshTime();
                }
                isWebBottom();
            }
        });
        topBarLeft = (TextView) findViewById(R.id.top_bar_left);
        topBarRight = (TextView) findViewById(R.id.top_bar_right);

        int separatorPosition = bookPath.lastIndexOf(File.separator);
        int dotPosition = bookPath.lastIndexOf(".");
        bookTitle = bookPath.substring(separatorPosition + 1, dotPosition);
        topBarLeft.setText(bookTitle);
        topBarRight.setText("第" + (currentChapter + 1) + "章" + "  " + date);

        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    private void refreshTime() {
        curDate.setTime(System.currentTimeMillis());//获取当前时间
        date = sdf.format(curDate);
        topBarRight.setText("第" + (currentChapter + 1) + "章" + "  " + date);
    }

    private void isWebBottom() {
        //WebView的总高度
        float webViewContentHeight = epubWebView.getContentHeight() * epubWebView.getScale();
        //WebView的现高度
        float webViewCurrentHeight = (epubWebView.getHeight() + epubWebView.getScrollY());
        System.out.println("webViewContentHeight=" + webViewContentHeight);
        System.out.println("webViewCurrentHeight=" + webViewCurrentHeight);
        if ((webViewContentHeight - webViewCurrentHeight) < 50 && !is_show_bottom) {
            System.out.println("WebView滑动到了底端");
            ToastUtils.showSingleToast("可以用音量键翻到下一章");
            is_show_bottom = true;
        }
    }

    private void translation(final String word) {
        clip.setText(word);
        //记录查过的单词
        db.insertWordsBookTb(word);
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (this, ShareKeys.CLOSE_TRANSLATE, false)) {
            return;
        }
        String url = Globle.YOUDAO + word;
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
                        showOnlyOkDialog(word, result.getQuery() + " [" + item.getPhonetic() +
                                "]: " + "\n" + t);
                    } else {
                        ToastUtil.tipShort(EpubActivity.this, "没查到该词" + word);
                    }
                } else {
                    ToastUtil.tipShort(EpubActivity.this, "网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(EpubActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                EpubActivity.this, url, params,
                YoudaoResponse.class, callback);

    }

    private void showOnlyOkDialog(String title, String msg) {
        if (null == dialog) {
            dialog = new MangaDialog(this);
        }
        dialog.show();
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setOkText("确定");
    }

    private void initEpubLib() {
        try {
            EpubReader epubReader = new EpubReader();

//            MediaType[] lazyTypes = {
////                    MediatypeService.CSS,
////                    MediatypeService.GIF,
////                    MediatypeService.JPG,
////                    MediatypeService.PNG,
//                    MediatypeService.MP3,
//                    MediatypeService.MP4};
//            book = epubReader.readEpubLazy(bookPath, "UTF-8", Arrays.asList(lazyTypes));
            book = epubReader.readEpub(new FileInputStream(bookPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 24:
                if (currentChapter != 0) {
                    currentChapter--;
                    loadChapter();
                }
                return true;
            case 25:
                currentChapter++;
                loadChapter();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveProgress() {
        SettingManager.getInstance().saveReadProgress(bookPath, currentChapter, epubWebView.getScrollY(), 0);
    }

    private void recoverProgress() {
        int pos[] = SettingManager.getInstance().getReadProgress(bookPath);
        currentChapter = pos[0];
        int currentProgress = pos[1];
        loadChapter();
        epubWebView.setScrollY(currentProgress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProgress();
        db.closeDb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProgress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveProgress();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverProgress();
    }

    private void loadChapter() {
        is_show_bottom = false;
        try {
            String baseUrl = "file://" + bookPath;
            String data = new String(book.getContents().get(currentChapter).getData());

            epubWebView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);

          refreshTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
