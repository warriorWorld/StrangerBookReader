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
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by Administrator on 2018/2/12.
 */

public class TestActivity extends BaseActivity {
    private String bookPath;
    private TextView testTv;
    private int page = 0;
    private boolean is_includeTag = false;

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
        testTv = (TextView) findViewById(R.id.test_tv);
        testTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                test();
            }
        });
        testTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                is_includeTag = !is_includeTag;
                test();
                return true;
            }
        });
        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    private void test() {
        try {
            File file = new File(bookPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            long length = file.length();
            if (length < 10) {
                return;
            }
            // find InputStream for book
            InputStream epubInputStream = new FileInputStream(file);

            // Load Book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);

            // Log the book's authors
            Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());

            // Log the book's title
            Log.i("epublib", "title: " + book.getTitle());

            // Log the book's coverimage property
            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
                    .getInputStream());
            Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by "
                    + coverImage.getHeight() + " pixels");

            // Log the tale of contents
            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
        } catch (IOException e) {
            Log.e("epublib", e.getMessage());
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

            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }
}
