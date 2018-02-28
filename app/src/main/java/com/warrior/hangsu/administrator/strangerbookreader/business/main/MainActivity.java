package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.LoginBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.ad.AdvertisingActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.login.LoginActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.other.AboutActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.CalendarStatisticsFragment;
import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.StatisticsActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.wordsbook.WordsBookActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ActivityPoor;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseParameterUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.DownloadDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.QrDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.drawer.SevenFourteenNavigationView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
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
    //版本更新
    private String versionName, msg;
    private int versionCode;
    private boolean forceUpdate;
    private AVFile downloadFile, qrCodeFile;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;
    private String qrFilePath;
    private final String[] DELETE_LIST = {"从书架中删除", "从文件中删除"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        db = new DbAdapter(this);
//        navWidth = DisplayUtil.dip2px(this, 266);
        doGetVersionInfo();
        handleIntent();
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            ToastUtils.showSingleToast("登录后可以记录并查看看书的统计数据(未登录时不记录)");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBooks();
        navigationView.setUserName(LoginBean.getInstance().getUserName());
    }

    /**
     * 已弃用
     */
    private void handleIntent() {
//        Intent intent = getIntent();
//
//        String urlTitle = intent.getStringExtra("url_title");
//        String url = intent.getStringExtra("url");
//        ToastUtils.showSingleToast(url + urlTitle);
//        if (!TextUtils.isEmpty(urlTitle) && !TextUtils.isEmpty(url)) {
//            Intent intent1 = new Intent(this, NewReadActivity.class);
//            intent1.putExtra("url_title", urlTitle);
//            intent1.putExtra("url", url);
//            startActivity(intent1);
//        }
    }

    private void initUI() {
        appBarMain = (RelativeLayout) findViewById(R.id.app_bar_main);
        navigationView = (SevenFourteenNavigationView) findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemClickListener(new SevenFourteenNavigationView.OnNavigationItemClickListener() {
            @Override
            public void onLoginBtnClick() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onUserInfoClick() {

            }

            @Override
            public void onNewWordClick() {
//                ToastUtils.showSingleToast("开发中...");
                Intent intent = new Intent(MainActivity.this, WordsBookActivity.class);
                startActivity(intent);
            }

            @Override
            public void onStatisticsClick() {
//                ToastUtils.showSingleToast("开发中...");
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onShare_appClick() {
                showQrDialog();
            }

            @Override
            public void onEnglishAdClick() {
                Intent intent = new Intent(MainActivity.this, AdvertisingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onOptionsClick() {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
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
                return true;
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

    private void showDeleteSelectorDialog(final int deletePosition) {
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
                        deleteBooks(booksList.get(deletePosition).getName());
                        break;
                    case 1:
                        showDeleteDialog(deletePosition);
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(DELETE_LIST);
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
//                        if (booksList.get(position).getFormat().equals("EPUB") ) {
//                            intent = new Intent(MainActivity.this, TestActivity.class);
//                        }
                        intent.putExtra("bookPath", booksList.get(position).getPath());
                        intent.putExtra("bookFormat", booksList.get(position).getFormat());
                        startActivity(intent);
                    }
                });
                adapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        showDeleteSelectorDialog(position);
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
        ToastUtils.showSingleToast("目前仅支持txt格式");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");//设置类型和后缀 txt
//        intent.setType("*/*");//设置类型和后缀  全部文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    private void showDeleteDialog(final int position) {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                try {
                    FileUtils.deleteFile(new File(booksList.get(position).getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                deleteBooks(booksList.get(position).getName());
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否从文件中删除该书?");
        dialog.setMessage("从文件中移除后无法恢复");
        dialog.setOkText("是");
        dialog.setCancelText("否");
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
            addBooks(path, format, null);
        }
    }

    private void doGetVersionInfo() {
        AVQuery<AVObject> query = new AVQuery<>("VersionInfo");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        versionName = list.get(0).getString("versionName");
                        versionCode = list.get(0).getInt("versionCode");
                        forceUpdate = list.get(0).getBoolean("forceUpdate");
                        msg = list.get(0).getString("description");
                        downloadFile = list.get(0).getAVFile("apk");
                        qrCodeFile = list.get(0).getAVFile("QRcode");
                        if (null != qrCodeFile) {
                            doDownloadQRcode();
                        }
                        if (BaseParameterUtil.getInstance(MainActivity.this).
                                getAppVersionCode() >= versionCode || SharedPreferencesUtils.
                                getBooleanSharedPreferencesData(MainActivity.this,
                                        ShareKeys.IGNORE_THIS_VERSION_KEY + versionName, false)) {
                        } else {
                            showVersionDialog();
                        }
                    }
                }
            }
        });
    }

    @AfterPermissionGranted(222)
    private void doDownloadQRcode() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            final String folderPath = Globle.DOWNLOAD_PATH;
            final File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            final String qrFileName = "QR" + versionName + ".png";
            qrFilePath = Globle.DOWNLOAD_PATH + "/" + qrFileName;
            final File qrFile = new File(qrFilePath);
            if (qrFile.exists()) {
                //有就不下了
                return;
            }
            qrCodeFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    // bytes 就是文件的数据流
                    if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
                        File apkFile = FileUtils.byte2File(bytes, folderPath, qrFileName);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                }
            });

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    222, perms);
        }
    }

    private void showQrDialog() {
        QrDialog qrDialog = new QrDialog(this);
        qrDialog.show();
        qrDialog.setImg("file://" + qrFilePath);
    }

    private void showVersionDialog() {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(MainActivity.this);
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    versionDialog.dismiss();
                    doDownload();
                }

                @Override
                public void onCancelClick() {
                    if (forceUpdate) {
                        ActivityPoor.finishAllActivity();
                    } else {
                        SharedPreferencesUtils.setSharedPreferencesData(MainActivity.this,
                                ShareKeys.IGNORE_THIS_VERSION_KEY + versionName, true);
                        ToastUtils.showSingleToast("忽略后可在'我的'页中点击'版本'按钮升级至最新版!");
                    }
                }
            });
        }
        versionDialog.show();

        versionDialog.setTitle("有新版本啦" + "v_" + versionName);
        versionDialog.setMessage(msg);
        versionDialog.setOkText("升级");
        versionDialog.setCancelable(false);

        if (!forceUpdate) {
            versionDialog.setCancelText("忽略");
        } else {
            versionDialog.setCancelText("退出");
        }
    }

    @AfterPermissionGranted(111)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            final String filePath = Globle.DOWNLOAD_PATH + "/apk";
            final File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            downloadFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    // bytes 就是文件的数据流
                    if (null != downloadDialog && downloadDialog.isShowing()) {
                        downloadDialog.dismiss();
                    }
                    if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
                        File apkFile = FileUtils.byte2File(bytes, filePath, "english_book_reader.apk");

                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                    downloadDialog.setProgress(integer);
                }
            });

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(this);
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    private void showLogoutDialog() {
        MangaDialog logoutDialog = new MangaDialog(MainActivity.this);
        logoutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                MainActivity.this.finish();
            }

            @Override
            public void onCancelClick() {

            }
        });
        logoutDialog.show();

        logoutDialog.setTitle("确定退出?");
        logoutDialog.setOkText("退出");
        logoutDialog.setCancelText("再逛逛");
        logoutDialog.setCancelable(true);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        ToastUtils.showSingleToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (111 == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(MainActivity.this);
            peanutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    ActivityPoor.finishAllActivity();
                }

                @Override
                public void onCancelClick() {

                }
            });
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试!");
        }
    }
}
