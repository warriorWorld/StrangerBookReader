package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.OnlineBookRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.MainBookBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.spider.SpiderBase;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 个人信息页
 */
public class OnlineBooksTableFragment extends BaseRefreshListFragment {
    private MainBookBean mainBookBean;
    private ArrayList<BookBean> booksList = new ArrayList<BookBean>();
    private OnlineBookRecyclerListAdapter adapter;
    private String url, bookType, spiderName;
    private SpiderBase spider;
    private TextView currentPageTv;

    @Override
    protected int getReFreshFragmentLayoutId() {
        return R.layout.fragment_online_book_list;
    }

    @Override
    protected void onCreateInit() {
        initSpider(spiderName);
    }

    @Override
    protected void initUI(View v) {
        super.initUI(v);
        currentPageTv = (TextView) v.findViewById(R.id.current_page_tv);
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
        spider.getBookList(url, page + "", new JsoupCallBack<MainBookBean>() {
            @Override
            public void loadSucceed(final MainBookBean result) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentPageTv.setText(page + "");
                        mainBookBean = result;
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
            if (page > 1) {
                //如果不是首页 那就加上之后的
                booksList.addAll(mainBookBean.getBook_list());
            } else {
                booksList = mainBookBean.getBook_list();
            }

            if (null == adapter) {
                adapter = new OnlineBookRecyclerListAdapter(getActivity());
                adapter.setList(booksList);
                adapter.setNoMoreData(false);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), OnlineBookDetailActivity.class);
                        intent.putExtra("spider", spiderName);
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

    public String getBookType() {
        return bookType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }
}
