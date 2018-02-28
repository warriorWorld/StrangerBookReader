package com.warrior.hangsu.administrator.strangerbookreader.widget.breakline;


import java.io.Serializable;

public class BrokenLineSet implements Serializable {
    private BrokenLineView.AxleType axle_type = BrokenLineView.AxleType.FullXY;
    private int arrows_size = 15, number_mark_size = 5, number_size = 10;// 箭头标记数字大小
    private float origin_x = 0, origin_y = 0;// 原点值
    private float x_d_value = 1, y_d_value = 1;// 数轴数字差值
    private int x_count = 10, y_count = 10;// 数轴数字总数量
    private String x_text, y_text;// xy轴分别代表什么
    private int background;// 背景色
    private int axleColor, markColor, lineColor;// 各种颜色
    private int precision = 3;// 精度

    public BrokenLineView.AxleType getAxle_type() {
        return axle_type;
    }

    public void setAxle_type(BrokenLineView.AxleType axle_type) {
        this.axle_type = axle_type;
    }

    public int getArrows_size() {
        return arrows_size;
    }

    public void setArrows_size(int arrows_size) {
        this.arrows_size = arrows_size;
    }

    public int getNumber_mark_size() {
        return number_mark_size;
    }

    public void setNumber_mark_size(int number_mark_size) {
        this.number_mark_size = number_mark_size;
    }

    public int getNumber_size() {
        return number_size;
    }

    public void setNumber_size(int number_size) {
        this.number_size = number_size;
    }

    public float getOrigin_x() {
        return origin_x;
    }

    public void setOrigin_x(float origin_x) {
        this.origin_x = origin_x;
    }

    public float getOrigin_y() {
        return origin_y;
    }

    public void setOrigin_y(float origin_y) {
        this.origin_y = origin_y;
    }

    public float getX_d_value() {
        return x_d_value;
    }

    public void setX_d_value(float x_d_value) {
        this.x_d_value = x_d_value;
    }

    public float getY_d_value() {
        return y_d_value;
    }

    public void setY_d_value(float y_d_value) {
        this.y_d_value = y_d_value;
    }

    public int getX_count() {
        return x_count;
    }

    public void setX_count(int x_count) {
        this.x_count = x_count;
    }

    public int getY_count() {
        return y_count;
    }

    public void setY_count(int y_count) {
        this.y_count = y_count;
    }

    public String getX_text() {
        return x_text;
    }

    public void setX_text(String x_text) {
        this.x_text = x_text;
    }

    public String getY_text() {
        return y_text;
    }

    public void setY_text(String y_text) {
        this.y_text = y_text;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getAxleColor() {
        return axleColor;
    }

    public void setAxleColor(int axleColor) {
        this.axleColor = axleColor;
    }

    public int getMarkColor() {
        return markColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

}
