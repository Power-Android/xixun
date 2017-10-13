package com.power.travel.xixuntravel.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.SystemBarTintManager;
import com.yixia.weibo.sdk.model.MediaObject;
import com.yixia.weibo.sdk.model.MediaObject$MediaPart;
import com.yixia.weibo.sdk.util.FileUtils;
import com.yixia.weibo.sdk.util.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;


public class BaseActivity extends Activity {


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTranslucentStatus();
		super.onCreate(savedInstanceState);
	}

	
	protected ProgressDialog mProgressDialog;

	public ProgressDialog showProgress(String title, String message) {
		return showProgress(title, message, -1);
	}

	public ProgressDialog showProgress(String title, String message, int theme) {
		if (mProgressDialog == null) {
			if (theme > 0)
				mProgressDialog = new ProgressDialog(this, theme);
			else
				mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
			mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
		}

		if (!StringUtils.isEmpty(title))
			mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
		return mProgressDialog;
	}

	public void hideProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		hideProgress();
		mProgressDialog = null;
	}
	
	
	/** 反序列化对象 */
	protected static MediaObject restoneMediaObject(String obj) {
		try {
			String str = FileUtils.readFile(new File(obj));
			Gson gson = new Gson();
			MediaObject result = gson.fromJson(str.toString(), MediaObject.class);
			result.getCurrentPart();
			preparedMediaObject(result);
			return result;
		} catch (Exception e) {
			if (e != null)
				Log.e("VCamera", "readFile", e);
		}
		return null;
	}

	/** 预处理数据对�?*/
	public static void preparedMediaObject(MediaObject mMediaObject) {
		if (mMediaObject != null && mMediaObject.getMedaParts() != null) {
			int duration = 0;
			for (com.yixia.weibo.sdk.model.MediaObject$MediaPart part : (LinkedList<MediaObject$MediaPart>)mMediaObject.getMedaParts()) {
				part.startTime = duration;
				part.endTime = part.startTime + part.duration;
				duration += part.duration;
			}
		}
	}
	
	/** 序列号保存视频数�?*/
	public static  boolean saveMediaObject(MediaObject mMediaObject) {
		if (mMediaObject != null) {
			try {
				if (StringUtils.isNotEmpty(mMediaObject.getObjectFilePath())) {
					FileOutputStream out = new FileOutputStream(mMediaObject.getObjectFilePath());
					Gson gson = new Gson();
					out.write(gson.toJson(mMediaObject).getBytes());
					out.flush();
					out.close();
					return true;
				}
			} catch (Exception e) {
//				Logger.e(e);
			}
		}
		return false;
	}
	
	/**
	 * 设置状态栏背景状态 
	 */
	private void setTranslucentStatus() {
		setStatusBarTranslucent(true);
//		setNavigationBarTranslucent(true);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.title_layout_bg_color);// 状态栏的背景颜色(0表示无背景)
//		tintManager.setNavigationBarTintEnabled(true);
//		tintManager.setNavigationBarTintResource(R.color.title_bar_bg);//导航栏背景颜色（0表示无背景）
	}

	/**
	 * 设置状态栏是否透明 
	 * 
	 * @param isTransparent
	 */
	private void setStatusBarTranslucent(boolean isTransparent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& isTransparent) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			// 导航栏透明 
			final int sBits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= sBits;
			win.setAttributes(winParams);
		}
	}

	/**
	 *设置导航栏是否透明 
	 * 
	 * @param isTransparent
	 */
	private void setNavigationBarTranslucent(boolean isTransparent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& isTransparent) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			// 导航栏透明 
			final int nBits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			winParams.flags |= nBits;
			win.setAttributes(winParams);
		}
	}
	
	public Resources getResources() {
	    Resources res = super.getResources();
	    Configuration config=new Configuration();
	    config.setToDefaults();    
	    res.updateConfiguration(config,res.getDisplayMetrics() );  
	    return res;  
	}  
}
