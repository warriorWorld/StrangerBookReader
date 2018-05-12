package com.warrior.hangsu.administrator.strangerbookreader.spider;

import android.content.Context;
import android.text.TextUtils;

import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.MainBookBean;
import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;

import org.jsoup.Jsoup;
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
                    doc = Jsoup.connect(url + "?&srt=1&r=103&p=" + page)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements elements1 = doc.getElementsByClass("z-list zhover zpointer ");
                    Elements mangaListElements = doc.select("a.stitle");
                    MainBookBean mainBookBean = new MainBookBean();
                    BookBean item;
                    ArrayList<BookBean> bookList = new ArrayList<>();
                    for (int i = 0; i < mangaListElements.size(); i++) {
                        item = new BookBean();
                        item.setName(mangaListElements.get(i).text());
                        item.setPath(mangaListElements.get(i).attr("href"));
                        item.setBpPath(mangaListElements.get(i).getElementsByClass("lazy cimage").attr("src"));
                        if (elements1.get(i).select("a").size()==4){
                            item.setAuthor(elements1.get(i).select("a").get(2).text());
                        }else  if (elements1.get(i).select("a").size()==2){
                            item.setAuthor(elements1.get(i).select("a").get(1).text());
                        }else  if (elements1.get(i).select("a").size()==3){
                            item.setAuthor(elements1.get(i).select("a").get(2).text());
                        }
                        item.setIntroduction(elements1.get(i).getElementsByClass("z-indent z-padtop").text());
                        item.setRate(elements1.get(i).select("div.z-indent z-padtop").select("div.z-padtop2 xgray").text());
                        bookList.add(item);
                    }
                    mainBookBean.setBook_list(bookList);
                    jsoupCallBack.loadSucceed((ResultObj) mainBookBean);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getBookDetail(String mangaURL, JsoupCallBack<ResultObj> jsoupCallBack) {

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
