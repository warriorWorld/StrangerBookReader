package com.warrior.hangsu.administrator.strangerbookreader.utils;/**
 * Created by Administrator on 2016/10/27.
 */

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 作者：苏航 on 2016/10/27 10:56
 * 邮箱：772192594@qq.com
 */
public class UltimateTextSizeUtil {
    /**
     * @param rate
     * @param textSize sp
     * @return
     */
//    public static SpannableString getRateSpannableString(double rate, int textSize) {
//        String rateStr = getFormatRate(rate);
//        SpannableString priceSpannableStr = new SpannableString(rateStr);
//        int activityPriceDotIndex = rateStr.lastIndexOf(".");
//        if (activityPriceDotIndex != -1) {
//            priceSpannableStr.setSpan(new AbsoluteSizeSpan(textSize, true), activityPriceDotIndex,
//                    rateStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return priceSpannableStr;
//    }

    //
//    public static SpannableString getLastOneMinString(String rate, int textSize) {
//        SpannableString priceSpannableStr = new SpannableString(rate);
//        int activityPriceDotIndex = rate.lastIndexOf(".");
//        int activityPriceAddIndex = rate.lastIndexOf("+");
//        if (activityPriceDotIndex != -1 && activityPriceAddIndex != -1) {
//            priceSpannableStr.setSpan(new AbsoluteSizeSpan(textSize, true), activityPriceDotIndex,
//                    activityPriceAddIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        priceSpannableStr.setSpan(new AbsoluteSizeSpan(textSize, true), rate.length() - 1,
//                rate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return priceSpannableStr;
//    }
//
    //这个方法会将传过来的 字符串改变大小
//    public static SpannableString getRateSpannableString(String rate, int textSize) {
//        SpannableString priceSpannableStr = new SpannableString(rate);
//        int activityPriceDotIndex = rate.lastIndexOf(".");
//        int activityPriceAddIndex = rate.lastIndexOf("+");
//        int perCentIndex = rate.lastIndexOf("%");
//        if (activityPriceDotIndex != -1 && activityPriceAddIndex != -1) {
//            priceSpannableStr.setSpan(new AbsoluteSizeSpan(textSize, true), activityPriceDotIndex,
//                    activityPriceAddIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        } else if (activityPriceDotIndex != -1) {
//            priceSpannableStr.setSpan(new AbsoluteSizeSpan(textSize, true), activityPriceDotIndex,
//                    rate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        if (activityPriceDotIndex != -1) {
//            priceSpannableStr.setSpan(new AbsoluteSizeSpan(15, true), perCentIndex, perCentIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return priceSpannableStr;
//    }

    //    //这个方法会处理附加利率和利率的问题
    public static SpannableString getRateSpannableString(String rate, String subsidyRate, int textSize, boolean includeDoteNum) {
        if (TextUtils.isEmpty(subsidyRate) || 0 == Float.valueOf(subsidyRate)) {
            return getEmphasizedSpannableString(rate + "%", "%", textSize, 0, 0);
        } else {
            //不处理这个了
//            float subsidyRateF = Float.valueOf(subsidyRate);
//            String[] emphasizes = new String[2];
//            emphasizes[0] = "%+";
//            emphasizes[1] = "%";
            String rateDotNum = "";
            String subsidyRateDotNum = "";
            try {
                rateDotNum = rate.substring(rate.lastIndexOf("."));
                subsidyRateDotNum = subsidyRate.substring(subsidyRate.lastIndexOf("."));
            } catch (Exception e) {

            }
            String[] emphasizes;
            if (includeDoteNum) {
                emphasizes = new String[5];
                emphasizes[0] = rateDotNum;
                emphasizes[1] = "%";
                emphasizes[2] = "+";
                emphasizes[3] = subsidyRateDotNum;
                emphasizes[4] = "%";
            } else {
                emphasizes = new String[3];
                emphasizes[0] = "%";
                emphasizes[1] = "+";
                emphasizes[2] = "%";
            }

            return getEmphasizedSpannableString(rate + "%+" + subsidyRate + "%", emphasizes, textSize, 0, 0);
        }
    }

    public static SpannableString getRateSpannableString(String rate, String subsidyRate, int textSize) {
        return getRateSpannableString(rate, subsidyRate, textSize, false);
    }


    //花生米富专用 130去掉已促成笔数
    public static SpannableString getAnnouncementSpannableString(double personNum, double moneyNum, int textSize, int color, int textStyle) {
        String TEXT1 = "已出借人次:";
        String TEXT2 = "已出借金额:";
//        String TEXT3 = "已促成借款:";
        String SPACE = "            ";
        String textP = NumberUtil.toCommaNumButNoDot(personNum) + "人" + SPACE;
        String textM = NumberUtil.toCommaNum(moneyNum) + "元" + SPACE;
//        String textB = NumberUtil.toCommaNumButNoDot(borrowNum) + "笔";
        int pStart = TEXT1.length();
        int pEnd = pStart + textP.length() - 1 - SPACE.length();
        int mStart = pEnd + TEXT2.length() + 1 + SPACE.length();
        int mEnd = mStart + textM.length() - 1 - SPACE.length();
//        int bStart = mEnd + TEXT3.length() + 1 + SPACE.length();
//        int bEnd = bStart + textB.length() - 1;
        SpannableString spannableString = new SpannableString(TEXT1 + textP + TEXT2 + textM);


        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        //必须得每次都new一遍 否则只有一个起效
//        ForegroundColorSpan spanColor = new ForegroundColorSpan(color);
//        AbsoluteSizeSpan spanSize = new AbsoluteSizeSpan(textSize, true);
//        StyleSpan spanStyle = new StyleSpan(textStyle);

        spannableString.setSpan(new ForegroundColorSpan(color), pStart, pEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new ForegroundColorSpan(color), bStart, bEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new AbsoluteSizeSpan(textSize, true), pStart, pEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(textSize, true), mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new AbsoluteSizeSpan(textSize, true), bStart, bEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new StyleSpan(textStyle), pStart, pEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(textStyle), mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new StyleSpan(textStyle), bStart, bEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public static SpannableString getAnnouncementSpannableString(String personNum, String moneyNum, int textSize, int color, int textStyle) {
        return getAnnouncementSpannableString(Double.valueOf(personNum), Double.valueOf(moneyNum), textSize, color, textStyle);
    }

    public static SpannableString getColoredText(String text, int start, int end, int color) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static String getFormatRate(double rate) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        return df.format(rate);
    }

    //获取仅数字被染色且被逗号的text
    public static SpannableString getColoredNumText(String frontText, String num, String behindText, int color, boolean needZero) {
        String dotNum;
        //需要.00的形式就用这个
        if (needZero) {
            dotNum = NumberUtil.toCommaNum(num);
        } else {
            dotNum = NumberUtil.toCommaNumButNoDot(num);
        }
        String res = frontText + dotNum + behindText;
        return getColoredText(res, frontText.length(), frontText.length() + dotNum.length(), color);
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
        try {
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
        } catch (Exception e) {
            return  new SpannableString(text);
        }
    }
}
