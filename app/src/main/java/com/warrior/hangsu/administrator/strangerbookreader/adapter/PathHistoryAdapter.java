package com.warrior.hangsu.administrator.strangerbookreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRecyclerAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/12.
 */

public class PathHistoryAdapter extends BaseRecyclerAdapter {
    private List<String> list = null;
    private OnRecycleItemClickListener mOnRecycleItemClickListener;

    public PathHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "";
    }

    @Override
    protected String getEmptyBtnText() {
        return "";
    }

    @Override
    protected String getListEndText() {
        return "";
    }

    @Override
    protected <T> List<T> getDatas() {
        return (List<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_path, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (position == list.size() - 1 && list.size() > 1) {
            ((NormalViewHolder) viewHolder).pathTv.setText(list.get(position));
            ((NormalViewHolder) viewHolder).pathTv.setTextColor(context.getResources().getColor(R.color.main_text_color));
        } else {
            ((NormalViewHolder) viewHolder).pathTv.setText(list.get(position) + " > ");
            ((NormalViewHolder) viewHolder).pathTv.setTextColor(context.getResources().getColor(R.color.main_text_color_gray));
        }
        ((NormalViewHolder) viewHolder).pathTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnRecycleItemClickListener) {
                    mOnRecycleItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        mOnRecycleItemClickListener = onRecycleItemClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView pathTv;

        public NormalViewHolder(View view) {
            super(view);
            pathTv = (TextView) view.findViewById(R.id.path_tv);
        }
    }
}
