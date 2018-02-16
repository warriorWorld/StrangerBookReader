package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * fragmentPager适配器
 * 
 * @author Administrator
 *
 */
public class MainAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragList;
	private List<String> titleList;

	public MainAdapter(FragmentManager fm, List<Fragment> fragList,
					   List<String> titleList) {
		super(fm);
		this.fragList = fragList;
		this.titleList = titleList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titleList.get(position);
	}
}
