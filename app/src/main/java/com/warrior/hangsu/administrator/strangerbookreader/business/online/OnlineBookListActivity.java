package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

/**
 * 个人信息页
 */
public class OnlineBookListActivity extends FragmentContainerActivity {
    private OnlineBooksTableFragment onlineBooksTableFragment;
    private String url, bookType, spider;
    private String[] optionsList = {"搜索", "跳转"};

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        bookType = intent.getStringExtra("type");
        spider = intent.getStringExtra("spider");
        onlineBooksTableFragment = new OnlineBooksTableFragment();
        onlineBooksTableFragment.setUrl(url);
        onlineBooksTableFragment.setBookType(bookType);
        onlineBooksTableFragment.setSpiderName(spider);

        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                OnlineBookListActivity.this.finish();
            }

            @Override
            public void onRightClick() {
                showOptionsSelectorDialog();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void showOptionsSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(optionsList);
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBooksTableFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return bookType;
    }

    @Override
    protected int getContainerLayoutId() {
        return 0;
    }
}
