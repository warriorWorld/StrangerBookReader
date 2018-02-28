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
import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/11/15.
 */
public class CalendarStatisticsListAdapter extends RecyclerView.Adapter<CalendarStatisticsListAdapter.ViewHolder> {
    private Context context;


    private ArrayList<CalendarStatisticsBean> list = new ArrayList<>();

    public CalendarStatisticsListAdapter(Context context) {
        this(context, null);
    }

    public CalendarStatisticsListAdapter(Context context, ArrayList<CalendarStatisticsBean> list) {
        this.context = context;
        this.list = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_statistics, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CalendarStatisticsBean item = list.get(position);
        viewHolder.book_title_tv.setText(item.getBook_name());
        viewHolder.query_word_c_tv.setText("当天查词量:  " + item.getToday_query_word_c());
        viewHolder.query_word_r_tv.setText("当天查词率:  " + NumberUtil.doubleDecimals(item.getToday_query_word_r()) + "%");
        viewHolder.read_word_c_tv.setText("当天阅读量:  " + item.getToday_read_c());
        viewHolder.date_tv.setText(WeekUtil.getDateStringWithDate(item.getCreate_at()));
        viewHolder.read_progress_tv.setText("当天进度:  " + NumberUtil.doubleDecimals(item.getProgress()) + "%");
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout item_book_rl;
        public TextView book_title_tv, read_progress_tv, query_word_c_tv, query_word_r_tv, read_word_c_tv, date_tv;

        public ViewHolder(View view) {
            super(view);
            item_book_rl = (RelativeLayout) view.findViewById(R.id.item_book_rl);
            book_title_tv = (TextView) view
                    .findViewById(R.id.book_title_tv);
            read_progress_tv = (TextView) view
                    .findViewById(R.id.read_progress_tv);
            query_word_c_tv = (TextView) view
                    .findViewById(R.id.query_word_c_tv);
            query_word_r_tv = (TextView) view
                    .findViewById(R.id.query_word_r_tv);
            read_word_c_tv = (TextView) view
                    .findViewById(R.id.read_word_c_tv);
            date_tv = (TextView) view
                    .findViewById(R.id.date_tv);
        }
    }

    public ArrayList<CalendarStatisticsBean> getList() {
        return list;
    }

    public void setList(ArrayList<CalendarStatisticsBean> list) {
        this.list = list;
    }
}
