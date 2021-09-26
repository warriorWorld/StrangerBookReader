package com.warrior.hangsu.administrator.strangerbookreader.business.other;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.TTSActivity;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnTTSDialogResultListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ActivityPoor;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseParameterUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileSizeUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.DownloadDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.TTSDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class AboutActivity extends TTSActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private ImageView appIconIv;
    private TextView versionTv;
    private RelativeLayout checkUpdateRl;
    private CheckBox closeTranslateCb;
    private RelativeLayout authorRl;
    private RelativeLayout feedbackRl;
    private TextView logoutTv;
    private RelativeLayout backgroundStyleRl;
    private TextView backgroundStyleTv;
    private RelativeLayout translateWayRl;
    private TextView translateWayTv;
    private RelativeLayout textSizeRl;
    private TextView textSizeTv;
    //版本更新
    private String versionName, msg;
    private int versionCode;
    private boolean forceUpdate;
    private AVFile downloadFile;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;
    private final String[] TRANSLATE_WAY_LIST = {"单击", "双击"};
    private CheckBox closeTTSCb;
    private RelativeLayout ttsPitchRl;
    private TextView ttsPitchTv;
    private RelativeLayout clean_cache_rl;
    private TextView cache_size_tv;
    private CheckBox openPremiumCb;
    private CheckBox openTTSTranslateCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
