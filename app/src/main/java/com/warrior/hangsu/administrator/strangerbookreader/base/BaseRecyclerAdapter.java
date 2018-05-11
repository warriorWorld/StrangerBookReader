package com.warrior.hangsu.administrator.strangerbookreader.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnEmptyBtnListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context context;
    private final int TYPE_NORMAL = 0;
    private final int TYPE_EMPTY = 1;
    private final int TYPE_END = 2;
    protected boolean noMoreData = false;//上下拉刷新的列表不一定最后一位就是真正的最后一位
    private OnEmptyBtnListener onEmptyBtnListener;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
    }

    // 创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_END) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_end, viewGroup, false);
            ListEndViewHolder listEndViewHolder = new ListEndViewHolder(view);
            return listEndViewHolder;
        } else if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emptyview_list, viewGroup, false);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(view);
            return emptyViewHolder;
        } else {
            return getNormalViewHolder(viewGroup);
        }
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) viewHolder).emptyText.setText(getEmptyText());
            if (TextUtils.isEmpty(getEmptyBtnText())) {
                ((EmptyViewHolder) viewHolder).emptyBtn.setVisibility(View.GONE);
            } else {
                ((EmptyViewHolder) viewHolder).emptyBtn.setVisibility(View.VISIBLE);
                ((EmptyViewHolder) viewHolder).emptyBtn.setText(getEmptyBtnText());
            }
            ((EmptyViewHolder) viewHolder).emptyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onEmptyBtnListener) {
                        onEmptyBtnListener.onEmptyBtnClick();
                    }
                }
            });
        } else if (viewHolder instanceof ListEndViewHolder) {
            ((ListEndViewHolder) viewHolder).listEndTv.setText(getListEndText());
        } else {
            refreshNormalViewHolder(viewHolder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null == getDatas() || getDatas().size() == 0) {
            return TYPE_EMPTY;
        } else if (position == getDatas().size() && noMoreData) {
            return TYPE_END;
        } else {
            return TYPE_NORMAL;
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == getDatas() || getDatas().size() == 0) {
            return 1;
        } else if (noMoreData) {
            return getDatas().size() + 1;
        } else {
            return getDatas().size();
        }
    }

    public void setOnEmptyBtnListener(OnEmptyBtnListener onEmptyBtnListener) {
        this.onEmptyBtnListener = onEmptyBtnListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        private ImageView emptyImage;
        private TextView emptyText;
        private Button emptyBtn;

        public EmptyViewHolder(View view) {
            super(view);
            emptyImage = (ImageView) view.findViewById(R.id.empty_list_iv);
            emptyText = (TextView) view.findViewById(R.id.empty_list_tv);
            emptyBtn = (Button) view.findViewById(R.id.ok_btn);
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ListEndViewHolder extends RecyclerView.ViewHolder {
        private TextView listEndTv;

        public ListEndViewHolder(View view) {
            super(view);
            listEndTv = (TextView) view.findViewById(R.id.list_end_tv);
        }
    }

    protected abstract String getEmptyText();

    protected abstract String getListEndText();

    protected abstract String getEmptyBtnText();

    protected abstract <T> ArrayList<T> getDatas();

    protected abstract RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup);

    protected abstract void refreshNormalViewHolder(final RecyclerView.ViewHolder viewHolder, final int position);

    public void setNoMoreData(boolean noMoreData) {
        this.noMoreData = noMoreData;
        if (noMoreData) {
            notifyDataSetChanged();
        }
    }
}
