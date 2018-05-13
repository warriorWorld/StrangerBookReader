package com.warrior.hangsu.administrator.strangerbookreader.business.search;

import android.content.Intent;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.online.OnlineBooksTableFragment;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;

/**
 * 个人信息页
 */
public class SearchActivity extends FragmentContainerActivity {

    @Override
    protected void createInit() {
        Intent intent = getIntent();
    }


    @Override
    protected BaseFragment getFragment() {
        return null;
    }

    @Override
    protected String getTopBarTitle() {
        return "搜索";
    }

    @Override
    protected int getContainerLayoutId() {
        return 0;
    }
}
