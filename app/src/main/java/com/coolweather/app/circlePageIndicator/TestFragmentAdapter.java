package com.coolweather.app.circlePageIndicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TestFragmentAdapter extends FragmentPagerAdapter {
   // protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };
   protected static final String[] CONTENT = new String[] { "This"};
    private int mCount = CONTENT.length;

    //构造函数
    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return com.coolweather.app.circlePageIndicator.TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TestFragmentAdapter.CONTENT[position % CONTENT.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}