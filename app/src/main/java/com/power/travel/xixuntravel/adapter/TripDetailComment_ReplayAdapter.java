package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.TripDetailComment_ReplayModel;
import com.power.travel.xixuntravel.utils.LogUtil;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 行程详情评论的回复
 * @author fan
 *
 */
public class TripDetailComment_ReplayAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	private String list_str;
	List<TripDetailComment_ReplayModel> list=new ArrayList<TripDetailComment_ReplayModel>();
	ForegroundColorSpan redSpan ;
	public TripDetailComment_ReplayAdapter(Context context, String list_str) {
		super();
		this.context = context;
		this.list_str = list_str;
		inflater = LayoutInflater.from(context);
		redSpan= new ForegroundColorSpan(context.getResources().getColor(R.color.btn_color_red_normal));
		try {
			JSONArray array=new JSONArray(list_str);
			list= JSON.parseArray(array.toString(), TripDetailComment_ReplayModel.class);
		} catch (JSONException e) {
			LogUtil.e("行程详评论的回复解析错误", e.toString());
			e.printStackTrace();
		}
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
			convertView = inflater.inflate(R.layout.item_tripdetailcomment_replay_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.nickname);
			holder.content = (TextView) convertView
					.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.title.setText(list.get(position).getNickname());
		SpannableStringBuilder builder = new SpannableStringBuilder("回复"+list.get(position).getUpName()+"："+list.get(position).getContent());
		builder.setSpan(redSpan, "回复".length(),"回复".length()+list.get(position).getUpName().length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.content.setText(builder);

		return convertView;
	}

	final class ViewHolder {
		TextView title,content;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
