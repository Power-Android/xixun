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
import com.power.travel.xixuntravel.model.AgeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 年龄
 * @author fan
 * 
 */
public class AgeAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	
	private List<AgeModel> list = new ArrayList<AgeModel>();
	
	public AgeAdapter(Context context, List<AgeModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_area_only_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.area_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(list.get(position).getVal());
		

		return convertView;
	}

	final class ViewHolder {
		TextView title;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
