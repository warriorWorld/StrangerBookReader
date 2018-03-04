package com.warrior.hangsu.administrator.strangerbookreader.business.epub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

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
        try {
            String baseUrl = "file://" + bookPath;
            String data = new String(book.getContents().get(currentChapter).getData());

            epubWebView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
