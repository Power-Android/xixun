package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.SystemMessageModel;
import com.power.travel.xixuntravel.utils.StringUtils;

/**
 * 系统消息
 * @author fan
 *
 */
public class SystemMessageAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	
	List<SystemMessageModel> list=new ArrayList<SystemMessageModel>();
	
	public SystemMessageAdapter(Context context, List<SystemMessageModel> list) {
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
			convertView = inflater.inflate(R.layout.item_systemmessage_layout, null, false);
			holder.time = (TextView) convertView
					.findViewById(R.id.time);
			holder.title = (TextView) convertView
					.findViewById(R.id.title);
			holder.content = (TextView) convertView
					.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.time.setText(StringUtils.getStrTimeMonthDaySF(list.get(position).getAddtime()));
		holder.title.setText("【"+StringUtils.getStrTimeMonthDay(list.get(position).getAddtime())+
				"】"+list.get(position).getTitle());
		holder.content.setText(list.get(position).getDescription());
		

		return convertView;
	}

	final class ViewHolder {
		TextView time,title,content;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
