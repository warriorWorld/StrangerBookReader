/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.warrior.hangsu.administrator.strangerbookreader.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.bean.ReadTheme;
import com.warrior.hangsu.administrator.strangerbookreader.utils.AppUtils;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuyh.
 * @date 2016/9/23.
 */
public class ThemeManager {

    public static final int NORMAL = 0;
    public static final int YELLOW = 1;
    public static final int GREEN = 2;
    public static final int LEATHER = 3;
    public static final int GRAY = 4;
    public static final int NIGHT = 5;
    public static final int WHITE = 6;
    public static final String[] THEME_LIST = {"默认", "黄色", "绿色", "羊皮纸", "灰色", "黑色", "白色"};

    public static Bitmap getThemeDrawable(int theme) {
        Bitmap bmp = Bitmap.createBitmap(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight(), Bitmap.Config.ARGB_8888);
        switch (theme) {
            case NORMAL:
                bmp.eraseColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.read_theme_white));
                break;
            case YELLOW:
                bmp.eraseColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.read_theme_yellow));
                break;
            case GREEN:
                bmp.eraseColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.read_theme_green));
                break;
            case LEATHER:
                bmp = BitmapFactory.decodeResource(AppUtils.getAppContext().getResources(), R.drawable.theme_leather_bg);
                break;
            case GRAY:
                bmp.eraseColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.read_theme_gray));
                break;
            case NIGHT:
                bmp.eraseColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.read_theme_night));
                break;
            case WHITE:
                bmp.eraseColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.white));
            default:
                break;
        }
        return bmp;
    }
}
