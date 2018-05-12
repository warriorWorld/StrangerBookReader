package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.content.Intent;

import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;

/**
 * 个人信息页
 */
public class OnlineBookDetailActivity extends FragmentContainerActivity {
    private OnlineBookDetailFragment onlineBookDetailFragment;
    private String url, spider;

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        spider = intent.getStringExtra("spider");
        onlineBookDetailFragment = new OnlineBookDetailFragment();
        onlineBookDetailFragment.setUrl(url);
        onlineBookDetailFragment.setSpiderName(spider);
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBookDetailFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return "详情";
    }
}
