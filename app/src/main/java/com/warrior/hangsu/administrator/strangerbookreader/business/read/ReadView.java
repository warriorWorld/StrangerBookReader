package com.warrior.hangsu.administrator.strangerbookreader.business.read;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadView extends TextView {
    //要设置的总字数,一个固定值 只要保证大于view能显示的下的数量就行了
    public static int WORD_COUNT = 1800;
    private Context context;
    //记录点击次数
    private int clickTime = 0;
    //记录上次点击的文本 只有同一个文本点击了两次才翻译
    private String lastClick = "";
    private boolean singleClick = false;
    private int spannableTextColor = getResources().getColor(R.color.black);//spannable的颜色会变 所以还得设置一遍
    private OnWordClickListener onWordClickListener;


    public ReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ReadView(Context context) {
        this(context, null);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 将每个单词变成可点击的Spannable
     *
     * @param textView
     */
    public void getEachWord() {
        // 先把从textview中得到的文字转成Spannable
        Spannable spans = (Spannable) getText();
        // 获取到每个空格的位置
        // Integer[] indices = getIndices(textView.getText().toString().trim(),
        // ' ');
        Integer[] indices = getUnLetterPosition(getText().toString());
        int start = 0;
        int end = 0;
        // to cater last/only word loop will run equal to the length of
        // indices.length
        //
        ClickableSpan clickSpan;
        for (int i = 0; i <= indices.length; i++) {
            // 给每个单词一个点击事件
            clickSpan = getClickableSpan();
            // 末尾不能超出文本
            end = (i < indices.length ? indices[i] : spans.length());
            // 循环给每个单词设置clickspan
            spans.setSpan(clickSpan, start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = end + 1;//+1表示只允许有一个非字母间隔 否则就会把别的东西加进去
        }
        // 改变选中文本的高亮颜色
        setHighlightColor(getResources().getColor(R.color.top_bar));
    }

    private ClickableSpan getClickableSpan() {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TextView tv = (TextView) widget;
                String s = tv
                        .getText()
                        .subSequence(tv.getSelectionStart(),
                                tv.getSelectionEnd()).toString();
                clickTime++;
                if (singleClick || (clickTime > 1 && lastClick.equals(s))) {
                    clickTime = 0;
                    if (onWordClickListener != null) {
                        onWordClickListener.onWordClick(getWordAgain(s));
                    }
                }
                lastClick = s;
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(spannableTextColor);
                ds.setUnderlineText(false);
            }
        };
    }

    public void setSingleClick(boolean singleClick) {
        this.singleClick = singleClick;
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
     * 取出当前页无法显示的字
     *
     * @return 去掉的字数
     */
//    public int resize() {
//        CharSequence oldContent = getText();
//        CharSequence newContent = oldContent.subSequence(0,
//                getVisibleWordAmount());
//        setText(newContent);
//        return oldContent.length() - newContent.length();
//    }

    /**
     * 获取可见的文字内容
     *
     * @return
     */
    public CharSequence getVisibleText() {
        CharSequence oldContent = getText();
        CharSequence newContent = oldContent.subSequence(0,
                getVisibleWordAmount());
        return newContent;
    }

    /**
     * 获取前一页的首字符位置
     */
    public int getPreviousStartPosition(int currentStartPosition) {
        int res = 0;
        res = currentStartPosition - getVisibleWordAmount();
        return res;
    }

    /**
     * 获取下一页的首字符位置
     */
    public int getNextStartPosition(int currentStartPosition) {
        int res = 0;
        res = currentStartPosition + getVisibleWordAmount();
        return res;
    }

    /**
     * 获取当前页总字数
     */
    public int getVisibleWordAmount() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom()
                - getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }

    public void setSpannableTextColor(int spannableTextColor) {
        this.spannableTextColor = spannableTextColor;
    }

    public void setOnWordClickListener(OnWordClickListener onWordClickListener) {
        this.onWordClickListener = onWordClickListener;
    }

    public interface OnWordClickListener {
        public void onWordClick(String word);
    }
}
