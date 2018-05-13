package com.warrior.hangsu.administrator.strangerbookreader.spider;

import android.content.Context;
import android.text.TextUtils;

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

public class FictionPressSpider extends SpiderBase {
    @Override
    public <ResultObj> void getBookList(final String url, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url  + page)
                            .timeout(10000).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    try {
                        Elements elements1 = doc.getElementsByClass("z-list zhover zpointer ");
                        if (elements1==null||elements1.size()==0){
                            elements1=doc.getElementsByClass("z-list");
                        }
                        Elements mangaListElements = doc.select("a.stitle");
                        MainBookBean mainBookBean = new MainBookBean();
                        BookBean item;
                        ArrayList<BookBean> bookList = new ArrayList<>();
                        for (int i = 0; i < mangaListElements.size(); i++) {
                            item = new BookBean();
                            item.setName(mangaListElements.get(i).text());
                            item.setPath(getWebUrl() + mangaListElements.get(i).attr("href"));
                            item.setBpPath(mangaListElements.get(i).getElementsByClass("lazy cimage").attr("src"));
                            if (elements1.get(i).select("a").size() == 4) {
                                item.setAuthor(elements1.get(i).select("a").get(2).text());
                            } else if (elements1.get(i).select("a").size() == 2) {
                                item.setAuthor(elements1.get(i).select("a").get(1).text());
                            } else if (elements1.get(i).select("a").size() == 3) {
                                item.setAuthor(elements1.get(i).select("a").get(2).text());
                            }
                            String[] introductions = elements1.get(i).getElementsByClass("z-indent z-padtop").text().split("Rated:");

                            item.setIntroduction(introductions[0]);
                            String[] others = introductions[1].split("-");
                            item.setRate(others[0]);
                            item.setLanguage(others[1]);
                            for (int j = 2; j < others.length; j++) {
                                if (others[j].contains("Chapters")) {
                                    item.setChapters(others[j].replace("Chapters:", "")
                                            .replace(" ", ""));
                                } else if (others[j].contains("Words")) {
                                    item.setWords(others[j].replace("Words:", "")
                                            .replace(" ", ""));
                                } else if (others[j].contains("Updated")) {
                                    item.setUpdateDate(others[j].replace("Updated:", "")
                                            .replace(" ", ""));
                                } else if (others[j].contains("Complete")) {
                                    item.setUpdateDate("已完结");
                                } else if (others[j].contains("Published")) {
                                    item.setPublishDate(others[j].replace("Published:", "")
                                            .replace(" ", ""));
                                }
                            }
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

                        Element element = doc.getElementById("chap_select");
                        Elements elements1 = element.children();
                        ArrayList<ChapterListBean> chapterList = new ArrayList<>();
                        for (int i = 0; i < elements1.size(); i++) {
                            ChapterListBean chapterListBean = new ChapterListBean();
                            chapterListBean.setTitle(elements1.get(i).text());
                            String[] urls = url.split("/");
                            urls[urls.length - 2] = (i + 1) + "";
                            String finalUrl = "";
                            for (int j = 0; j < urls.length; j++) {
                                finalUrl += urls[j] + "/";
                            }
                            chapterListBean.setUrl(finalUrl);
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
        return 0;
    }

    @Override
    public String getWebUrl() {
        return "https://www.fictionpress.com";
    }
}
