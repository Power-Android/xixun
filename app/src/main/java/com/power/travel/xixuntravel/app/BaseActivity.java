package com.power.travel.xixuntravel.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.utils.DialogUtils;


public class BaseActivity extends FragmentActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle arg0) {
		setTranslucentStatus();
		super.onCreate(arg0);
	}

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void showEnsure(String message){
		DialogUtils.showEnsure(this, message, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * isConnect 判断网络连接
	 * @return
	 */
	public boolean isConnect() {

		ConnectivityManager conManager = (ConnectivityManager)

		getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错

			return networkInfo.isAvailable();

		}

		return false;
	}
	
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
		if (isConnect() == false) {// 判断网络连接 if语句

			DialogUtils.showEnsure(this, "网络连接失败， 请确认网络连接!",  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
	}

	
	/**
	 * 设置状态栏背景状态 
	 */
	private void setTranslucentStatus() {
		setStatusBarTranslucent(true);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.title_layout_bg_color);// 状态栏的背景颜色(0表示无背景)
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
	
	public Resources getResources() {
	    Resources res = super.getResources();
	    Configuration config=new Configuration();
	    config.setToDefaults();    
	    res.updateConfiguration(config,res.getDisplayMetrics() );  
	    return res;  
	}  


}
