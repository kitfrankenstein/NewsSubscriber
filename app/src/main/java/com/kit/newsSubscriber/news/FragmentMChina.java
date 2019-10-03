package com.kit.newsSubscriber.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.kit.newsSubscriber.base.SwipeFlushView;
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

public class FragmentMChina extends BaseFragment {
    private Context context;
    private List<News> newsList;//添加新闻
    private NewsAdapter adapter;//新闻适配器
    private Handler handler;
    private ListView listView;//展示新闻列表
    private NewsCache cache;
    private SwipeFlushView mFlush;

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    	context = getActivity();
        //fragment的布局
        View fragmentView = inflater.inflate(R.layout.fragment_mchina, container, false);
        mFlush = fragmentView.findViewById(R.id.refresh);
        newsList = new ArrayList<>();//新闻列表
        listView = fragmentView.findViewById(R.id.list_mChina);

        adapter = new NewsAdapter(context, newsList);//适配器
        listView.setAdapter(adapter);//设置适配器

        registerForContextMenu(listView);

        cache = NewsCache.getInstance(context);
        getNews(context);
        /*
         * 又开一个线程
         * 判断message
         * 装上适配器
         * 适配器版本的点击事件
         */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    mFlush.setFlushing(false);
                    mFlush.setLoading(false);
                    adapter.notifyDataSetChanged();
                    listView.setOnItemClickListener((parent, view, position, id) -> {//这一部分是点击之后转到新闻内容页面
                        /*
                         * 获取位置,把数据装进news
                         * intent切换布局
                         * 传输数据
                         * 启动
                         */
                        News news = newsList.get(position);//往定好数据框架的news里面传入数据
                        cache.addHistory(news);
                        Intent intent = new Intent(getActivity(), NewsDisplayActivity.class);//转到展示新闻的界面
                        intent.putExtra("news_url", news.getNewsUrl());//把要打开的网址给他
                        startActivity(intent);
                    });
                }
            }
        };
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFlush.setOnFlushListener(() -> {
            newsList.clear();
            getNews(context);
        });
        mFlush.setOnLoadListener(() -> getNews(context));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "收藏");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0) {
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
                default:
            }
        }
        return super.onContextItemSelected(item);
    }

    private void getNews(final Context context) {

        new Thread(() -> {
            if (!SysInfoUtil.isNetworkAvailable(context)) {//无网络连接时加载缓存
                newsList.clear();
                newsList.addAll(cache.loadCache(MySQLite.TABLE_MCHINA));
            } else {
                String src = SysInfoUtil.HOST + "/News/getNews";
                String lastUrl = "";
                int listSize = newsList.size();
                if (listSize > 0) {
                	lastUrl = newsList.get(listSize - 1).getNewsUrl();
                }
                try {
                    JSONObject json = new JSONObject();
                    json.put("source", "mchina");
                    json.put("lastUrl", lastUrl);
                    json.put("limit", 10);
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
                            if (imgUrl.equals("null")) {
                                imgUrl = "drawable://" + R.drawable.empty;
                                Log.d("img", imgUrl);
                            }
                            Log.d("img", imgUrl);
                            News news = new News(title, url, digest, time, imgUrl);
                            newsList.add(news);
                        }
                    }
                    cache.saveInCache(newsList, MySQLite.TABLE_MCHINA);//缓存
                } catch (Exception e) {
                    e.printStackTrace();
                    newsList.clear();
                    newsList.addAll(cache.loadCache(MySQLite.TABLE_MCHINA));
                }
            }
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }).start();
    }

}
