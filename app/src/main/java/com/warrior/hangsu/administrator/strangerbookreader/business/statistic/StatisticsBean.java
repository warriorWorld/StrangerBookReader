package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import com.warrior.hangsu.administrator.strangerbookreader.bean.BaseBean;

import java.util.Date;

/**
 * Created by Administrator on 2016-06-16.
 */
public class StatisticsBean extends BaseBean {
    private String dateStart;
    private String dateEnd;
    private String type;
    private String book_name;
    private int query_word_c;
    private int word_c;//
    private float progress;
    private Date create_at;

    private int today_query_word_c;//当天查词量
    private float today_query_word_r;//当天查词率
    private int today_read_c;//当天阅读量

    private float query_word_r;//查词率
    private int read_c;//阅读量

    public int getToday_query_word_c() {
        return today_query_word_c;
    }

    public void setToday_query_word_c(int today_query_word_c) {
        this.today_query_word_c = today_query_word_c;
    }

    public float getToday_query_word_r() {
        return today_query_word_r;
    }

    public void setToday_query_word_r(float today_query_word_r) {
        this.today_query_word_r = today_query_word_r;
    }

    public int getToday_read_c() {
        return today_read_c;
    }

    public void setToday_read_c(int today_read_c) {
        this.today_read_c = today_read_c;
    }

    public float getQuery_word_r() {
        return query_word_r;
    }

    public void setQuery_word_r(float query_word_r) {
        this.query_word_r = query_word_r;
    }

    public int getRead_c() {
        return read_c;
    }

    public void setRead_c(int read_c) {
        this.read_c = read_c;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public int getQuery_word_c() {
        return query_word_c;
    }

    public void setQuery_word_c(int query_word_c) {
        this.query_word_c = query_word_c;
    }

    public int getWord_c() {
        return word_c;
    }

    public void setWord_c(int word_c) {
        this.word_c = word_c;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }
}
