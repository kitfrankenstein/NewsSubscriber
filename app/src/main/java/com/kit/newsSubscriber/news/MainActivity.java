package com.kit.newsSubscriber.news;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.newsSubscriber.R;
import com.kit.newsSubscriber.notification.MqttManager;
import com.kit.newsSubscriber.notification.PreferencesManager;
import com.kit.newsSubscriber.util.SysInfoUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
	private DrawerLayout mDrawerLayout;
	private DisplayImageOptions options;//图片加载框架配置
	private FragmentHistory fragmentHistory;
	private FragmentNews fragmentNews;
	private FragmentFavor fragmentFavor;
	private FragmentSetting fragmentSetting;
	private Menu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		final Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("网络新闻");
		setSupportActionBar(toolbar);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		NavigationView navView = findViewById(R.id.nav_view);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
		}
		navView.setCheckedItem(R.id.nav_news);
		navView.setNavigationItemSelectedListener(item -> {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			MenuItem searchItem = mMenu.findItem(R.id.app_bar_search);
			switch (item.getItemId()) {
				case R.id.nav_history:
					System.out.println("item = " + item.getTitle());
					fragmentHistory = new FragmentHistory();
					transaction.add(R.id.mainframe, fragmentHistory);
					hideFragments(transaction);
					transaction.show(fragmentHistory);
					toolbar.setTitle("历史记录");
					searchItem.setVisible(false);
					break;
				case R.id.nav_news:
					System.out.println("item = " + item.getTitle());
					if (fragmentNews == null) {
						fragmentNews = new FragmentNews();
						transaction.add(R.id.mainframe, fragmentNews);
					}
					hideFragments(transaction);
					transaction.show(fragmentNews);
					toolbar.setTitle("网络新闻");
					searchItem.setVisible(true);
					break;
				case R.id.nav_favor:
					System.out.println("item = " + item.getTitle());
					fragmentFavor = new FragmentFavor();
					transaction.add(R.id.mainframe, fragmentFavor);
					hideFragments(transaction);
					transaction.show(fragmentFavor);
					toolbar.setTitle("我的收藏");
					searchItem.setVisible(false);
					break;
				case R.id.nav_setting:
					System.out.println("item = " + item.getTitle());
					fragmentSetting = new FragmentSetting();
					transaction.add(R.id.mainframe, fragmentSetting);
					hideFragments(transaction);
					transaction.show(fragmentSetting);
					toolbar.setTitle("设置");
					searchItem.setVisible(false);
					break;
				default:
			}
			transaction.commit();
			mDrawerLayout.closeDrawers();
			return true;
		});

		//推送设置
		MqttManager mqttManager = MqttManager.getInstance(MainActivity.this);
		mqttManager.creatConnect(SysInfoUtil.getClientID(), "android bye");

		PreferencesManager preferencesManager = PreferencesManager.getInstance(MainActivity.this);
		Map<String, Boolean> preferences = preferencesManager.getPreferences();
		if (preferences.get("focus")) {
			mqttManager.subscribe("focus", 1);
			for (String topic : preferences.keySet()) {
				if (preferences.get(topic) && !topic.equals("focus")) {
					mqttManager.subscribe(topic, 1);
				}
			}
		}

		//uil初始化配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.newsicon)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.displayer(new RoundedBitmapDisplayer(10))
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCacheSize(10 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheFileCount(200)
				.diskCacheSize(30 * 1024 * 1024)
				.defaultDisplayImageOptions(options)
				.build();
		ImageLoader.getInstance().init(config);

		//初始化视图
		initViews();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isFinishing()){
			MqttManager.release();//释放资源
		}
	}

	/**
	 * 创建顶部菜单视图，设置搜索按钮监听
	 *
	 * @param menu 菜单
	 * @return boolean
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		getMenuInflater().inflate(R.menu.toolbar, menu);
		MenuItem searchItem = menu.findItem(R.id.app_bar_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint("搜索");
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent intent = new Intent(MainActivity.this, SearchActivity.class);
				intent.putExtra("key", query);
				startActivity(intent);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return true;
	}

	/**
	 * 监听菜单按钮点击事件
	 *
	 * @param item 菜单项
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				break;
			default:
		}
		return true;
	}

	/**
	 * 初始化主页面视图
	 */
	private void initViews() {
		fragmentNews = new FragmentNews();
		FragmentManager mainManager = getSupportFragmentManager();
		FragmentTransaction mainTransaction = mainManager.beginTransaction();
		mainTransaction.add(R.id.mainframe, fragmentNews);
		mainTransaction.show(fragmentNews);
		mainTransaction.commit();
	}

	/**
	 * 隐藏所有页面
	 *
	 * @param transaction 事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (fragmentNews != null) {
			transaction.hide(fragmentNews);
		}
		if (fragmentHistory != null) {
			transaction.hide(fragmentHistory);
		}
		if (fragmentFavor != null) {
			transaction.hide(fragmentFavor);
		}
		if (fragmentSetting != null) {
			transaction.hide(fragmentSetting);
		}
	}

}