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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.strangerbookreader.business.login.LoginActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.enums.BookStatus;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnJsoupListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyTool;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.VerticalSeekBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.EpubReadDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ReadDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String bookTitle;
    private EpubReadDialog readDialog;
    private int chapterSize = 0;
    private static org.jsoup.nodes.Document doc;
    private ArrayList<String> contents = new ArrayList<>();
    private String txtPath;
    private TextView progress_explain_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        Intent intent = getIntent();
        bookPath = intent.getStringExtra("bookPath");
        if (TextUtils.isEmpty(bookPath)) {
            finish();
        }
        initUI();
        initEpubLib();
        loadChapter();
        recoverProgress();
    }


    private void initUI() {
        progress_explain_tv = (TextView) findViewById(R.id.progress_explain_tv);
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
                isWebBottom();
            }
        });

        int separatorPosition = bookPath.lastIndexOf(File.separator);
        int dotPosition = bookPath.lastIndexOf(".");
        bookTitle = bookPath.substring(separatorPosition + 1, dotPosition);
        baseTopBar.setTitle(bookTitle);
        baseTopBar.setLeftBackground(R.drawable.back);
        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                EpubActivity.this.finish();
            }

            @Override
            public void onRightClick() {
                showReadDialog();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    private boolean isProgressChange = false;

    private void isWebBottom() {
        //WebView的总高度
        float webViewContentHeight = epubWebView.getContentHeight() * epubWebView.getScale();
        //WebView的现高度
        float webViewCurrentHeight = (epubWebView.getHeight() + epubWebView.getScrollY());
        if (webViewContentHeight == 0) {
            return;
        }
        progress_explain_tv.setText
                ("第" + (currentChapter + 1) + "章  " + NumberUtil.doubleDecimals((webViewCurrentHeight / webViewContentHeight) * 100) + "%");

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
            chapterSize = book.getContents().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doGetData(final int chapter, final OnJsoupListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
//                    doc = Jsoup.connect(url)
//                            .timeout(10000).get();
                    String baseUrl = "file://" + bookPath;
                    String data = new String(book.getContents().get(chapter).getData());
                    doc = Jsoup.parseBodyFragment(data, baseUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (null != doc) {
                    Elements test = doc.select("p");
                    String content = "";
                    for (int i = 0; i < test.size(); i++) {
                        content += test.get(i).text() + "\n";
                    }
                    listener.onSuccess(content);
                }
            }
        }.start();
    }

    private int tempChapter = 0;

    private void toText() {
        txtPath = bookPath.replaceAll("\\.epub", "(txt).txt");
        txtPath = txtPath.replaceAll("\\.EPUB", "(txt).txt");

        File file = new File(txtPath);
        if (file.exists()) {
            ToastUtils.showSingleToast("已存在TXT版本");
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(this);

        doGetData(tempChapter, new OnJsoupListener() {
            @Override
            public void onSuccess(String content) {
                if (!TextUtils.isEmpty(content)) {
                    contents.add(content);
                }
                if (tempChapter + 1 < chapterSize) {
                    doGetData(tempChapter++, this);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FileWriter fw = new FileWriter(txtPath, true);
                                for (int i = 0; i < contents.size(); i++) {
                                    fw.write(contents.get(i));
                                }
                                fw.close();
                                ToastUtils.showSingleToast("转换成功");
                                SingleLoadBarUtil.getInstance().dismissLoadBar();
                                addBooks(txtPath, "TXT", null);
                                EpubActivity.this.finish();
                            } catch (IOException e) {
                                ToastUtils.showSingleToast(e + "");
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void addBooks(String path, String format, String bpPath) {
        db.insertBooksTableTb(path, StringUtil.cutString(path, '/', '.'), 0, format, bpPath);
    }

    private void showReadDialog() {
        if (null == readDialog) {
            readDialog = new EpubReadDialog(this);
            readDialog.setOnReadDialogClickListener(new OnReadDialogClickListener() {
                @Override
                public void onSearchJumpClick() {
                }

                @Override
                public void onProgressJumpSelected(int progress) {
                    currentChapter = progress - 1;
                    loadChapter();
                }

                @Override
                public void onSunMoonToggleClick() {
                }

                @Override
                public void onTextSizeClick() {
                }

                @Override
                public void onBackgroundStyleClick() {
                }

                @Override
                public void onToggleTranslateWayClick() {
                }

                @Override
                public void onCloseTranslateClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (EpubActivity.this, ShareKeys.CLOSE_TRANSLATE,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(EpubActivity.this,
                                            ShareKeys.CLOSE_TRANSLATE, false));
                }

                @Override
                public void onUserHeadClick() {
                    if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
                        Intent intent = new Intent(EpubActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCloseDialogClick() {

                }

                @Override
                public void onToTXTClick() {
                    toText();
                }

                @Override
                public void onHelpClick() {
                    showHelpDialog();
                }
            });
        }
        readDialog.show();
        readDialog.refreshUI();
        readDialog.initSeekBar(currentChapter + 1, chapterSize);
    }

    private void showHelpDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.show();
        dialog.setTitle("epub格式仅支持长按翻译\nepub是上下翻页的\n可以用音量键跳转章节。\nepub不计入统计数据\n可以将epub转换为TXT阅读");
        dialog.setOkText("知道了");
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
                if (currentChapter + 1 < chapterSize) {
                    currentChapter++;
                    loadChapter();
                }
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

            progress_explain_tv.setText("第" + (currentChapter + 1) + "章  0%");
            if (currentChapter == 0) {
                //不是第一章没必要管它
                doc = Jsoup.parseBodyFragment(data, baseUrl);
                if (null != doc) {
                    Elements test = doc.select("p");
                    if (null == test || test.size() == 0) {
                        //无文字内容
                        currentChapter++;
                        loadChapter();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
