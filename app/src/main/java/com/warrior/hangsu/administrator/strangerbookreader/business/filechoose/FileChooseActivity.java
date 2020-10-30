package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.FileChooseAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;

import java.util.ArrayList;


public class FileChooseActivity extends BaseActivity implements View.OnClickListener {
    private FileChooseAdapter mAdapter;
    private ArrayList<FileBean> fileList = new ArrayList<>();
    private Button doneBtn;
    private RecyclerView fileRcv;
    private TextView sizeTv;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                ArrayList<FileBean> result = mAdapter.getSelectedList();
                Intent intent = new Intent();
                intent.putExtra("selectedList", result);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    public enum SelectAllState {
        SELECT_ALL,
        CANCEL_ALL
    }

    private SelectAllState mSelectAllState = SelectAllState.CANCEL_ALL;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_chooser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        doGetData();
    }

    private void initUI() {
        doneBtn = (Button) findViewById(R.id.ok_btn);
        sizeTv = (TextView) findViewById(R.id.file_size_tv);
        fileRcv = (RecyclerView) findViewById(R.id.file_rcv);
        fileRcv.setLayoutManager(new LinearLayoutManager(this));
        fileRcv.setFocusableInTouchMode(false);
        fileRcv.setFocusable(false);
        fileRcv.setHasFixedSize(true);
        baseTopBar.setTitle("选择书籍");
        baseTopBar.setRightText("全选");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                switch (mSelectAllState) {
                    case CANCEL_ALL:
                        mAdapter.selectAll();
                        mSelectAllState = SelectAllState.SELECT_ALL;
                        baseTopBar.setRightText("取消全选");
                        break;
                    case SELECT_ALL:
                        mAdapter.removeAllSelected();
                        mSelectAllState = SelectAllState.CANCEL_ALL;
                        baseTopBar.setRightText("全选");
                        break;
                }
            }

            @Override
            public void onTitleClick() {

            }
        });
        doneBtn.setOnClickListener(this);
    }

    private void doGetData() {
        fileList = FileUtils.getFilesByType(this, Globle.BOOK_PATH, FileUtils.TYPE_BOOK);
        sizeTv.setText(fileList.size() + "");
        initRec();
    }

    private void initRec() {
        try {
            if (null == mAdapter) {
                mAdapter = new FileChooseAdapter(this);
                mAdapter.setList(fileList);
                fileRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(fileList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
