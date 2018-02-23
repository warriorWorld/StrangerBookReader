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
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;

import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.strangerbookreader.manager.SettingManager;
import com.warrior.hangsu.administrator.strangerbookreader.manager.ThemeManager;

import java.util.List;

/**
 * @author yuyh.
 * @date 2016/10/18.
 */
public class OverlappedWidget extends BaseReadView {

    private Path mPath0;

    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;

    public OverlappedWidget(Context context, String bookId,
                            OnReadStateChangeListener listener) {
        super(context, bookId, listener);

        mTouch.x = 0.01f;
        mTouch.y = 0.01f;

        mPath0 = new Path();

        int[] mBackShadowColors = new int[]{0xaa666666, 0x666666};
        mBackShadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    @Override
    protected void drawCurrentPageArea(Canvas canvas) {
        mPath0.reset();

        canvas.save();
//        if (actiondownX > mScreenWidth >> 1) {//>>1相当于除2
        float adjustedTouchDown = 0;
        if (touch_down < -FLIP_THRESHOLD) {
            adjustedTouchDown = touch_down + FLIP_THRESHOLD;
            mPath0.moveTo(mScreenWidth + adjustedTouchDown, 0);
            mPath0.lineTo(mScreenWidth + adjustedTouchDown, mScreenHeight);
            mPath0.lineTo(mScreenWidth, mScreenHeight);
            mPath0.lineTo(mScreenWidth, 0);
            mPath0.lineTo(mScreenWidth + adjustedTouchDown, 0);
            mPath0.close();
            canvas.clipPath(mPath0, Region.Op.XOR);
            canvas.drawBitmap(mCurPageBitmap, adjustedTouchDown, 0, null);
        } else if (touch_down > FLIP_THRESHOLD) {
            adjustedTouchDown = touch_down - FLIP_THRESHOLD;
            mPath0.moveTo(adjustedTouchDown, 0);
            mPath0.lineTo(adjustedTouchDown, mScreenHeight);
            mPath0.lineTo(mScreenWidth, mScreenHeight);
            mPath0.lineTo(mScreenWidth, 0);
            mPath0.lineTo(adjustedTouchDown, 0);
            mPath0.close();
            canvas.clipPath(mPath0);
            canvas.drawBitmap(mCurPageBitmap, adjustedTouchDown, 0, null);
        } else {
            canvas.drawBitmap(mCurPageBitmap, 0, 0, null);
        }
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    @Override
    protected void drawCurrentPageShadow(Canvas canvas) {
        canvas.save();
        GradientDrawable shadow;
        float adjustedTouchDown = 0;
        if (touch_down < -FLIP_THRESHOLD) {
            adjustedTouchDown = touch_down + FLIP_THRESHOLD;
            shadow = mBackShadowDrawableLR;
            shadow.setBounds((int) (mScreenWidth + adjustedTouchDown - 5), 0, (int) (mScreenWidth + adjustedTouchDown + 5), mScreenHeight);

        } else if (touch_down > FLIP_THRESHOLD) {
            adjustedTouchDown = touch_down - FLIP_THRESHOLD;
            shadow = mBackShadowDrawableRL;
            shadow.setBounds((int) (adjustedTouchDown - 5), 0, (int) (adjustedTouchDown + 5), mScreenHeight);
        } else {
            shadow = mBackShadowDrawableRL;
            shadow.setBounds((int) (-5), 0, (int) (+5), mScreenHeight);
        }
        shadow.draw(canvas);
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    @Override
    protected void drawCurrentBackArea(Canvas canvas) {
        // none
    }

    @Override
    protected void drawNextPageAreaAndShadow(Canvas canvas) {
        canvas.save();
//        if (actiondownX > mScreenWidth >> 1) {
        if (touch_down < -FLIP_THRESHOLD) {
            canvas.clipPath(mPath0);
            canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
        } else if (touch_down > FLIP_THRESHOLD) {
            canvas.clipPath(mPath0, Region.Op.XOR);
            canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
        }
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    @Override
    protected void calcPoints() {

    }

    @Override
    protected void calcCornerXY(float x, float y) {

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            if (actiondownX > mScreenWidth >> 1) {
                touch_down = -(mScreenWidth - x);
            } else {
                touch_down = x;
            }
            mTouch.y = y;
            //touch_down = mTouch.x - actiondownX;
            postInvalidate();
        }
    }

    @Override
    protected void startAnimation() {
        int dx;
        float adjustedTouchDown = 0;
        if (touch_down < -FLIP_THRESHOLD) {
            adjustedTouchDown = touch_down + FLIP_THRESHOLD;
            dx = (int) -(mScreenWidth + adjustedTouchDown);
            mScroller.startScroll((int) (mScreenWidth + touch_down), (int) mTouch.y, dx, 0, 700);
        } else if (touch_down > FLIP_THRESHOLD) {
            adjustedTouchDown = touch_down - FLIP_THRESHOLD;
            dx = (int) (mScreenWidth - adjustedTouchDown);
            mScroller.startScroll((int) touch_down, (int) mTouch.y, dx, 0, 700);
        } else {
            mScroller.startScroll((int) touch_down, (int) mTouch.y, 0, 0, 700);
        }
    }

    @Override
    protected void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    @Override
    protected void restoreAnimation() {
//        int dx;
//        float adjustedTouchX = 0;
//        if (touch_down < -FLIP_THRESHOLD) {
//            adjustedTouchX =touch_down- FLIP_THRESHOLD;
//            dx = (int) (mScreenWidth -adjustedTouchX);
//            mScroller.startScroll((int)adjustedTouchX, (int) mTouch.y, dx, 0, 600);
//        } else if (touch_down > FLIP_THRESHOLD) {
//            adjustedTouchX = mTouch.x + FLIP_THRESHOLD;
//            dx = (int) (-adjustedTouchX);
//            mScroller.startScroll((int)adjustedTouchX, (int) mTouch.y, dx, 0, 600);
//        }
        int startPosition;
        float travelX = 0;
        if (touch_down < CANCEL_THRESHOLD && touch_down > 0) {
            //右滑
            if (actiondownX > mScreenWidth / 2) {
                //右屏开始
                startPosition = (int)(mScreenWidth-(touch_down + FLIP_THRESHOLD));
                travelX = (int) ((touch_down + FLIP_THRESHOLD));
            } else {
                //左屏开始
                startPosition = (int) (touch_down - FLIP_THRESHOLD);
                travelX = (int) (-startPosition);
            }
            mScroller.startScroll((int) startPosition, (int) mTouch.y, (int)travelX, 0, 600);
        } else if (touch_down > -CANCEL_THRESHOLD && touch_down < 0) {
            //左滑
            if (actiondownX > mScreenWidth / 2) {
                //右屏开始
                travelX = touch_down + FLIP_THRESHOLD;
                startPosition = (int) (mScreenWidth - travelX);
            } else {
                //左屏开始
                travelX = touch_down + FLIP_THRESHOLD;
                startPosition = (int) (-travelX);
            }

            mScroller.startScroll((int) startPosition, (int) mTouch.y, (int) travelX, 0, 600);
        } else {
        }

    }

    public void setBitmaps(Bitmap bm1, Bitmap bm2) {
        mCurPageBitmap = bm1;
        mNextPageBitmap = bm2;
    }

    @Override
    public synchronized void setTheme(int theme) {
        resetTouchPoint();
        Bitmap bg = ThemeManager.getThemeDrawable(theme);
        if (bg != null) {
            pagefactory.setBgBitmap(bg);
            if (isPrepared) {
                pagefactory.onDraw(mCurrentPageCanvas);
                pagefactory.onDraw(mNextPageCanvas);
                postInvalidate();
            }
        }
        if (theme < 5) {
            SettingManager.getInstance().saveReadTheme(theme);
        }
    }
}
