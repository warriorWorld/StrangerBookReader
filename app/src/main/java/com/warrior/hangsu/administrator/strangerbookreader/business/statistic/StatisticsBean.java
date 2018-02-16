package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

/**
 * Created by Administrator on 2016-06-16.
 */
public class StatisticsBean {
    private String dateStart;
    private String dateEnd;
    private String type;
    private String book_name;
    private int query_word_c;
    private int word_c;//


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
}
