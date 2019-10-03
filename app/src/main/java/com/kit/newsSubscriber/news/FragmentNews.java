package com.kit.newsSubscriber.news;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newsSubscriber.R;
import com.kit.newsSubscriber.adapter.MyFragmentPagerAdapter;
import com.kit.newsSubscriber.base.BaseFragment;


public class FragmentNews extends BaseFragment {
	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter myFragmentPagerAdapter;
	private TabLayout.Tab one;
	private TabLayout.Tab two;
	private TabLayout.Tab three;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_news, container, false);
		//使用适配器将ViewPager与Fragment绑定在一起
		mViewPager = fragmentView.findViewById(R.id.viewPager);
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
		mViewPager.setAdapter(myFragmentPagerAdapter);

		mViewPager.setOffscreenPageLimit(3);

		//将TabLayout与ViewPager绑定在一起
		mTabLayout =  fragmentView.findViewById(R.id.tabLayout);
		mTabLayout.setupWithViewPager(mViewPager);

		//指定Tab的位置
		one = mTabLayout.getTabAt(0);
		two = mTabLayout.getTabAt(1);
		three = mTabLayout.getTabAt(2);

		//设置Tab的图标，假如不需要则把下面的代码删去
		one.setIcon(R.drawable.mchina);
		two.setIcon(R.drawable.wangyi);
		three.setIcon(R.drawable.sina);

		return fragmentView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
