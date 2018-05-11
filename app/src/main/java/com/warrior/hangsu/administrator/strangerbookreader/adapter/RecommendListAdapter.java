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

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRecyclerAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.imageloader.AuthImageDownloader;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/11/15.
 */
public class RecommendListAdapter extends BaseRecyclerAdapter {
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener onRecycleItemLongClickListener;

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    private ArrayList<BookBean> list = new ArrayList<>();

    public RecommendListAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "没有推荐";
    }

    @Override
    protected String getListEndText() {
        return "";
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_online_manga_recyler, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        BookBean item = list.get(position);
        if (!TextUtils.isEmpty(item.getBpPath())) {
            ImageLoader.getInstance().displayImage(item.getBpPath(), ((NormalViewHolder) viewHolder).mangaThumbnailIv, Globle.normalImageOptions);
        } else {
        }

        ((NormalViewHolder) viewHolder).mangaTitleTv.setText(item.getName());
        ((NormalViewHolder) viewHolder).item_collect_manga_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        ((NormalViewHolder) viewHolder).item_collect_manga_rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onRecycleItemLongClickListener) {
                    onRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        this.onRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public ImageView mangaThumbnailIv;
        public TextView mangaTitleTv;
        public RelativeLayout item_collect_manga_rl;

        public NormalViewHolder(View view) {
            super(view);
            item_collect_manga_rl = (RelativeLayout) view.findViewById(R.id.item_collect_manga_rl);
            mangaThumbnailIv = (ImageView) view.findViewById(R.id.manga_thumbnail_iv);
            mangaTitleTv = (TextView) view.findViewById(R.id.manga_title_tv);
        }
    }

    public ArrayList<BookBean> getList() {
        return list;
    }

    public void setList(ArrayList<BookBean> list) {
        this.list = list;
    }
}
