package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookStatisticsListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息页
 */
public class BookStatisticsFragment extends BaseFragment implements View.OnClickListener {
    protected ArrayList<StatisticsBean> data_list = new ArrayList<>();
    private ArrayList<StatisticsBean> handled_list = new ArrayList<>();
    private RecyclerView calendarStatisticsRcv;
    private View emptyView;
    private BookStatisticsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_book_statistics, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(getActivity()))) {
            getActivity().finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> ownerQuery = new AVQuery<>("Statistics");
        ownerQuery.whereEqualTo("owner", LoginBean.getInstance().getUserName());

        ownerQuery.limit(999);
        ownerQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    data_list = new ArrayList<StatisticsBean>();
                    if (null != list && list.size() > 0) {
                        StatisticsBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new StatisticsBean();
                            item.setBook_name(list.get(i).getString("book_name"));
                            item.setProgress((float) list.get(i).getDouble("progress"));
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setQuery_word_c(list.get(i).getInt("query_word_c"));
                            item.setWord_c(list.get(i).getInt("word_c"));

                            data_list.add(item);
                        }
                    }
                } else {
                    data_list = null;
                }
                refreshData();
            }
        });
    }

    private void initDateRv() {
        try {
            if (null == handled_list || handled_list.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new BookStatisticsListAdapter(getActivity(), handled_list);
                calendarStatisticsRcv.setAdapter(adapter);
            } else {
                adapter.setList(handled_list);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }


    private void initUI(View v) {
        calendarStatisticsRcv = (RecyclerView) v.findViewById(R.id.calendar_statistics_rcv);
        calendarStatisticsRcv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        calendarStatisticsRcv.setFocusableInTouchMode(false);
        calendarStatisticsRcv.setFocusable(false);
        calendarStatisticsRcv.setHasFixedSize(true);
        emptyView = v.findViewById(R.id.empty_view);
    }

    private void refreshData() {
        handled_list = handleBookList(data_list);
        initDateRv();
    }

    /**
     * 获取书的列表
     *
     * @param list
     * @return
     */
    private ArrayList<StatisticsBean> handleBookList(ArrayList<StatisticsBean> list) {
        ArrayList<String> bookList = new ArrayList<>();
        ArrayList<StatisticsBean> finalRes = new ArrayList<>();//总体统计数据
        for (int i = list.size() - 1; i >= 0; i--) {
            //获取所有书
            if (!bookList.contains(list.get(i).getBook_name())) {
                bookList.add(list.get(i).getBook_name());
                finalRes.add(list.get(i));
            }
        }
        while (bookList.size() > 0) {
            String bookName = bookList.get(0);
            int queryC = 0;
            ArrayList<StatisticsBean> oneBook = new ArrayList<>();//一本书的统计数据
            for (int i = 0; i < list.size(); i++) {
                if (bookName.equals(list.get(i).getBook_name())) {
                    oneBook.add(list.get(i));
                }
            }
            for (int i = 0; i < oneBook.size(); i++) {
                if (i > 0 && oneBook.get(i).getQuery_word_c() < oneBook.get(i - 1).getQuery_word_c()) {
                    //这种情况说明数据库被删了 把每次最后一个大值相加
                    queryC += oneBook.get(i - 1).getQuery_word_c();
                }
                if (i == oneBook.size() - 1) {
                    //最后一位两种情况都得加上
                    queryC+=oneBook.get(i).getQuery_word_c();
                }
            }
            if (queryC == 0) {
                queryC = oneBook.get(oneBook.size() - 1).getQuery_word_c();
            }
            for (int i = 0; i < finalRes.size(); i++) {
                //把得到的真正的查词数给他
                if (bookName.equals(finalRes.get(i).getBook_name())) {
                    StatisticsBean item = finalRes.get(i);
                    item.setQuery_word_c(queryC);
                    item.setDateStart(WeekUtil.getDateStringWithDate(oneBook.get(0).getCreate_at()));
                    item.setDateEnd(WeekUtil.getDateStringWithDate(oneBook.get(oneBook.size() - 1).getCreate_at()));
                    finalRes.set(i, item);
                }
            }
            bookList.remove(0);
        }
        for (int i = 0; i < finalRes.size(); i++) {
            //计算没有的`
            StatisticsBean item = finalRes.get(i);
            int readC = (int) (((item.getProgress()) / 100) * item.getWord_c()) / 5;//英文单词平均为5个字符
            item.setRead_c(readC);
            if (readC == 0) {
                item.setQuery_word_r(0);
            } else {
                item.setQuery_word_r(((float) item.getQuery_word_c() * 100 / (float) readC));
            }
            finalRes.set(i, item);
        }
        return finalRes;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
