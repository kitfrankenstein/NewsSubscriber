package com.kit.newsSubscriber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsSubscriber.R;
import com.kit.newsSubscriber.entity.News;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
	private List<News> newsList;
	private View view;
	private Context context;
	private ViewHolder viewHolder;

	public NewsAdapter(){

	}

	public NewsAdapter(Context context, List<News> newsList) {
		this.context = context;
		this.newsList = newsList;
	}

	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		//如果是第一次创建就获取
		if (convertView == null) {
			//将来自上下文的地方充气一个布局
			view = LayoutInflater.from(context).inflate(R.layout.news_item, null);
			//创建一个viewHolder对象用来接收需要展示的
			viewHolder = new ViewHolder();
			viewHolder.newsDesc = view.findViewById(R.id.news_desc);
			viewHolder.newsTime = view.findViewById(R.id.news_time);
			viewHolder.newsTitle = view.findViewById(R.id.news_title);
			viewHolder.newsImage = view.findViewById(R.id.news_imageView);
			//设置一个标签以便以后使用
			view.setTag(viewHolder);
			//如果已经创建过则直接读取标签
		} else {
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.newsTitle.setText(newsList.get(position).getNewsTitle());
		viewHolder.newsDesc.setText(newsList.get(position).getDesc());
		viewHolder.newsTime.setText(newsList.get(position).getNewsTime());
		ImageLoader.getInstance().displayImage(newsList.get(position).getNewsImage(),
				viewHolder.newsImage);
		return view;
	}

	class ViewHolder {
		TextView newsTitle;
		TextView newsDesc;
		TextView newsTime;
		ImageView newsImage;
	}
}
