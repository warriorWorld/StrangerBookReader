package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;

import java.util.ArrayList;

public class StatisticsActivity extends BaseActivity implements OnItemLongClickListener {
    private ArrayList<StatisticsBean> list = new ArrayList<StatisticsBean>();
    private DbAdapter db;//数据库
    private ListView bookLv;
    private View emptyView;
    private BooksListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisctics);
        db = new DbAdapter(this);

        initUI();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(list.get(i).getBook_name());
        return true;
    }

    /**
     * arraylistadapter
     */
    private class BooksListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<StatisticsBean> list;

        public BooksListAdapter(Context context, ArrayList<StatisticsBean> list) {
            this.context = context;
            this.list = list;
        }

        public void setList(ArrayList<StatisticsBean> list) {
            this.list = list;
        }

        public ArrayList<StatisticsBean> getList() {
            return list;
        }

        @Override
        public int getCount() {
            if (null == list || list.size() == 0)
                return 0;
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_listview_book, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.bookName_tv = (TextView) convertView
                        .findViewById(R.id.book_name_tv);
                viewHolder.date_tv = (TextView) convertView
                        .findViewById(R.id.date_tv);
                viewHolder.char_amount_tv = (TextView) convertView
                        .findViewById(R.id.char_amount_tv);
                viewHolder.word_amount_tv = (TextView) convertView
                        .findViewById(R.id.word_amount_tv);

                convertView.setTag(viewHolder);
            } else {
                // 初始化过的话就直接获取
                viewHolder = (ViewHolder) convertView.getTag();
            }
            StatisticsBean item = list.get(position);
            viewHolder.bookName_tv.setText(item.getBook_name());
            viewHolder.date_tv.setText(item.getDateStart() + "至" + item.getDateEnd());
            viewHolder.char_amount_tv.setText(item.getWord_c() + "");
            viewHolder.word_amount_tv.setText(item.getQuery_word_c() + "");
            return convertView;
        }
    }

    private class ViewHolder {
        TextView bookName_tv;
        TextView date_tv;
        TextView char_amount_tv;
        TextView word_amount_tv;
    }


    private void initUI() {
        bookLv = (ListView) findViewById(R.id.book_list);
        emptyView = findViewById(R.id.empty_view);
        refresh();
    }

    private void refresh() {
        list = db.queryAllStatistic();
        initListView();
    }

    private void initListView() {
        if (null == adapter) {
            adapter = new BooksListAdapter(StatisticsActivity.this, list);
            bookLv.setAdapter(adapter);
            bookLv.setOnItemLongClickListener(this);
            bookLv.setEmptyView(emptyView);
        } else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }


    private void showDeleteDialog(final String bookName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除该漫画所有数据?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteStatiscticsByBookName(bookName);
                refresh();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
