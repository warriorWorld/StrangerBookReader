package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.CalendarStatisticsListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.main.MainActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnCalendarMonthChangeClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.layout.CalendarViewLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 个人信息页
 */
public class CalendarStatisticsActivity extends BaseActivity implements View.OnClickListener {
    private ArrayList<CalendarStatisticsBean> data_list = new ArrayList<>();
    private ArrayList<CalendarStatisticsBean> handled_list = new ArrayList<>();
    private CalendarViewLayout calendarCvl;
    private RecyclerView calendarStatisticsRcv;
    private View emptyView;
    private int currentYear, currentMonth;
    private CalendarStatisticsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        initUI();
        doGetData();
    }

    private void initUI() {
        calendarCvl = (CalendarViewLayout) findViewById(R.id.calendar_cvl);
        calendarCvl.setCurrentMonth(currentYear, currentMonth);
        calendarCvl.setOnCalendarMonthChangeClickListener(new OnCalendarMonthChangeClickListener() {
            @Override
            public void onChange(int year, int month) {
                currentYear = year;
                currentMonth = month;
                doGetData();
            }
        });
        calendarStatisticsRcv = (RecyclerView) findViewById(R.id.calendar_statistics_rcv);
        calendarStatisticsRcv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        calendarStatisticsRcv.setFocusableInTouchMode(false);
        calendarStatisticsRcv.setFocusable(false);
        calendarStatisticsRcv.setHasFixedSize(true);
        emptyView = findViewById(R.id.empty_view);

        baseTopBar.setTitle("数据统计");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calendar_statistics;
    }

    private void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(CalendarStatisticsActivity.this);
        AVQuery<AVObject> ownerQuery = new AVQuery<>("Statistics");
        ownerQuery.whereEqualTo("owner", LoginBean.getInstance().getUserName());

        final AVQuery<AVObject> startDateQuery = new AVQuery<>("Statistics");
        startDateQuery.whereGreaterThanOrEqualTo("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-1"));

        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Statistics");
        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-" + calendarCvl.getMaxDay() + 1));

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(ownerQuery, startDateQuery, endDateQuery));

        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(CalendarStatisticsActivity.this, e)) {
                    data_list = new ArrayList<CalendarStatisticsBean>();
                    if (null != list && list.size() > 0) {
                        CalendarStatisticsBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new CalendarStatisticsBean();
                            item.setBook_name(list.get(i).getString("book_name"));
                            item.setProgress((float) list.get(i).getDouble("progress"));
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setQuery_word_c(list.get(i).getInt("query_word_c"));
                            item.setWord_c(list.get(i).getInt("word_c"));

                            data_list.add(item);
                        }
                    }
                    refreshData();
                }
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
                adapter = new CalendarStatisticsListAdapter(this, handled_list);
                calendarStatisticsRcv.setAdapter(adapter);
            } else {
                adapter.setList(handled_list);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void refreshData() {
        int[] selecteds = new int[data_list.size()];
        for (int i = 0; i < selecteds.length; i++) {
            selecteds[i] = Integer.valueOf(WeekUtil.getDayStringWithDate(data_list.get(i).getCreate_at()));
        }
        calendarCvl.setSelecties(selecteds);

        handled_list = handleBookList(data_list);
        initDateRv();
    }

    /**
     * 获取书的列表
     *
     * @param list
     * @return
     */
    private ArrayList<CalendarStatisticsBean> handleBookList(ArrayList<CalendarStatisticsBean> list) {
        ArrayList<String> bookList = new ArrayList<>();
        //获取书名列表
        for (int i = 0; i < list.size(); i++) {
            if (!bookList.contains(list.get(i).getBook_name())) {
                bookList.add(list.get(i).getBook_name());
            }
        }


        ArrayList<CalendarStatisticsBean> finalRes = new ArrayList<>();//总体统计数据
        while (bookList.size() > 0) {
            ArrayList<CalendarStatisticsBean> res = new ArrayList<>();//一本书的统计数据
            String bookName = bookList.get(0);
            for (int i = 0; i < list.size(); i++) {
                if (bookName.equals(list.get(i).getBook_name())) {
                    res.add(list.get(i));
                }
            }
            finalRes.addAll(handleList(res));
            bookList.remove(0);
        }
        return finalRes;
    }

    /**
     * 某本书的处理 纵向比对
     *
     * @param list 具体某本书的统计数据
     * @return
     */
    private ArrayList<CalendarStatisticsBean> handleList(ArrayList<CalendarStatisticsBean> list) {
        ArrayList<CalendarStatisticsBean> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && list.get(i).getProgress() > list.get(i - 1).getProgress()) {
                //每月第一条不统计 因为这些是比对出来的数据 每月第一条没得比对  并且进度得向前才行
                CalendarStatisticsBean itemOri = list.get(i);
                CalendarStatisticsBean itemLast = list.get(i - 1);
                CalendarStatisticsBean item = new CalendarStatisticsBean();
                item.setBook_name(itemOri.getBook_name());
                item.setProgress(itemOri.getProgress());
                item.setCreate_at(itemOri.getCreate_at());
                int todayQueryC = itemOri.getQuery_word_c() - itemLast.getQuery_word_c();
                if (todayQueryC < 0) {
                    //删除数据库后会出现这个小于之前的情况
                    todayQueryC = itemOri.getQuery_word_c();
                }
                item.setToday_query_word_c(todayQueryC);
                int todayReadC = (int) (((itemOri.getProgress() - itemLast.getProgress()) / 100) * itemOri.getWord_c()) / 5;//英文单词平均为5个字符
                item.setToday_read_c(todayReadC);
                if (todayReadC == 0) {
                    item.setToday_query_word_r(0);
                } else {
                    item.setToday_query_word_r(((float) todayQueryC * 100 / (float) todayReadC));
                }

                res.add(item);
            }
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
