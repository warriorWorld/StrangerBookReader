package com.warrior.hangsu.administrator.strangerbookreader.business.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;

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
            Reader reader = new Reader();
            reader.setMaxContentPerSection(1000); // Max string length for the current page.
            reader.setIsIncludingTextContent(true); // Optional, to return the tags-excluded version.
            reader.setFullContent(bookPath); // Must call before readSection.


            BookSection bookSection = null;

            bookSection = reader.readSection(page);
            String sectionContent = bookSection.getSectionContent(); // Returns content as html.
            String sectionTextContent = bookSection.getSectionTextContent(); // Excludes html tags.
            if (is_includeTag) {
                testTv.setText(sectionContent);
            } else {
                testTv.setText(sectionTextContent);
            }
        } catch (ReadingException e) {
            e.printStackTrace();
        } catch (OutOfPagesException e) {
            e.printStackTrace();
        }
    }

}
