package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.TravelDetailActivity;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyImageViewSize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 个人中心游记图片展示
 * @author fan
 *
 */
public class MyTravelGrid_UserCenterAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象

	int LINK_URL = 1;//url,要用split拆分連接
	int PIC_PATH = 2;//本地URL
	private LayoutInflater mLayoutInflater;
	ArrayList<String> mPic_list = new ArrayList<String>();
	Context mContext;
	int mType;
	MyTravelModel travelModel;
	int screenWidth, screenHeight;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类

	public MyTravelGrid_UserCenterAdapter(String pic_link, Context context, int type) {
		mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
		mType=type;
		if (type == LINK_URL) {
			if (!TextUtils.isEmpty(pic_link)) {
				setPiclistData(pic_link);
			}
		}  
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(false)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
		// .displayer(new RoundedBitmapDisplayer(20))
		// //设置用户加载图片task(这里是圆角图片显示)
		.build();
	}
	
	public MyTravelGrid_UserCenterAdapter(MyTravelModel model,String[]  pic_link, Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		travelModel=model;
		mContext = context; 
		setPiclistData2(pic_link);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(false)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
		// .displayer(new RoundedBitmapDisplayer(20))
		// //设置用户加载图片task(这里是圆角图片显示)
		.build();
	}
	public MyTravelGrid_UserCenterAdapter(ArrayList<String> list_url,Context context, int type) {
		mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
		mType=type;
		  if (type == PIC_PATH) {
			screenWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
			screenHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
		    mPic_list=list_url;
		  }
	}
	@Override
	public int getCount() {
		int fanhui = 0;
		if(mPic_list == null){
			fanhui=0;
		}else{
			if(mPic_list.size()>4){
				fanhui=4;
			}else{
				fanhui=mPic_list.size();
			}
		} 
//		return mPic_list == null ? 0 : mPic_list.size();
		return fanhui;
	}

	@Override
	public String getItem(int position) {
		return mPic_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyGridViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyGridViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.image_layout,parent, false);
			viewHolder.imageView = (MyImageViewSize) convertView.findViewById(R.id.iv_user_img);
			viewHolder.imageView2 = (MyImageViewSize) convertView.findViewById(R.id.iv_user_img2);
			// 用来监听ImageView的宽和高
			viewHolder.imageView.setOnMeasureListener(new MyImageViewSize.OnMeasureListener() {

				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			viewHolder.imageView2.setOnMeasureListener(new MyImageViewSize.OnMeasureListener() {

				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyGridViewHolder) convertView.getTag();
		}
		String url = getItem(position);

//		if(mType==LINK_URL){
		ImageLoader imageLoader = ImageLoader.getInstance();

		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

		/**
		 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
		 */
		if(mPic_list.size()>1){
			viewHolder.imageView.setVisibility(View.VISIBLE);
			viewHolder.imageView2.setVisibility(View.GONE);
			imageLoader.displayImage(url,viewHolder.imageView, options, animateFirstListener);
		}else{
			viewHolder.imageView.setVisibility(View.GONE);
			viewHolder.imageView2.setVisibility(View.VISIBLE);
			imageLoader.displayImage(url,
					viewHolder.imageView2, options, animateFirstListener);
		}
		
//			ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
//		}
//		else if(mType==PIC_PATH){
//			Bitmap mBitmap = ImageUtils.getSampledBitmapFromFile(url, screenWidth,screenHeight);
//			viewHolder.imageView.setImageBitmap(mBitmap);
//		}	
			viewHolder.imageView.setTag(position);
			viewHolder.imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							TravelDetailActivity.class);
					intent.putExtra("id", travelModel.getId());
					mContext.startActivity(intent);
				}
			});
			
			viewHolder.imageView2.setTag(position);
			viewHolder.imageView2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							TravelDetailActivity.class);
					intent.putExtra("id", travelModel.getId());
					mContext.startActivity(intent);
//					int po=(Integer) v.getTag();
//					Intent mIntent = ImageViewPagerActivity.newIntent(mContext,travelModel,mPic_list, po);
//					mContext.startActivity(mIntent);
 					
				}
			});
		return convertView;
	}

	private static class MyGridViewHolder {
		MyImageViewSize imageView,imageView2;
	}

	/***
	 * String ---split
	 * 
	 * @param
	 */
	void setPiclistData(String pic_link) {
		String[] pic_ = pic_link.split(",");
		for (String str : pic_) {
			mPic_list.add(str);
		}
	}
	
	void setPiclistData2(String[] pic_link) {
		String[] pic_ = pic_link;
		for (String str : pic_) {
			mPic_list.add(str);
		}
	}
}
