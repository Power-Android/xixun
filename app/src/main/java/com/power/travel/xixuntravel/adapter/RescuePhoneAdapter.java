package com.power.travel.xixuntravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.RescuePhoneModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 救援知识
 * @author fan
 *
 */
public class RescuePhoneAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	
	List<RescuePhoneModel> list=new ArrayList<RescuePhoneModel>();
	
	public RescuePhoneAdapter(Context context, List<RescuePhoneModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void setUpdate(int mPosition){
		this.mPosition=mPosition;
		super.notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_rescuephone_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.title);
//			holder.content = (TextView) convertView
//					.findViewById(R.id.item_knowledge_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(list.get(position).getTitle()+"："+list.get(position).getDescription());
//		holder.content.setText(list.get(position).getDescription());
		

		return convertView;
	}

	final class ViewHolder {
		TextView title,content;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
