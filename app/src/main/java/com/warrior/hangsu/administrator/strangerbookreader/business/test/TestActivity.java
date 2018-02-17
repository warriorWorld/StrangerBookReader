package com.warrior.hangsu.administrator.strangerbookreader.business.test;

import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.business.readview.BaseReadView;
import com.warrior.hangsu.administrator.strangerbookreader.business.readview.OverlappedWidget;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LogUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtil;

/**
 * Created by Administrator on 2018/2/12.
 */

public class TestActivity extends BaseActivity {
    private BaseReadView mPageWidget;
    private FrameLayout readWidgetFl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initUI();
        initPagerWidget();
    }

    private void initUI() {
        readWidgetFl = (FrameLayout) findViewById(R.id.read_widget_fl);
    }

    private void initPagerWidget() {
        mPageWidget = new OverlappedWidget(this, Globle.nowBookPath, new ReadListener());

        if (SharedPreferencesUtil.getInstance().getBoolean(ShareKeys.ISNIGHT, false)) {
            mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_night),
                    getResources().getColor((R.color.chapter_title_night)));
        }
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
        }, 1500);//n秒后执行Runnable中的run方法

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
}
