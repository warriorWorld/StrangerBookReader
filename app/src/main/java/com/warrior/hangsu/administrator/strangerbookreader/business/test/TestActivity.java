package com.warrior.hangsu.administrator.strangerbookreader.business.test;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Created by Administrator on 2018/2/12.
 */

public class TestActivity extends BaseActivity {
    private String bookPath;
//    private TextView testTv;
    private int page = 0;
    private boolean is_includeTag = false;
    private WebView epubWebView;

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
        test();
    }

    private void initUI() {
//        testTv = (TextView) findViewById(R.id.test_tv);
//        testTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                page++;
//                test();
//            }
//        });
//        testTv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                is_includeTag = !is_includeTag;
//                test();
//                return true;
//            }
//        });
        epubWebView= (WebView) findViewById(R.id.epub_web_view);
        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void onBackPressed() {
        page++;
        test();
    }

    private void test() {
        try {
            EpubReader epubReader = new EpubReader();

            MediaType[] lazyTypes = {
                    MediatypeService.CSS,
                    MediatypeService.GIF, MediatypeService.JPG,
                    MediatypeService.PNG,
                    MediatypeService.MP3,
                    MediatypeService.MP4};
            String fileName =bookPath;
            Book book = epubReader.readEpubLazy(fileName,"UTF-8", Arrays.asList(lazyTypes));
//            List<Resource> contents = book.getContents();
            String baseUrl="file://"+bookPath;
            String data = new String(book.getContents().get(page).getData());

            epubWebView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
//            try {
//              String  strParagraph = new String(contents.get(page).getData(), "UTF-8");//转成UTF-8
//                testTv.setText(strParagraph);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            // Log the tale of contents
//            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Recursively Log the Table of Contents
     *
     * @param tocReferences
     * @param depth
     */
    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("epublib", tocString.toString());
//            testTv.setText(tocString.toString());
            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }
}
