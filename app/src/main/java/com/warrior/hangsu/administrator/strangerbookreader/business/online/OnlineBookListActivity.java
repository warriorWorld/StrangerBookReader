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
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

/**
 * 个人信息页
 */
public class OnlineBookListActivity extends FragmentContainerActivity {
    private OnlineBooksTableFragment onlineBooksTableFragment;
    private String url, type;

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        type = intent.getStringExtra("type");
        onlineBooksTableFragment = new OnlineBooksTableFragment();
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBooksTableFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return type;
    }
}
