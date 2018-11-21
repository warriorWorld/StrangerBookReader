package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookCollectListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.OnlineBookRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.MainBookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.online.OnlineBookDetailActivity;
import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.spider.SpiderBase;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaEditDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息页
 */
public class CollectBooksTableFragment extends BaseRefreshListFragment {
    private ArrayList<BookBean> booksList = new ArrayList<BookBean>();
    private BookCollectListRecyclerListAdapter adapter;

    @Override
    protected int getReFreshFragmentLayoutId() {
        return 0;
    }

    @Override
    protected void onCreateInit() {
    }

    @Override
    protected void initUI(View v) {
        super.initUI(v);
        swipeToLoadLayout.setLoadMoreEnabled(false);
    }

    @Override
    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            initRec();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Collected");
        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    booksList = new ArrayList<BookBean>();
                    if (null != list && list.size() > 0) {
                        BookBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new BookBean();
                            item.setName(list.get(i).getString("bookName"));
                            item.setPath(list.get(i).getString("bookUrl"));
                            item.setSpider(list.get(i).getString("spider"));
                            booksList.add(item);
                        }
                    }
                    initRec();
                }
            }
        });
    }

    @Override
    protected String getType() {
        return "";
    }

    @Override
    protected boolean initRcvLayoutManger() {
        return false;
    }

    @Override
    protected void initRec() {
        SingleLoadBarUtil.getInstance().dismissLoadBar();
        try {
            if (null == adapter) {
                adapter = new BookCollectListRecyclerListAdapter(getActivity());
                adapter.setList(booksList);
                adapter.setNoMoreData(true);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), OnlineBookDetailActivity.class);
                        intent.putExtra("spider", booksList.get(position).getSpider());
                        intent.putExtra("bookBean", (Serializable) booksList.get(position));
                        startActivity(intent);
                    }
                });
                adapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
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
