package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.support.v7.widget.DefaultItemAnimator;


import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;

import java.util.ArrayList;

/**
 * 个人信息页
 */
public  class BooksTableFragment extends BaseRefreshListFragment {
    protected ArrayList<NoticeListBean> noticeList = new ArrayList<>();
    protected NoticeBean mainItem;
    private BookListRecyclerListAdapter adapter;

    @Override
    protected abstract void doGetData();

    @Override
    protected abstract String getType();

    @Override
    protected void initRec() {
        try {
            if (page > 0) {//智融钥匙的page是从0开始的
                //如果不是首页 那就加上之后的
                noticeList.addAll(mainItem.getNotice_list());
            } else {
                if (null != mainItem) {
                    noticeList = mainItem.getNotice_list();
                }
            }
            if (null == adapter) {
                adapter = new NoticeAdapter(getActivity());
                adapter.setNoticeList(noticeList);
                if ("1".equals(getType())) {
                    adapter.setEmptyText("暂无公告");
                } else {
                    adapter.setEmptyText("暂无提醒");
                }
                refreshRcv.setAdapter(adapter);
                refreshRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                adapter.setNoticeList(noticeList);
                adapter.notifyDataSetChanged();
            }
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        } catch (Exception e) {
            if (UrlAddress.isTest) {
            }
        }
    }

}
