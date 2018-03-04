package com.warrior.hangsu.administrator.strangerbookreader.business.epub;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;

import java.util.Arrays;
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
    private EpubReader epubReader;
    private Book book;
    private boolean is_show_bottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        bookPath = intent.getStringExtra("bookPath");
        if (TextUtils.isEmpty(bookPath)) {
            finish();
        }
        initUI();
        initEpubLib();
        loadChapter();
        ToastUtils.showSingleToast("epub格式仅支持长按翻译");
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
//                translation(word);
                ToastUtils.showSingleToast(word);
            }
        });
        epubWebView.setOnCustomScroolChangeListener(new TranslateWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
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
        });
        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    private void initEpubLib() {
        try {
            epubReader = new EpubReader();

            MediaType[] lazyTypes = {
//                    MediatypeService.CSS,
//                    MediatypeService.GIF,
//                    MediatypeService.JPG,
//                    MediatypeService.PNG,
                    MediatypeService.MP3,
                    MediatypeService.MP4};
            book = epubReader.readEpubLazy(bookPath, "UTF-8", Arrays.asList(lazyTypes));

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

    private void loadChapter() {
        is_show_bottom=false;
        try {
            String baseUrl = "file://" + bookPath;
            String data = new String(book.getContents().get(currentChapter).getData());

            epubWebView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
