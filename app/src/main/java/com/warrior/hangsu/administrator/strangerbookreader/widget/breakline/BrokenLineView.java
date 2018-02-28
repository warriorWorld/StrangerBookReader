package com.warrior.hangsu.administrator.strangerbookreader.widget.breakline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.warrior.hangsu.administrator.strangerbookreader.R;

import java.util.ArrayList;

public class BrokenLineView extends View {
    // 显而易见 当且仅当XY的单位同时为1时,需要尽量等比例绘制(一般情况下,XY不会出现非单位同时为1的情况)
    private Paint axlePaint, markNumberPaint, brokenLinePaint;// 画笔
    private String[] xPointNameList;//x坐标轴坐标点文字
    private int axleColor, markColor, lineColor;
    private int arrowsSize = 15;// 箭头大小
    private int numberMarkSize = 5;// 数字标记点大小
    private int numberSize = 10;// 数字字体大小
    private int textXMargin, textYMargin;// 文字的margin
    private int markLineSize = 10;// 标记线长度
    private String XText = "X", YText = "Y";// 数轴文字
    private float XDValue = 10, YDValue = 10;// 等差数轴的差值
    private int XCount = 5, YCount = 5;// 等差数轴的差值总数量
    private float originX = 0, originY = 0;// XY的原点值
    private float unitYlenth = 1, unitXlenth = 1;// XY轴单位实际长度
    private Context context;
    // private int specialPoint = Integer.MAX_VALUE;
    // 这个东西是为了记录无穷时的x位置
    private ArrayList<Integer> specialPoint = new ArrayList<Integer>();


    // 计算出N个方程解然后将坐标赋值给list,再通过list绘制曲线图
    private ArrayList<PointF> pointLineList = new ArrayList<PointF>();
    // X集,通过这个决定都计算哪些Y
    private float[] XList;
    // 计算精度,数值越高 曲线越圆滑
    private int precision = 5;
    private float startX;// 这个是从左开始的第一个X的值,用于初始化xlist

    public void setxPointNameList(String[] xPointNameList) {
        this.xPointNameList = xPointNameList;
    }

    // private BrokenLineSet brokenLineSet;

    // 数轴样式
    public enum AxleType {
        // 完整的
        FullXY,
        // 半X
        HalfX,
        // 半XY
        HalfXY
    }

    private AxleType axleType;
    private String formula;

    /**
     * 在代码中动态声明的控件自由决定调用哪个构造方法
     *
     * @param context
     */
    public BrokenLineView(Context context) {
        this(context, null);
        this.context = context;
        // 将硬件加速关闭,如果开启会有各种问题.硬件加速对自定义控件有问题
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * 在XML中直接声明的控件会调用这个构造方法
     *
     * @param context
     * @param attrs
     */
    public BrokenLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 将硬件加速关闭,如果开启会有各种问题.硬件加速对自定义控件有问题
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.broken_line);
        // 给各种值初始化
        int i = ta.getInt(R.styleable.broken_line_axle_type, 0);
        arrowsSize = ta.getInt(R.styleable.broken_line_arrows_size, arrowsSize);
        numberMarkSize = ta.getInt(R.styleable.broken_line_number_mark_size,
                numberMarkSize);
        numberSize = ta.getInt(R.styleable.broken_line_number_size, numberSize);
        originX = ta.getFloat(R.styleable.broken_line_origin_x, 0);
        originY = ta.getFloat(R.styleable.broken_line_origin_y, 0);
        XDValue = ta.getFloat(R.styleable.broken_line_x_d_value, XDValue);
        YDValue = ta.getFloat(R.styleable.broken_line_y_d_value, YDValue);
        XCount = ta.getInt(R.styleable.broken_line_x_count, XCount);
        YCount = ta.getInt(R.styleable.broken_line_y_count, YCount);
        XText = ta.getString(R.styleable.broken_line_x_text);
        YText = ta.getString(R.styleable.broken_line_y_text);
        axleColor = context.getResources().getColor(R.color.axle);
        markColor = context.getResources().getColor(R.color.mark);
        lineColor = context.getResources().getColor(R.color.english_book_reader);
        switch (i) {
            case 0:
                axleType = AxleType.FullXY;
                break;
            case 1:
                axleType = AxleType.HalfX;
                break;
            case 2:
                axleType = AxleType.HalfXY;
                break;
        }
        // 准备画笔 轴线画笔
        axlePaint = new Paint();
        axlePaint.setColor(axleColor);
        axlePaint.setStrokeJoin(Paint.Join.ROUND);
        axlePaint.setStrokeCap(Paint.Cap.ROUND);
        axlePaint.setStrokeWidth(3);
        // 准备画笔 标记画笔
        markNumberPaint = new Paint();
        markNumberPaint.setColor(markColor);
        markNumberPaint.setStrokeJoin(Paint.Join.ROUND);
        markNumberPaint.setStrokeCap(Paint.Cap.ROUND);
        markNumberPaint.setTextSize(25);
        markNumberPaint.setStrokeWidth(2);
        // 准备画笔 折线画笔
        brokenLinePaint = new Paint();
        brokenLinePaint.setColor(lineColor);
        brokenLinePaint.setStyle(Paint.Style.STROKE);// ���ĵ�
        brokenLinePaint.setAntiAlias(true); // �������
        brokenLinePaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        axlePaint.setColor(axleColor);
        markNumberPaint.setColor(markColor);
        brokenLinePaint.setColor(lineColor);
        // 之前我写的是canvas.getwidth 但是当我关闭硬件加速后
        // 发现这样获取的数值不对,也即是说,硬件加速修改了这部分的代码.所以现在改成这样.
        // 硬件加速开启和关闭是有级别的 我应该在View层面关闭硬件加速
        int width = getWidth();
        int height = getHeight();
        int originXLocation = 0;// 原点位置
        int originYLocation = 0;
        // 我认为以下这两个值需要考虑textsize的值
        textXMargin = width - 120;
        textYMargin = 25;

