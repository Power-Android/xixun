package com.power.travel.xixuntravel.utils;

import android.content.SharedPreferences;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.app.MyApplication;


/**
 *通过sharedpreference来本地存储的
 */
public class SPStoreUtils {


	private static SPStoreUtils manager = null;

	synchronized public static SPStoreUtils getInstance() {
		if (manager == null) {
			manager = new SPStoreUtils();
		}
		return manager;
	}

	private SharedPreferences settings;

	private SPStoreUtils() {
		this.settings = MyApplication.getContext().getSharedPreferences(
				XZContranst.MAIN_SHARED_PREFERENCES, 0);
	}

	/**
	 * 存储对象 只支持String的POM，不含GET/SET
	 * 
	 * @param obj
	 */
	public void saveObject(String key, Object obj) {
		SharedPreferences.Editor edit = settings.edit();
		String str = JSON.toJSONString(obj);
		str = Base64.encodeToString(str.getBytes(), 0);
		edit.putString(key, str);
		edit.commit();
	}

	/**
	 * 获取对象
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(String key, Class<?> classItem) {
		try {
			String str = settings.getString(key, null);
			str = new String(Base64.decode(str, 0));
			if (str != null) {
				return (T) JSON.parseObject(str, classItem);
			}
		} catch (Exception e) {

		}

		return null;

	}

	public String getString(String key) {
		return settings.getString(key, null);
	}

	public void saveString(String key, String value) {
		SharedPreferences.Editor edit = settings.edit();
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * 存储boolean值
	 * 
	 * @param key
	 * @param value
	 */
	public void saveBoolean(String key, boolean value) {
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 获取boolean值
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		return settings.getBoolean(key, false);
	}

}