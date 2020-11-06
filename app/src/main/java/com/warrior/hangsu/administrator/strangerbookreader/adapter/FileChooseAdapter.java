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
import com.warrior.hangsu.administrator.strangerbookreader.enums.FileType;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnFolderClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/12.
 */

public class FileChooseAdapter extends BaseRecyclerAdapter {
    private ArrayList<FileBean> list = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private ArrayList<FileBean> selectedList = new ArrayList<>();
    private OnFolderClickListener mOnFolderClickListener;

    public FileChooseAdapter(Context context) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chooser, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final FileBean item = list.get(position);
        ((NormalViewHolder) viewHolder).titleTv.setText(item.name);
        ((NormalViewHolder) viewHolder).iconIv.setImageResource(item.iconId);
        if (item.modifiedDate == 0) {
            ((NormalViewHolder) viewHolder).dateTv.setVisibility(View.GONE);
        } else {
            ((NormalViewHolder) viewHolder).dateTv.setText(StringUtil.getDateToString(item.modifiedDate,
                    "yyyy-MM-dd HH:mm:ss"));
        }
        switch (item.fileType) {
            case BOOK:
                ((NormalViewHolder) viewHolder).itemCb.setVisibility(View.VISIBLE);
                break;
            case FOLDER:
                ((NormalViewHolder) viewHolder).itemCb.setVisibility(View.GONE);
                break;
        }
        ((NormalViewHolder) viewHolder).itemCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedList.contains(item)) {
                        selectedList.add(item);
                    }
                } else {
                    selectedList.remove(item);
                }
            }
        });
        ((NormalViewHolder) viewHolder).itemCb.setChecked(null != selectedList && selectedList.contains(item));
        ((NormalViewHolder) viewHolder).itemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (item.fileType) {
                    case BOOK:
                        ((NormalViewHolder) viewHolder).itemCb.setChecked(!((NormalViewHolder) viewHolder).itemCb.isChecked());
                        break;
                    case FOLDER:
                        if (null!=mOnFolderClickListener){
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


    public ArrayList<FileBean> getSelectedList() {
        return selectedList;
    }

    public void selectAll() {
        selectedList = getAllBooks();
        notifyDataSetChanged();
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

    public void removeAllSelected() {
        //因为selectedList已经被赋值为list 所以他们指向同一个资源 如果清空就全清空了
//        selectedList.clear();
        selectedList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener) {
        mOnFolderClickListener = onFolderClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public View itemRl;
        public TextView titleTv;
        public CheckBox itemCb;
        public ImageView iconIv;
        public TextView dateTv;

        public NormalViewHolder(View view) {
            super(view);
            itemRl = (View) view.findViewById(R.id.item_rl);
            iconIv = (ImageView) view.findViewById(R.id.item_icon);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
            itemCb = (CheckBox) view.findViewById(R.id.item_cb);
            dateTv = (TextView) view.findViewById(R.id.date_tv);
        }
    }
}
