package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.RecommendListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.imageloader.AuthImageDownloader;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息页
 */
public class RecommendFragment extends BaseRefreshListFragment {
    private ArrayList<BookBean> recommendsList = new ArrayList<BookBean>();
    private RecommendListAdapter adapter;


    @Override
    protected int getReFreshFragmentLayoutId() {
        return 0;
    }

    @Override
    protected void onCreateInit() {
    }

    @Override
    protected void doGetData() {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Recommend");
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    recommendsList = new ArrayList<BookBean>();
                    if (null != list && list.size() > 0) {
                        BookBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new BookBean();
                            item.setName(list.get(i).getString("title"));
                            item.setBpPath(list.get(i).getString("img_url"));
                            item.setPath(list.get(i).getString("url"));
                            recommendsList.add(item);
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
        refreshRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return true;
    }

    @Override
    protected void initRec() {
        try {
            if (null == adapter) {
                adapter = new RecommendListAdapter(getActivity());
                adapter.setList(recommendsList);
                adapter.setNoMoreData(false);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        //TODO
                        ToastUtils.showSingleToast(recommendsList.get(position).getPath() + "\n图片地址" + recommendsList.get(position).getBpPath());
                    }
                });
                refreshRcv.setAdapter(adapter);
                refreshRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                adapter.setList(recommendsList);
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
