package com.power.travel.xixuntravel.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;

public class MenuGridViewAdapter extends BaseAdapter {
	
	private Integer[] img = new Integer[] {
			R.drawable.service_guide, R.drawable.service_car, R.drawable.service_knowledge, R.drawable.service_rescuenews,
			R.drawable.service_viewspot, R.drawable.yueche1, R.drawable.zufang1, R.drawable.service_contactus};// 存放图片

	private String[] name = new String[] { "导游", "司机", "常识", "救援", "景区", "约车","租房","联系我们" };
	
	Context context;

	public MenuGridViewAdapter(Context context) {
		this.context = context;
		
	}

	public int getCount() {

		return name.length;
	}

	public Object getItem(int arg0) {

		return 0;
	}

	public long getItemId(int arg0) {

		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater i = LayoutInflater.from(context);
		RelativeLayout ll = (RelativeLayout) i.inflate(
				R.layout.menu_gradeview_item, null);
		ImageView imageview = (ImageView) ll
				.findViewById(R.id.menu_gradeview_itemima);
		TextView textview = (TextView) ll
				.findViewById(R.id.menu_gradeview_itemtv);

			imageview.setImageResource(img[position]);
			textview.setText(name[position]);
	
		return ll;
	}

}
