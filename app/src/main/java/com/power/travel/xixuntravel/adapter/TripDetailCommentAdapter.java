package com.power.travel.xixuntravel.adapter;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.impl.AllTripDetailComment_replayOnItemOnClickListener;
import com.power.travel.xixuntravel.model.TripDetailCommentModel;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 行程/约伴详情的评论
 * @author fan
 *
 */
public class TripDetailCommentAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<TripDetailCommentModel> list=new ArrayList<TripDetailCommentModel>();
	ForegroundColorSpan redSpan ;
	AllTripDetailComment_replayOnItemOnClickListener onItemClick;
	
	public TripDetailCommentAdapter(Context context, List<TripDetailCommentModel> list){
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
//		.cacheInMemory(true)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
		 .displayer(new RoundedBitmapDisplayer(100))//设置用户加载图片task(这里是圆角图片显示)
		.build();
		redSpan= new ForegroundColorSpan(context.getResources().getColor(R.color.btn_color_red_normal));
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}
	
	public void setOnItemClick(AllTripDetailComment_replayOnItemOnClickListener onItemClick){
		this.onItemClick=onItemClick;
	}
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_tripdetailcomment_layout, null, false);
		
			holder.sendname = (TextView) convertView
					.findViewById(R.id.item_traveldetailcomment_sendname);
			holder.sendtime = (TextView) convertView
					.findViewById(R.id.item_traveldetailcomment_sendtime);
			holder.comment = (TextView) convertView
					.findViewById(R.id.item_traveldetailcomment_comment);
			holder.replay = (TextView) convertView
					.findViewById(R.id.item_tripdetailcomment_replay);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_traveldetailcomment_head);
			holder.sex=(ImageView)convertView
					.findViewById(R.id.item_traveldetailcomment_sex);
			holder.replaylist = (MyListView) convertView
					.findViewById(R.id.item_tripdetailcomment_replaylist);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		imageLoader.displayImage(list.get(position).getFace(),
				holder.face, options,animateFirstListener);
		holder.sendname.setText(list.get(position).getNickname());
		if(TextUtils.equals(list.get(position).getSex(), "1")){
			holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_boy));
		}else if(TextUtils.equals(list.get(position).getSex(), "2")){
			holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_girle));
		}
		holder.sendtime.setText(StringUtils.getStandardDate(list.get(position).getAddtime()));
		holder.comment.setText(list.get(position).getContent());
	
		if(!TextUtils.isEmpty(list.get(position).getList())){
			holder.replaylist.setAdapter(new TripDetailComment_ReplayAdapter(context, list.get(position).getList()));
		}
		
		
		holder.replay.setOnClickListener(this);
		holder.replay.setTag(position);
		
		return convertView;
	}
	
	final class ViewHolder {
		TextView sendname,sendtime,comment,replay;
		ImageView face,sex;
		MyListView replaylist;
	}

	@Override
	public void onClick(View v) {
		int posi=(Integer)v.getTag();
		if(v.getId()==R.id.item_tripdetailcomment_replay){
			onItemClick.OnItemClick(1, posi);
		}
	}

}
