package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.ChapterListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.ChapterListBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.listener.JsoupCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnDownloadChapterEndListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnEmptyBtnListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.spider.SpiderBase;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 个人信息页
 */
public class OnlineBookDetailFragment extends BaseRefreshListFragment implements View.OnClickListener {
    private ArrayList<ChapterListBean> chapterList = new ArrayList<>();
    private ChapterListAdapter adapter;
    private String spiderName;
    private SpiderBase spider;
    private static org.jsoup.nodes.Document doc;
    private TextView bookTitleTv;
    private TextView bookAuthorTv;
    private TextView bookOtherInfoTv;
    private TextView bookIntroductionTv;
    private TextView bookDateTv;
    private BookBean mainBean;
    private boolean chooseing = false;//判断是否在选择状态
    private boolean firstChoose = true;
    private int downloadStartPoint = 0;

    @Override
    protected int getReFreshFragmentLayoutId() {
        return R.layout.fragment_book_detail;
    }

    @Override
    protected void onCreateInit() {
        initSpider(spiderName);
    }

    @Override
    protected void initUI(View v) {
        super.initUI(v);
        bookTitleTv = (TextView) v.findViewById(R.id.book_title_tv);
        bookAuthorTv = (TextView) v.findViewById(R.id.book_author_tv);
        bookOtherInfoTv = (TextView) v.findViewById(R.id.book_other_info_tv);
        bookIntroductionTv = (TextView) v.findViewById(R.id.book_introduction_tv);
        bookDateTv = (TextView) v.findViewById(R.id.book_date_tv);

        bookIntroductionTv.setOnClickListener(this);
    }

