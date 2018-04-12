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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnTTSDialogResultListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Administrator on 2016/10/5.
 */
public class TTSDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private RelativeLayout closeDialogRl;
    private EditText ttsEt, ttsPitchEt;
    private OnTTSDialogResultListener onTTSDialogResultListener;

    public TTSDialog(Context context) {
        super(context);
//        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tts);
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
        ttsEt = (EditText) findViewById(R.id.test_tts_et);
        ttsPitchEt = (EditText) findViewById(R.id.tts_pitch_et);
        closeDialogRl = (RelativeLayout) findViewById(R.id.close_dialog_rl);
        closeDialogRl.setOnClickListener(this);
    }


    public void refreshUI() {
        ttsEt.setText(SharedPreferencesUtils.getSharedPreferencesData(context, ShareKeys.TTS_TEST_TEXT));
        ttsPitchEt.setText(SharedPreferencesUtils.getFloatSharedPreferencesData(context, ShareKeys.TTS_PITCH_KEY)+"");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_dialog_rl:
                if (null != onTTSDialogResultListener && !TextUtils.isEmpty(ttsEt.getText().toString().trim())
                        && !TextUtils.isEmpty(ttsPitchEt.getText().toString().trim())) {
                    SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.TTS_TEST_TEXT, ttsEt.getText().toString().trim());
                    SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.TTS_PITCH_KEY, Float.valueOf(ttsPitchEt.getText().toString().trim()));
                    onTTSDialogResultListener.onTTSModifyDone(ttsEt.getText().toString().trim(),
                            Float.valueOf(ttsPitchEt.getText().toString().trim()));
                }
                break;
        }
        dismiss();
    }

    public void setOnTTSDialogResultListener(OnTTSDialogResultListener onTTSDialogResultListener) {
        this.onTTSDialogResultListener = onTTSDialogResultListener;
    }
}
