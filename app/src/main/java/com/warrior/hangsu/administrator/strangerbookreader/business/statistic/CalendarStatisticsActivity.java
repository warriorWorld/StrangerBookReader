package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.os.Bundle;
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
import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnCalendarMonthChangeClickListener;
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
        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-" + calendarCvl.getMaxDay()));

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
                            item.setProgress(list.get(i).getLong("progress"));
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

    private void refreshData() {
        int[] selecteds = new int[data_list.size()];
        for (int i = 0; i < selecteds.length; i++) {
            selecteds[i] = Integer.valueOf(WeekUtil.getDayStringWithDate(data_list.get(i).getCreate_at()));
        }
        calendarCvl.setSelecties(selecteds);
    }

    private ArrayList<CalendarStatisticsBean> handleList(ArrayList<CalendarStatisticsBean> list) {
        ArrayList<CalendarStatisticsBean> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

        }
        return res;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
