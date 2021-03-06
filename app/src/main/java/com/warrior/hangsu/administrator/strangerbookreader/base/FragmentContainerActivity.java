package com.warrior.hangsu.administrator.strangerbookreader.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.warrior.hangsu.administrator.strangerbookreader.R;


/**
 * 投资记录页
 */
public abstract class FragmentContainerActivity extends TTSFragmentActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createInit();
        initUI();
    }

    protected abstract void createInit();

    protected abstract BaseFragment getFragment();

    protected abstract String getTopBarTitle();

    protected void initUI() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getFragment());
        transaction.commit();

        baseTopBar.setTitle(getTopBarTitle());
    }

    protected abstract int getContainerLayoutId();

    @Override
    protected int getLayoutId() {
        if (getContainerLayoutId() == 0) {
            return R.layout.activity_container;
        } else {
            return getContainerLayoutId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

}
