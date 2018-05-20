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
    protected int page = 1;
    private boolean isAutoBottomLoadMore = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v;
        if (getReFreshFragmentLayoutId() == 0) {
            v = inflater.inflate(R.layout.activity_only_refresh_recycler, container, false);
        } else {
            v = inflater.inflate(getReFreshFragmentLayoutId(), container, false);
        }
        onCreateInit();
        initUI(v);
        doGetData();
        return v;
    }

    public void setAutoBottomLoadMore(boolean autoBottomLoadMore) {
        isAutoBottomLoadMore = autoBottomLoadMore;
    }

    protected abstract int getReFreshFragmentLayoutId();

    protected abstract void onCreateInit();

    protected abstract void doGetData();

    protected abstract String getType();

    protected abstract boolean initRcvLayoutManger();

    protected void initUI(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        refreshRcv = (RecyclerView) v.findViewById(R.id.swipe_target);
        if (!initRcvLayoutManger()) {
            refreshRcv.setLayoutManager
                    (new LinearLayoutManager
                            (getActivity(), LinearLayoutManager.VERTICAL, false));
        }
        refreshRcv.setFocusableInTouchMode(false);
        refreshRcv.setFocusable(false);
        refreshRcv.setHasFixedSize(true);
        refreshRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView)) {
                    if (isAutoBottomLoadMore) {
                        onLoadMore();
                    }
                }
            }
        });
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
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
        page = 1;
        doGetData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
