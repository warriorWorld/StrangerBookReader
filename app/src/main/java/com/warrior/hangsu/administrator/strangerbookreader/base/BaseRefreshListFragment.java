package com.warrior.hangsu.administrator.strangerbookreader.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.warrior.hangsu.administrator.strangerbookreader.R;

/**
 * 个人信息页
 */
public abstract class BaseRefreshListFragment extends BaseFragment implements View.OnClickListener,
        OnRefreshListener,
        OnLoadMoreListener {
    protected RecyclerView refreshRcv;
    protected SwipeToLoadLayout swipeToLoadLayout;
    protected int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_only_refresh_recycler, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    protected abstract void doGetData();

    protected abstract String getType();

    private void initUI(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        refreshRcv = (RecyclerView) v.findViewById(R.id.swipe_target);
        refreshRcv.setLayoutManager
                (new LinearLayoutManager
                        (getActivity(), LinearLayoutManager.VERTICAL, false));
        refreshRcv.setFocusableInTouchMode(false);
        refreshRcv.setFocusable(false);
        refreshRcv.setHasFixedSize(true);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
    }

    protected abstract void initRec();

    protected void noMoreDate() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onLoadMore() {
        page++;
        doGetData();
    }

    @Override
    public void onRefresh() {
        page = 0;
        doGetData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
