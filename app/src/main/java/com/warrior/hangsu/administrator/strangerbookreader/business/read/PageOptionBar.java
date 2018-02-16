package com.warrior.hangsu.administrator.strangerbookreader.business.read;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class PageOptionBar extends RelativeLayout {
    private Context context;
    private ImageView typefaceV, wordSizeV, nightV, clickTimeV;
    private TextView clickTimeTv;
    private OnPageOptionsBarClickListener l;
    private DiscreteSeekBar seekBar;
    private int finalPosition;
    private boolean isSun = true;
    private boolean singleClick = true;

    public PageOptionBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PageOptionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.page_options, this);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        initSeekBar();

        wordSizeV = (ImageView) findViewById(R.id.word_size);
        typefaceV = (ImageView) findViewById(R.id.typeface);
        nightV = (ImageView) findViewById(R.id.night);
        clickTimeV = (ImageView) findViewById(R.id.click_time);
        clickTimeTv = (TextView) findViewById(R.id.click_time_tv);
        String single = SharedPreferencesUtils.getSharedPreferencesData(context,
                "singleClick");
        if (!"false".equals(single)) {
            //这样保证了 默认值是true
            singleClick = true;
        } else {
            singleClick = false;
        }
        wordSizeV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != l) {
                    l.onWordSizeVClick();
                }
            }
        });
        typefaceV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != l) {
                    l.onTypeFaceVClick();
                }
            }
        });
        nightV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSunMoon();
                if (null != l) {
                    l.onNightVClick();
                }
            }
        });
        clickTimeV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleClickTime();
            }
        });
    }

    private void toggleClickTime() {
        if (singleClick) {
            clickTimeTv.setText("1");
        } else {
            clickTimeTv.setText("2");
        }
        if (null != l) {
            l.onClickTimeClick(singleClick);
        }
        SharedPreferencesUtils.setSharedPreferencesData(
                context, "singleClick", singleClick + "");
        singleClick = !singleClick;
    }

    private void toggleSunMoon() {
        if (isSun) {
            nightV.setImageResource(R.drawable.sun);
        } else {
            nightV.setImageResource(R.drawable.night);
        }
        isSun = !isSun;
    }

    /**
     * 进度条
     */
    private void initSeekBar() {

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                finalPosition = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (finalPosition >= 0) {
                    if (null != l) {
                        l.onPercentChange(finalPosition);
                    }
                }
            }
        });
    }

    public void setNowPercent(int percent) {
        seekBar.setProgress(percent);
    }

    public void setOnPageOptionsBarClickListener(OnPageOptionsBarClickListener l) {
        this.l = l;
        //在这调用一遍 让他上来就是正确的值
        toggleClickTime();
    }


    public interface OnPageOptionsBarClickListener {
        public void onTypeFaceVClick();

        public void onWordSizeVClick();

        public void onNightVClick();

        public void onPercentChange(int percent);

        public void onClickTimeClick(boolean singleClick);
    }
}
