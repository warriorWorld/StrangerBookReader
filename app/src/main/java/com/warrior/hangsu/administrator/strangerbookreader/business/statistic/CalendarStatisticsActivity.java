package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 个人信息页
 */
public class CalendarStatisticsActivity extends BaseActivity implements View.OnClickListener {
    private EditText feedbackEt;
    private Button okBtn;
    private ArrayList<CalendarStatisticsBean> data_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        doGetData();
    }

    private void initUI() {
        feedbackEt = (EditText) findViewById(R.id.feedback_et);
        okBtn = (Button) findViewById(R.id.ok_btn);

        okBtn.setOnClickListener(this);
        baseTopBar.setTitle("意见反馈");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
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
        startDateQuery.whereGreaterThanOrEqualTo("createdAt", WeekUtil.getDateWithDateString("2018-2-1"));

        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Statistics");
        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString("2018-2-28"));

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
//                    initListView();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                break;
        }
    }
}
