package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.CalendarStatisticsListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnCalendarMonthChangeClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.layout.CalendarViewLayout;

import java.util.ArrayList;

/**
 * 个人信息页
 */
public class CalendarStatisticsFragment extends BaseStatisticsFragment implements View.OnClickListener {
    private ArrayList<CalendarStatisticsBean> handled_list = new ArrayList<>();
    private CalendarViewLayout calendarCvl;
    private RecyclerView calendarStatisticsRcv;
    private View emptyView;
    private CalendarStatisticsListAdapter adapter;

    private void initDateRv() {
        try {
            if (null == handled_list || handled_list.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new CalendarStatisticsListAdapter(getActivity(), handled_list);
                calendarStatisticsRcv.setAdapter(adapter);
            } else {
                adapter.setList(handled_list);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_calendar_statistics;
    }

    @Override
    protected void initUI(View v) {
        calendarCvl = (CalendarViewLayout) v.findViewById(R.id.calendar_cvl);
        calendarCvl.setCurrentMonth(currentYear, currentMonth);
        calendarCvl.setOnCalendarMonthChangeClickListener(new OnCalendarMonthChangeClickListener() {
            @Override
            public void onChange(int year, int month) {
                currentYear = year;
                currentMonth = month;
                doGetData();
            }
        });
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

    @Override
    protected void refreshData() {
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
