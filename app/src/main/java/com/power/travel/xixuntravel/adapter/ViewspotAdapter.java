package com.power.travel.xixuntravel.adapter;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.ViewsportModel;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 景区
 * @author fan
 *
 */
public class ViewspotAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<ViewsportModel> list=new ArrayList<ViewsportModel>();
	
	public ViewspotAdapter(Context context, List<ViewsportModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
//		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
//		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
//		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
//		.cacheInMemory(true)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
		 .displayer(new RoundedBitmapDisplayer(20))//设置用户加载图片task(这里是圆角图片显示)
		.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
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
			convertView = inflater.inflate(R.layout.item_viewsport_layout, null, false);
		
			holder.description = (TextView) convertView
					.findViewById(R.id.item_viewsport_description);
			holder.title = (TextView) convertView
					.findViewById(R.id.item_viewsport_title);
			holder.star = (TextView) convertView
					.findViewById(R.id.item_viewsport_star);
			holder.label = (TextView) convertView
					.findViewById(R.id.item_viewsport_label);
			holder.distance = (TextView) convertView
					.findViewById(R.id.item_viewsport_distance);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_viewsport_face);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		imageLoader.displayImage(HttpUrl.Url+list.get(position).getThumb(),
				holder.face, options,animateFirstListener);
		
		holder.description.setText(list.get(position).getDescription());
		holder.title.setText(list.get(position).getTitle());
		holder.star.setText(list.get(position).getStar()+"A景区");
		holder.distance.setText(list.get(position).getApart());
		if(!TextUtils.isEmpty(list.get(position).getLabel())){
			holder.label.setVisibility(View.VISIBLE);
			holder.label.setText(list.get(position).getLabel());
		}else{
			holder.label.setVisibility(View.GONE);
		}


		return convertView;
	}

	final class ViewHolder {
		TextView description,title,star,label,distance;
		ImageView face;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
