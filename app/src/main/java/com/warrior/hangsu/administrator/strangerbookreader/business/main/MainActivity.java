package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wandoujia.ads.sdk.Ads;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.animation.DepthPageTransformer;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private List<Fragment> fragList;
    private ViewPager pager;
    private UserFragment usrFragment = new UserFragment();
    private BooksTableFragment booksTableFragment = new BooksTableFragment();
    private int nowPosition;
    /**
     * 豌豆荚接入广告
     *
     * @param savedInstanceState
     */
    private static final String APP_ID = "100042316";
    private static final String SECRET_KEY = "28431d48ab4da69f9d6b6bc3e83acddd";
    private static final String BANNER = "6594674fd4f46557b72fbb9fb927848a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkConnected()) {
            nowPosition = 1;
        } else {
            //TODO 有资讯页后再改成2
            nowPosition = 1;
        }
        initUI();
        // 获取版本名称
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
            Globle.versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initVP();
        initAd();
        // 用于判断是否第一次运行
        String isRun = SharedPreferencesUtils.getSharedPreferencesData(this,
                "isrun");
        if (TextUtils.isEmpty(isRun) || isRun.equals("reject")) { // 应用第一次运行时执行
            //TODO
        }
    }

    private void initAd() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Ads.init(MainActivity.this, APP_ID, SECRET_KEY);
                    return true;
                } catch (Exception e) {
                    Log.e("ads-sample", "error", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                final ViewGroup container = (ViewGroup) findViewById(R.id.banner);

                if (success) {
                    /**
                     * pre load
                     */
                    Ads.preLoad(BANNER, Ads.AdFormat.banner);

                    /**
                     * add ad views
                     */
                    View bannerView = Ads.createBannerView(MainActivity.this, BANNER);
                    container.addView(bannerView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                } else {
                    TextView errorMsg = new TextView(MainActivity.this);
                    errorMsg.setText("init failed");
                    container.addView(errorMsg);
                }
            }
        }.execute();
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    private void initVP() {
        // 构建viewpager
        fragList = new ArrayList<Fragment>();
        fragList.add(usrFragment);
        fragList.add(booksTableFragment);


        MainAdapter adapter = new MainAdapter(
                getSupportFragmentManager(), fragList, null);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);//设置缓存页数
        pager.setCurrentItem(nowPosition);
        pager.setPageTransformer(true, new DepthPageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initUI() {
        pager = (ViewPager) findViewById(R.id.view_pager);


//        topBar = (SHTopBar) findViewById(R.id.top_bar);
//        topBar.setOnTopBarClickListener(new SHTopBar.OnTopBarClickListener() {
//
//            @Override
//            public void onLeft1Click() {
//                pager.setCurrentItem(0);
//            }
//
//            @Override
//            public void onLeft2Click() {
//                pager.setCurrentItem(1);
//            }
//
//            @Override
//            public void onLeft3Click() {
//                pager.setCurrentItem(2);
//            }
//
//            @Override
//            public void onRight1Click() {
//                showFileChooser();
//            }
//
//            @Override
//            public void onRight2Click() {
//
//            }
//        });
//        topBar.cutNowLeftView(nowPosition + 1);
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
            booksTableFragment.addBooks(path, null);
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否退出?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
