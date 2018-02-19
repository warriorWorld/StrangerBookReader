/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.warrior.hangsu.administrator.strangerbookreader.business.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.enums.BookStatus;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.AppUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LogUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageFactory {
    private Context mContext;
    /**
     * 屏幕宽高
     */
    private int mHeight, mWidth;
    /**
     * 文字区域宽高
     */
    private int mVisibleHeight, mVisibleWidth;
    /**
     * 间距
     */
    private int marginHeight, marginWidth;
    /**
     * 字体大小
     */
    private int mFontSize, mNumFontSize;
    /**
     * 每页行数
     */
    private int mPageLineCount;
    /**
     * 行间距
     **/
    private int mLineSpace;
    /**
     * 字节长度  整个书籍文件的字符长度
     */
    private int mbBufferLen;
    /**
     * MappedByteBuffer：高效的文件内存映射
     */
    private MappedByteBuffer mbBuff;
    /**
     * 页首页尾的位置
     */
    private int curEndPos = 0, curBeginPos = 0, tempBeginPos, tempEndPos;
    private Vector<String> mLines = new Vector<>();

    private Paint mPaint;
    private Paint mTitlePaint;
    private Bitmap mBookPageBg;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private int timeLen = 0, percentLen = 0;
    private String time;
    private int battery = 40;
    private Rect rectF;
    private ProgressBar batteryView;
    private Bitmap batteryBitmap;

    private String bookId;
    private int currentPage = 1;

    private OnReadStateChangeListener listener;
    private String charset = "UTF-8";
    private float currentPercent;

    public PageFactory(Context context, String bookId) {
        this(context, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight(),
                //SettingManager.getInstance().getReadFontSize(bookId),
                SettingManager.getInstance().getReadFontSize(),
                bookId);
    }

    public PageFactory(Context context, int width, int height, int fontSize, String bookId) {
        mContext = context;
        mWidth = width;
        mHeight = height;
        mFontSize = fontSize;
        mLineSpace = mFontSize / 5 * 2;
        mNumFontSize = ScreenUtils.dpToPxInt(16);
        marginWidth = ScreenUtils.dpToPxInt(15);
        marginHeight = ScreenUtils.dpToPxInt(15);
        mVisibleHeight = mHeight - marginHeight * 2 - mNumFontSize * 2 - mLineSpace * 2;
        mVisibleWidth = mWidth - marginWidth * 2;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        rectF = new Rect(0, 0, mWidth, mHeight);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(Color.BLACK);
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextSize(mNumFontSize);
        mTitlePaint.setColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.chapter_title_day));
        timeLen = (int) mTitlePaint.measureText("00:00");
        percentLen = (int) mTitlePaint.measureText("00.00%");
        // Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/FZBYSK.TTF");
        // mPaint.setTypeface(typeface);
        // mNumPaint.setTypeface(typeface);

        this.bookId = bookId;

        time = dateFormat.format(new Date());
    }

    public File getBookFile(String path) {
        File file = FileUtils.getChapterFile(path);
        if (file != null && file.length() > 10) {
            // 解决空文件造成编码错误的问题
            charset = FileUtils.getCharset(file.getAbsolutePath());
        }
        LogUtils.i("charset=" + charset);
        return file;
    }


    /**
     * 打开书籍文件
     *
     * @param position 阅读位置
     * @return 0：文件不存在或打开失败  1：打开成功
     */
    public int openBook(String path, int[] position) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            long length = file.length();
            if (length > 10) {
                mbBufferLen = (int) length;
                // 创建文件通道，映射为MappedByteBuffer
                mbBuff = new RandomAccessFile(file, "r")
                        .getChannel()
                        .map(FileChannel.MapMode.READ_ONLY, 0, length);
                curBeginPos = position[0];
                curEndPos = position[1];
                mLines.clear();
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 绘制阅读页面
     *
     * @param canvas
     */
    public synchronized void onDraw(Canvas canvas) {
        if (mLines.size() == 0) {
            curEndPos = curBeginPos;
            mLines = pageDown();
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
            int separatorPosition = bookId.lastIndexOf(File.separator);
            int dotPosition = bookId.lastIndexOf(".");
            String title = bookId.substring(separatorPosition + 1, dotPosition);
            canvas.drawText(title, marginWidth, y, mTitlePaint);
            y += mLineSpace + mNumFontSize;
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
            currentPercent = (float) curBeginPos * 100 / mbBufferLen;
            canvas.drawText(decimalFormat.format(currentPercent) + "%", (mWidth - percentLen) / 2,
                    mHeight - marginHeight, mTitlePaint);
            //绘制时间
            String mTime = dateFormat.format(new Date());
            canvas.drawText(mTime, mWidth - marginWidth - timeLen, mHeight - marginHeight, mTitlePaint);

            // 保存阅读进度
            SettingManager.getInstance().saveReadProgress(bookId, curBeginPos, curEndPos, currentPercent);
        }
    }

    public float getCurrentPercent() {
        return currentPercent;
    }

    /**
     * 指针移到上一页页首
     */
    private void pageUp() {
        String strParagraph = "";
        Vector<String> lines = new Vector<>(); // 页面行
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        while ((lines.size() < mPageLineCount) && (curBeginPos > 0)) {
            Vector<String> paraLines = new Vector<>(); // 段落行
            byte[] parabuffer = readParagraphBack(curBeginPos); // 1.读取上一个段落

            curBeginPos -= parabuffer.length; // 2.变换起始位置指针
            try {
                strParagraph = new String(parabuffer, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "  ");
            strParagraph = strParagraph.replaceAll("\n", " ");

            while (strParagraph.length() > 0) { // 3.逐行添加到lines
                int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                paraLines.add(strParagraph.substring(0, paintSize));
                strParagraph = strParagraph.substring(paintSize);
            }
            lines.addAll(0, paraLines);

            while (lines.size() > mPageLineCount) { // 4.如果段落添加完，但是超出一页，则超出部分需删减
                try {
                    curBeginPos += lines.get(0).getBytes(charset).length; // 5.删减行数同时起始位置指针也要跟着偏移
                    lines.remove(0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            curEndPos = curBeginPos; // 6.最后结束指针指向下一段的开始处
            paraSpace += mLineSpace;//段落间的空白高度
            //因为段落间也有空白 所以需要更新总可以显示的行数
            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace); // 添加段落间距，实时更新容纳行数
        }
    }

    /**
     * 根据起始位置指针，读取一页内容 配置mLines
     *
     * @return
     */
    private Vector<String> pageDown() {
        String strParagraph = "";
        Vector<String> lines = new Vector<>();
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        //这是一段一段的循环添加的 直到填满整页
        while ((lines.size() < mPageLineCount) && (curEndPos < mbBufferLen)) {
            byte[] parabuffer = readParagraphForward(curEndPos);
            curEndPos += parabuffer.length;
            try {
                strParagraph = new String(parabuffer, charset);//转成UTF-8
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "  ")
                    .replaceAll("\n", " "); // 段落中的换行符去掉，绘制的时候再换行
            //这是一行一行添加的 直到把某一段的内容添加完或者超过整页能容纳的行数
            while (strParagraph.length() > 0) {
                //通过breakText把段落划分成一行行的字符
                int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                lines.add(strParagraph.substring(0, paintSize));
                strParagraph = strParagraph.substring(paintSize);
                if (lines.size() >= mPageLineCount) {
                    break;
                }
            }
            //在段落的末尾加@作为段落尾换行符
            lines.set(lines.size() - 1, lines.get(lines.size() - 1) + "@");
            //当段落太长 不够地方显示的话 重置curEndPos
            if (strParagraph.length() != 0) {
                try {
                    curEndPos -= (strParagraph).getBytes(charset).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            paraSpace += mLineSpace;//段落间的空白高度
            //因为段落间也有空白 所以需要更新总可以显示的行数
            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
        }
        return lines;
    }

    /**
     * 获取最后一页的内容。比较繁琐，待优化
     *
     * @return
     */
    public Vector<String> pageLast() {
        String strParagraph = "";
        Vector<String> lines = new Vector<>();
        currentPage = 0;
        while (curEndPos < mbBufferLen) {
            int paraSpace = 0;
            mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
            curBeginPos = curEndPos;
            while ((lines.size() < mPageLineCount) && (curEndPos < mbBufferLen)) {
                byte[] parabuffer = readParagraphForward(curEndPos);
                curEndPos += parabuffer.length;
                try {
                    strParagraph = new String(parabuffer, charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                strParagraph = strParagraph.replaceAll("\r\n", "  ");
                strParagraph = strParagraph.replaceAll("\n", " "); // 段落中的换行符去掉，绘制的时候再换行

                while (strParagraph.length() > 0) {
                    int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                    lines.add(strParagraph.substring(0, paintSize));
                    strParagraph = strParagraph.substring(paintSize);
                    if (lines.size() >= mPageLineCount) {
                        break;
                    }
                }
                lines.set(lines.size() - 1, lines.get(lines.size() - 1) + "@");

                if (strParagraph.length() != 0) {
                    try {
                        curEndPos -= (strParagraph).getBytes(charset).length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                paraSpace += mLineSpace;
                mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
            }
            if (curEndPos < mbBufferLen) {
                lines.clear();
            }
            currentPage++;
        }
        //SettingManager.getInstance().saveReadProgress(bookId, currentChapter, curBeginPos, curEndPos);
        return lines;
    }

    /**
     * 读取下一段落
     *
     * @param curEndPos 当前页结束位置指针
     * @return
     */
    private byte[] readParagraphForward(int curEndPos) {
        byte b0;
        int i = curEndPos;
        //计算段落的长度 遇到回车就停下
        while (i < mbBufferLen) {
            b0 = mbBuff.get(i++);
            if (b0 == 0x0a) {//回车
                break;
            }
        }
        int nParaSize = i - curEndPos;
        byte[] buf = new byte[nParaSize];
        //根据段落长度 把一个个字符加进段落
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mbBuff.get(curEndPos + i);
        }
        return buf;
    }

    /**
     * 读取上一段落
     *
     * @param curBeginPos 当前页起始位置指针
     * @return
     */
    private byte[] readParagraphBack(int curBeginPos) {
        byte b0;
        int i = curBeginPos - 1;
        while (i > 0) {
            b0 = mbBuff.get(i);
            if (b0 == 0x0a && i != curBeginPos - 1) {
                //遇到回车 跳出循环
                i++;
                break;
            }
            i--;
        }
        int nParaSize = curBeginPos - i;
        byte[] buf = new byte[nParaSize];
        for (int j = 0; j < nParaSize; j++) {
            buf[j] = mbBuff.get(i + j);
        }
        return buf;
    }

    public boolean hasNextPage() {
        //TODO
        return curEndPos < mbBufferLen;
    }

    public boolean hasPrePage() {
        //TODO
        return curBeginPos > 0;
    }

    /**
     * 跳转下一页
     */
    public BookStatus nextPage() {
        if (!hasNextPage()) { // 最后一章的结束页
            return BookStatus.NO_NEXT_PAGE;
        } else {
            tempBeginPos = curBeginPos;
            tempEndPos = curEndPos;
            if (curEndPos >= mbBufferLen) { // 中间章节结束页
                //文件结束 我这边没有这种情况
            } else {
                curBeginPos = curEndPos; // 起始指针移到结束位置
            }
            mLines.clear();
            mLines = pageDown(); // 读取一页内容
            onPageChanged(++currentPage);
        }
        return BookStatus.LOAD_SUCCESS;
    }

    /**
     * 跳转上一页
     */
    public BookStatus prePage() {
        if (!hasPrePage()) { // 第一章第一页
            return BookStatus.NO_PRE_PAGE;
        } else {
            // 保存当前页的值
            tempBeginPos = curBeginPos;
            tempEndPos = curEndPos;
            if (curBeginPos <= 0) {
                //文件结束 我这边没有这种情况
            }
            mLines.clear();
            pageUp(); // 起始指针移到上一页开始处
            mLines = pageDown(); // 读取一页内容
            onPageChanged(--currentPage);
        }
        return BookStatus.LOAD_SUCCESS;
    }

    public void cancelPage() {
        curBeginPos = tempBeginPos;
        curEndPos = curBeginPos;

        int ret = openBook(bookId, new int[]{curBeginPos, curEndPos});
        if (ret == 0) {
            onLoadChapterFailure(bookId);
            return;
        }
        mLines.clear();
        mLines = pageDown();
    }

    /**
     * 获取当前阅读位置
     *
     * @return index 0：起始位置 1：结束位置
     */
    public int[] getPosition() {
        return new int[]{curBeginPos, curEndPos};
    }

    public String getHeadLineStr() {
        if (mLines != null && mLines.size() > 1) {
            return mLines.get(0);
        }
        return "";
    }

    /**
     * 设置字体大小
     *
     * @param fontsize 单位：px
     */
    public void setTextFont(int fontsize) {
        LogUtils.i("fontSize=" + fontsize);
        mFontSize = fontsize;
        mLineSpace = mFontSize / 5 * 2;
        mPaint.setTextSize(mFontSize);
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        curEndPos = curBeginPos;
        nextPage();
    }

    /**
     * 设置字体颜色
     *
     * @param textColor
     * @param titleColor
     */
    public void setTextColor(int textColor, int titleColor) {
        mPaint.setColor(textColor);
        mTitlePaint.setColor(titleColor);
    }

    public int getTextFont() {
        return mFontSize;
    }

    /**
     * 根据百分比，跳到目标位置
     *
     * @param persent
     */
    public void setPercent(int persent) {
        float a = (float) (mbBufferLen * persent) / 100;
        curEndPos = (int) a;
        if (curEndPos == 0) {
            nextPage();
        } else {
            nextPage();
            prePage();
            nextPage();
        }
    }

    public void setBgBitmap(Bitmap BG) {
        mBookPageBg = BG;
    }

    public void setOnReadStateChangeListener(OnReadStateChangeListener listener) {
        this.listener = listener;
    }


    private void onPageChanged(int page) {
        if (listener != null)
            listener.onPageChanged(page);
    }

    private void onLoadChapterFailure(String path) {
        if (listener != null)
            listener.onLoadFailure(path);
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

    public void setTime(String time) {
        this.time = time;
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

    public String getClickWord(int touchX, int touchY) {
        String res = "";
        int contentStartY = marginHeight + mLineSpace + mNumFontSize;//内容起始y
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
                //处理当最后一个单词被行断开的情况
                if (i == indices.length && !lineString.endsWith("@")) {
                    try {
                        String nextLineString = mLines.get(linePosition + 1);
                        int firstUnLetterPosition = getUnLetterPosition(nextLineString)[0];
                        res += nextLineString.substring(0, firstUnLetterPosition);
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

    private Integer[] getUnLetterPosition(String s) {
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
    private String getWordAgain(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】\"‘；：”“’。，、？|-]", "");
        str = str.replaceAll("\n", "");
        str = str.replaceAll("\r", "");
        str = str.replaceAll("\\s", "");
        return str;
    }
}
