package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.support.v7.widget.DefaultItemAnimator;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.ChapterListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.OnlineBookRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.ChapterListBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.MainBookBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.spider.SpiderBase;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;

/**
 * 个人信息页
 */
public class OnlineBookDetailFragment extends BaseRefreshListFragment {
    private ArrayList<ChapterListBean> chapterList = new ArrayList<>();
    private ChapterListAdapter adapter;
    private String url, spiderName;
    private SpiderBase spider;

    @Override
    protected int getReFreshFragmentLayoutId() {
        return R.layout.fragment_book_detail;
    }

    @Override
    protected void onCreateInit() {
        initSpider(spiderName);
    }

    private void initSpider(String spiderName) {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.warrior.hangsu.administrator.strangerbookreader.spider." + spiderName + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            ToastUtils.showSingleToast(e + "");
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            ToastUtils.showSingleToast(e + "");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ToastUtils.showSingleToast(e + "");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGetData() {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        spider.getBookDetail(url, new JsoupCallBack<BookBean>() {
            @Override
            public void loadSucceed(final BookBean result) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chapterList = result.getChapterList();
                        initRec();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
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
                adapter = new ChapterListAdapter(getActivity());
                adapter.setList(chapterList);
                adapter.setNoMoreData(true);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ToastUtils.showSingleToast(chapterList.get(position).getUrl());
                    }
                });
                refreshRcv.setAdapter(adapter);
                refreshRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                adapter.setList(chapterList);
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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }
}
