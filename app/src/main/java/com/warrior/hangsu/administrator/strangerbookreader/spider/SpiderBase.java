package com.warrior.hangsu.administrator.strangerbookreader.spider;

import android.content.Context;

import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;


/**
 * Created by Administrator on 2017/7/18.
 */

public abstract class SpiderBase {
    protected org.jsoup.nodes.Document doc;

    public enum SearchType {
        BY_NAME,
        BY_AUTHOR,
    }

    public abstract <ResultObj> void getBookList(String url, String page, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract <ResultObj> void getBookDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract <ResultObj> void getBookChapterContent
            (final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract <ResultObj> void getSearchResultList(SearchType type, String keyWord, final JsoupCallBack<ResultObj> jsoupCallBack);

    //很多网页的下一页并不是在网址后+1 而是+n
    public abstract int nextPageNeedAddCount();

    public abstract String getWebUrl();
}
