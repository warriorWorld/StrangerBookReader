package com.warrior.hangsu.administrator.strangerbookreader.bean;

import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.StatisticsBean;

/**
 * Created by Administrator on 2016-06-16.
 */
public class CalendarStatisticsBean extends StatisticsBean {
    private String today_query_word_c;
    private String today_query_word_r;
    private String today_read_c;

    public String getToday_query_word_c() {
        return today_query_word_c;
    }

    public void setToday_query_word_c(String today_query_word_c) {
        this.today_query_word_c = today_query_word_c;
    }

    public String getToday_query_word_r() {
        return today_query_word_r;
    }

    public void setToday_query_word_r(String today_query_word_r) {
        this.today_query_word_r = today_query_word_r;
    }

    public String getToday_read_c() {
        return today_read_c;
    }

    public void setToday_read_c(String today_read_c) {
        this.today_read_c = today_read_c;
    }

}
