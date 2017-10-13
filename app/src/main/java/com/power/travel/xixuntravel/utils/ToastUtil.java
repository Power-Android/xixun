/**
 * 
 */
package com.power.travel.xixuntravel.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

//	public static void show(Context context, String info) {
//		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
//
//	}
//
	public static void showInt(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}
//
//	public static void showLong(Context context, String info) {
//		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
//	}

	private static Toast mToast;

	public static void showToast(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.setGravity(Gravity.BOTTOM, 0, 60);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.BOTTOM, 0, 60);
		}
		mToast.show();
	}

	public void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	public void onBackPressed() {
		cancelToast();
	}
}
