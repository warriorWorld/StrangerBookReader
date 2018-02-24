package com.warrior.hangsu.administrator.strangerbookreader.business.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.enums.BookStatus;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnSearchResultListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.AppUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LogUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/2/24.
 */

public abstract class BasePageFactory {
    protected Context mContext;
    /**
     * 屏幕宽高
     */
    protected int mHeight, mWidth;
    /**
     * 字体大小 普通和标题
     */
    protected int mFontSize, mTitleFontSize;
    protected String bookPath;
    /**
     * 间距
     */
    protected int marginHeight, marginWidth;
    /**
     * 行间距
     **/
    protected int mLineSpace;
    /**
     * 文字区域宽高
     */
    protected int mVisibleHeight, mVisibleWidth;
    /**
     * 每页行数
     */
    protected int mPageLineCount;
    /**
     * 背景方块
     */
    protected Rect rectF;
    protected Paint mPaint;
    protected Paint mTitlePaint;
    protected Bitmap mBookPageBg;
    /**
     * 时间和进度长度
     */
    protected int timeLen = 0, percentLen = 0;
    protected String time;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    protected float currentPercent;
    protected String title;
    protected int bookSize;
    protected OnReadStateChangeListener listener;
    protected Bitmap batteryBitmap;
    protected ProgressBar batteryView;
    protected int battery = 40;

    public BasePageFactory(Context context, String bookPath) {
        this(context, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight(),
                SettingManager.getInstance().getReadFontSize(),
                bookPath);
    }

    public BasePageFactory(Context context, int width, int height, int fontSize, String bookPath) {
        mContext = context;
        mWidth = width;
        mHeight = height;
        mFontSize = fontSize;
        mLineSpace = mFontSize / 5 * 2;
        mTitleFontSize = ScreenUtils.dpToPxInt(16);
        marginWidth = ScreenUtils.dpToPxInt(15);
        marginHeight = ScreenUtils.dpToPxInt(15);
        mVisibleHeight = mHeight - marginHeight * 2 - mTitleFontSize * 2 - mLineSpace * 2;
        mVisibleWidth = mWidth - marginWidth * 2;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        rectF = new Rect(0, 0, mWidth, mHeight);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(Color.BLACK);
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextSize(mTitleFontSize);
        mTitlePaint.setColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.chapter_title_day));
        timeLen = (int) mTitlePaint.measureText("00:00");
        percentLen = (int) mTitlePaint.measureText("00.00%");
        // Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/FZBYSK.TTF");
        // mPaint.setTypeface(typeface);
        // mNumPaint.setTypeface(typeface);

        this.bookPath = bookPath;

        time = dateFormat.format(new Date());
    }

    /**
     * 打开书籍文件
     *
     * @param position 阅读位置
     * @return 0：文件不存在或打开失败  1：打开成功
     */
    public abstract int openBook(String path, int[] position);

    /**
     * 绘制阅读页面
     *
     * @param canvas
     */
    public abstract void onDraw(Canvas canvas);

    /**
     * 跳转下一页
     */
    public abstract BookStatus nextPage();

    /**
     * 跳转上一页
     */
    public abstract BookStatus prePage();

    /**
     * 取消翻页 重置指针
     */
    public abstract void cancelPage();

    /**
     * 设置字体大小
     *
     * @param fontsize 单位：px
     */
    public abstract void setTextFont(int fontsize);

    /**
     * 设置字体颜色
     *
     * @param textColor
     * @param titleColor
     */
    public abstract void setTextColor(int textColor, int titleColor);

    /**
     * 根据百分比，跳到目标位置
     *
     * @param persent
     */
    public abstract void setPercent(int persent);

    /**
     * 根据xy获取点击的单词
     *
     * @param touchX
     * @param touchY
     * @return
     */
    public abstract String getClickWord(int touchX, int touchY);

    /**
     * 根据输入的关键词 查找并跳转
     *
     * @param searchText
     * @param listener
     */
    public abstract void jumpToPositionBySearchText(String searchText, OnSearchResultListener listener);

    /**
     * 根据百分比，跳到目标位置
     */
    public abstract void jumpToPosition(int position);

    protected Integer[] getUnLetterPosition(String s) {
        List<Integer> indices = new ArrayList<Integer>();
        Pattern p;
        Matcher m;
        p = Pattern.compile("[a-zA-Z]");
        m = p.matcher(s);
        // 尝试在目标字符串里查找下一个匹配子串。
        int lastOnePosition = -1;
        while (m.find()) {
            // 获取到匹配字符的结束点
            if (m.start() - lastOnePosition != 1) {
                // 获取的m.start是每个符合正则表达式的字符的位置
                // 而我想要的是不符合的位置 而断掉的位置就必然是不符合的位置
                indices.add(lastOnePosition + 1);
            }
            lastOnePosition = m.start();
        }
        return (Integer[]) indices.toArray(new Integer[0]);
    }

    /**
     * 因为当单词间的非字母多于1个时会导致后边的单词附加上非字母 用这个方法去掉
     *
     * @param s
     * @return
     */
    protected String getWordAgain(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】\"‘；：”“’。，、？|-]", "");
        str = str.replaceAll("\n", "");
        str = str.replaceAll("\r", "");
        str = str.replaceAll("\\s", "");
        return str;
    }

    public void convertBetteryBitmap() {
        batteryView = (ProgressBar) LayoutInflater.from(mContext).inflate(R.layout.layout_battery_progress, null);
        batteryView.setProgressDrawable(ContextCompat.getDrawable(mContext,
                SettingManager.getInstance().getReadTheme() < 4 ?
                        R.drawable.seekbar_battery_bg : R.drawable.seekbar_battery_night_bg));
        batteryView.setProgress(battery);
        batteryView.setDrawingCacheEnabled(true);
        batteryView.measure(View.MeasureSpec.makeMeasureSpec(ScreenUtils.dpToPxInt(26), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(ScreenUtils.dpToPxInt(14), View.MeasureSpec.EXACTLY));
        batteryView.layout(0, 0, batteryView.getMeasuredWidth(), batteryView.getMeasuredHeight());
        batteryView.buildDrawingCache();
        //batteryBitmap = batteryView.getDrawingCache();
        // tips: @link{https://github.com/JustWayward/BookReader/issues/109}
        batteryBitmap = Bitmap.createBitmap(batteryView.getDrawingCache());
        batteryView.setDrawingCacheEnabled(false);
        batteryView.destroyDrawingCache();
    }

    public void setBattery(int battery) {
        this.battery = battery;
        convertBetteryBitmap();
    }

    public void recycle() {
        if (mBookPageBg != null && !mBookPageBg.isRecycled()) {
            mBookPageBg.recycle();
            mBookPageBg = null;
            LogUtils.d("mBookPageBg recycle");
        }

        if (batteryBitmap != null && !batteryBitmap.isRecycled()) {
            batteryBitmap.recycle();
            batteryBitmap = null;
            LogUtils.d("batteryBitmap recycle");
        }
    }

    public float getCurrentPercent() {
        return currentPercent;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBookTitle() {
        return title;
    }

    public int getBookSize() {
        return bookSize;
    }

    public void setBgBitmap(Bitmap BG) {
        mBookPageBg = BG;
    }

    public void setOnReadStateChangeListener(OnReadStateChangeListener listener) {
        this.listener = listener;
    }

    public int getTextFont() {
        return mFontSize;
    }
}
