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
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRecyclerAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.bean.ChapterListBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/12.
 */

public class ChapterListAdapter extends BaseRecyclerAdapter {
    private int currentItem = -1;
    private ArrayList<ChapterListBean> list = new ArrayList<>();
    private OnRecycleItemClickListener onRecycleItemClickListener;

    public ChapterListAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "没有章节";
    }

    @Override
    protected String getListEndText() {
        return "已经到头了~";
    }

    @Override
    protected String getEmptyBtnText() {
        return "";
    }

    @Override
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_book, viewGroup, false);
        BookListRecyclerListAdapter.NormalViewHolder vh = new BookListRecyclerListAdapter.NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ChapterListBean item = list.get(position);
        ((NormalViewHolder) viewHolder).chapterTitleTv.setText(item.getTitle());
        if (currentItem == position) {
            ((NormalViewHolder) viewHolder).bookmarkIv.setVisibility(View.VISIBLE);
        } else {
            ((NormalViewHolder) viewHolder).bookmarkIv.setVisibility(View.GONE);
        }

        ((NormalViewHolder) viewHolder).itemChapterRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemChapterRl;
        private TextView chapterTitleTv;
        private ImageView bookmarkIv;

        public NormalViewHolder(View view) {
            super(view);
            itemChapterRl = (RelativeLayout) view.findViewById(R.id.item_chapter_rl);
            chapterTitleTv = (TextView) view.findViewById(R.id.chapter_title_tv);
            bookmarkIv = (ImageView) view.findViewById(R.id.bookmark_iv);
        }
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        notifyDataSetChanged();
    }

    public void setList(ArrayList<ChapterListBean> list) {
        this.list = list;
    }
}
