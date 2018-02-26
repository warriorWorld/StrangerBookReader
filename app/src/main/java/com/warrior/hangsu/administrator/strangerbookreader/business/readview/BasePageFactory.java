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

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
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

    protected Vector<String> mLines = new Vector<>();
    protected DecimalFormat decimalFormat = new DecimalFormat("#0.00");

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
//    public abstract void onDraw(Canvas canvas);
    public synchronized void onDraw(Canvas canvas) {
        if (mLines.size() == 0) {
            loadLines();
        }
        if (mLines.size() > 0) {
            int y = marginHeight + (mLineSpace << 1);
            // 绘制背景
            if (mBookPageBg != null) {
                canvas.drawBitmap(mBookPageBg, null, rectF, null);
            } else {
                canvas.drawColor(Color.WHITE);
            }
            // 绘制标题       //TODO
            int separatorPosition = bookPath.lastIndexOf(File.separator);
            int dotPosition = bookPath.lastIndexOf(".");
            title = bookPath.substring(separatorPosition + 1, dotPosition);
            canvas.drawText(title, marginWidth, y, mTitlePaint);
            y += mLineSpace + mTitleFontSize;
            // 绘制阅读页面文字
            for (String line : mLines) {
                y += mLineSpace;
                if (line.endsWith("@")) {
                    //@当做段落尾换行符 这样段落间距就会比行间距大 这里正好是行间距的两倍
                    canvas.drawText(line.substring(0, line.length() - 1), marginWidth, y, mPaint);
                    y += mLineSpace;
                } else {
                    canvas.drawText(line, marginWidth, y, mPaint);
                }
                y += mFontSize;
            }
            // 绘制提示内容
            if (batteryBitmap != null) {
                canvas.drawBitmap(batteryBitmap, marginWidth + 2,
                        mHeight - marginHeight - ScreenUtils.dpToPxInt(12), mTitlePaint);
            }
            currentPercent = calculatePercent();
            canvas.drawText(decimalFormat.format(currentPercent) + "%", (mWidth - percentLen) / 2,
                    mHeight - marginHeight, mTitlePaint);
            //绘制时间
            String mTime = dateFormat.format(new Date());
            canvas.drawText(mTime, mWidth - marginWidth - timeLen, mHeight - marginHeight, mTitlePaint);
            saveReadProgress();
        }
    }

    /**
     * 保存阅读进度
     */
    protected abstract void saveReadProgress();

    /**
     * 读取mLines
     *
     * @return
     */
    protected abstract void loadLines();

    /**
     * 根据起始位置指针，读取一页内容 配置mLines
     *
     * @return
     */
    protected abstract Vector<String> pageDown();

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
    public String getClickWord(int touchX, int touchY) {
        String res = "";
        int contentStartY = marginHeight + mLineSpace + mTitleFontSize;//内容起始y
        int y = contentStartY;
        int absoluteY = touchY - contentStartY;
        if (absoluteY < 0) {
            return "这是标题";
        }
        int linePosition = 0;
        String lineString = "";
        for (int i = 0; i < mLines.size(); i++) {
            y += mLineSpace;
            if (mLines.get(i).endsWith("@")) {
                //@当做段落尾换行符 这样段落间距就会比行间距大 这里正好是行间距的两倍
                y += mLineSpace;
            }
            y += mFontSize;
            if (y >= touchY) {
                linePosition = i;
                lineString = mLines.get(linePosition);
                break;
            }
        }

        //获取横向位置
        float[] measuredWidth = {(float) mVisibleWidth};
        //这个方法会返回一个字符串真正的宽度
        mPaint.breakText(lineString, true, mVisibleWidth, measuredWidth);
        float touchPercentX = (float) (touchX - marginWidth) / measuredWidth[0];
        int touchCharacterPosition = (int) (touchPercentX * lineString.length());


        Integer[] indices = getUnLetterPosition(lineString);
        int start = 0;
        int end = 0;
        // to cater last/only word loop will run equal to the length of
        // indices.length
        //因为末尾不一定有特殊字符 所以一直循环到超出数组1然后把最后一个加上
        for (int i = 0; i <= indices.length; i++) {
            // 末尾不能超出文本
            end = (i < indices.length ? indices[i] : lineString.length());
            if (end >= touchCharacterPosition) {
                res = lineString.substring(start, end);
                //处理当最后一个单词被行断开的情况 不能是句尾 末尾是标点或空格也不行
//                if (i == indices.length && !lineString.endsWith("@") &&
//                        StringUtil.isWord(lineString.substring(lineString.length() - 1, lineString.length()))) {
                if (i == indices.length && lineString.endsWith("-")) {
                    //因为我给所有末尾断开的单词加换行符了 所以直接这么判断就好了
                    try {
                        String nextLineString = mLines.get(linePosition + 1);
                        Integer[] nextUnletters = getUnLetterPosition(nextLineString);
                        if (nextUnletters == null || nextUnletters.length == 0) {
                            //说明下一行整行都没有特殊字符
                            res += nextLineString;
                        } else {
                            int firstUnLetterPosition = getUnLetterPosition(nextLineString)[0];
                            res += nextLineString.substring(0, firstUnLetterPosition);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //当最后一个字符是特殊字符时直接catch就好
                    }
                } else if (start == 0 && linePosition != 0) {
                    //处理当这一行第一个单词实际上上一行最后一个单词一部分的情况
                    try {
                        String lastLineString = mLines.get(linePosition - 1);
//                        if (lastLineString.endsWith("@") ||
//                                !StringUtil.isWord(lastLineString.substring(lastLineString.length() - 1, lastLineString.length()))) {
                        //当上一行最后一个单词是句尾 或者末尾是标点或空格也不行
                        if (!lastLineString.endsWith("-")) {
                            //因为我加了换行符 所以直接改成这么判断了
                            break;
                        }
                        Integer[] lastUnletters = getUnLetterPosition(lastLineString);
                        int endUnLetterPosition = lastUnletters[lastUnletters.length - 1];
                        res = lastLineString.substring(endUnLetterPosition, lastLineString.length()) + res;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //当最后一个字符是特殊字符时直接catch就好
                    }
                }
                break;
            }
            start = end + 1;//+1表示只允许有一个非字母间隔 否则就会把别的东西加进去
        }
        res = getWordAgain(res);
        return res;
    }

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
        for (int i = 0; i < 10; i++) {
            str = str.replaceAll(i + "", "");
        }
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

    /**
     * 计算阅读进度
     *
     * @return
     */
    protected abstract float calculatePercent();

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
