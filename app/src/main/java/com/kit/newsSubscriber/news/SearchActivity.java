package com.kit.newsSubscriber.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.newsSubscriber.R;
import com.kit.newsSubscriber.adapter.NewsAdapter;
import com.kit.newsSubscriber.db.MySQLite;
import com.kit.newsSubscriber.db.NewsCache;
import com.kit.newsSubscriber.entity.News;
import com.kit.newsSubscriber.util.SysInfoUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

	private List<News> newsList;
	private NewsAdapter adapter;
	private Handler handler;
	private ListView listView;
	private NewsCache cache;
	private Toolbar toolbar;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("搜索结果");
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		listView = findViewById(R.id.search_list);
		newsList = new ArrayList<>();

		registerForContextMenu(listView);

		cache = NewsCache.getInstance(this);

		String key = getIntent().getStringExtra("key");

		show(key);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					adapter = new NewsAdapter(SearchActivity.this, newsList);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener((parent, view, position, id) -> {//这一部分是点击之后转到新闻内容页面
						/*
						 * 获取位置,把数据装进news
						 * intent切换布局
						 * 传输数据
						 * 启动
						 */
						News news = newsList.get(position);//往定好数据框架的news里面传入数据
						cache.addHistory(news);
						Intent intent = new Intent(SearchActivity.this, NewsDisplayActivity.class);//转到展示新闻的界面
						intent.putExtra("news_url", news.getNewsUrl());//把要打开的网址给他
						Log.d("news_url", news.getNewsUrl());
						startActivity(intent);
					});
					toolbar.setTitle("搜索结果 共" + String.valueOf(listView.getCount()) + "条");
				}
			}
		};
	}

	/**
	 * 点击返回按钮，销毁此活动
	 *
	 * @param item 菜单项
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 设置上下文菜单项
	 *
	 * @param menu 菜单
	 * @param v 视图
	 * @param menuInfo 菜单信息
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(7, 0, 0, "收藏");
	}

	/**
	 * 处理上下文菜单点击事件
	 *
	 * @param item 菜单项
	 * @return boolean
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == 7) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			//info.id得到listview中选择的条目绑定的id
			long id = info.id;
			News news = newsList.get((int) id);
			switch (item.getItemId()) {
				case 0:
					if (cache.add2Cache(news, MySQLite.TABLE_FAVORITE)) {
						Toast.makeText(this, "收藏成功!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, "你已经收藏过了", Toast.LENGTH_SHORT).show();
					}
					return true;
				default:
			}
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 展示搜索结果
	 *
	 * @param key 关键词
	 */
	private void show(String key) {
		new Thread(() -> {
			String src = SysInfoUtil.HOST + "/News/search";
			try {
				JSONObject json = new JSONObject();
				json.put("key", key);
				String response = Jsoup.connect(src)
						.ignoreContentType(true)
						.requestBody(json.toString())
						.header("Accept", "application/json, text/plain, */*")
						.header("Content-Type", "application/json;charset=UTF-8")
						.method(Connection.Method.POST)
						.execute()
						.body();
				JSONObject jsonObject = new JSONObject(response);
				int count = jsonObject.getInt("count");
				if (count > 0) {
					JSONArray result = jsonObject.getJSONArray("result");
					for (int i = 0; i < result.length(); i++) {
						JSONObject jO = result.getJSONObject(i);
						String url = jO.getString("url");
						String title = jO.getString("title");
						String digest = jO.getString("digest");
						String time = jO.getString("time");
						String imgUrl = jO.getString("imgUrl");
						if (imgUrl == null) {
							imgUrl = "drawable://" + R.drawable.empty;
						}
						News news = new News(title, url, digest, time, imgUrl);
						newsList.add(news);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}).start();
	}
}
