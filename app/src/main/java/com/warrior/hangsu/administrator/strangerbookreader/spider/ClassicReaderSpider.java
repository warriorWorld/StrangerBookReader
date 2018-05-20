package com.warrior.hangsu.administrator.strangerbookreader.spider;

import android.content.Context;

import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.ChapterListBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.MainBookBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/11.
 */

public class ClassicReaderSpider extends SpiderBase {
    @Override
    public <ResultObj> void getBookList(final String url, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url)
                            .timeout(10000).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    try {
//                        Elements elements1 = doc.getElementsByClass("titlelist");
                        Elements elements2 = doc.getElementsByClass("book-title");
                        MainBookBean mainBookBean = new MainBookBean();
                        BookBean item;
                        ArrayList<BookBean> bookList = new ArrayList<>();
                        for (int i = 0; i < elements2.size(); i++) {
                            item = new BookBean();
                            item.setName(elements2.get(i).text());
                            item.setPath(getWebUrl() + elements2.get(i).attr("href"));
//                            item.setAuthor(elements2.get(i).text());
                            bookList.add(item);
                        }
                        mainBookBean.setBook_list(bookList);
                        jsoupCallBack.loadSucceed((ResultObj) mainBookBean);
                    } catch (Exception e) {
                        jsoupCallBack.loadFailed("failed");
                    }
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getBookDetail(final String url, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    try {
                        BookBean item;
                        item = new BookBean();

                        Element element = doc.getElementsByClass("book-header").first();
                        Element element1 = doc.getElementsByClass("by-line").first();
                        item.setName(element.text());
                        item.setAuthor(element1.text());

                        Elements elements1 = doc.getElementsByClass("chapter-title");
                        ArrayList<ChapterListBean> chapterList = new ArrayList<>();
                        for (int i = 0; i < elements1.size(); i++) {
                            ChapterListBean chapterListBean = new ChapterListBean();
                            chapterListBean.setTitle(elements1.get(i).text());
                            chapterListBean.setUrl(getWebUrl() + elements1.get(i).attr("href"));
                            chapterList.add(chapterListBean);
                        }
                        item.setChapterList(chapterList);
                        jsoupCallBack.loadSucceed((ResultObj) item);
                    } catch (Exception e) {
                        jsoupCallBack.loadFailed("failed");
                    }
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getBookChapterContent(Context context, String chapterUrl, JsoupCallBack<ResultObj> jsoupCallBack) {

    }

    @Override
    public <ResultObj> void getSearchResultList(SearchType type, String keyWord, JsoupCallBack<ResultObj> jsoupCallBack) {

    }

    @Override
    public int nextPageNeedAddCount() {
        return 1;
    }

    @Override
    public String getWebUrl() {
        return "http://www.classicreader.com";
    }
}
