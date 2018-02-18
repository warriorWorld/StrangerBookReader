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

import android.animation.FloatArrayEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;


import com.warrior.hangsu.administrator.strangerbookreader.enums.BookStatus;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnWordClickListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;
import com.warrior.hangsu.administrator.strangerbookreader.utils.LogUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;

/**
 * @author yuyh.
 * @date 2016/10/18.
 */
public abstract class BaseReadView extends View {
    protected Context context;
    protected int mScreenWidth;
    protected int mScreenHeight;

    protected PointF mTouch = new PointF();
    protected float actiondownX, actiondownY;
    protected float touch_down = 0; // 当前触摸点与按下时的点的差值

    protected Bitmap mCurPageBitmap, mNextPageBitmap;
    protected Canvas mCurrentPageCanvas, mNextPageCanvas;
    protected PageFactory pagefactory = null;

    protected OnReadStateChangeListener listener;
    protected String bookId;
    public boolean isPrepared = false;

    Scroller mScroller;
    private int FLIP_THRESHOLD = 30;//滑动到下一页或上一页的阈值
    private boolean is_threshold = false;//是否超越阈值
    private OnWordClickListener onWordClickListener;

    public BaseReadView(Context context, String bookId,
                        OnReadStateChangeListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.bookId = bookId;

        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();

        mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        mScroller = new Scroller(getContext());


        pagefactory = new PageFactory(getContext(), bookId);
        pagefactory.setOnReadStateChangeListener(listener);
    }

    public synchronized void init(int theme) {
        if (!isPrepared) {
            try {
                pagefactory.setBgBitmap(ThemeManager.getThemeDrawable(theme));
                // 自动跳转到上次阅读位置
                int pos[] = SettingManager.getInstance().getReadProgress(bookId);
                int ret = pagefactory.openBook(bookId, new int[]{pos[0], pos[1]});
                LogUtils.i(" startPos=" + pos[0] + " endPos=" + pos[1]);
                if (ret == 0) {
                    listener.onLoadFailure(bookId);
                    return;
                }
                pagefactory.onDraw(mCurrentPageCanvas);
                postInvalidate();
            } catch (Exception e) {
            }
            isPrepared = true;
        }
    }

    private int dx, dy;
    private long et = 0;
    private boolean cancel = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                et = System.currentTimeMillis();
                dx = (int) e.getX();
                dy = (int) e.getY();
                mTouch.x = dx;
                mTouch.y = dy;
                actiondownX = dx;
                actiondownY = dy;
                touch_down = 0;
                pagefactory.onDraw(mCurrentPageCanvas);

                is_threshold = false;
                calcCornerXY(actiondownX, actiondownY);

                setBitmaps(mCurPageBitmap, mNextPageBitmap);
                break;
            case MotionEvent.ACTION_MOVE:
                int mx = (int) e.getX();
                int my = (int) e.getY();
                cancel = (actiondownX < mScreenWidth / 2 && mx < mTouch.x) || (actiondownX > mScreenWidth / 2 && mx > mTouch.x);
                mTouch.x = mx;
                mTouch.y = my;


                //控制平移动画的
                touch_down = mTouch.x - actiondownX;
                if (touch_down < -FLIP_THRESHOLD && !is_threshold) {
                    is_threshold = true;
                    listener.onFlip();
                    BookStatus status = pagefactory.nextPage();
                    if (status == BookStatus.NO_NEXT_PAGE) {
                        ToastUtils.showSingleToast("没有下一页啦");
                        return false;
                    } else if (status == BookStatus.LOAD_SUCCESS) {
                        abortAnimation();
                        pagefactory.onDraw(mNextPageCanvas);
                    } else {
                        return false;
                    }
                } else if (touch_down > FLIP_THRESHOLD && !is_threshold) {
                    is_threshold = true;
                    listener.onFlip();
                    BookStatus status = pagefactory.prePage();
                    if (status == BookStatus.NO_PRE_PAGE) {
                        ToastUtils.showSingleToast("没有上一页啦");
                        return false;
                    } else if (status == BookStatus.LOAD_SUCCESS) {
                        abortAnimation();
                        pagefactory.onDraw(mNextPageCanvas);
                    } else {
                        return false;
                    }
                }
                //实时绘制
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                long t = System.currentTimeMillis();
                int ux = (int) e.getX();
                int uy = (int) e.getY();


