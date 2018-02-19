package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.DisplayUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.drawer.SevenFourteenNavigationView;

import java.net.URISyntaxException;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private DrawerLayout drawer;
    private SevenFourteenNavigationView navigationView;
    private RelativeLayout appBarMain;
    //    private int navWidth;
    private TopBar mainTopbar;
    private RecyclerView bookListRcv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
//        navWidth = DisplayUtil.dip2px(this, 266);
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
        bookListRcv.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        bookListRcv.setFocusableInTouchMode(false);
        bookListRcv.setFocusable(false);
        bookListRcv.setHasFixedSize(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
            //TODO
//            booksTableFragment.addBooks(path, null);
        }
    }

    private void showLogoutDialog() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
