package com.warrior.hangsu.administrator.strangerbookreader.business.pdf;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.TTSActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;

import java.io.File;

public class PdfActivity extends TTSActivity {
    private PDFView mPDFView;
    private String bookPath, bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bookPath = intent.getStringExtra("bookPath");
        bookName = intent.getStringExtra("bookName");
        if (TextUtils.isEmpty(bookPath)) {
            finish();
        }
        initUI();
        loadBook();
    }

    private void initUI() {
        mPDFView = (PDFView) findViewById(R.id.pdf_v);
        hideBaseTopBar();
    }

    private void loadBook() {
        mPDFView.fromFile(new File(bookPath))
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(SharedPreferencesUtils.getIntSharedPreferencesData(this, bookName+ShareKeys.LAST_PAGE_POSITION))
                // allows to draw something on the current page, usually visible in the middle of the screen
//                .onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
//                .onDrawAll(onDrawListener)
//                .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
//                .onPageChange(onPageChangeListener)
//                .onPageScroll(onPageScrollListener)
//                .onError(onErrorListener)
//                .onPageError(onPageErrorListener)
//                .onRender(onRenderListener) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
//                .onTap(onTapListener)
//                .onLongPress(onLongPressListener)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//                .linkHandler(DefaultLinkHandler)
                .pageFitPolicy(FitPolicy.WIDTH)
                .pageSnap(true) // snap pages to screen boundaries
                .pageFling(false) // make a fling change only a single page like ViewPager
                .nightMode(false) // toggle night mode
                .load();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveLastPosition();
    }

    private void saveLastPosition() {
        try {
            SharedPreferencesUtils.setSharedPreferencesData(this, bookName + ShareKeys.LAST_PAGE_POSITION, mPDFView.getCurrentPage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pdf;
    }
}
