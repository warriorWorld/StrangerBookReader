//package com.warrior.hangsu.administrator.strangerbookreader.animation;
//
//import android.animation.Animator;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Typeface;
//import android.support.v7.app.AlertDialog;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.VelocityTracker;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.warrior.hangsu.administrator.strangerbookreader.R;
//import com.warrior.hangsu.administrator.strangerbookreader.analysis.AnalysisBook;
//import com.warrior.hangsu.administrator.strangerbookreader.analysis.AnalysisTXTBook;
//import com.warrior.hangsu.administrator.strangerbookreader.business.read.PageOptionBar;
//import com.warrior.hangsu.administrator.strangerbookreader.business.read.ReadView;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.DisplayUtil;
//import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.Logger;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
//
///**
// * 动画布局  为子view提供翻页动画效果
// * Created by Administrator on 2016/7/24.
// */
//public class ReadAnimationLayout extends RelativeLayout {
//    private Context context;
//    private LayoutParams lp, optionLp;//子view的lp
//    private ReadView read1View, read2View;
//    private PageOptionBar pageOptionBar;
//    private int PAGE_OPTION_BAR_HEIGHT = 80;
//    private float firstDownX, firstDownY;//触摸点位置
//    private int screenWidth, screenHeight;//屏幕宽度
//    private int duration = 300;//动画时长
//    private AnalysisBook ab;//书籍文件解析类
//    private String previsousContent, currentContent, nextContent;//前一页 后一页 当前页内容
//    private int nowPosition, previsousPosition, nextPosition;//当前前一页 后一页文字位置
//    private boolean is2Next;//判断是向左还是向右滑动
//    private boolean canChangePage;
//    private float CHANGE_PAGE_THRESHOLD;//翻页阀值
//    private float DRAG_THRESHOLD = 80;//滑动初始阀值
//    private float INTERCEPT_THRESHOLD = 20;//滑动初始阀值
//    private boolean isDay = true;
//    private String[] typefaceList = {"default", "acmesab", "Boomerang", "carmina", "diskustd",
//            "erasOutline"}, textSizeList = {"小", "一般", "大", "超大"};
//    public Typeface face;
//    private int[] textSizeVList = {15, 18, 24, 30};
//    /**
//     * 滑动速度检测类
//     */
//    private VelocityTracker mVelocityTracker;
//
//    /**
//     * 百分比监听
//     *
//     * @param context
//     */
//    private OnPercentChangeListener onPercentChangeListener;
//
//
//    public ReadAnimationLayout(Context context) {
//        this(context, null);
//    }
//
//    public ReadAnimationLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.context = context;
//        init();
//        setCurrentContent2Read1View();
//    }
//
//    public int getNowPosition() {
//        return nowPosition;
//    }
//
//    private void setCurrentContent2Read1View() {
//        //获取当前页内容
//        currentContent = ab.getBookContent(nowPosition, ReadView.WORD_COUNT);
//        //获取下一页内容
//        //必须先给V1设置了文字 且通过V1才能得知下一页开始的位置
//        read1View.setText(currentContent, TextView.BufferType.SPANNABLE);
//        read1View.getEachWord();
//        //必须先给V2设置了文字 且通过V2才能得知下一页开始的位置 这里虽然设置了文字 但是并不为了显示
//        read2View.setText(ab.getBookContent(nowPosition - ReadView.WORD_COUNT, ReadView.WORD_COUNT));
//    }
//
//    //获取到 前后的内容
//    private void getNextAndPrevisousContent() {
//        //这个东西得等V onlayout完成之后才能调用 否则的话会空指针
//        nextPosition = read1View.getNextStartPosition(nowPosition);
//        nextContent = ab.getBookContent(nextPosition,
//                ReadView.WORD_COUNT);
//        //获取前一页内容
//        //这个东西得等V onlayout完成之后才能调用 否则的话会空指针
//        previsousPosition = read2View.getPreviousStartPosition(nowPosition);
//        previsousContent = ab.getBookContent(previsousPosition,
//                ReadView.WORD_COUNT);
//    }
//
//    private void init() {
//        //获取上次所读位置
//        nowPosition = Globle.nowBookPosition;
//        //初始化分页器 给他个文件地址
//        if (Globle.nowBookPath.contains(".txt") || Globle.nowBookPath.contains(".TXT")) {
//            ab = new AnalysisTXTBook(Globle.nowBookPath);
//        }
//        calculatorPercent();
//        //获取屏幕宽度
//        WindowManager wm = (WindowManager) getContext()
//                .getSystemService(Context.WINDOW_SERVICE);
//        screenWidth = wm.getDefaultDisplay().getWidth();
//        screenHeight = wm.getDefaultDisplay().getHeight();
//        CHANGE_PAGE_THRESHOLD = screenWidth / 3;
//        //创建子view
//        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        read1View = new ReadView(context);
//        read1View.setMovementMethod(LinkMovementMethod.getInstance());
//        read2View = new ReadView(context);
//        initReadView();
//        //后添加的在上面
//        addView(read2View, lp);
//        addView(read1View, lp);
//        initPageOptionBar();
//    }
//
//    private void initReadView() {
//        toggleDayNight();
//        setReadViewTextSize();
//        setReadViewTypeFace();
//        read1View.setPadding(15, 15, 15, 15);
//        read2View.setPadding(15, 15, 15, 15);
//    }
//
//    private void toggleDayNight() {
//        int readBg;
//        int readTextColor;
//        if (isDay) {
//            readBg = R.color.read_bg;
//            readTextColor = getResources().getColor(R.color.black);
//        } else {
//            readBg = R.color.read_night_bg;
//            readTextColor = getResources().getColor(R.color.read_night_text);
//        }
//        read1View.setBackgroundResource(readBg);
//        read2View.setBackgroundResource(readBg);
//        read1View.setTextColor(readTextColor);
//        read1View.setSpannableTextColor(readTextColor);
//        read2View.setTextColor(readTextColor);
//        isDay = !isDay;
//    }
//
//    private void setReadViewTextSize() {
//        int size = SharedPreferencesUtils.getIntSharedPreferencesData(context, "textSize");
//        if (size == 0) {
//            //0是没获取到的默认值
//            size = textSizeVList[1];
//        }
//        read1View.setTextSize(size);
//        read2View.setTextSize(size);
//    }
//
//    private void showTextSizeListDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(
//                context);
//        builder.setTitle("选择大小");
//        builder.setItems(textSizeList, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Typeface face;
//                switch (which) {
//                    case 0:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "textSize", textSizeVList[0]);
//                        break;
//                    case 1:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "textSize", textSizeVList[1]);
//                        break;
//                    case 2:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "textSize", textSizeVList[2]);
//                        break;
//                    case 3:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "textSize", textSizeVList[3]);
//                        break;
//                }
//                setReadViewTextSize();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    private void setReadViewTypeFace() {
//        String typeface = SharedPreferencesUtils.getSharedPreferencesData(context,
//                "typeface");
//        if (!TextUtils.isEmpty(typeface)) {
//            face = Typeface.createFromAsset(context.getAssets(), typeface);
//        } else {
//            face = Typeface.DEFAULT;
//        }
//        read1View.setTypeface(face);
//        read2View.setTypeface(face);
//    }
//
//    private void showtypefaceListDialog() {
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
//                context);
//        builder.setTitle("选择字体");
//        builder.setItems(typefaceList, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Typeface face;
//                switch (which) {
//                    case 0:
//                        // face = Typeface.DEFAULT;
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "typeface", "");
//                        break;
//                    case 1:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "typeface",
//                                "typeface/acmesab.TTF");
//                        break;
//                    case 2:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "typeface",
//                                "typeface/boomerang_italic.ttf");
//                        break;
//                    case 3:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "typeface",
//                                "typeface/carmina_bold_italic.ttf");
//                        break;
//                    case 4:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "typeface",
//                                "typeface/diskustd.otf");
//                        break;
//                    case 5:
//                        SharedPreferencesUtils.setSharedPreferencesData(
//                                context, "typeface",
//                                "typeface/eras_outline.otf");
//                        break;
//                }
//                setReadViewTypeFace();
//            }
//        });
//        android.app.AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//
//    private void initPageOptionBar() {
//        optionLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PAGE_OPTION_BAR_HEIGHT);
//        pageOptionBar = new PageOptionBar(context);
//        pageOptionBar.setOnPageOptionsBarClickListener(new PageOptionBar.OnPageOptionsBarClickListener() {
//            @Override
//            public void onTypeFaceVClick() {
//                showtypefaceListDialog();
//            }
//
//            @Override
//            public void onWordSizeVClick() {
//                showTextSizeListDialog();
//            }
//
//            @Override
//            public void onNightVClick() {
//                toggleDayNight();
//            }
//
//            @Override
//            public void onPercentChange(int percent) {
//                if (null != onPercentChangeListener) {
//                    onPercentChangeListener.onPercentChange(percent);
//                }
//                nowPosition = (int) (((float) percent / 100) * ab.getBookLength());
//                setCurrentContent2Read1View();
//            }
//
//            @Override
//            public void onClickTimeClick(boolean singleClick) {
//                read1View.setSingleClick(singleClick);
//            }
//
//        });
//        addView(pageOptionBar);
//        pageOptionBar.setY(screenHeight + PAGE_OPTION_BAR_HEIGHT);
//    }
//
//    private void initDrag(MotionEvent event) {
//        //记录初始点
//        firstDownX = event.getX();
//        firstDownY = event.getY();
//        getNextAndPrevisousContent();
//        //加入速度检测
//        mVelocityTracker = VelocityTracker.obtain();
//        mVelocityTracker.addMovement(event);
//    }
//
//    private void onDrag(MotionEvent event) {
//        mVelocityTracker.addMovement(event);
//        //当前页是平移动画
//        float drag1X = event.getX() - firstDownX;
//        //根据滑动方向决定V2内容
//        if (drag1X > 0) {
//            read2View.setText(previsousContent);
//            is2Next = false;
//        } else {
//            read2View.setText(nextContent);
//            is2Next = true;
//        }
//        if (isEdge()) {
//            return;
//        }
//        if (Math.abs(drag1X) > DRAG_THRESHOLD) {
//            //大于阈值才滑动
////        drag2X = screenWidth + drag1X;
////        Logger.d("getX:" + event.getX() + "first:" + firstDownX + "screenwidth" + screenWidth);
//            //如果不加或减阈值的话 就会像卡顿一样
//            if (drag1X < 0) {
//                drag1X = drag1X + DRAG_THRESHOLD;
//            } else {
//                drag1X = drag1X - DRAG_THRESHOLD;
//            }
//            read1View.setX(drag1X);
////        read2View.setX(drag2X);
//            //第二个页面是缩放和透明度动画
//            float scale = (Math.abs(drag1X) / screenWidth) * 0.2f + 0.8f;//加权平均值 保证最大值是1 最小值是0.8
//            float alpha = (Math.abs(drag1X) / screenWidth) * 0.2f + 0.5f;//加权平均值 保证最大值是1 最小值是0.8
//            read2View.setScaleX(scale);
//            read2View.setScaleY(scale);
//            read2View.setAlpha(alpha);
//        }
//    }
//
//    //TODO 结束四种情况 左滑右滑 不到阀值 回原点 速度可以是决定因素之一
//    private void stopDrag(MotionEvent event) {
//
//        //通过滑动的距离计算出X,Y方向的速度
//        mVelocityTracker.computeCurrentVelocity(1000);
//        float velocityX = Math.abs(mVelocityTracker.getXVelocity());
//        //获取到最终移动距离
//        float endDragLenth = event.getX() - firstDownX;
//        if (endDragLenth < 0) {
//            endDragLenth = endDragLenth - velocityX / 160;
//        } else {
//            endDragLenth = endDragLenth + velocityX / 160;
//        }
//
//        //获取到最终移动距离
//        float endDragYLenth = event.getY() - firstDownY;
//        Logger.d("速度:" + velocityX + "endDragLenth" + endDragLenth);
//        //然后决定是播哪种动画
//        int v1XPosition = 0;
//        int optionPosition = screenHeight;
//        float v2Alpha = 1, v2ScaleX = 1, v2ScaleY = 1;
//        //判断是哪种情况
//        if (Math.abs(endDragLenth) > CHANGE_PAGE_THRESHOLD) {
//            if (isEdge()) {
//                //在这里return可以避免一个问题:第一页和最后一页不能调出设置项
//                return;
//            }
//            if (endDragLenth < 0) {
//                //这里不能通过istonext判断 因为如果滑动过快的话 压根就不会进入MOVE而是直接进入这里
//                v1XPosition = -screenWidth;
//                nowPosition = nextPosition;
//            } else {
//                v1XPosition = screenWidth;
//                nowPosition = previsousPosition;
//            }
//            v2Alpha = 1;
//            v2ScaleX = 1;
//            v2ScaleY = 1;
//        } else {
//            if (Math.abs(endDragYLenth) > CHANGE_PAGE_THRESHOLD) {
//                //调出选项判断
//                if (endDragYLenth > 0) {
//                    //下滑 收起选项
//                    optionPosition = screenHeight + 80;
//                } else {
//                    //上滑显示选项 20是topbar的高度
//                    optionPosition = screenHeight - DisplayUtil.dip2px(context, PAGE_OPTION_BAR_HEIGHT + 20);
//
//                    pageOptionBar.setNowPercent(calculatorPercent());
//                }
//            }
//            //滑回去
//            v1XPosition = 0;
//            v2Alpha = 0.8f;
//            v2ScaleX = 0.8f;
//            v2ScaleY = 0.8f;
//        }
//        //属性动画,可以指定起始值,也可以直接指定最终值,这样就会直接从当前值到最终值
//        ObjectAnimator translation1 = ObjectAnimator.ofFloat(read1View, "translationX", v1XPosition);
////        ObjectAnimator translation2 = ObjectAnimator.ofFloat(read2View, "translationX", 0);
//        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(read2View, "alpha", v2Alpha);
//        ObjectAnimator scale2X = ObjectAnimator.ofFloat(read2View, "scaleX", v2ScaleX);
//        ObjectAnimator scale2Y = ObjectAnimator.ofFloat(read2View, "scaleY", v2ScaleY);
//        ObjectAnimator translation3 = ObjectAnimator.ofFloat(pageOptionBar, "translationY", optionPosition);
//        AnimatorSet set = new AnimatorSet();
//        //属性动画监听类
//        set.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                calculatorPercent();
//                setCurrentContent2Read1View();
//                read1View.setX(0);
////                read2View.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        set.playTogether(translation1, alpha2, scale2X, scale2Y, translation3);
//        duration = (1 - Math.abs((int) endDragLenth) / screenWidth) * duration;
//        set.setDuration(duration);
//        set.start();
//
//        if (mVelocityTracker != null) { //移除速度检测
//            mVelocityTracker.recycle();
//            mVelocityTracker = null;
//        }
//    }
//
//    private boolean isEdge() {
//        if ((nowPosition <= 0 && !is2Next) || (nowPosition >= ab.getBookLength() && is2Next)) {
//            //开始页
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public void setOnPercentChangeListener(OnPercentChangeListener onPercentChangeListener) {
//        this.onPercentChangeListener = onPercentChangeListener;
//    }
//
//    /**
//     * 获取当前所看百分比
//     *
//     * @return
//     */
//    private int calculatorPercent() {
//        float p = (float) nowPosition / (float) ab.getBookLength();
//        int ip = (int) (p * 100);
//        if (null != onPercentChangeListener) {
//            onPercentChangeListener.onPercentChange(ip);
//        }
//        return ip;
//    }
//
//    public long getBookLenth() {
//        return ab.getBookLength();
//    }
//
//
//    public interface OnPercentChangeListener {
//        public void onPercentChange(int percent);
//    }
//
//    public ReadView getReadView() {
//        return read1View;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                //在这里初始化
//                initDrag(ev);
//                break;
//            case MotionEvent.ACTION_MOVE:
//
//                if (Math.abs(ev.getX() - firstDownX) > INTERCEPT_THRESHOLD ||
//                        Math.abs(ev.getY() - firstDownY) > INTERCEPT_THRESHOLD) {
//                    //发现是滑动事件 就自己处理了
//                    return true;
//                } else {
//                    //发现不是自己处理，该给谁给谁
//                    return false;
//                }
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                //其实这里是多余的
////                getParent().requestDisallowInterceptTouchEvent(false);
//        }
//
//        return super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                initDrag(event);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                onDrag(event);
//                break;
//            case MotionEvent.ACTION_UP:
//                stopDrag(event);
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//}
