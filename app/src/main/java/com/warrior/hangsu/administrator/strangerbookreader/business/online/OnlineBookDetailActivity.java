package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.content.Intent;

import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;

/**
 * 个人信息页
 */
public class OnlineBookDetailActivity extends FragmentContainerActivity {
    private OnlineBooksTableFragment onlineBooksTableFragment;
    private String url;

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        onlineBooksTableFragment = new OnlineBooksTableFragment();
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBooksTableFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return "详情";
    }
}
