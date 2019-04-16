package com.warrior.hangsu.administrator.strangerbookreader.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys;
import com.warrior.hangsu.administrator.strangerbookreader.utils.DisplayUtil;
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/7/29.
 */

public abstract class BaseMultiTabActivity extends TTSFragmentActivity {
    private TabLayout tabLayout;
    private ViewPager vp;
    private MyFragmentPagerAdapter adapter;
    private RelativeLayout notice_num_rl;
    private TextView notice_num_tv1, notice_num_tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initFragment();
    }

    protected abstract void initFragment();

    protected abstract String getActivityTitle();

    protected abstract int getPageCount();

    protected abstract ViewPager.OnPageChangeListener getPageListener();

    protected abstract String[] getTabTitleList();

    protected abstract Fragment getFragmentByPosition(int position);

    protected abstract int getMutiLayoutId();

    private void initUI() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        vp = (ViewPager) findViewById(R.id.view_pager);
        if (null != getPageListener()) {
            vp.addOnPageChangeListener(getPageListener());
        }
        notice_num_rl = (RelativeLayout) findViewById(R.id.notice_num_rl);
        notice_num_tv1 = (TextView) findViewById(R.id.notice_num_tv1);
        notice_num_tv2 = (TextView) findViewById(R.id.notice_num_tv2);

        vp.setAdapter(adapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager()));
        vp.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(vp);
        vp.setCurrentItem(SharedPreferencesUtils.getIntSharedPreferencesData(this, ShareKeys.LAST_PAGE_POSITION));
//        setTabLayoutIndicatorWidth(tabLayout, 84, 84);
//        reflex(tabLayout);
        baseTopBar.setTitle(getActivityTitle());
    }

    @Override
    protected int getLayoutId() {
        if (getMutiLayoutId() != 0) {
            return getMutiLayoutId();
        } else {
            return R.layout.activity_only_vp;
        }
    }

    protected void showNoticeLayout() {
        notice_num_rl.setVisibility(View.VISIBLE);
    }

    public void setMsgCount1(int count) {
        if (count > 0) {
            notice_num_tv1.setVisibility(View.VISIBLE);
            notice_num_tv1.setTextColor(getResources().getColor(R.color.white));
            notice_num_tv1.setText(count + "");
            if (count > 99) {
                notice_num_tv1.setText("99+");
            }
        } else {
            notice_num_tv1.setVisibility(View.GONE);
        }
    }

    public void setMsgCount2(int count) {
        if (count > 0) {
            notice_num_tv2.setVisibility(View.VISIBLE);
            notice_num_tv2.setTextColor(getResources().getColor(R.color.white));
            notice_num_tv2.setText(count + "");
            if (count > 99) {
                notice_num_tv2.setText("99+");
            }
        } else {
            notice_num_tv2.setVisibility(View.GONE);
        }
    }

    public void reflex(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = DisplayUtil.dip2px(tabLayout.getContext(), 84);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setTabLayoutIndicatorWidth(final TabLayout targetTl, final int leftDip, final int rightDip) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Class<?> tabLayout = targetTl.getClass();
                    Field tabStrip = null;
                    try {
                        tabStrip = tabLayout.getDeclaredField("mTabStrip");
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }

                    tabStrip.setAccessible(true);
                    LinearLayout llTab = null;
                    try {
                        llTab = (LinearLayout) tabStrip.get(targetTl);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
                    int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

                    for (int i = 0; i < llTab.getChildCount(); i++) {
                        View child = llTab.getChildAt(i);
                        child.setPadding(0, 0, 0, 0);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        params.leftMargin = left;
                        params.rightMargin = right;
                        child.setLayoutParams(params);
                        child.invalidate();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void saveLastPosition() {
        try {
            SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.LAST_PAGE_POSITION, vp.getCurrentItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveLastPosition();
    }

    /*
              setOffscreenPageLimit对此无用,全都在内存里
              FragmentPagerAdapter 继承自 PagerAdapter。相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment
               的情况。如文档所述，<b>该类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态的页</b>，数量也比
               较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用
               FragmentStatePagerAdapter。FragmentPagerAdapter 重载实现了几个必须的函数，因此来自 PagerAdapter
               的函数，我们只需要实现 getCount()，即可。且，由于 FragmentPagerAdapter.instantiateItem() 的实现中，
               调用了一个新增的虚函数 getItem()，因此，我们还至少需要实现一个 getItem()。因此，总体上来说，相对于继承自
               PagerAdapter，更方便一些。*/
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentByPosition(position);
        }


        @Override
        public int getCount() {
            return getPageCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTabTitleList()[position];
        }
    }
}
