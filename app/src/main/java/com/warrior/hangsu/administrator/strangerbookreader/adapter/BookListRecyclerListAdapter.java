package com.warrior.hangsu.administrator.strangerbookreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/11/15.
 */
public class BookListRecyclerListAdapter extends RecyclerView.Adapter<BookListRecyclerListAdapter.ViewHolder> {
    private Context context;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener onRecycleItemLongClickListener;

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    private ArrayList<BookBean> list = new ArrayList<>();

    public BookListRecyclerListAdapter(Context context) {
        this(context, null);
    }

    public BookListRecyclerListAdapter(Context context, ArrayList<BookBean> list) {
        this.context = context;
        this.list = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_book, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        BookBean item = list.get(position);
        if (!TextUtils.isEmpty(item.getBpPath())) {
            ImageLoader.getInstance().displayImage(item.getBpPath(), viewHolder.book_iv, Globle.normalImageOptions);
        }
        viewHolder.book_title_tv.setText(item.getName());
        if (TextUtils.isEmpty(item.getFormat())) {
            viewHolder.book_file_format_tv.setText("TXT");
        } else {
            viewHolder.book_file_format_tv.setText(item.getFormat());
        }
        viewHolder.read_progress_tv.setText("阅读进度:  " + NumberUtil.doubleDecimals(item.getProgress()) + "%");

        viewHolder.item_book_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        viewHolder.item_book_rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onRecycleItemLongClickListener) {
                    onRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        this.onRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout item_book_rl;
        public ImageView book_iv;
        public TextView book_title_tv, read_progress_tv, book_file_format_tv;

        public ViewHolder(View view) {
            super(view);
            item_book_rl = (RelativeLayout) view.findViewById(R.id.item_book_rl);
            book_iv = (ImageView) view
                    .findViewById(R.id.book_iv);
            book_title_tv = (TextView) view
                    .findViewById(R.id.book_title_tv);
            read_progress_tv = (TextView) view
                    .findViewById(R.id.read_progress_tv);
            book_file_format_tv = (TextView) view
                    .findViewById(R.id.book_file_format_tv);
        }
    }

    public ArrayList<BookBean> getList() {
        return list;
    }

    public void setList(ArrayList<BookBean> list) {
        this.list = list;
    }
}
