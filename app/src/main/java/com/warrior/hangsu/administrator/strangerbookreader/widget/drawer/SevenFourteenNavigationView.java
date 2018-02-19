package com.warrior.hangsu.administrator.strangerbookreader.widget.drawer;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.widget.imageview.CircleImage;


/**
 * Created by Administrator on 2017/8/18.
 */

public class SevenFourteenNavigationView extends RelativeLayout implements OnClickListener {
    private Context context;
    private OnNavigationItemClickListener onNavigationItemClickListener;
    private Button loginBtn;
    private RelativeLayout loggedRl;
    private CircleImage userHeadIv;
    private TextView userNameTv;
    private LinearLayout newWordLl;
    private LinearLayout statisticsLl;
    private LinearLayout shareAppLl;
    private LinearLayout englishAdLl;
    private LinearLayout settingsLl;

    public SevenFourteenNavigationView(Context context) {
        this(context, null);
    }

    public SevenFourteenNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SevenFourteenNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_navigation, this);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loggedRl = (RelativeLayout) findViewById(R.id.logged_rl);
        userHeadIv = (CircleImage) findViewById(R.id.user_head_iv);
        userNameTv = (TextView) findViewById(R.id.user_name_tv);
        newWordLl = (LinearLayout) findViewById(R.id.new_word_ll);
        statisticsLl = (LinearLayout) findViewById(R.id.statistics_ll);
        shareAppLl = (LinearLayout) findViewById(R.id.share_app_ll);
        englishAdLl = (LinearLayout) findViewById(R.id.english_ad_ll);
        settingsLl = (LinearLayout) findViewById(R.id.settings_ll);

        loginBtn.setOnClickListener(this);
        newWordLl.setOnClickListener(this);
        statisticsLl.setOnClickListener(this);
        shareAppLl.setOnClickListener(this);
        englishAdLl.setOnClickListener(this);
        settingsLl.setOnClickListener(this);
        loggedRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (null == onNavigationItemClickListener) {
            return;
        }
        switch (v.getId()) {
            case R.id.login_btn:
                onNavigationItemClickListener.onLoginBtnClick();
                break;
            case R.id.new_word_ll:
                onNavigationItemClickListener.onNewWordClick();
                break;
            case R.id.statistics_ll:
                onNavigationItemClickListener.onStatisticsClick();
                break;
            case R.id.share_app_ll:
                onNavigationItemClickListener.onShare_appClick();
                break;
            case R.id.english_ad_ll:
                onNavigationItemClickListener.onEnglishAdClick();
                break;
            case R.id.settings_ll:
                onNavigationItemClickListener.onOptionsClick();
                break;
            case R.id.logged_rl:
                onNavigationItemClickListener.onUserInfoClick();
                break;
        }
    }

    public void setUserName(String userName) {
        userNameTv.setText(userName);
        toggleLogged(!TextUtils.isEmpty(userName));
    }


    public void toggleLogged(boolean logged) {
        if (logged) {
            loggedRl.setVisibility(VISIBLE);
            loginBtn.setVisibility(GONE);
        } else {
            loggedRl.setVisibility(GONE);
            loginBtn.setVisibility(VISIBLE);
        }
    }


    public void setOnNavigationItemClickListener(OnNavigationItemClickListener onNavigationItemClickListener) {
        this.onNavigationItemClickListener = onNavigationItemClickListener;
    }

    public interface OnNavigationItemClickListener {
        void onLoginBtnClick();

        void onUserInfoClick();

        void onNewWordClick();

        void onStatisticsClick();

        void onShare_appClick();

        void onEnglishAdClick();

        void onOptionsClick();
    }
}
