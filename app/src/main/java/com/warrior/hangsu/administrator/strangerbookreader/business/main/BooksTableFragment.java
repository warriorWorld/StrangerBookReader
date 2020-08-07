package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;


import com.warrior.hangsu.administrator.strangerbookreader.adapter.BookListRecyclerListAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRefreshListFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.epub.EpubActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.filechoose.FileChooseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.pdf.PdfActivity;
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnEmptyBtnListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.MangaDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 个人信息页
 */
public class BooksTableFragment extends BaseRefreshListFragment {
    private ArrayList<BookBean> booksList = new ArrayList<BookBean>();
    private BookListRecyclerListAdapter adapter;
    private DbAdapter db;//数据库
    private final String[] DELETE_LIST = {"从书架中删除", "从文件中删除"};

    @Override
    protected int getReFreshFragmentLayoutId() {
        return 0;
    }

    @Override
    protected void onCreateInit() {
        db = new DbAdapter(getActivity());
    }

    @Override
    protected void doGetData() {
        booksList = db.queryAllBooks();

        initRec();
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
        try {
            if (null == adapter) {
                adapter = new BookListRecyclerListAdapter(getActivity());
                adapter.setList(booksList);
                adapter.setNoMoreData(true);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), NewReadActivity.class);
                        if (booksList.get(position).getFormat().equals("EPUB")) {
                            intent = new Intent(getActivity(), EpubActivity.class);
                        }else if (booksList.get(position).getFormat().equals("PDF")) {
                            intent = new Intent(getActivity(), PdfActivity.class);
                        }
                        intent.putExtra("bookName",booksList.get(position).getName());
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
                adapter.setOnEmptyBtnListener(new OnEmptyBtnListener() {
                    @Override
                    public void onEmptyBtnClick() {
                        Intent intent = new Intent(getActivity(), FileChooseActivity.class);
                        startActivityForResult(intent, 2);
                    }
                });
                refreshRcv.setAdapter(adapter);
                refreshRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                adapter.setList(booksList);
                adapter.notifyDataSetChanged();
            }
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    public void showFileChooser() {
        ToastUtils.showSingleToast("目前支持txt,epub,pdf格式");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/plain");//设置类型和后缀 txt
        intent.setType("*/*");//设置类型和后缀  全部文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    private void showDeleteDialog(final int position) {
        MangaDialog dialog = new MangaDialog(getActivity());
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

    public void addBooks(String path, String format, String bpPath) {
        db.insertBooksTableTb(path, StringUtil.cutString(path, '/', '.'), 0, format, bpPath);
        doGetData();
    }

    public void deleteBooks(String bookName) {
        db.deleteBookByBookName(bookName);
        doGetData();
    }

    private void showDeleteSelectorDialog(final int deletePosition) {
        ListDialog listDialog = new ListDialog(getActivity());
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

    @Override
    protected void initUI(View v) {
        super.initUI(v);
        swipeToLoadLayout.setLoadMoreEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
