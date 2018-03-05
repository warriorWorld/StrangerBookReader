package com.warrior.hangsu.administrator.strangerbookreader.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Administrator on 2016/10/5.
 */
public class EpubReadDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private OnReadDialogClickListener onReadDialogClickListener;
    private LinearLayout closeTranslateLl;
    private TextView closeTranslateTv;
    private ImageView userHeadIv;
    private TextView userNameTv;
    private DiscreteSeekBar progressSeekBar;
    private RelativeLayout closeDialogRl;
    private LinearLayout headLl;
    private LinearLayout toTXTLl;
    private boolean isProgressChange = false;

    public EpubReadDialog(Context context) {
        super(context);
//        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_epub);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(lp);
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay(); // 闁兼儳鍢茶ぐ鍥╀沪韫囨挾顔庨悗鐟邦潟閿熸垝绶氶悵顕�鎮介敓锟�
        lp.width = (int) (d.getWidth() * 1); // 閻庣妫勭�瑰磭鎷嬮崜褏鏋�
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimationDialog);
        window.setBackgroundDrawableResource(android.R.color.transparent);
//        lp.y = DisplayUtil.dip2px(context, 50); // 闁哄倿顣︾紞鍛磾閻㈡洟宕搁幇顓犲灱
        window.setAttributes(lp);

        refreshUI();
    }

    private void init() {
        closeTranslateLl = (LinearLayout) findViewById(R.id.close_translate_ll);
        closeTranslateTv = (TextView) findViewById(R.id.close_translate_tv);
        userHeadIv = (ImageView) findViewById(R.id.user_head_iv);
        userNameTv = (TextView) findViewById(R.id.user_name_tv);
        progressSeekBar = (DiscreteSeekBar) findViewById(R.id.progress_seek_bar);
        closeDialogRl = (RelativeLayout) findViewById(R.id.close_dialog_rl);
        headLl = (LinearLayout) findViewById(R.id.head_ll);
        toTXTLl = (LinearLayout) findViewById(R.id.to_txt_ll);

        toTXTLl.setOnClickListener(this);
        closeTranslateLl.setOnClickListener(this);
        closeDialogRl.setOnClickListener(this);
        headLl.setOnClickListener(this);
    }

    /**
     * 进度条
     */
    public void initSeekBar(int progress, int max) {
        progressSeekBar.setMin(1);
        progressSeekBar.setMax(max);
        progressSeekBar.setProgress(progress);
        progressSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                isProgressChange = true;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (seekBar.getProgress() > 0 && isProgressChange) {
                    isProgressChange = false;
                    if (null != onReadDialogClickListener) {
                        onReadDialogClickListener.onProgressJumpSelected(seekBar.getProgress());
                    }
                    dismiss();
                }
            }
        });
    }

    public void refreshUI() {
        if (!TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            setUserName(LoginBean.getInstance().getUserName());
        } else {
            setUserName("立即登录");
        }
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(context,
                ShareKeys.CLOSE_TRANSLATE, false)) {
            closeTranslateTv.setText("开启查词");
        } else {
            closeTranslateTv.setText("关闭查词");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_translate_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onCloseTranslateClick();
                }
                break;
            case R.id.head_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onUserHeadClick();
                }
                break;
            case R.id.to_txt_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onToTXTClick();
                }
                break;
            case R.id.close_dialog_rl:
                break;
        }
        dismiss();
    }

    public void setUserName(String name) {
        userNameTv.setText(name);
    }

    public void setOnReadDialogClickListener(OnReadDialogClickListener onReadDialogClickListener) {
        this.onReadDialogClickListener = onReadDialogClickListener;
    }
}
