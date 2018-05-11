package com.warrior.hangsu.administrator.strangerbookreader.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/11.
 */

public class MainBookBean extends BaseBean {
    private ArrayList<BookBean> book_list;

    public ArrayList<BookBean> getBook_list() {
        return book_list;
    }

    public void setBook_list(ArrayList<BookBean> book_list) {
        this.book_list = book_list;
    }
}
