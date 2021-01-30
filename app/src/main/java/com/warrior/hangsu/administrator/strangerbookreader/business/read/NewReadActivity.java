package com.warrior.hangsu.administrator.strangerbookreader.business.read;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.TTSActivity;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.login.LoginActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.readview.BaseReadView;
import com.warrior.hangsu.administrator.strangerbookreader.business.readview.OverlappedWidget;
import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.StatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnFreeTTSListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSearchResultListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSpeakClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnUpFlipListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnWordClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LogUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.Logger;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.VibratorUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaEditDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ReadDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.TranslateDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.TranslateResultDialog;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/2/12.
 */

public class NewReadActivity extends TTSActivity implements
        EasyPermissions.PermissionCallbacks {
    private BaseReadView mPageWidget;
    private FrameLayout readWidgetFl;
    private ClipboardManager clip;//复制文本用
    private String bookPath, bookFormat;
    private MangaDialog dialog;
    private ReadDialog readDialog;
    private DbAdapter db;//数据库
    /**
     * 时间
     */
    private SimpleDateFormat sdf;
    private Date curDate;
    private String date;
    /**
     * 电量和时间监听
     */
    private Receiver receiver = new Receiver();
    private IntentFilter intentFilter = new IntentFilter();
    private TranslateDialog translateResultDialog;
    private TranslateParameters tps = new TranslateParameters.Builder()
            .source("MangaReader")
            .from(LanguageUtils.getLangByName("中文"))
            .to(LanguageUtils.getLangByName("英文"))
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        curDate = new Date(System.currentTimeMillis());//获取当前时间
        date = sdf.format(curDate);

        Intent intent = getIntent();
        bookPath = intent.getStringExtra("bookPath");
        bookFormat = intent.getStringExtra("bookFormat");
        if (TextUtils.isEmpty(bookPath)) {
            finish();
        }
        initUI();
        initPagerWidget();
    }

    private void initUI() {
        readWidgetFl = (FrameLayout) findViewById(R.id.read_widget_fl);
        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_new;
    }


    @AfterPermissionGranted(111)
    private void initPagerWidget() {
        SingleLoadBarUtil.getInstance().showLoadBar(this);
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            mPageWidget = new OverlappedWidget(this, bookPath, bookFormat, new ReadListener());

            if (SharedPreferencesUtil.getInstance().getBoolean(ShareKeys.ISNIGHT, false)) {
                mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_night),
                        getResources().getColor((R.color.chapter_title_night)));
            }
            mPageWidget.setOnWordClickListener(new OnWordClickListener() {
                @Override
                public void onWordClick(String word) {
                    if (StringUtil.isWord(word)) {
                        translation(word);
                    }
                }
            });
            mPageWidget.setOnUpFlipListener(new OnUpFlipListener() {
                @Override
                public void onUpFlip() {
                    showReadDialog();
                }
            });
            readWidgetFl.removeAllViews();
            readWidgetFl.addView(mPageWidget);

            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(receiver, intentFilter);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SingleLoadBarUtil.getInstance().dismissLoadBar();
                    /**
                     *要执行的操作
                     */
                    mPageWidget.init(SettingManager.getInstance().getReadTheme());
                    toggleDayNight();
                }
            }, 500);//n秒后执行Runnable中的run方法
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void translation(final String word) {
        clip.setText(word);
        VibratorUtil.Vibrate(this, word.length() * 10);
        //记录查过的单词
        db.insertWordsBookTb(word);
        updateStatisctics();
        if (!date.equals(SharedPreferencesUtils.getSharedPreferencesData(
                NewReadActivity.this, ShareKeys.STATISTICS_UPDATE_KEY + mPageWidget.getBookTitle()))) {
            doStatisctics();
        }
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (this, ShareKeys.CLOSE_TRANSLATE, false)) {
            return;
        }
        //使用SDK查词
        Translator.getInstance(tps).lookup(word, "requestId", new TranslateListener() {

            @Override
            public void onError(TranslateErrorCode code, String s) {
                Logger.d("error" + s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("网络连接失败");
                    }
                });
            }

            @Override
            public void onResult(final Translate translate, final String s, String s1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != translate && null != translate.getExplains() && translate.getExplains().size() > 0) {
                            showTranslateDialog(translate);
                        } else {
                            ToastUtils.showToast("没查到该词");
                        }
                    }
                });
            }

            @Override
            public void onResult(List<Translate> list, List<String> list1, List<TranslateErrorCode> list2, String s) {
                Logger.d(s);
            }
        });
    }

    private void showTranslateDialog(Translate translate) {
        TranslateResultDialog dialog = new TranslateResultDialog(this);
        dialog.setOnFreeTTSListener(new OnFreeTTSListener() {
            @Override
            public void onTTS(String word) {
                text2Speech(word);
            }
        });
        dialog.show();
        dialog.setTranslate(translate);
    }

    private void updateStatisctics() {
        //初始记录
        int queryC = db.queryStatisticsed(mPageWidget.getBookTitle());
        if (queryC > 0) {
            //如果这本书没有记录 那就update 并且time+1
            queryC++;
            db.updateStatistics(date, queryC, mPageWidget.getBookTitle(), mPageWidget.getCurrentPercent());
        } else {
            db.insertStatiscticsTb(date, date, "book", 1, mPageWidget.getBookSize(),
                    mPageWidget.getBookTitle(), mPageWidget.getCurrentPercent());
        }
    }

    private void doStatisctics() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        try {
            StatisticsBean item = db.queryStatisticsByBookName(mPageWidget.getBookTitle());
            AVObject object = new AVObject("Statistics");
            object.put("owner", LoginBean.getInstance().getUserName());
            object.put("query_word_c", item.getQuery_word_c());
            object.put("word_c", item.getWord_c());
            object.put("progress", item.getProgress());
            object.put("book_name", item.getBook_name());
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (LeanCloundUtil.handleLeanResult(NewReadActivity.this, e)) {
                        SharedPreferencesUtils.setSharedPreferencesData
                                (NewReadActivity.this, ShareKeys.STATISTICS_UPDATE_KEY + mPageWidget.getBookTitle(), date);
                    }
                }
            });
        } catch (Exception e) {
            //有可能空指针 不处理 不能阻断看书进程
        }
    }


    private void showTranslateResultDialog(final String title, String msg) {
        if (null == translateResultDialog) {
            translateResultDialog = new TranslateDialog(this);
            translateResultDialog.setOnSpeakClickListener(new OnSpeakClickListener() {
                @Override
                public void onSpeakClick(String word) {
                    text2Speech(word);
                }
            });
        }
        translateResultDialog.show();

        translateResultDialog.setTitle(title);
        translateResultDialog.setMessage(msg);
        translateResultDialog.setOkText("确定");
        translateResultDialog.setCancelable(true);
    }

    private void showReadDialog() {
        if (null == readDialog) {
            readDialog = new ReadDialog(this);
            readDialog.setOnReadDialogClickListener(new OnReadDialogClickListener() {
                @Override
                public void onSearchJumpClick() {
                    showSearchJumpDialog();
                }

                @Override
                public void onProgressJumpSelected(int progress) {
                    mPageWidget.setPercent(progress);
                }

                @Override
                public void onSunMoonToggleClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (NewReadActivity.this, ShareKeys.ISNIGHT,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(NewReadActivity.this,
                                            ShareKeys.ISNIGHT, false));
                    toggleDayNight();
                }

                @Override
                public void onTextSizeClick() {
                    showFontSizeSelectorDialog();
                }

                @Override
                public void onBackgroundStyleClick() {
                    showThemeSelectorDialog();
                }

                @Override
                public void onToggleTranslateWayClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (NewReadActivity.this, ShareKeys.DOUBLE_CLICK_TRANSLATE,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(NewReadActivity.this,
                                            ShareKeys.DOUBLE_CLICK_TRANSLATE, false));
                }

                @Override
                public void onCloseTranslateClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (NewReadActivity.this, ShareKeys.CLOSE_TRANSLATE,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(NewReadActivity.this,
                                            ShareKeys.CLOSE_TRANSLATE, false));
                }

                @Override
                public void onUserHeadClick() {
                    if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
                        Intent intent = new Intent(NewReadActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCloseDialogClick() {

                }

                @Override
                public void onToTXTClick() {

                }

                @Override
                public void onHelpClick() {

                }
            });
        }
        readDialog.show();
        readDialog.refreshUI();
        readDialog.initSeekBar((int) mPageWidget.getCurrentPercent());
    }

    private void toggleDayNight() {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(NewReadActivity.this,
                ShareKeys.ISNIGHT, false)) {
            SettingManager.getInstance().saveReadTheme(ThemeManager.NIGHT);
            mPageWidget.setTheme(ThemeManager.NIGHT);
            mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_night),
                    getResources().getColor(R.color.chapter_title_night));
        } else {
            mPageWidget.setTheme(SettingManager.getInstance().getReadTheme());
            mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_day),
                    getResources().getColor(R.color.chapter_title_day));
        }
    }

    private void showThemeSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                SettingManager.getInstance().saveReadTheme(position);
                if (position == ThemeManager.NIGHT) {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (NewReadActivity.this, ShareKeys.ISNIGHT,
                                    true);
                    toggleDayNight();
                    return;
                }
                SharedPreferencesUtils.setSharedPreferencesData
                        (NewReadActivity.this, ShareKeys.ISNIGHT,
                                false);
                toggleDayNight();
                mPageWidget.setTheme(position);
            }
        });
        listDialog.show();
        listDialog.setOptionsList(ThemeManager.THEME_LIST);
    }

    private void showFontSizeSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {
                mPageWidget.setFontSize(ScreenUtils.dpToPxInt(Integer.valueOf(selectedCodeRes)));
            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
            }
        });
        listDialog.show();
        listDialog.setOptionsList(SettingManager.FONT_SIZE_LIST);
        listDialog.setCodeList(SettingManager.FONT_SIZE_CODE_LIST);
    }

    private void showSearchJumpDialog() {
        MangaEditDialog dialog = new MangaEditDialog(this);
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SingleLoadBarUtil.getInstance().showLoadBar(NewReadActivity.this);
                mPageWidget.jumpToPositionBySearchText(text, new OnSearchResultListener() {
                    @Override
                    public void onSearchDone() {
                        ToastUtils.showSingleLongToast("查找完成");
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
                    }

                    @Override
                    public void onSearchFail() {
                        ToastUtils.showSingleLongToast("没找到!");
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
                    }
                });
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setHint("输入要查找的内容");
        dialog.setTitle("查找跳转");
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPageWidget != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);
                    mPageWidget.setBattery(100 - level);
                } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    mPageWidget.setTime(sdf.format(new Date()));
                }
            }
        }
    }

    private class ReadListener implements OnReadStateChangeListener {
        @Override
        public void onPageChanged(int page) {
            LogUtils.i("onPageChanged:" + "-" + page);
        }

        @Override
        public void onLoadFailure(String path) {
            LogUtils.i("onLoadChapterFailure:" + path);
        }

        @Override
        public void onCenterClick() {
            LogUtils.i("onCenterClick");
            //TODO
//            toggleReadBar();
        }

        @Override
        public void onFlip() {
            //TODO
//            hideReadBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            LogUtils.e("Receiver not registered");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        baseToast.showToast("没文件读取/写入授权,你让我怎么读取本地漫画?", true);
    }
}