        if (width > arrowsSize && height > arrowsSize) {

            switch (axleType) {
                case FullXY:
                    // X轴
                    canvas.drawLine(0, height / 2, width, height / 2, axlePaint);
                    // Y轴
                    canvas.drawLine(width / 2, 0, width / 2, height, axlePaint);
                    // X箭头
                    canvas.drawLine(width, height / 2, width - arrowsSize, height
                            / 2 + arrowsSize, axlePaint);
                    canvas.drawLine(width, height / 2, width - arrowsSize, height
                            / 2 - arrowsSize, axlePaint);
                    // Y箭头
                    canvas.drawLine(width / 2, 0, width / 2 - arrowsSize,
                            0 + arrowsSize, axlePaint);
                    canvas.drawLine(width / 2, 0, width / 2 + arrowsSize,
                            0 + arrowsSize, axlePaint);

                    originXLocation = width / 2;
                    originYLocation = height / 2;
                    // 绘制原点
                    canvas.drawText(originX + "/" + originY, originXLocation - 2
                            * arrowsSize, originYLocation, markNumberPaint);
                    // 绘制X轴数字
                    if (XDValue != 0 && XCount != 0) {
                        if (XCount % 2 == 0) {
                            // 偶数情况
                            int XLenthForUse = width - 2 * arrowsSize;// x轴长度
                            unitXlenth = XLenthForUse / XCount;// 将可用段等分成Xcount份,然后得到的长度
                            for (int i = 0; i < XCount / 2; i++) {
                                canvas.drawLine(originXLocation + (i + 1)
                                        * unitXlenth, originYLocation
                                        - markLineSize, originXLocation + (i + 1)
                                        * unitXlenth, originYLocation
                                        + markLineSize, markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originX + (i + 1) * XDValue),
                                        originXLocation + (i + 1) * unitXlenth - 10,
                                        originYLocation + 2 * markLineSize,
                                        markNumberPaint);
                                canvas.drawLine(originXLocation - (i + 1)
                                        * unitXlenth, originYLocation
                                        - markLineSize, originXLocation - (i + 1)
                                        * unitXlenth, originYLocation
                                        + markLineSize, markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originX - (i + 1) * XDValue),
                                        originXLocation - (i + 1) * unitXlenth - 10,
                                        originYLocation + 2 * markLineSize,
                                        markNumberPaint);
                            }
                            startX = originX - (XCount / 2) * XDValue;
                            // endX = originX + XCount * XDValue;
                        } else {
                            // 奇数情况
                            int trueCount = (XCount - 1) / 2;
                            int XLenthForUse = width / 2 - 2 * arrowsSize;// x轴长度
                            unitXlenth = XLenthForUse / (trueCount + 1);// 将可用段等分成Xcount份,然后得到的长度
                            for (int i = 0; i < trueCount; i++) {
                                canvas.drawLine(originXLocation + (i + 1)
                                        * unitXlenth, originYLocation
                                        - markLineSize, originXLocation + (i + 1)
                                        * unitXlenth, originYLocation
                                        + markLineSize, markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originX + (i + 1) * XDValue),
                                        originXLocation + (i + 1) * unitXlenth - 10,
                                        originYLocation + 2 * markLineSize,
                                        markNumberPaint);
                                canvas.drawLine(originXLocation - (i + 1)
                                        * unitXlenth, originYLocation
                                        - markLineSize, originXLocation - (i + 1)
                                        * unitXlenth, originYLocation
                                        + markLineSize, markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originX - (i + 1) * XDValue),
                                        originXLocation - (i + 1) * unitXlenth - 10,
                                        originYLocation + 2 * markLineSize,
                                        markNumberPaint);
                            }
                            // 奇数得多画一个才行
                            canvas.drawLine(originXLocation + (trueCount + 1)
                                            * unitXlenth, originYLocation - markLineSize,
                                    originXLocation + (trueCount + 1) * unitXlenth,
                                    originYLocation + markLineSize, markNumberPaint);
                            canvas.drawText(
                                    String.valueOf(originX + (trueCount + 1)
                                            * XDValue), originXLocation
                                            + (trueCount + 1) * unitXlenth - 10,
                                    originYLocation + 2 * markLineSize,
                                    markNumberPaint);
                            startX = originX - trueCount * XDValue;
                        }
                    }
                    // 绘制Y轴数字
                    if (YDValue != 0 && YCount != 0) {

                        if (YCount % 2 == 0) {
                            // 如果Ycount是偶数
                            int trueCount = YCount / 2;// 因为是先画一半再画一半所以实际的数量为
                            int YLenthForUse = height / 2 - 2 * arrowsSize;// Y轴可用长度
                            unitYlenth = YLenthForUse / trueCount;
                            for (int i = 0; i < trueCount; i++) {
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY - (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY + (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                            }
                        } else {
                            // 如果Ycount是奇数
                            int trueCount = (YCount - 1) / 2;// 因为是先画一半再画一半所以实际的数量为
                            int YLenthForUse = width / 2 - 2 * arrowsSize;// Y轴可用长度
                            unitYlenth = YLenthForUse / (trueCount + 1);
                            for (int i = 0; i < trueCount; i++) {
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY - (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY + (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                            }
                            // 奇数得多往上画一个才行
                            canvas.drawLine(originXLocation - markLineSize,
                                    originYLocation - (trueCount + 1) * unitYlenth,
                                    originXLocation + markLineSize, originYLocation
                                            - (trueCount + 1) * unitYlenth,
                                    markNumberPaint);
                            canvas.drawText(
                                    String.valueOf(originY + (trueCount + 1)
                                            * YDValue), originXLocation
                                            - markLineSize, originYLocation
                                            - (trueCount + 1) * unitYlenth,
                                    markNumberPaint);
                        }
                    }
                    drawBrokenLine(canvas, originXLocation, originYLocation);
                    break;
                case HalfX:
                    // X轴
                    canvas.drawLine(2 * arrowsSize, height / 2, width, height / 2,
                            axlePaint);
                    // Y轴
                    canvas.drawLine(2 * arrowsSize, 0, 2 * arrowsSize, height,
                            axlePaint);
                    // X箭头
                    canvas.drawLine(width, height / 2, width - arrowsSize, height
                            / 2 + arrowsSize, axlePaint);
                    canvas.drawLine(width, height / 2, width - arrowsSize, height
                            / 2 - arrowsSize, axlePaint);
                    // Y箭头
                    canvas.drawLine(2 * arrowsSize, 0, 2 * arrowsSize - arrowsSize,
                            0 + arrowsSize, axlePaint);
                    canvas.drawLine(2 * arrowsSize, 0, 2 * arrowsSize + arrowsSize,
                            0 + arrowsSize, axlePaint);
                    originXLocation = 2 * arrowsSize;
                    originYLocation = height / 2;
                    // 绘制原点
                    canvas.drawText(originX + "/" + originY, 5, originYLocation
                            + arrowsSize, markNumberPaint);
                    // 绘制X轴数字
                    if (XDValue != 0 && XCount != 0) {
                        int XLenthForUse = width - originXLocation - 2 * arrowsSize;// x轴长度
                        unitXlenth = XLenthForUse / XCount;// 将可用段等分成Xcount份,然后得到的长度
                        for (int i = 0; i < XCount; i++) {
                            canvas.drawLine(originXLocation + (i + 1) * unitXlenth,
                                    originYLocation - markLineSize, originXLocation
                                            + (i + 1) * unitXlenth, originYLocation
                                            + markLineSize, markNumberPaint);
                            canvas.drawText(
                                    String.valueOf(originX + (i + 1) * XDValue),
                                    originXLocation + (i + 1) * unitXlenth - 10,
                                    originYLocation + 2 * markLineSize,
                                    markNumberPaint);
                        }
                        startX = originX;
                        // endX = originX + XCount * XDValue;
                    }
                    // 绘制Y轴数字
                    if (YDValue != 0 && YCount != 0) {

                        if (YCount % 2 == 0) {
                            // 如果Ycount是偶数
                            int trueCount = YCount / 2;// 因为是先画一半再画一半所以实际的数量为
                            int YLenthForUse = height / 2 - 2 * arrowsSize;// Y轴可用长度
                            unitYlenth = YLenthForUse / trueCount;
                            for (int i = 0; i < trueCount; i++) {
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY - (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY + (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                            }
                        } else {
                            // 如果Ycount是奇数
                            int trueCount = (YCount - 1) / 2;// 因为是先画一半再画一半所以实际的数量为
                            int YLenthForUse = height / 2 - 2 * arrowsSize;// Y轴可用长度
                            unitYlenth = YLenthForUse / (trueCount + 1);
                            for (int i = 0; i < trueCount; i++) {
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        originXLocation + markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY - (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation + (i + 1) * unitYlenth,
                                        markNumberPaint);
                                canvas.drawText(
                                        String.valueOf(originY + (i + 1) * YDValue),
                                        originXLocation - markLineSize,
                                        originYLocation - (i + 1) * unitYlenth,
                                        markNumberPaint);
                            }
                            // 奇数得多往上画一个才行
                            canvas.drawLine(originXLocation - markLineSize,
                                    originYLocation - (trueCount + 1) * unitYlenth,
                                    originXLocation + markLineSize, originYLocation
                                            - (trueCount + 1) * unitYlenth,
                                    markNumberPaint);
                            canvas.drawText(
                                    String.valueOf(originY + (trueCount + 1)
                                            * YDValue), originXLocation
                                            - markLineSize, originYLocation
                                            - (trueCount + 1) * unitYlenth,
                                    markNumberPaint);
                        }
                    }
                    drawBrokenLine(canvas, originXLocation, originYLocation);
                    break;
                case HalfXY:
                    // X轴
                    canvas.drawLine(2 * arrowsSize, height - 2 * arrowsSize, width,
                            height - 2 * arrowsSize, axlePaint);
                    // Y轴
                    canvas.drawLine(2 * arrowsSize, 0, 2 * arrowsSize, height - 2
                            * arrowsSize, axlePaint);
                    // X箭头
                    canvas.drawLine(width, height - 2 * arrowsSize, width
                                    - arrowsSize, height - 2 * arrowsSize + arrowsSize,
                            axlePaint);
                    canvas.drawLine(width, height - 2 * arrowsSize, width
                                    - arrowsSize, height - 2 * arrowsSize - arrowsSize,
                            axlePaint);
                    // Y箭头
                    canvas.drawLine(2 * arrowsSize, 0, 2 * arrowsSize - arrowsSize,
                            0 + arrowsSize, axlePaint);
                    canvas.drawLine(2 * arrowsSize, 0, 2 * arrowsSize + arrowsSize,
                            0 + arrowsSize, axlePaint);

                    originXLocation = 2 * arrowsSize;
                    originYLocation = height - 2 * arrowsSize;
                    // 绘制原点
                    canvas.drawText((int) originX + "/" + (int) originY, 5, originYLocation
                            + arrowsSize, markNumberPaint);
                    // 绘制X轴数字
                    if (XDValue != 0 && XCount != 0) {
                        int XLenthForUse = width - originXLocation - 2 * arrowsSize;// x轴长度
                        unitXlenth = XLenthForUse / XCount;// 将可用段等分成Xcount份,然后得到的长度
                        for (int i = 0; i < XCount; i++) {
                            canvas.drawLine(originXLocation + (i + 1) * unitXlenth,
                                    originYLocation - markLineSize, originXLocation
                                            + (i + 1) * unitXlenth, originYLocation
                                            + markLineSize, markNumberPaint);
                            if (null != xPointNameList && xPointNameList.length == XCount) {
                                canvas.drawText(
                                        xPointNameList[i],
                                        originXLocation + (i + 1) * unitXlenth - 10,
                                        originYLocation + 2 * markLineSize,
                                        markNumberPaint);
                            } else {
                                canvas.drawText(
                                        String.valueOf((int) (originX + (i + 1) * XDValue)),
                                        originXLocation + (i + 1) * unitXlenth - 10,
                                        originYLocation + 2 * markLineSize,
                                        markNumberPaint);
                            }
                        }
                        startX = originX;
                        // endX = originX + XCount * XDValue;
                    }
                    // 绘制Y轴数字
                    if (YDValue != 0 && YCount != 0) {
                        int YLenthForUse = height - 4 * arrowsSize;// Y轴可用长度
                        unitYlenth = YLenthForUse / YCount;
                        // 绘制Y轴数字
//                        for (int i = 0; i < YCount; i++) {
//                            canvas.drawLine(originXLocation - markLineSize,
//                                    originYLocation - (i + 1) * unitYlenth,
//                                    originXLocation + markLineSize, originYLocation
//                                            - (i + 1) * unitYlenth, markNumberPaint);
//                            canvas.drawText(
//                                    String.valueOf((int) (originY + (i + 1) * YDValue)),
//                                    originXLocation - markLineSize, originYLocation
//                                            - (i + 1) * unitYlenth, markNumberPaint);
//                        }
                        //仅绘制有值的Y TODO
                        for (int i = 0; i < pointLineList.size(); i++) {
                            if (pointLineList.get(i).y != originY) {
                                canvas.drawLine(originXLocation - markLineSize,
                                        originYLocation - (pointLineList.get(i).y - originY) * unitYlenth,
                                        originXLocation + markLineSize, originYLocation
                                                - (pointLineList.get(i).y - originY) * unitYlenth, markNumberPaint);
                                canvas.drawText(
                                        String.valueOf((int) (pointLineList.get(i).y)),
                                        originXLocation - markLineSize, originYLocation
                                                - (pointLineList.get(i).y - originY) * unitYlenth, markNumberPaint);
                            }
                        }
                        drawBrokenLine(canvas, originXLocation, originYLocation);
                        break;
                    }
            }
            // 绘制XY的说明
            if (!TextUtils.isEmpty(XText)) {
                Log.d("sss", XText);
                Path path = new Path();
                path.moveTo(0, 0);
                path.lineTo(width, 0);
                canvas.drawTextOnPath("X:" + XText, path, textXMargin - 200,
                        textYMargin, markNumberPaint);
            }
            if (!TextUtils.isEmpty(YText)) {
                Log.d("sss", YText);
                Path path = new Path();
                path.moveTo(0, textYMargin);
                path.lineTo(width, textYMargin);
                canvas.drawTextOnPath("Y:" + YText, path, textXMargin - 50,
                        textYMargin, markNumberPaint);
            }
        }
    }

    private void drawBrokenLine(Canvas canvas, int originXLocation,
                                int originYLocation) {
        // 绘制曲线图
        if (pointLineList.size() > 0) {
            PointF item = pointLineList.get(0);
            // 真正意义上的XY原点的位置
            float trueOriginX = originXLocation
                    + ((item.x - originX) / XDValue) * unitXlenth;
            float trueOriginY = originYLocation
                    - ((item.y - originY) / YDValue) * unitYlenth;
            Path path = new Path();
            path.moveTo(trueOriginX, trueOriginY);
            Log.d("sss", "��ʼ��" + "TX:" + trueOriginX + "TY:" + trueOriginY
                    + "item.x" + item.x + "item.y" + item.y + "originX:"
                    + originX + "originY:" + originY + "XDValue" + XDValue
                    + "YDValue" + YDValue + "unitXlenth" + unitXlenth
                    + "unitYlenth" + unitYlenth);
            int temp = 0;// 因为当为无穷时 XYlist就会少一位,然后specialpoint就会多一位
            if (specialPoint.size() > 0 && 0 == specialPoint.get(0)) {
                // 因为i是从1开始的,所以当第0个位置就是无穷时,并没有正确的移除specialpoint的第0位,所以这里做下判断
                temp++;
                specialPoint.remove(0);
            }
            for (int i = 1; i < pointLineList.size(); i++) {
                item = pointLineList.get(i);
                trueOriginX = originXLocation + ((item.x - originX) / XDValue)
                        * unitXlenth;
                trueOriginY = originYLocation + ((originY - item.y) / YDValue)
                        * unitYlenth;
                // 如果这个点是无穷点则moveto 因为这种情况下Xlist.size与XYList.size数值不同
                if (specialPoint.size() > 0
                        && (i + temp) == specialPoint.get(0)) {
                    path.moveTo(trueOriginX, trueOriginY);
                    temp++;
                    Log.d("sss", "specialPoint:" + specialPoint.get(0));
                    specialPoint.remove(0);
                } else {
                    path.lineTo(trueOriginX, trueOriginY);
                }
                Log.d("sss", "TX:" + trueOriginX + "TY:" + trueOriginY
                        + "item.x" + item.x + "item.y" + item.y);
            }
            canvas.drawPath(path, brokenLinePaint);
        }
    }

    public void setPointLineList(ArrayList<PointF> pointLineList) {
        this.pointLineList = pointLineList;
    }


    public void setArrowsSize(int arrowsSize) {
        this.arrowsSize = arrowsSize;
    }

    public void setNumberMarkSize(int numberMarkSize) {
        this.numberMarkSize = numberMarkSize;
    }

    public void setNumberSize(int numberSize) {
        this.numberSize = numberSize;
    }

    public void setXText(String xText) {
        XText = xText;
        invalidate();  // 重新绘制区域
    }

    public void setYText(String yText) {
        YText = yText;
    }

    public void setXDValue(int xDValue, int xCount) {
        XDValue = xDValue;
        XCount = xCount;
    }

    public void setYDValue(int yDValue, int yCount) {
        YDValue = yDValue;
        YCount = yCount;
    }

    public void setAxleType(AxleType axleType) {
        this.axleType = axleType;
    }

    public String getFormula() {
        return formula;
    }

    public void setBrokenLineSet(BrokenLineSet brokenLineSet) {
        // this.brokenLineSet = brokenLineSet;
        if (null != brokenLineSet.getAxle_type()) {
            axleType = brokenLineSet.getAxle_type();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getOrigin_x()))) {
            originX = brokenLineSet.getOrigin_x();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getOrigin_y()))) {
            originY = brokenLineSet.getOrigin_y();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getX_d_value()))) {
            XDValue = brokenLineSet.getX_d_value();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getY_d_value()))) {
            YDValue = brokenLineSet.getY_d_value();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getX_count()))) {
            XCount = brokenLineSet.getX_count();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getY_count()))) {
            YCount = brokenLineSet.getY_count();
        }
        if (!TextUtils.isEmpty(brokenLineSet.getX_text())) {
            XText = brokenLineSet.getX_text();
        }
        if (!TextUtils.isEmpty(brokenLineSet.getY_text())) {
            YText = brokenLineSet.getY_text();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getAxleColor()))) {
            axleColor = brokenLineSet.getAxleColor();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getMarkColor()))) {
            markColor = brokenLineSet.getMarkColor();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getLineColor()))) {
            lineColor = brokenLineSet.getLineColor();
        }
        if (!TextUtils.isEmpty(String.valueOf(brokenLineSet.getPrecision()))) {
            precision = brokenLineSet.getPrecision();
        }
        Log.d("line", "���ǿ�?" + originX);
        invalidate();
    }

    private void initXList() {
        XList = new float[XCount * precision + 1];
        for (int i = 0; i < XCount * precision + 1; i++) {
            XList[i] = startX + XDValue / precision * i;
        }
    }

    /**
     * 判断是否是整数
     */
    private boolean verifyCanToInt(String num) {
        if (num.contains(".") && num.substring(num.length() - 1).equals("0")
                && !num.contains("E")) {
            return true;
        } else {
            return false;
        }
    }
    // 这方法本来就是公用的
    // public void ViewInvalidate() {
    // invalidate();// 重新绘制区域
    // }
}
