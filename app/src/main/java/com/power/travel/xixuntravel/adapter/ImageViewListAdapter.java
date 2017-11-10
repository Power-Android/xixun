package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 所有约伴(行程) 图片列表
 * 
 * @author fan
 * 
 */
public class ImageViewListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	Context context;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	ImageLoader imageLoader = ImageLoader.getInstance();
	String list ;
	private DisplayMetrics dm;
	private RelativeLayout.LayoutParams imagebtn_params;

	public ImageViewListAdapter(Context context, String list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.imaleloadlogo)
				.showImageForEmptyUri(R.drawable.imaleloadlogo)
				.showImageOnFail(R.drawable.imaleloadlogo)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(10))
				.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		dm = context.getResources().getDisplayMetrics();
		imagebtn_params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params.height = dm.widthPixels;
	}

	public int getCount() {
		return list.split(",").length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_imageview_layout,
					null, false);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_tripdetail_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.face.setLayoutParams(imagebtn_params);
		if (!TextUtils.isEmpty(list)) {
			imageLoader.displayImage(list.split(",")[position],
					holder.face, options,animateFirstListener);
		}

		return convertView;
	}

	final class ViewHolder {
		ImageView face;
	}


}
