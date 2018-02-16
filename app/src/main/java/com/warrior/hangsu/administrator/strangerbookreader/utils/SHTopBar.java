package com.warrior.hangsu.administrator.strangerbookreader.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;


public class SHTopBar extends LinearLayout {
    private ImageView right1IV, right2IV;
    private TextView right1TV, right2TV;
    private ImageView left1IV, left2IV, left3IV;
    private TextView left1TV, left2TV, left3TV;
    private View right1Layout, right2Layout;
    private View left1Layout, left2Layout, left3Layout;
    private View tip1, tip2, tip3;
    private View topBarLayout;
    private Drawable topBarBg;
    private OnTopBarClickListener l;

    public SHTopBar(Context context) {
        super(context);
        init();
    }

    public SHTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.SHTopBar);
        topBarBg = ta.getDrawable(R.styleable.SHTopBar_topbar_background);

        Drawable left1Bg = ta.getDrawable(R.styleable.SHTopBar_left1_background);
        Drawable left2Bg = ta.getDrawable(R.styleable.SHTopBar_left2_background);
        Drawable left3Bg = ta.getDrawable(R.styleable.SHTopBar_left3_background);
        Drawable right1Bg = ta.getDrawable(R.styleable.SHTopBar_right1_background);
        Drawable right2Bg = ta.getDrawable(R.styleable.SHTopBar_right2_background);


        Drawable leftLayout1Bg = ta
                .getDrawable(R.styleable.SHTopBar_left1_press_background);
        Drawable leftLayout2Bg = ta
                .getDrawable(R.styleable.SHTopBar_left2_press_background);
        Drawable leftLayout3Bg = ta
                .getDrawable(R.styleable.SHTopBar_left3_press_background);

        float right1TextSize = ta.getDimension(
                R.styleable.SHTopBar_right1_textSize, 18);
        float right2TextSize = ta.getDimension(
                R.styleable.SHTopBar_right2_textSize, 18);

        float left1TextSize = ta.getDimension(R.styleable.SHTopBar_left1_textSize,
                18);
        float left2TextSize = ta.getDimension(R.styleable.SHTopBar_left2_textSize,
                18);
        float left3TextSize = ta.getDimension(R.styleable.SHTopBar_left3_textSize,
                18);

        String right1Text = ta.getString(R.styleable.SHTopBar_right1_text);
        String right2Text = ta.getString(R.styleable.SHTopBar_right2_text);
        String left1Text = ta.getString(R.styleable.SHTopBar_left1_text);
        String left2Text = ta.getString(R.styleable.SHTopBar_left2_text);
        String left3Text = ta.getString(R.styleable.SHTopBar_left3_text);
        int right1TextColor = ta.getColor(R.styleable.SHTopBar_right1_textColor,
                Color.WHITE);
        int right2TextColor = ta.getColor(R.styleable.SHTopBar_right2_textColor,
                Color.WHITE);
        int left1TextColor = ta.getColor(R.styleable.SHTopBar_left1_textColor,
                Color.WHITE);
        int left2TextColor = ta.getColor(R.styleable.SHTopBar_left2_textColor,
                Color.WHITE);
        int left3TextColor = ta.getColor(R.styleable.SHTopBar_left3_textColor,
                Color.WHITE);
        ta.recycle();
        if (null != left1Bg)
            left1IV.setImageDrawable(left1Bg);
        if (null != left2Bg)
            left2IV.setImageDrawable(left2Bg);
        if (null != left3Bg)
            left3IV.setImageDrawable(left3Bg);
        if (null != right1Bg) {
            right1IV.setImageDrawable(right1Bg);
        }
        if (null != right2Bg) {
            right2IV.setImageDrawable(right2Bg);
        }
        right1TV.setText(right1Text);
        right1TV.setTextColor(right1TextColor);
        right1TV.setTextSize(right1TextSize);
        right2TV.setText(right2Text);
        right2TV.setTextColor(right2TextColor);
        right2TV.setTextSize(right2TextSize);

        left1TV.setText(left1Text);
        left1TV.setTextColor(left1TextColor);
        left1TV.setTextSize(left1TextSize);
        left2TV.setText(left2Text);
        left2TV.setTextColor(left2TextColor);
        left2TV.setTextSize(left2TextSize);
        left3TV.setText(left3Text);
        left3TV.setTextColor(left3TextColor);
        left3TV.setTextSize(left3TextSize);
        if (null == right1Bg && TextUtils.isEmpty(right1Text)) {
            right1Layout.setVisibility(View.GONE);
        } else {
            right1Layout.setVisibility(View.VISIBLE);
        }
        if (null == right2Bg && TextUtils.isEmpty(right2Text)) {
            right2Layout.setVisibility(View.GONE);
        } else {
            right2Layout.setVisibility(View.VISIBLE);
        }

        if (null == left1Bg && TextUtils.isEmpty(left1Text)) {
            left1Layout.setVisibility(View.GONE);
        } else {
            left1Layout.setVisibility(View.VISIBLE);
        }
        if (null == left2Bg && TextUtils.isEmpty(left2Text)) {
            left2Layout.setVisibility(View.GONE);
        } else {
            left2Layout.setVisibility(View.VISIBLE);
        }
        if (null == left3Bg && TextUtils.isEmpty(left3Text)) {
            left3Layout.setVisibility(View.GONE);
        } else {
            left3Layout.setVisibility(View.VISIBLE);
        }


        if (null != topBarBg) {
            topBarLayout.setBackgroundDrawable(topBarBg);
        }
        if (null != leftLayout1Bg) {
            left1Layout.setBackgroundDrawable(leftLayout1Bg);
        }
        if (null != leftLayout2Bg) {
            left2Layout.setBackgroundDrawable(leftLayout2Bg);
        }
        if (null != leftLayout3Bg) {
            left3Layout.setBackgroundDrawable(leftLayout3Bg);
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_sh_topbar, this);

        left1IV = (ImageView) findViewById(R.id.left1_iv);
        left2IV = (ImageView) findViewById(R.id.left2_iv);
        left3IV = (ImageView) findViewById(R.id.left3_iv);
        left1TV = (TextView) findViewById(R.id.left1_tv);
        left2TV = (TextView) findViewById(R.id.left2_tv);
        left3TV = (TextView) findViewById(R.id.left3_tv);

        right1IV = (ImageView) findViewById(R.id.right1_iv);
        right2IV = (ImageView) findViewById(R.id.right2_iv);
        right1TV = (TextView) findViewById(R.id.right1_tv);
        right2TV = (TextView) findViewById(R.id.right2_tv);

        right1Layout = findViewById(R.id.right1_layout);
        right2Layout = findViewById(R.id.right2_layout);
        left1Layout = findViewById(R.id.left1);
        left2Layout = findViewById(R.id.left2);
        left3Layout = findViewById(R.id.left3);

        tip1 = findViewById(R.id.tip1);
        tip2 = findViewById(R.id.tip2);
        tip3 = findViewById(R.id.tip3);

        topBarLayout = findViewById(R.id.topbar_layout);

        left1Layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cutNowLeftView(1);
                if (null != l)
                    l.onLeft1Click();
            }
        });
        left2Layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cutNowLeftView(2);
                if (null != l)
                    l.onLeft2Click();
            }
        });
        left3Layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cutNowLeftView(3);
                if (null != l)
                    l.onLeft3Click();
            }
        });
        right1Layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != l)
                    l.onRight1Click();
            }
        });
        right2Layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != l)
                    l.onRight2Click();
            }
        });
    }

    public void setOnTopBarClickListener(OnTopBarClickListener l) {
        this.l = l;
    }

    public void setTopBarBackground(int color) {
        // TODO
        topBarLayout.setBackgroundColor(color);
    }


    public void setRight1Background(Drawable bg) {
        right1Layout.setVisibility(View.VISIBLE);
        right1IV.setVisibility(View.VISIBLE);
        right1IV.setImageDrawable(bg);
        right1TV.setVisibility(View.GONE);
    }

    public void setRight2Background(Drawable bg) {
        right2Layout.setVisibility(View.VISIBLE);
        right2IV.setVisibility(View.VISIBLE);
        right2IV.setImageDrawable(bg);
        right2TV.setVisibility(View.GONE);
    }

    public void setRight1Text(String text) {
        right1Layout.setVisibility(View.VISIBLE);
        right1TV.setVisibility(View.VISIBLE);
        right1TV.setText(text);
        right1IV.setVisibility(View.GONE);
    }

    public void setRight2Text(String text) {
        right2Layout.setVisibility(View.VISIBLE);
        right2TV.setVisibility(View.VISIBLE);
        right2TV.setText(text);
        right2IV.setVisibility(View.GONE);
    }

    public void setLeft1Text(String text) {
        left1Layout.setVisibility(View.VISIBLE);
        left1TV.setVisibility(View.VISIBLE);
        left1TV.setText(text);
        left1IV.setVisibility(View.GONE);
    }

    public void setLeft2Text(String text) {
        left2Layout.setVisibility(View.VISIBLE);
        left2TV.setVisibility(View.VISIBLE);
        left2TV.setText(text);
        left2IV.setVisibility(View.GONE);
    }

    public void setLeft3Text(String text) {
        left3Layout.setVisibility(View.VISIBLE);
        left3TV.setVisibility(View.VISIBLE);
        left3TV.setText(text);
        left3IV.setVisibility(View.GONE);
    }

    public void cutNowLeftView(int position) {
        switch (position) {
            case 1:
                tip1.setVisibility(VISIBLE);
                tip2.setVisibility(GONE);
                tip3.setVisibility(GONE);
                break;
            case 2:
                tip1.setVisibility(GONE);
                tip2.setVisibility(VISIBLE);
                tip3.setVisibility(GONE);
                break;
            case 3:
                tip1.setVisibility(GONE);
                tip2.setVisibility(GONE);
                tip3.setVisibility(VISIBLE);
                break;
        }
    }

    public interface OnTopBarClickListener {
        public void onLeft1Click();

        public void onLeft2Click();

        public void onLeft3Click();

        public void onRight1Click();

        public void onRight2Click();
    }
}
