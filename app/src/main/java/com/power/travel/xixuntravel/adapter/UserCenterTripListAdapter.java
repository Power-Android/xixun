package com.power.travel.xixuntravel.adapter;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.impl.MyTripOnItemOnClickListener;
import com.power.travel.xixuntravel.model.MyTripModel;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心 行程
 * 
 * @author fan
 * 
 */
public class UserCenterTripListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<MyTripModel> list = new ArrayList<MyTripModel>();
	int mScreentWidth;
	MyTripOnItemOnClickListener onItemOnClick;

	public UserCenterTripListAdapter(Context context, List<MyTripModel> list,
                                     int screenWidth) {
		super();
		this.context = context;
		this.list = list;
		this.mScreentWidth = screenWidth;
		inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
				// .cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.displayer(new RoundedBitmapDisplayer(20))// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

	}

	public void setOnItemOnClick(MyTripOnItemOnClickListener onItemOnClick) {
		this.onItemOnClick = onItemOnClick;
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

	public void setUpdate(int mPosition) {
		this.mPosition = mPosition;
		super.notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_usercentertriplist_layout,
					null, false);

			holder.title = (TextView) convertView
					.findViewById(R.id.item_trip_title);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_trip_time);
			holder.canceltype=(TextView) convertView
					.findViewById(R.id.item_trip_canceltype);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_trip_content);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_trip_face);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(list.get(position).getArr_img())) {
			imageLoader.displayImage(
					list.get(position).getArr_img().split(",")[0], holder.face,
					options, animateFirstListener);
		}

		holder.title.setText(list.get(position).getTitle());
		holder.time.setText(StringUtils.getStrTimeMonthDay(list.get(position)
				.getStarttime())
				+ "-"
				+ StringUtils.getStrTimeMonthDay(list.get(position)
						.getEndtime()));
		holder.content.setText(list.get(position).getInfo());
		return convertView;
	}

	final class ViewHolder {
		TextView title, time, content,canceltype;
		ImageView face;
	}
}
