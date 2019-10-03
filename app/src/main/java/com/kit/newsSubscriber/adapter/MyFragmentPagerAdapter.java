package com.kit.newsSubscriber.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kit.newsSubscriber.news.Fragment163;
import com.kit.newsSubscriber.news.FragmentHistory;
import com.kit.newsSubscriber.news.FragmentMChina;
import com.kit.newsSubscriber.news.FragmentSina;

/**
 * Created by Jawor
 * Time: 2018/5/28
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"手机中国", "网易新闻", "新浪新闻"};

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentMChina();
            case 1:
                return new Fragment163();
            case 2:
                return new FragmentSina();
            default:
                return new FragmentHistory();
        }
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}