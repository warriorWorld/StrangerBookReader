package com.warrior.hangsu.administrator.strangerbookreader.bean;

import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.StatisticsBean;

/**
 * Created by Administrator on 2016-06-16.
 */
public class CalendarStatisticsBean extends StatisticsBean {
    private int today_query_word_c;//当天查词量
    private float today_query_word_r;//当天查词率
    private int today_read_c;//当天阅读量


    public int getToday_query_word_c() {
        return today_query_word_c;
    }

    public void setToday_query_word_c(int today_query_word_c) {
        this.today_query_word_c = today_query_word_c;
    }


    public int getToday_read_c() {
        return today_read_c;
    }

    public void setToday_read_c(int today_read_c) {
        this.today_read_c = today_read_c;
    }

    public float getToday_query_word_r() {
        return today_query_word_r;
    }

    public void setToday_query_word_r(float today_query_word_r) {
        this.today_query_word_r = today_query_word_r;
    }
}
