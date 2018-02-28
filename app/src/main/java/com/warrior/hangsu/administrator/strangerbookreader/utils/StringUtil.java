package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static boolean checkLength(String str, int length) {
        int valueLength = 0;

        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度�?2，否则为1 */
        for (int i = 0; i < str.length(); i++) {
            /* 获取�?个字�? */
            String temp = str.substring(i, i + 1);
            /* 判断是否为中文字�? */
            if (temp.matches(chinese)) {
                /* 中文字符长度�?2 */
                valueLength += 2;
            } else {
                /* 其他字符长度�?1 */
                valueLength += 1;
            }
        }

        return length < valueLength;
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 截取指定位数的条码
     */
    public static String cutString(String string, int targetNum) {
        if (string.length() > targetNum) {
            string = string.substring(0, targetNum);
            Logger.d("截取方法" + string);
        }
        return string;
    }


    public static String cutString(String string, int start, int end) {
        if (string.length() > 0) {
            string = string.substring(start, end);
        }
        return string;
    }

    public static String cutString(String string, char start, char end) {
        if (string.length() > 0) {
            string = string.substring(lastIndexOf(string, start) + 1, lastIndexOf(string, end));
        }
        return string;
    }

    public static int lastIndexOf(String str, char target) {
        int location = -1;
        char[] list = str.toCharArray();
        for (int i = str.length() - 1; i >= 0; i--) {
            char s = list[i];
            if (s == target) {
                location = i;
                break;
            }
        }
        Logger.d("( location is:" + location);
        return location;
    }

    /**
     * 判断是否是规范的数字
     */
    public static boolean verifyNumber(String string) {
        if (string.length() == 1 && "+".equals(string) || "-".equals(string)
                || ".".equals(string)) {
            return false;
        }
        return true;
    }

    public static boolean isWord(String word) {
        if (word.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern
                .compile("[\\w]+");
        Matcher matcher = pattern.matcher(word);
        return matcher.matches();
    }

    public static SpannableString getEmphasizedSpannableString(String text, ArrayList<String> emphasizeTexts,
                                                               int textSize, int color, int textStyle) {
        String[] empasize = new String[emphasizeTexts.size()];
        for (int i = 0; i < emphasizeTexts.size(); i++) {
            empasize[i] = emphasizeTexts.get(i);
        }
        return getEmphasizedSpannableString(text, empasize, textSize, color, textStyle);
    }

    public static SpannableString getEmphasizedSpannableString(String text, String emphasizeText,
                                                               int textSize, int color, int textStyle) {
        String[] empasize = new String[1];
        empasize[0] = emphasizeText;
        return getEmphasizedSpannableString(text, empasize, textSize, color, textStyle);
    }

    public static SpannableString getEmphasizedSpannableString(String text, String[] emphasizeTexts,
                                                               int textSize, int color, int textStyle) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        SpannableString spannableString = new SpannableString(text);
        String emphasizeText;
        int start = -1, end = 0;

        for (int i = 0; i < emphasizeTexts.length; i++) {
            emphasizeText = emphasizeTexts[i];
            if (!text.contains(emphasizeText)) {
                break;
            }
            //从上一个的结束开始搜 否则如果有重复的就会有bug
            start = text.indexOf(emphasizeText, end);

            end = start + emphasizeText.length();

            if (start == -1) {
                break;
            }
            //必须得每次都new一遍 否则只有一个起效
            if (color != 0) {
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (textSize != 0) {
                spannableString.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (textStyle != 0) {
                spannableString.setSpan(new StyleSpan(textStyle), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
}