                if (!is_threshold) {
                    if ((Math.abs(ux - dx) != 0)) {
                        pagefactory.cancelPage();
                        restoreAnimation();
                    }
                    if ((t - et < 1000)) { // 单击
                        onWordClickListener.onWordClick(pagefactory.getClickWord((int) actiondownX, (int) actiondownY));
                    } else { // 长按
                        onWordClickListener.onWordClick(pagefactory.getClickWord((int) actiondownX, (int) actiondownY));
                    }
                    return true;
                }
                if (cancel) {
                    pagefactory.cancelPage();
                    restoreAnimation();
                    postInvalidate();
                } else {
                    startAnimation();
                    postInvalidate();
                }
                cancel = false;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calcPoints();
        drawCurrentPageArea(canvas);
        drawNextPageAreaAndShadow(canvas);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas);
    }

    protected abstract void drawNextPageAreaAndShadow(Canvas canvas);

    protected abstract void drawCurrentPageShadow(Canvas canvas);

    protected abstract void drawCurrentBackArea(Canvas canvas);

    protected abstract void drawCurrentPageArea(Canvas canvas);

    protected abstract void calcPoints();

    protected abstract void calcCornerXY(float x, float y);

    /**
     * 开启翻页
     */
    protected abstract void startAnimation();

    /**
     * 停止翻页动画（滑到一半调用停止的话  翻页效果会卡住 可调用#{restoreAnimation} 还原效果）
     */
    protected abstract void abortAnimation();

    /**
     * 还原翻页
     */
    protected abstract void restoreAnimation();

    protected abstract void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap);

    public abstract void setTheme(int theme);

    /**
     * 复位触摸点位
     */
    protected void resetTouchPoint() {
        mTouch.x = 0.1f;
        mTouch.y = 0.1f;
        touch_down = 0;
        calcCornerXY(mTouch.x, mTouch.y);
    }


    public void nextPage() {
        BookStatus status = pagefactory.nextPage();
        if (status == BookStatus.NO_NEXT_PAGE) {
            ToastUtils.showSingleToast("没有下一页啦");
            return;
        } else if (status == BookStatus.LOAD_SUCCESS) {
            if (isPrepared) {
                pagefactory.onDraw(mCurrentPageCanvas);
                pagefactory.onDraw(mNextPageCanvas);
                postInvalidate();
            }
        } else {
            return;
        }

    }

    public void prePage() {
        BookStatus status = pagefactory.prePage();
        if (status == BookStatus.NO_PRE_PAGE) {
            ToastUtils.showSingleToast("没有上一页啦");
            return;
        } else if (status == BookStatus.LOAD_SUCCESS) {
            if (isPrepared) {
                pagefactory.onDraw(mCurrentPageCanvas);
                pagefactory.onDraw(mNextPageCanvas);
                postInvalidate();
            }
        } else {
            return;
        }
    }

    public synchronized void setFontSize(final int fontSizePx) {
        resetTouchPoint();
        pagefactory.setTextFont(fontSizePx);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            pagefactory.onDraw(mNextPageCanvas);
            //SettingManager.getInstance().saveFontSize(bookId, fontSizePx);
            SettingManager.getInstance().saveFontSize(fontSizePx);
            postInvalidate();
        }
    }

    public synchronized void setTextColor(int textColor, int titleColor) {
        resetTouchPoint();
        pagefactory.setTextColor(textColor, titleColor);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            pagefactory.onDraw(mNextPageCanvas);
            postInvalidate();
        }
    }

    public void setBattery(int battery) {
        pagefactory.setBattery(battery);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            postInvalidate();
        }
    }

    public void setTime(String time) {
        pagefactory.setTime(time);
    }

    public void setPosition(int[] pos) {
        int ret = pagefactory.openBook(bookId, new int[]{pos[0], pos[1]});
        if (ret == 0) {
            listener.onLoadFailure(bookId);
            return;
        }
        pagefactory.onDraw(mCurrentPageCanvas);
        postInvalidate();
    }

    public int[] getReadPos() {
        return pagefactory.getPosition();
    }

    public String getHeadLine() {
        return pagefactory.getHeadLineStr().replaceAll("@", "");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (pagefactory != null) {
            pagefactory.recycle();
        }

        if (mCurPageBitmap != null && !mCurPageBitmap.isRecycled()) {
            mCurPageBitmap.recycle();
            mCurPageBitmap = null;
            LogUtils.d("mCurPageBitmap recycle");
        }

        if (mNextPageBitmap != null && !mNextPageBitmap.isRecycled()) {
            mNextPageBitmap.recycle();
            mNextPageBitmap = null;
            LogUtils.d("mNextPageBitmap recycle");
        }
    }

    public void setOnWordClickListener(OnWordClickListener onWordClickListener) {
        this.onWordClickListener = onWordClickListener;
    }
}
