package com.kit.newsSubscriber.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库管理类
 */

public class MySQLite extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String SQL_NAME = "NEWS_CACHE";//数据库名称

	//表名称
	public static final String TABLE_HISTORY = "history";
	public static final String TABLE_MCHINA = "mChinaCache";
	public static final String TABLE_NETEASE = "netEaseCache";
	public static final String TABLE_SINA = "sinaCache";
	public static final String TABLE_FAVORITE = "favorite";

	//超时时间
	//public static final long ONE_QUARTER = 900000;

	//建表语句
	private static final String history = "create table if not exists history(id integer primary key autoincrement," +
			"url text," +
			"title text," +
			"digest text," +
			"time text," +
			"imageUrl text," +
			"timestamp TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP," +
			"unique (url))";
	private static final String mChinaCache = "create table if not exists mChinaCache(id integer primary key autoincrement," +
			"url text," +
			"title text," +
			"digest text," +
			"time text," +
			"imageUrl text," +
			"timestamp TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP," +
			"unique(url))";
	private static final String netEaseCache = "create table if not exists netEaseCache(id integer primary key autoincrement," +
			"url text," +
			"title text," +
			"digest text," +
			"time text," +
			"imageUrl text," +
			"timestamp TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP," +
			"unique(url))";
	private static final String sinaCache = "create table if not exists sinaCache(id integer primary key autoincrement," +
			"url text," +
			"title text," +
			"digest text," +
			"time text," +
			"imageUrl text," +
			"timestamp TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP," +
			"unique(url))";
	private static final String favorite = "create table if not exists favorite(id integer primary key autoincrement," +
			"url text," +
			"title text," +
			"digest text," +
			"time text," +
			"imageUrl text," +
			"timestamp TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP," +
			"unique (url))";

	//建立索引
	private static final String createHIndex =
			"create index if not exists time_stamp_index on history(timestamp desc)";
	private static final String createFIndex =
			"create index if not exists time_stamp_index on favorite(timestamp desc)";

	/**
	 * 构造方法，若数据库不存在则创建
	 * @param context activity context
	 */
	MySQLite(Context context) {
		super(context, SQL_NAME, null, VERSION);
	}

	/**
	 * 新建对象时调用，执行建表语句
	 * @param db database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(history);
		db.execSQL(mChinaCache);
		db.execSQL(netEaseCache);
		db.execSQL(sinaCache);
		db.execSQL(favorite);
		db.execSQL(createHIndex);
		db.execSQL(createFIndex);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}