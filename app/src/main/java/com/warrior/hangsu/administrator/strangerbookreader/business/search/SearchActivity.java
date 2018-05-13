package com.warrior.hangsu.administrator.strangerbookreader.business.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.online.OnlineBooksTableFragment;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.spider.SpiderBase;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;

/**
 * 个人信息页
 */
public class SearchActivity extends FragmentContainerActivity implements View.OnClickListener {
    private OnlineBooksTableFragment onlineBooksTableFragment;
    private SpiderBase.SearchType selectedSearchType = SpiderBase.SearchType.BY_NAME;
    private String selectedWebSite = "FictionPress", keyWord;
    private boolean immediateSearch = false;
    private RelativeLayout websiteRl;
    private TextView selectedWebsiteTv;
    private RelativeLayout searchTypeRl;
    private TextView selectedSearchTypeTv;
    private EditText mangaSearchEt;
    private ImageView searchIv;
    private String[] searchTypeOptions = {"按名称搜索", "按作者搜索"};

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        String searchType = intent.getStringExtra("searchType");
        if ("name".equals(searchType)) {
            selectedSearchType = SpiderBase.SearchType.BY_NAME;
        } else if ("author".equals(searchType)) {
            selectedSearchType = SpiderBase.SearchType.BY_AUTHOR;
        }
        String t = intent.getStringExtra("selectedWebSite");
        if (!TextUtils.isEmpty(t)) {
            selectedWebSite = t;
        }
        keyWord = intent.getStringExtra("keyWord");
        immediateSearch = intent.getBooleanExtra("immediateSearch", false);


        onlineBooksTableFragment = new OnlineBooksTableFragment();
        onlineBooksTableFragment.setSpiderName(selectedWebSite);
        onlineBooksTableFragment.setBookType("");

        if (immediateSearch) {
            doSearch();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        websiteRl = (RelativeLayout) findViewById(R.id.website_rl);
        selectedWebsiteTv = (TextView) findViewById(R.id.selected_website_tv);
        searchTypeRl = (RelativeLayout) findViewById(R.id.search_type_rl);
        selectedSearchTypeTv = (TextView) findViewById(R.id.selected_search_type_tv);
        mangaSearchEt = (EditText) findViewById(R.id.manga_search_et);
        mangaSearchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //因为DOWN和UP都算回车 所以这样写 避免调用两次
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            doSearch();
                            break;
                    }
                }
                return false;
            }
        });
        searchIv = (ImageView) findViewById(R.id.search_iv);

        websiteRl.setOnClickListener(this);
        searchTypeRl.setOnClickListener(this);
        searchIv.setOnClickListener(this);
    }

    private void refreshUI() {
        selectedWebsiteTv.setText(selectedWebSite);
        switch (selectedSearchType) {
            case BY_NAME:
                selectedSearchTypeTv.setText("按漫画名称搜索");
                break;
            case BY_AUTHOR:
                selectedSearchTypeTv.setText("按漫画作者搜索");
                break;
        }
        mangaSearchEt.setText(keyWord);
    }

    private void showSearchTypeSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        selectedSearchType = SpiderBase.SearchType.BY_NAME;
                        break;
                    case 1:
                        selectedSearchType = SpiderBase.SearchType.BY_AUTHOR;
                        break;
                }
                refreshUI();
                doSearch();
            }
        });
        listDialog.show();
        listDialog.setOptionsList(searchTypeOptions);
    }

    private void showWebsiteSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {
                selectedWebSite = selectedRes;
                refreshUI();
                doSearch();
            }

            @Override
            public void onItemClick(int position) {
            }
        });
        listDialog.show();
        listDialog.setOptionsList(Globle.websList);
    }

    private void closeKeyBroad() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mangaSearchEt, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(mangaSearchEt.getWindowToken(), 0);
    }

    private void doSearch() {
        if (TextUtils.isEmpty(mangaSearchEt.getText().toString())) {
            ToastUtils.showSingleToast("请输入搜索内容");
            return;
        }
        keyWord = mangaSearchEt.getText().toString();
        onlineBooksTableFragment.setSpiderName(selectedWebSite);
        onlineBooksTableFragment.setKeyWord(keyWord);
        String type = "";

        if (selectedWebSite.equals("FictionPress")) {
            switch (selectedSearchType) {
                case BY_NAME:
                    type="story";
                    break;
                case BY_AUTHOR:
                    type="story";
                    break;
            }
            onlineBooksTableFragment.setUrl("https://www.fictionpress.com/search/?ready=1&keywords=" +
                    keyWord + "&type=" + type + "&ppage=");
        }
        onlineBooksTableFragment.refreshData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv:
                doSearch();
                break;
            case R.id.search_type_rl:
                showSearchTypeSelectorDialog();
                break;
            case R.id.website_rl:
                showWebsiteSelectorDialog();
                break;
        }
        closeKeyBroad();
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBooksTableFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return "搜索";
    }

    @Override
    protected int getContainerLayoutId() {
        return R.layout.activity_search;
    }
}
