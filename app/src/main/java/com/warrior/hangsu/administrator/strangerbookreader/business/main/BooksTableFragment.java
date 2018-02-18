package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.test.NewReadActivity;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;

import java.util.ArrayList;

public class BooksTableFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private View mainView;
    private View emptyView;
    private GridView mangaGV;
    private ArrayList<BookBean> booksList = new ArrayList<BookBean>();
    private MangaGridAdapter adapter;
    private DbAdapter db;//数据库


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.activity_books_table, null);
        db = new DbAdapter(getActivity());
        initUI(mainView);
        refreshBooks();

        return mainView;
    }

    public void refreshBooks() {
        booksList = db.queryAllBooks();

        initGridView();
    }

    public void addBooks(String path, String bpPath) {
        db.insertBooksTableTb(path, StringUtil.cutString(path, '/', '.'), 0, bpPath);
        refreshBooks();
    }

    public void deleteBooks(String bookName) {
        db.deleteBookByBookName(bookName);
        refreshBooks();
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new MangaGridAdapter(getActivity());
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setOnItemLongClickListener(this);
            mangaGV.setEmptyView(emptyView);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    private void initUI(View v) {
        mangaGV = (GridView) v.findViewById(R.id.manga_gv);
        emptyView = v.findViewById(R.id.empty_view);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //刷新是为了刷新当前进度
        booksList = db.queryAllBooks();
        BookBean item = booksList.get(position);
        Globle.nowBookName = item.getName();
        Globle.nowBookPath = item.getPath();
        Globle.nowBookPosition = item.getProgress();
        Intent intent = new Intent(getActivity(), NewReadActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(i);
        return true;
    }

    private void showDeleteDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("是否删除?");
        builder.setMessage("不会删除原文件");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookBean item = booksList.get(i);
                deleteBooks(item.getName());
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    class MangaGridAdapter extends BaseAdapter {
        private Context context;

        public MangaGridAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return booksList.size();
        }

        @Override
        public Object getItem(int position) {
            return booksList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_book, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.book_view = (ImageView) convertView
                        .findViewById(R.id.book_view);
                viewHolder.book_txt_view = (TextView) convertView
                        .findViewById(R.id.book_txt_view);
                viewHolder.book_name = (TextView) convertView
                        .findViewById(R.id.book_name);

                convertView.setTag(viewHolder);
            } else {
                // 初始化过的话就直接获取
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BookBean item = booksList.get(position);
            if (!TextUtils.isEmpty(item.getBpPath())) {
                //有封面就显示封面
                ImageLoader.getInstance().displayImage(item.getBpPath(), viewHolder.book_view, Globle.options);
                viewHolder.book_view.setVisibility(View.VISIBLE);
                viewHolder.book_txt_view.setVisibility(View.GONE);
            } else {
                viewHolder.book_txt_view.setText(item.getName());
                viewHolder.book_view.setVisibility(View.GONE);
                viewHolder.book_txt_view.setVisibility(View.VISIBLE);
            }
            viewHolder.book_name.setText(item.getName());
            return convertView;
        }
    }

    private class ViewHolder {
        private ImageView book_view;
        private TextView book_txt_view;
        private TextView book_name;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        db.closeDb();
    }
}
