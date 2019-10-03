package com.kit.newsSubscriber.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class PreferencesManager {

	private static final String Focus = "focus";//推送开关
	private static final String Rcmd = "recommend";//推荐
	private static final String Intl = "international";//国际
	private static final String Fin = "finance";//财经
	private static final String Mil = "military";//军事
	private static final String Tech = "technology";//科技
	private static final String Spt = "sport";//体育
	private static final String Ent = "entertainment";//娱乐
	private static final String Edu = "education";//教育
	private static final String Game = "game";//游戏
	private static final String Heal = "health";//健康

	private Context context;
	@SuppressLint("StaticFieldLeak")
	private static PreferencesManager instance = null;

//	public static final Map<String, String> Val_Dic = new HashMap<String, String>() {
//		{
//			Val_Dic.put("接收通知", Switch);
//			Val_Dic.put("推荐", Rcmd);
//			Val_Dic.put("国际", Intl);
//			Val_Dic.put("财经", Fin);
//			Val_Dic.put("军事", Mil);
//			Val_Dic.put("科技", Tech);
//			Val_Dic.put("体育", Spt);
//			Val_Dic.put("娱乐", Ent);
//			Val_Dic.put("教育", Edu);
//			Val_Dic.put("游戏", Game);
//			Val_Dic.put("健康", Heal);
//		}
//	};

	private PreferencesManager(Context context) {
		this.context = context;
	}

	public static PreferencesManager getInstance(Context context) {
		if (instance == null) {
			synchronized (PreferencesManager.class) {
				if (instance == null) {
					instance = new PreferencesManager(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 保存参数
	 * @param params 偏好设置
	 */
	public void savePreferences(Map<String, Boolean> params) {
		SharedPreferences preferences = context.getSharedPreferences("newsPreferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Focus, params.get(Focus));
		editor.putBoolean(Rcmd, params.get(Rcmd));
		editor.putBoolean(Intl, params.get(Intl));
		editor.putBoolean(Fin, params.get(Fin));
		editor.putBoolean(Mil, params.get(Mil));
		editor.putBoolean(Tech, params.get(Tech));
		editor.putBoolean(Spt, params.get(Spt));
		editor.putBoolean(Ent, params.get(Ent));
		editor.putBoolean(Edu, params.get(Edu));
		editor.putBoolean(Game, params.get(Game));
		editor.putBoolean(Heal, params.get(Heal));
		editor.apply();
	}

	/**
	 * 获取各项配置参数
	 * @return map
	 */
	public Map<String, Boolean> getPreferences(){
		Map<String, Boolean> params = new HashMap<>();
		SharedPreferences preferences = context.getSharedPreferences("newsPreferences", Context.MODE_PRIVATE);
		params.put(Focus, preferences.getBoolean(Focus, false));
		params.put(Rcmd, preferences.getBoolean(Rcmd, false));
		params.put(Intl, preferences.getBoolean(Intl, false));
		params.put(Fin, preferences.getBoolean(Fin, false));
		params.put(Mil, preferences.getBoolean(Mil, false));
		params.put(Tech, preferences.getBoolean(Tech, false));
		params.put(Spt, preferences.getBoolean(Spt, false));
		params.put(Ent, preferences.getBoolean(Ent, false));
		params.put(Edu, preferences.getBoolean(Edu, false));
		params.put(Game, preferences.getBoolean(Game, false));
		params.put(Heal, preferences.getBoolean(Heal, false));
		return params;
	}

	/**
	 * 设置单个偏好参数
	 * @param key 参数键
	 * @param param 参数值
	 */
	public void setPreference(String key, boolean param) {
		SharedPreferences preferences = context.getSharedPreferences("newsPreferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, param);
		editor.apply();
	}

	/**
	 * 获取单个偏好参数
	 * @param key 参数键
	 * @return 参数值
	 */
	public boolean getPreference(String key) {
		SharedPreferences preferences = context.getSharedPreferences("newsPreferences", Context.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}
}
