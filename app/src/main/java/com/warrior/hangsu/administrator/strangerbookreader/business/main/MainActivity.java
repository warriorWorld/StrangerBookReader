package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.drawer.SevenFourteenNavigationView;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private DrawerLayout drawer;
    private SevenFourteenNavigationView navigationView;
    private RelativeLayout appBarMain;
    //    private int navWidth;
    private TopBar mainTopbar;
    private RecyclerView bookListRcv;
    private View emptyView;
    private TextView emptyTv;
    private ArrayList<BookBean> booksList = new ArrayList<BookBean>();
    private BookListRecyclerListAdapter adapter;
    private DbAdapter db;//数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        db = new DbAdapter(this);
//        navWidth = DisplayUtil.dip2px(this, 266);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBooks();
    }

    private void initUI() {
        appBarMain = (RelativeLayout) findViewById(R.id.app_bar_main);
        navigationView = (SevenFourteenNavigationView) findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemClickListener(new SevenFourteenNavigationView.OnNavigationItemClickListener() {
            @Override
            public void onLoginBtnClick() {

            }

            @Override
            public void onUserInfoClick() {

            }

            @Override
            public void onNewWordClick() {

            }

            @Override
            public void onStatisticsClick() {

            }

            @Override
            public void onShare_appClick() {

            }

            @Override
            public void onEnglishAdClick() {

            }

            @Override
            public void onOptionsClick() {

            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //slideOffset是个从0-1的值
//                appBarMain.setTranslationX(slideOffset * navWidth);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        toggle.syncState();
        hideBaseTopBar();
        mainTopbar = (TopBar) findViewById(R.id.main_topbar);
        mainTopbar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void onRightClick() {
                showFileChooser();
            }

            @Override
            public void onTitleClick() {

            }
        });
        bookListRcv = (RecyclerView) findViewById(R.id.book_list_rcv);
        bookListRcv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        bookListRcv.setFocusableInTouchMode(false);
        bookListRcv.setFocusable(false);
        bookListRcv.setHasFixedSize(true);
        emptyView = findViewById(R.id.empty_view);
        emptyTv = (TextView) findViewById(R.id.empty_tv);
        emptyTv.setText("书架是空的!点击添加!");

        emptyView.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void refreshBooks() {
        booksList = db.queryAllBooks();

        initDateRv();
    }

    public void addBooks(String path, String format, String bpPath) {
        db.insertBooksTableTb(path, StringUtil.cutString(path, '/', '.'), 0, format, bpPath);
        refreshBooks();
    }

    public void deleteBooks(String bookName) {
        db.deleteBookByBookName(bookName);
        refreshBooks();
    }

    private void initDateRv() {
        try {
            if (null == booksList || booksList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new BookListRecyclerListAdapter(this, booksList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(MainActivity.this, NewReadActivity.class);
                        intent.putExtra("bookPath", booksList.get(position).getPath());
                        startActivity(intent);
                    }
                });
                adapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        deleteBooks(booksList.get(position).getName());
                    }
                });
                bookListRcv.setAdapter(adapter);
            } else {
                adapter.setList(booksList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");//设置类型和后缀
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
//            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//            Logger.d("地址:" + uri.toString());
//            String path = data.getDataString();
//            //得解码下 不然中文乱码
//            path = Uri.decode(path);

            // Get the Uri of the selected file
            Uri uri = data.getData();
            // Get the path
            String path = null;
            try {
                path = FileUtils.getPath(this, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            ToastUtils.showSingleToast(path);
            String format = "";
            if (path.endsWith(".txt") || path.endsWith(".TXT")) {
                format = "TXT";
            } else if (path.endsWith(".pdf") || path.endsWith(".PDF")) {
                format = "PDF";
            } else if (path.endsWith(".epub") || path.endsWith(".EPUB")) {
                format = "EPUB";
            }
            addBooks(path, format,null);
        }
    }

    private void showLogoutDialog() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_view:
                showFileChooser();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
