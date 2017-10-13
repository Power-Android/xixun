package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.CarModel;

/**
 * 车辆品牌
 * @author fan
 * 
 */
public class CarTypeAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	
	private List<CarModel> list = new ArrayList<CarModel>();
	
	public CarTypeAdapter(Context context, List<CarModel> list) {
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
			convertView = inflater.inflate(R.layout.item_cartype_listview_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.area_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(list.get(position).getName());
		

		return convertView;
	}

	final class ViewHolder {
		TextView title;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
