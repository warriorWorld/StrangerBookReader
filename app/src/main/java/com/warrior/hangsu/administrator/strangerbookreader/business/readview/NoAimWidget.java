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

import com.warrior.hangsu.administrator.strangerbookreader.enums.BookFormat;
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnReadStateChangeListener;

/**
 * @author zths.
 * @date 2017/08/03.
 */
public class NoAimWidget extends OverlappedWidget {

    public NoAimWidget(Context context, String bookId, String format, OnReadStateChangeListener listener) {
        super(context, bookId, format, listener);
    }

    @Override
    protected void startAnimation() {
        startAnimation(700);
    }

    protected void startAnimation(int duration) {
        int dx;
        if (actiondownX > mScreenWidth / 2) {
            dx = (int) -(mScreenWidth + touch_down);
            mScroller.startScroll((int) (mScreenWidth + touch_down), (int) mTouch.y, dx, 0, duration);
        } else {
            dx = (int) (mScreenWidth - touch_down);
            mScroller.startScroll((int) touch_down, (int) mTouch.y, dx, 0, duration);
        }
    }

}
