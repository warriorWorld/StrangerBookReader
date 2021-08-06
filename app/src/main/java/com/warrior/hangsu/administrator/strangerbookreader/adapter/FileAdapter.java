package com.warrior.hangsu.administrator.strangerbookreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseRecyclerAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.enums.FileType;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnFolderClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/12.
 */

public class FileAdapter extends BaseRecyclerAdapter {
    private ArrayList<FileBean> list = null;
    private OnFolderClickListener mOnFolderClickListener;
    private OnRecycleItemClickListener mOnRecycleItemClickListener;
    private int lastReadPosition;

    public FileAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "没有本地书籍";
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
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final FileBean item = list.get(position);
        ((NormalViewHolder) viewHolder).titleTv.setText(item.name);
        if (lastReadPosition == position) {
            ((NormalViewHolder) viewHolder).titleTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            ((NormalViewHolder) viewHolder).titleTv.setTextColor(context.getResources().getColor(R.color.main_text_color));
        }
        ((NormalViewHolder) viewHolder).iconIv.setImageResource(item.iconId);
        ((NormalViewHolder) viewHolder).itemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (item.fileType) {
                    case BOOK:
                        if (null != mOnRecycleItemClickListener) {
                            mOnRecycleItemClickListener.onItemClick(position);
                        }
                        break;
                    case FOLDER:
                        if (null != mOnFolderClickListener) {
                            mOnFolderClickListener.onClick(item.path);
                        }
                        break;
                }
            }
        });
    }

    public void setList(ArrayList<FileBean> list) {
        this.list = list;
    }

    public ArrayList<FileBean> getList() {
        return list;
    }

    private ArrayList<FileBean> getAllBooks() {
        ArrayList<FileBean> result = new ArrayList<>();
        for (FileBean item : list) {
            if (item.fileType == FileType.BOOK) {
                result.add(item);
            }
        }
        return result;
    }

    public int getBookCount() {
        return getAllBooks().size();
    }

    public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener) {
        mOnFolderClickListener = onFolderClickListener;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        mOnRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public View itemRl;
        public TextView titleTv;
        public ImageView iconIv;

        public NormalViewHolder(View view) {
            super(view);
            itemRl = (View) view.findViewById(R.id.item_rl);
            iconIv = (ImageView) view.findViewById(R.id.item_icon);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
        }
    }
}
