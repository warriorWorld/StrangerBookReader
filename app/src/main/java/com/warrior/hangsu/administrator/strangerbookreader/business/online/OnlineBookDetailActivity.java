package com.warrior.hangsu.administrator.strangerbookreader.business.online;

import android.Manifest;
import android.content.Intent;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.search.SearchActivity;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人信息页
 */
public class OnlineBookDetailActivity extends FragmentContainerActivity implements
        EasyPermissions.PermissionCallbacks {
    private OnlineBookDetailFragment onlineBookDetailFragment;
    private String spider;
    private BookBean bookBean;
    private String[] optionsList = {"下载全部", "选择起始点下载"};

    @Override
    protected void createInit() {
        Intent intent = getIntent();
        spider = intent.getStringExtra("spider");
        bookBean = (BookBean) intent.getSerializableExtra("bookBean");
        onlineBookDetailFragment = new OnlineBookDetailFragment();
        onlineBookDetailFragment.setSpiderName(spider);
        onlineBookDetailFragment.setMainBean(bookBean);

        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                OnlineBookDetailActivity.this.finish();
            }

            @Override
            public void onRightClick() {
                showOptionsSelectorDialog();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void showOptionsSelectorDialog() {
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
                        onlineBookDetailFragment.downloadAll();
                        break;
                    case 1:
                        onlineBookDetailFragment.setChooseing(true);
                        onlineBookDetailFragment.setFirstChoose(true);
                        ToastUtils.showSingleToast("请先点击起始章然后点击结束章!");
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(optionsList);
    }

    @Override
    protected BaseFragment getFragment() {
        return onlineBookDetailFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return bookBean.getName();
    }

    @Override
    protected int getContainerLayoutId() {
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        try {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        } catch (RuntimeException e) {
            //RuntimeException: Cannot execute method doDownload because it is non-void method and/or has input parameters
            //google这个东西没法调用带参数的方法
            MangaDialog dialog = new MangaDialog(OnlineBookDetailActivity.this);
            dialog.show();
            dialog.setTitle("已获得授权,请重新点击下载.");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        ToastUtils.showSingleToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showSingleToast("没文件读取/写入授权,你让我怎么下载?");
    }
}
