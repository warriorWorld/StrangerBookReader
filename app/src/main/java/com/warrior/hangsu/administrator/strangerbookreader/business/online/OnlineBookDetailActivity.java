package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.content.Intent;

import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;

import nl.siegmann.epublib.domain.Book;

/**
 * 个人信息页
 */
public class OnlineBookDetailActivity extends FragmentContainerActivity {
    private OnlineBookDetailFragment onlineBookDetailFragment;
    private String spider;
    private BookBean bookBean;

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        spider = intent.getStringExtra("spider");
        bookBean = (BookBean) intent.getSerializableExtra("bookBean");
        onlineBookDetailFragment = new OnlineBookDetailFragment();
        onlineBookDetailFragment.setSpiderName(spider);
        onlineBookDetailFragment.setMainBean(bookBean);
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBookDetailFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return bookBean.getName();
    }
}
