package com.kit.newsSubscriber.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kit.newsSubscriber.entity.News;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存管理
 */

public class NewsCache {
	private MySQLite mySQLite;
	private static volatile NewsCache instance = null;

	/**
	 * 构造方法创建MySQLite对象对数据库进行操作
	 *
	 * @param context 上下文
	 */
	private NewsCache(Context context) {
		this.mySQLite = new MySQLite(context);
	}

	public static NewsCache getInstance(Context context) {
		if (instance == null) {
			synchronized (NewsCache.class) {
				if (instance == null) {
					instance = new NewsCache(context.getApplicationContext());
				}
			}
		}
		return instance;
	}

//	/**
//	 * 更新缓存数据
//	 *
//	 * @param newsList
//	 * @param table
//	 */
//	public void updateCache(List<News> newsList, String table) {
//		SQLiteDatabase db = mySQLite.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select * from " + table, null);
//		if (cursor.moveToNext()) {
//			db.execSQL("delete from " + table);
//			db.execSQL("update sqlite_sequence SET seq=0 where name=?", new Object[]{table});
//		}
//		ContentValues values = new ContentValues();
//		for (int i = 0; i < newsList.size(); i++) {
//			values.put("url", newsList.get(i).getNewsUrl());
//			values.put("title", newsList.get(i).getNewsTitle());
//			values.put("digest", newsList.get(i).getDesc());
//			values.put("time", newsList.get(i).getNewsTime());
//			values.put("imageUrl", newsList.get(i).getNewsImage());
//			db.insert(table, null, values);
//		}
//		Log.d("updateFinish", table + "updateCacheFinish");
//		cursor.close();
//		db.close();
//	}

	/**
	 * 读取表数据
	 *
	 * @param table 表名
	 * @return List<News>
	 */
	public List<News> loadCache(String table) {
		List<News> newsList = new ArrayList<>();
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		Cursor cursor = db.rawQuery("select *" +
				" from " + table +
				" order by timestamp desc" +
				" limit 100", null);
		if (!cursor.moveToFirst()) {
			newsList.add(new News("无记录",
					"https://github.com/kitfrankenstein",
					null,
					null,
					null));
		} else {
			do {
				String url = cursor.getString(1);
				String title = cursor.getString(2);
				String desc = cursor.getString(3);
				String time = cursor.getString(4);
				String imageUrl = cursor.getString(5);
				News news = new News(title, url, desc, time, imageUrl);
				newsList.add(news);
			} while (cursor.moveToNext());
			Log.d("load", table + " finish loading");
		}
		cursor.close();
		db.close();
		return newsList;
	}

	/**
	 * 缓存
	 *
	 * @param newsList 新闻列表
	 * @param table 表名
	 * @return boolean
	 */
	public boolean saveInCache(List<News> newsList, String table) {
		if (newsList == null || newsList.size() == 0) {
			return false;
		}
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		String url, title, digest, time, imageUrl;
		for (int i = 0; i < newsList.size(); i++) {
			url = newsList.get(i).getNewsUrl();
			title = newsList.get(i).getNewsTitle();
			digest = newsList.get(i).getDesc();
			time = newsList.get(i).getNewsTime();
			imageUrl = newsList.get(i).getNewsImage();
			db.execSQL("insert or ignore into " + table + " (url,title,digest,time,imageUrl) " +
							"values (?,?,?,?,?)",
					new Object[]{url, title, digest, time, imageUrl});
		}
		db.close();
		Log.d("save2cache", table + "save cache finished");
		return true;
	}

	/**
	 * 从缓存读取offset开始的十条记录
	 *
	 * @param table 表名
	 * @param offset 偏移量
	 * @return 新闻列表
	 */
	public List<News> loadCacheByOffset(String table, int offset) {
		List<News> newsList = new ArrayList<>();
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		Cursor cursor = db.rawQuery("select *" +
				" from " + table +
				" order by timestamp desc" +
				" limit ?,10", new String[]{String.valueOf(offset)});
		if (!cursor.moveToFirst()) {
			newsList.add(new News("无记录", "https://gitee.com/jaworgjx/projects", null, null, null));
		} else {
			do {
				String url = cursor.getString(1);
				String title = cursor.getString(2);
				String desc = cursor.getString(3);
				String time = cursor.getString(4);
				String imageUrl = cursor.getString(5);
				News news = new News(title, url, desc, time, imageUrl);
				newsList.add(news);
			} while (cursor.moveToNext());
			Log.d("load by offset", table + " finish loading");
		}
		cursor.close();
		db.close();
		return newsList;
	}

