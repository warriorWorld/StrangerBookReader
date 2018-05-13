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
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnRecycleItemLongClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.utils.NumberUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.UltimateTextSizeUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/11/15.
 */
public class OnlineBookRecyclerListAdapter extends BaseRecyclerAdapter {
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener onRecycleItemLongClickListener;

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    private ArrayList<BookBean> list = new ArrayList<>();

    public OnlineBookRecyclerListAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "没有数据";
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_online_book, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        BookBean item = list.get(position);
        if (!TextUtils.isEmpty(item.getBpPath())) {
            ImageLoader.getInstance().displayImage(item.getBpPath(), ((NormalViewHolder) viewHolder).bookIv, Globle.normalImageOptions);
        }
        ((NormalViewHolder) viewHolder).bookTitleTv.setText
                (UltimateTextSizeUtil.getEmphasizedSpannableString(item.getName(), keyWord, 0,
                        context.getResources().getColor(R.color.english_book_reader), 0));
        ((NormalViewHolder) viewHolder).bookAuthorTv.setText("作者:  " + item.getAuthor());
        ((NormalViewHolder) viewHolder).bookOtherInfoTv.setText("等级:  " + item.getRate() +
                "    语言:  " + item.getLanguage() + "    章节数:  " + item.getChapters() +
                "    单词量:  " + item.getWords());
        ((NormalViewHolder) viewHolder).bookIntroductionTv.setText
                (UltimateTextSizeUtil.getEmphasizedSpannableString(item.getIntroduction(), keyWord, 0,
                context.getResources().getColor(R.color.english_book_reader), 0));
        String updateString = item.getUpdateDate();
        if (TextUtils.isEmpty(item.getUpdateDate())) {
            updateString = "无";
        }
        ((NormalViewHolder) viewHolder).bookDateTv.setText("公布日期:  " + item.getPublishDate() +
                "    最后更新:  " + updateString);

        ((NormalViewHolder) viewHolder).itemBookRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        ((NormalViewHolder) viewHolder).itemBookRl.setOnLongClickListener(new View.OnLongClickListener() {
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
        private RelativeLayout itemBookRl;
        private ImageView bookIv;
        private TextView bookTitleTv;
        private TextView bookAuthorTv;
        private TextView bookOtherInfoTv;
        private TextView bookIntroductionTv;
        private TextView bookDateTv;

        public NormalViewHolder(View view) {
            super(view);
            itemBookRl = (RelativeLayout) view.findViewById(R.id.item_book_rl);
            bookIv = (ImageView) view.findViewById(R.id.book_iv);
            bookTitleTv = (TextView) view.findViewById(R.id.book_title_tv);
            bookAuthorTv = (TextView) view.findViewById(R.id.book_author_tv);
            bookOtherInfoTv = (TextView) view.findViewById(R.id.book_other_info_tv);
            bookIntroductionTv = (TextView) view.findViewById(R.id.book_introduction_tv);
            bookDateTv = (TextView) view.findViewById(R.id.book_date_tv);
        }
    }

    public ArrayList<BookBean> getList() {
        return list;
    }

    public void setList(ArrayList<BookBean> list) {
        this.list = list;
    }
}