//        doGetVersionInfo();
    }

    private void initUI() {
        appIconIv = (ImageView) findViewById(R.id.app_icon_iv);
        versionTv = (TextView) findViewById(R.id.version_tv);
        checkUpdateRl = (RelativeLayout) findViewById(R.id.check_update_rl);
        closeTranslateCb = (CheckBox) findViewById(R.id.close_translate_cb);
        closeTranslateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TRANSLATE, isChecked);
            }
        });
        closeTTSCb = (CheckBox) findViewById(R.id.close_tts_cb);
        closeTTSCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TTS_KEY, isChecked);
            }
        });
        authorRl = (RelativeLayout) findViewById(R.id.author_rl);
        feedbackRl = (RelativeLayout) findViewById(R.id.feedback_rl);
        logoutTv = (TextView) findViewById(R.id.logout_tv);

        closeTranslateCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TRANSLATE, false));
        closeTTSCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TTS_KEY, false));
        openPremiumCb = (CheckBox) findViewById(R.id.open_premium_cb);
        openPremiumCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.OPEN_PREMIUM_KEY, isChecked);
            }
        });
        openPremiumCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(this,
                        ShareKeys.OPEN_PREMIUM_KEY, false));
        openTTSTranslateCb = (CheckBox) findViewById(R.id.open_tts_translate_cb);
        openTTSTranslateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.OPEN_TTS_TRANSLATE_KEY, isChecked);
            }
        });
        openTTSTranslateCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(this,
                        ShareKeys.OPEN_TTS_TRANSLATE_KEY, false));
        backgroundStyleRl = (RelativeLayout) findViewById(R.id.background_style_rl);
        backgroundStyleTv = (TextView) findViewById(R.id.background_style_tv);
        translateWayRl = (RelativeLayout) findViewById(R.id.translate_way_rl);
        translateWayTv = (TextView) findViewById(R.id.translate_way_tv);
        textSizeRl = (RelativeLayout) findViewById(R.id.text_size_rl);
        textSizeTv = (TextView) findViewById(R.id.text_size_tv);
        ttsPitchRl = (RelativeLayout) findViewById(R.id.tts_pitch_rl);
        ttsPitchTv = (TextView) findViewById(R.id.tts_pitch_tv);
        clean_cache_rl = (RelativeLayout) findViewById(R.id.clean_cache_rl);
        cache_size_tv = (TextView) findViewById(R.id.cache_size_tv);

        clean_cache_rl.setOnClickListener(this);
        ttsPitchRl.setOnClickListener(this);
        backgroundStyleRl.setOnClickListener(this);
        translateWayRl.setOnClickListener(this);
        textSizeRl.setOnClickListener(this);
        checkUpdateRl.setOnClickListener(this);
        authorRl.setOnClickListener(this);
        feedbackRl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        baseTopBar.setTitle("设置");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    private void refreshUI() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            logoutTv.setVisibility(View.GONE);
        } else {
            logoutTv.setVisibility(View.VISIBLE);
        }
        backgroundStyleTv.setText(ThemeManager.THEME_LIST[SettingManager.getInstance().getReadTheme()]);
        textSizeTv.setText(SettingManager.getInstance().getFontSizeExplain());
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(this,
                ShareKeys.DOUBLE_CLICK_TRANSLATE, false)) {
            translateWayTv.setText("双击查词");
        } else {
            translateWayTv.setText("单击查词");
        }
        cache_size_tv.setText(NumberUtil.cutNum(FileSizeUtil.getFileOrFilesSize(Globle.CACHE_PATH, FileSizeUtil.SIZETYPE_MB)) + "MB");
        versionTv.setText(BaseParameterUtil.getInstance().getAppVersionName(AboutActivity.this));
    }

    private void doGetVersionInfo() {
        SingleLoadBarUtil.getInstance().showLoadBar(AboutActivity.this);
        AVQuery<AVObject> query = new AVQuery<>("VersionInfo");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(AboutActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        versionName = list.get(0).getString("versionName");
                        versionCode = list.get(0).getInt("versionCode");
                        forceUpdate = list.get(0).getBoolean("forceUpdate");
                        msg = list.get(0).getString("description");
                        downloadFile = list.get(0).getAVFile("apk");
                        if (versionCode == BaseParameterUtil.getInstance().getAppVersionCode(AboutActivity.this)) {
                            versionTv.setText(BaseParameterUtil.getInstance().getAppVersionName(AboutActivity.this)
                                    + "(最新版本)");
                            ToastUtils.showSingleToast("已经是最新版本啦~");
                        } else {
                            versionTv.setText(BaseParameterUtil.getInstance().getAppVersionName(AboutActivity.this)
                                    + "(有新版本啦~)");
                            showVersionDialog();
                        }
                    }
                }
            }
        });
    }

    @AfterPermissionGranted(111)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            final String filePath = Globle.DOWNLOAD_PATH + "/apk";
            final File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            downloadFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    // bytes 就是文件的数据流
                    if (null != downloadDialog && downloadDialog.isShowing()) {
                        downloadDialog.dismiss();
                    }
                    if (LeanCloundUtil.handleLeanResult(AboutActivity.this, e)) {
                        File apkFile = FileUtils.byte2File(bytes, filePath, "english_book_reader.apk");

                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                    downloadDialog.setProgress(integer);
                }
            });

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void doLogout() {
        AVUser.getCurrentUser().logOut();
        LoginBean.getInstance().clean(this);
        refreshUI();
        ToastUtils.showSingleToast("退出成功!");
    }

    private void showVersionDialog() {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(AboutActivity.this);
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    versionDialog.dismiss();
                    doDownload();
                }

                @Override
                public void onCancelClick() {
                }
            });
        }
        versionDialog.show();

        versionDialog.setTitle("有新版本啦" + "v_" + versionName);
        versionDialog.setMessage(msg);
        versionDialog.setOkText("升级");
        versionDialog.setCancelable(true);
        versionDialog.setCancelText("取消");
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(this);
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    private void showLogoutDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                doLogout();
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否退出登录?");
        dialog.setOkText("是");
        dialog.setCancelText("否");
    }

    private void showAuthorDialog() {
        MangaDialog authorDialog = new MangaDialog(this);
        authorDialog.show();
        authorDialog.setTitle("联系作者");
        authorDialog.setOkText("知道了");
        authorDialog.setMessage("作者:  苏航\n邮箱:  772192594@qq.com");
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
                            (AboutActivity.this, ShareKeys.ISNIGHT,
                                    true);
                    return;
                }
                refreshUI();
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
                SettingManager.getInstance().saveFontSize(ScreenUtils.dpToPxInt(Integer.valueOf(selectedCodeRes)));
                refreshUI();
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

    private void showTranslateWaySelectorDialog() {
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
                switch (position) {
                    case 0:
                        SharedPreferencesUtils.setSharedPreferencesData
                                (AboutActivity.this, ShareKeys.DOUBLE_CLICK_TRANSLATE,
                                        false);
                        break;
                    case 1:
                        SharedPreferencesUtils.setSharedPreferencesData
                                (AboutActivity.this, ShareKeys.DOUBLE_CLICK_TRANSLATE,
                                        true);
                        break;
                }
                refreshUI();
            }
        });
        listDialog.show();
        listDialog.setOptionsList(TRANSLATE_WAY_LIST);
    }

    private void showCleanCacheDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                try {
                    FileUtils.deleteFile(new File(Globle.CACHE_PATH + "/"));
                    ToastUtils.showSingleToast("清理完成");
                    refreshUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否清理缓存");
        dialog.setOkText("是");
        dialog.setCancelText("否");
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.check_update_rl:
                doGetVersionInfo();
                break;
            case R.id.author_rl:
                showAuthorDialog();
                break;
            case R.id.feedback_rl:
                intent = new Intent(AboutActivity.this, FeedbackActivity.class);
                break;
            case R.id.logout_tv:
                showLogoutDialog();
                break;
            case R.id.background_style_rl:
                showThemeSelectorDialog();
                break;
            case R.id.text_size_rl:
                showFontSizeSelectorDialog();
                break;
            case R.id.translate_way_rl:
                showTranslateWaySelectorDialog();
                break;
            case R.id.tts_pitch_rl:
                showTTSDialog();
                break;
            case R.id.clean_cache_rl:
                showCleanCacheDialog();
                break;
        }
        if (null != intent) {
            startActivity(intent);
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
        ToastUtils.showSingleToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (111 == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(AboutActivity.this);
            peanutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    ActivityPoor.finishAllActivity();
                }

                @Override
                public void onCancelClick() {

                }
            });
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试!");
        }
    }

    private void showTTSDialog() {
        TTSDialog dialog = new TTSDialog(this);
        dialog.setOnTTSDialogResultListener(new OnTTSDialogResultListener() {
            @Override
            public void onTTSModifyDone(String text, float result) {
                text2Speech(text);
            }
        });
        dialog.show();
    }
}