	/**
	 * 保存一条记录到表
	 *
	 * @param news 新闻
	 * @param table 表名
	 * @return boolean
	 */
	public boolean add2Cache(News news, String table) {
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		String url = news.getNewsUrl();
		String title = news.getNewsTitle();
		String digest = news.getDesc();
		String time = news.getNewsTime();
		String imageUrl = news.getNewsImage();
		Cursor cursor = db.rawQuery("select *" +
						" from " + table +
						" where url like ?",
				new String[]{url});
		if (cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return false;
		}
		db.execSQL("insert or ignore into " + table + " (url,title,digest,time,imageUrl) " +
						"values (?,?,?,?,?)",
				new Object[]{url, title, digest, time, imageUrl});
		cursor.close();
		db.close();
		return true;
	}

	/**
	 * 从表中删除一条记录
	 *
	 * @param news 新闻
	 * @param table 表名
	 */
	public void delFromCache(News news, String table) {
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		db.execSQL("delete from " + table + " where url=?", new Object[]{news.getNewsUrl()});
		db.close();
	}

	/**
	 * 清除数据缓存
	 */
	public void clearCache() {
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		db.execSQL("delete from " + MySQLite.TABLE_MCHINA);
		db.execSQL("delete from " + MySQLite.TABLE_NETEASE);
		db.execSQL("delete from " + MySQLite.TABLE_SINA);
		db.close();
	}

	/**
	 * 清空历史记录
	 */
	public void clearHistory() {
		SQLiteDatabase db = mySQLite.getReadableDatabase();
		db.execSQL("delete from " + MySQLite.TABLE_HISTORY);
		db.close();
	}

	/**
	 * 添加到历史记录
	 *
	 * @param news 要添加的新闻
	 */
	public void addHistory(News news) {
		delFromCache(news, MySQLite.TABLE_HISTORY);
		add2Cache(news, MySQLite.TABLE_HISTORY);
	}

//	/**
//	 * 搜索表数据返回
//	 *
//	 * @param table
//	 * @param key
//	 * @return List<News>
//	 */
//	public List<News> searchInCache(String table, String key) {
//		List<News> result = new ArrayList<>();
//		SQLiteDatabase db = mySQLite.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select * from " + table + " where title like ?", new String[]{key});
//		if (!cursor.moveToFirst()) {
//			result = null;
//		} else {
//			do {
//				String url = cursor.getString(1);
//				String title = cursor.getString(2);
//				String desc = cursor.getString(3);
//				String time = cursor.getString(4);
//				String imageUrl = cursor.getString(5);
//				News temp = new News(title, url, desc, time, imageUrl);
//				result.add(temp);
//			} while (cursor.moveToNext());
//			Log.d("search", "Finish searching in " + table);
//		}
//		cursor.close();
//		db.close();
//		return result;
//	}

//	/**
//	 * 判断缓存数据是否超过时限
//	 *
//	 * @param table
//	 * @param time_limits
//	 * @return boolean
//	 */
//	public boolean isTimeOut(String table, long time_limits) {
//		SQLiteDatabase db = mySQLite.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select * from " + table + " where id=1", null);
//		if (cursor.moveToFirst()) {
//			try {
//				String date = cursor.getString(6);
//				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+:08:00"));//模拟器调试注释此行
//				long millis = System.currentTimeMillis() - simpleDateFormat.parse(date).getTime();
//				Log.d("check", String.valueOf(millis));
//				if (millis < time_limits) {
//					cursor.close();
//					db.close();
//					return false;
//				}
//			} catch (Exception e) {
//				cursor.close();
//				db.close();
//				e.printStackTrace();
//			}
//		}
//		cursor.close();
//		db.close();
//		return true;
//	}
}