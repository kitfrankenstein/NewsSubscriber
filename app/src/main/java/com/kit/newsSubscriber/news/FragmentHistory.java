package com.kit.newsSubscriber.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.newsSubscriber.R;
import com.kit.newsSubscriber.adapter.NewsAdapter;
import com.kit.newsSubscriber.base.BaseFragment;
import com.kit.newsSubscriber.db.MySQLite;
import com.kit.newsSubscriber.db.NewsCache;
import com.kit.newsSubscriber.entity.News;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史记录页
 **/

public class FragmentHistory extends BaseFragment {
	private List<News> newsList;//添加新闻
    private NewsAdapter adapter;//新闻适配器
    private ListView listView;//展示新闻列表
    private NewsCache cache;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	    Context context = getActivity();
        View fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        listView = fragmentView.findViewById(R.id.food_list);
	    newsList = new ArrayList<>();

	    adapter = new NewsAdapter(context, newsList);
	    listView.setAdapter(adapter);

        registerForContextMenu(listView);

        cache = NewsCache.getInstance(context);

	    getHistory();
        return fragmentView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 设置上下文菜单项
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(3, 0, 0, "收藏");
		menu.add(3, 1, 1, "移除");
	}

	/**
	 * 处理上下文菜单点击事件
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == 3) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			//info.id得到listview中选择的条目绑定的id
			long id = info.id;
			News news = newsList.get((int)id);
			switch (item.getItemId()) {
				case 0:
					if (cache.add2Cache(news, MySQLite.TABLE_FAVORITE)) {
						Toast.makeText(getActivity(), "收藏成功!", Toast.LENGTH_SHORT).show();
					}
					else {
						Toast.makeText(getActivity(), "你已经收藏过了", Toast.LENGTH_SHORT).show();
					}
					return true;
				case 1:
					cache.delFromCache(news, MySQLite.TABLE_HISTORY);
					newsList.remove(news);
					adapter.notifyDataSetChanged();
					Toast.makeText(getActivity(), "已移除", Toast.LENGTH_SHORT).show();
					return true;
				default:
			}
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 获取历史记录数据并展示
	 */
	private void getHistory() {
		List<News> append = cache.loadCacheByOffset(MySQLite.TABLE_HISTORY, newsList.size());
    	newsList.addAll(append);
		if (newsList != null) {
			adapter.notifyDataSetChanged();
			listView.setOnItemClickListener((parent, view, position, id) -> {//这一部分是点击之后转到新闻内容页面
				/*
				 * 获取位置,把数据装进news
				 * intent切换布局
				 * 传输数据
				 * 启动
				 */
				News news = newsList.get(position);//往定好数据框架的news里面传入数据
				Intent intent = new Intent(getActivity(), NewsDisplayActivity.class);//转到展示新闻的界面
				intent.putExtra("news_url", news.getNewsUrl());//把要打开的网址给他
				startActivity(intent);
			});
		}
	}
}
