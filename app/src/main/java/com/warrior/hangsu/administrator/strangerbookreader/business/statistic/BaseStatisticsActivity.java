package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 个人信息页
 */
public abstract class BaseStatisticsActivity extends BaseActivity implements View.OnClickListener {
    protected ArrayList<CalendarStatisticsBean> data_list = new ArrayList<>();
    protected int currentYear, currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        doGetData();
    }

    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(BaseStatisticsActivity.this);
        AVQuery<AVObject> ownerQuery = new AVQuery<>("Statistics");
        ownerQuery.whereEqualTo("owner", LoginBean.getInstance().getUserName());

        final AVQuery<AVObject> startDateQuery = new AVQuery<>("Statistics");
        startDateQuery.whereGreaterThanOrEqualTo("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-1"));

        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Statistics");
//        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-" + calendarCvl.getMaxDay()));
        //结束日貌似是不包含的
        int nextMonth = currentMonth + 1;
        int year = currentYear;
        if (nextMonth == 13) {
            nextMonth = 1;
            year = year + 1;
        }
        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString(year + "-" + nextMonth + "-1"));
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(ownerQuery, startDateQuery, endDateQuery));

        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(BaseStatisticsActivity.this, e)) {
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
                } else {
                    data_list = null;
                }
                refreshData();
            }
        });
    }

    protected abstract void refreshData();
}
