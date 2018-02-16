package com.warrior.hangsu.administrator.strangerbookreader.analysis;

/**
 * 给它一个书籍文件地址,返回书籍内容
 *
 * @author Administrator
 */
public abstract class AnalysisBook {
    public long bookLength;// 书总字数

    public abstract String getBookContent(int start, int lenth);

    public abstract long getBookLength();
}
