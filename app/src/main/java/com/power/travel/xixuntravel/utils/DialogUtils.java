package com.power.travel.xixuntravel.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.power.travel.xixuntravel.weight.CustomDialog;


public class DialogUtils {
//	static Context mContext;
	static CustomDialog.Builder dialog;
	
	/**
	 * ensure cancle 自定义
	 */
	public static CustomDialog.Builder show(Context context, String tipMessage,
                                            DialogInterface.OnClickListener ensure,
                                            DialogInterface.OnClickListener cancel) {
		
		dialog = new CustomDialog.Builder(context);
		dialog.setTitle("提示");
				dialog.setMessage(tipMessage);
				dialog.setPositiveButton("确定", ensure);
				dialog.setNegativeButton("取消", cancel);
		dialog.create().show();
		return dialog;
	}
	/**
	 * ensure 自定义
	 */
	public static CustomDialog.Builder showEnsure(Context context, String tipMessage,
                                                  DialogInterface.OnClickListener ensure){
		dialog = new CustomDialog.Builder(context);
		dialog.setTitle("提示");
				dialog.setMessage(tipMessage);
				dialog.setPositiveButton("确定", ensure);
		dialog.create().show();
//		dialog.setCancelable(false);
		return dialog;
	}
	
	/**
	 * title message  ensuretitle cancletitle ensure cancle 全部自定义
	 */
	public static CustomDialog.Builder show(Context context, String tipMessage,
                                            String Title
			, String ensureTitle, String cancleTitle,
                                            DialogInterface.OnClickListener ensure,
                                            DialogInterface.OnClickListener cancel) {
		dialog = new CustomDialog.Builder(context);
		dialog.setTitle(Title)
				.setMessage(tipMessage)
				.setNegativeButton(cancleTitle, cancel)
				.setPositiveButton(ensureTitle, ensure);
		dialog.create().show();
//		dialog.setCancelable(false);
		return dialog;
	}
	
}
