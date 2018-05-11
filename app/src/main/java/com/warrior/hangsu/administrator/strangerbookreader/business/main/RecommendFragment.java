package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;

import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.RecommendListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.epub.EpubActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnEmptyBtnListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 个人信息页
 */
public class RecommendFragment extends BaseRefreshListFragment {
    private ArrayList<BookBean> booksList = new ArrayList<BookBean>();
    private RecommendListAdapter adapter;

    @Override
    protected void onCreateInit() {
    }

    @Override
    protected void doGetData() {

    }

    @Override
    protected String getType() {
        return "";
    }

    @Override
    protected void initRec() {
        try {
            if (null == adapter) {
                adapter = new RecommendListAdapter(getActivity());
                adapter.setList(booksList);
                adapter.setNoMoreData(true);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        //TODO
                    }
                });
                refreshRcv.setAdapter(adapter);
                refreshRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                adapter.setList(booksList);
                adapter.notifyDataSetChanged();
            }
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
