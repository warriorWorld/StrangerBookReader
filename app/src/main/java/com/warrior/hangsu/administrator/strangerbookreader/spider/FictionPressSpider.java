//package com.warrior.hangsu.administrator.strangerbookreader.spider;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;
//
//import org.jsoup.Jsoup;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Created by Administrator on 2018/5/11.
// */
//
//public class FictionPressSpider extends SpiderBase {
//    @Override
//    public <ResultObj> void getBookList(final String url, final String page,final JsoupCallBack<ResultObj> jsoupCallBack) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    doc = Jsoup.connect(url + page)
//                            .timeout(10000).get();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    jsoupCallBack.loadFailed(e.toString());
//                }
//                if (null != doc) {
//                    Elements mangaListElements = doc.select("div.list-truyen-item-wrap");
//                    MangaBean item;
//                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
//                    for (int i = 0; i < mangaListElements.size(); i++) {
//                        item = new MangaBean();
//                        item.setName(mangaListElements.get(i).getElementsByTag("img").last().attr("alt"));
//                        item.setUrl(mangaListElements.get(i).select("a").first().attr("href"));
//                        item.setWebThumbnailUrl(mangaListElements.get(i).getElementsByTag("img").last().attr("src"));
//                        mangaList.add(item);
//                    }
//                    MangaListBean mangaListBean = new MangaListBean();
//                    mangaListBean.setMangaList(mangaList);
//                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
//                } else {
//                    jsoupCallBack.loadFailed("doc load failed");
//                }
//            }
//        }.start();
//    }
//
//    @Override
//    public <ResultObj> void getBookDetail(String mangaURL, JsoupCallBack<ResultObj> jsoupCallBack) {
//
//    }
//
//    @Override
//    public <ResultObj> void getBookChapterContent(Context context, String chapterUrl, JsoupCallBack<ResultObj> jsoupCallBack) {
//
//    }
//
//    @Override
//    public <ResultObj> void getSearchResultList(SearchType type, String keyWord, JsoupCallBack<ResultObj> jsoupCallBack) {
//
//    }
//
//    @Override
//    public int nextPageNeedAddCount() {
//        return 0;
//    }
//}