    private void initSpider(String spiderName) {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.warrior.hangsu.administrator.strangerbookreader.spider." + spiderName + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            ToastUtils.showSingleToast(e + "");
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            ToastUtils.showSingleToast(e + "");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ToastUtils.showSingleToast(e + "");
            e.printStackTrace();
        }
    }

    private void refreshUI() {
        bookTitleTv.setText(mainBean.getName());
        bookAuthorTv.setText(mainBean.getAuthor());
        bookOtherInfoTv.setText("等级:  " + mainBean.getRate() +
                "    语言:  " + mainBean.getLanguage() + "    章节数:  " + mainBean.getChapters() +
                "    单词量:  " + mainBean.getWords());
        bookIntroductionTv.setText(mainBean.getIntroduction());

        String updateString = mainBean.getUpdateDate();
        if (TextUtils.isEmpty(mainBean.getUpdateDate())) {
            updateString = "无";
        }
        bookDateTv.setText("公布日期:  " + mainBean.getPublishDate() +
                "    最后更新:  " + updateString);
    }

    @Override
    protected void doGetData() {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        spider.getBookDetail(mainBean.getPath(), new JsoupCallBack<BookBean>() {
            @Override
            public void loadSucceed(final BookBean result) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleMainBean(result);
                        refreshUI();
                        chapterList = result.getChapterList();
                        initRec();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
                        refreshUI();
                        initRec();
                    }
                });
            }
        });
    }

    private void handleMainBean(BookBean item) {
        if (TextUtils.isEmpty(mainBean.getUpdateDate())) {
            mainBean.setUpdateDate(item.getUpdateDate());
        }
        if (TextUtils.isEmpty(mainBean.getFormat())) {
            mainBean.setFormat(item.getFormat());
        }
        if (TextUtils.isEmpty(mainBean.getBpPath())) {
            mainBean.setBpPath(item.getBpPath());
        }
        if (TextUtils.isEmpty(mainBean.getAuthor())) {
            mainBean.setAuthor(item.getAuthor());
        }
        if (TextUtils.isEmpty(mainBean.getChapters())) {
            mainBean.setChapters(item.getChapters());
        }
        if (TextUtils.isEmpty(mainBean.getIntroduction())) {
            mainBean.setIntroduction(item.getIntroduction());
        }
        if (TextUtils.isEmpty(mainBean.getLanguage())) {
            mainBean.setLanguage(item.getLanguage());
        }
        if (TextUtils.isEmpty(mainBean.getName())) {
            mainBean.setName(item.getName());
        }
        if (TextUtils.isEmpty(mainBean.getPublishDate())) {
            mainBean.setPublishDate(item.getPublishDate());
        }
        if (TextUtils.isEmpty(mainBean.getRate())) {
            mainBean.setRate(item.getRate());
        }
        if (TextUtils.isEmpty(mainBean.getWords())) {
            mainBean.setWords(item.getWords());
        }
        mainBean.setChapterList(item.getChapterList());
    }

    private void resetDownloadState() {
        firstChoose = false;
        chooseing = false;
    }

    public void downloadAll() {
        doDownload(0, chapterList.size() - 1);
    }

    private void doDownload(int start, final int end) {
        resetDownloadState();
        doGetChapterContent(chapterList.get(start).getUrl(), start + 1, new OnDownloadChapterEndListener() {
            @Override
            public void downloadChapterEnd(int chapter) {
                if (chapter == end + 1) {
                    adapter.notifyDataSetChanged();
                    MangaDialog dialog = new MangaDialog(getActivity());
                    dialog.show();
                    dialog.setTitle("全部下载完成");
                    dialog.setOkText("确定");
                } else {
                    doGetChapterContent(chapterList.get(chapter).getUrl(), chapter + 1, this);
                }
            }
        });
    }

    private void doGetChapterContent(final String chapterUrl, final int chapter) {
        doGetChapterContent(chapterUrl, chapter, null);
    }

    private void doGetChapterContent(final String chapterUrl, final int chapter, final OnDownloadChapterEndListener listener) {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        final String bookPath = Globle.CACHE_PATH + File.separator
                + mainBean.getName() + "弟" + chapter + "章.txt";
        File bookFile = new File(bookPath);
        if (bookFile.exists()) {
            SingleLoadBarUtil.getInstance().dismissLoadBar();
            if (null != listener) {
                listener.downloadChapterEnd(chapter);
            } else {
                openBook(bookPath);
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(chapterUrl)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (null != doc) {
                    Elements test = doc.select("p");
                    String urlContent = "";
                    for (int i = 0; i < test.size(); i++) {
                        urlContent += test.get(i).text() + "\n";
                    }

                    File file = new File(Globle.CACHE_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    try {
                        FileWriter fw = new FileWriter(bookPath, true);
                        fw.write(urlContent);
                        fw.close();
                    } catch (IOException e) {
                        if (null != listener) {
                            listener.downloadChapterEnd(chapter);
                        }
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SingleLoadBarUtil.getInstance().dismissLoadBar();
                            if (null != listener) {
                                listener.downloadChapterEnd(chapter);
                            } else {
                                openBook(bookPath);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    private void openBook(String bookPath) {
        SingleLoadBarUtil.getInstance().dismissLoadBar();
        Intent intent = new Intent(getActivity(), NewReadActivity.class);
        intent.putExtra("bookPath", bookPath);
        intent.putExtra("bookFormat", "TXT");
        startActivity(intent);
    }

    @Override
    protected String getType() {
        return "";
    }

    @Override
    protected boolean initRcvLayoutManger() {
        return false;
    }

    @Override
    protected void initRec() {
        SingleLoadBarUtil.getInstance().dismissLoadBar();
        try {
            if (null == adapter) {
                adapter = new ChapterListAdapter(getActivity());
                adapter.setList(chapterList);
                adapter.setNoMoreData(true);
                adapter.setHideEmptyIv(true);
                adapter.setBookName(mainBean.getName());
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (chooseing) {
                            if (firstChoose) {
                                downloadStartPoint = position;
                                firstChoose = false;
                            } else {
                                doDownload(downloadStartPoint, position);
                            }
                        } else {
                            SharedPreferencesUtils.setSharedPreferencesData(getActivity(),
                                    ShareKeys.ONLINE_BOOK_READ_CHAPTER_POSITION + mainBean.getName(), position);
                            adapter.setCurrentItem(SharedPreferencesUtils.getIntSharedPreferencesData
                                    (getActivity(),
                                            ShareKeys.ONLINE_BOOK_READ_CHAPTER_POSITION + mainBean.getName()));
                            doGetChapterContent(chapterList.get(position).getUrl(), position + 1);
                        }
                    }
                });
                adapter.setOnEmptyBtnListener(new OnEmptyBtnListener() {
                    @Override
                    public void onEmptyBtnClick() {
                        doGetChapterContent(mainBean.getPath(), 1);
                    }
                });
                refreshRcv.setAdapter(adapter);
                refreshRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                adapter.setList(chapterList);
                adapter.notifyDataSetChanged();
            }
            adapter.setCurrentItem(SharedPreferencesUtils.getIntSharedPreferencesData
                    (getActivity(),
                            ShareKeys.ONLINE_BOOK_READ_CHAPTER_POSITION + mainBean.getName()));
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        } catch (Exception e) {
            Log.e("tds", "fdsafd");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }

    public void setMainBean(BookBean mainBean) {
        this.mainBean = mainBean;
    }

    public BookBean getMainBean() {
        return mainBean;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.book_introduction_tv:
                text2Speech(mainBean.getIntroduction());
                break;
        }
    }

    public boolean isChooseing() {
        return chooseing;
    }

    public void setChooseing(boolean chooseing) {
        this.chooseing = chooseing;
    }

    public boolean isFirstChoose() {
        return firstChoose;
    }

    public void setFirstChoose(boolean firstChoose) {
        this.firstChoose = firstChoose;
    }
}
