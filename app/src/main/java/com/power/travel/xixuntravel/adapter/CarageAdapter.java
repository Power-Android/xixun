package com.power.travel.xixuntravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;

/**
 * 车龄
 * @author fan
 * 
 */
public class CarageAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	
	private String[] list;
	
	public CarageAdapter(Context context, String[] list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return list.length;
	}

	public Object getItem(int position) {
		return list.length;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_cartype_listview_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.area_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(list[position]);
		

		return convertView;
	}

	final class ViewHolder {
		TextView title;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
