package com.warrior.hangsu.administrator.strangerbookreader.business.read;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.strangerbookreader.business.readview.BaseReadView;
import com.warrior.hangsu.administrator.strangerbookreader.business.readview.OverlappedWidget;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnWordClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LogUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtil;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyTool;

import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/2/12.
 */

public class NewReadActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks {
    private BaseReadView mPageWidget;
    private FrameLayout readWidgetFl;
    private ClipboardManager clip;//复制文本用
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        initPagerWidget();
        initDialog();
    }

    private void initUI() {
        readWidgetFl = (FrameLayout) findViewById(R.id.read_widget_fl);
        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @AfterPermissionGranted(111)
    private void initPagerWidget() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            mPageWidget = new OverlappedWidget(this, Globle.nowBookPath, new ReadListener());

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
            readWidgetFl.removeAllViews();
            readWidgetFl.addView(mPageWidget);


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    mPageWidget.init(ThemeManager.NORMAL);
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
                        ToastUtil.tipShort(NewReadActivity.this, "没查到该词");
                    }
                } else {
                    ToastUtil.tipShort(NewReadActivity.this, "网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(NewReadActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                NewReadActivity.this, url, params,
                YoudaoResponse.class, callback);

    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
        dialog.setCancelable(true);
    }

    private void showOnlyOkDialog(String title, String msg) {
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
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
