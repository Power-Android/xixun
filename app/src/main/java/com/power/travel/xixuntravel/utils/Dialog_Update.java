package com.power.travel.xixuntravel.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.DialogListViewAdapter;

import java.util.ArrayList;

/**
 * 自动更新提示框（更新的提示信息为列表）
 * @author fan
 *
 */
public class Dialog_Update extends Dialog {
	private Context context;
	private String title;
	private DialogListViewAdapter adapter;
	private ArrayList<String> list;
	UpdateManager mUpdateManager;

	public Dialog_Update(Context context, String title, ArrayList<String> list) {
		super(context, R.style.blend_theme_dialog);
		this.context=context;
		this.title=title;
		this.list=list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater= LayoutInflater.from(context);
		LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.dialog_update, null);
		
		TextView title_tv=(TextView)layout.findViewById(R.id.updateTip);
		ListView content=(ListView)layout.findViewById(R.id.updateContent);
		TextView ensure = (TextView) layout.findViewById(R.id.ensure);
		TextView cancel = (TextView) layout.findViewById(R.id.cancel);
		
		title_tv.setText(title);
		adapter= new DialogListViewAdapter(context, list);
		content.setAdapter(adapter);
		mUpdateManager = new UpdateManager(context);
		
		this.setCanceledOnTouchOutside(true);
		this.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				
			}
		});
		
		
		ensure.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUpdateManager.showDownloadDialog();
				dismiss();
			}
		});
		
		
		this.setContentView(layout);
		
	}
	
	

}
