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
import com.power.travel.xixuntravel.impl.PraiseOnItemOnClickListener;
import com.power.travel.xixuntravel.model.RecommentModel;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.StringUtils;
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

/**
 * 推荐
 * @author fan
 *
 */
public class RecommendListListAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<RecommentModel> list=new ArrayList<RecommentModel>();
	PraiseOnItemOnClickListener onItemOnclick;
	
	public RecommendListListAdapter(Context context, List<RecommentModel> list) {
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
	
	public void OnItemOnClick(PraiseOnItemOnClickListener onItemOnclick ){
		this.onItemOnclick=onItemOnclick;
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
			convertView = inflater.inflate(R.layout.item_recommend_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.item_recommend_title);
			holder.des = (TextView) convertView
					.findViewById(R.id.item_recommend_des);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_recommend_time);
			holder.onclick = (TextView) convertView
					.findViewById(R.id.item_recommend_onclick);
			holder.zan = (TextView) convertView
					.findViewById(R.id.item_recommend_zan);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_recommend_face);
			holder.zan_iv = (ImageView) convertView
					.findViewById(R.id.item_recommend_zan_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		imageLoader.displayImage(HttpUrl.Url+list.get(position).getThumb(),
				holder.face, options,animateFirstListener);
		
		holder.title.setText(list.get(position).getTitle());
		holder.des.setText(list.get(position).getDescription());
		holder.time.setText(StringUtils.getStandardDate(list.get(position).getAddtime()));
		holder.onclick.setText(list.get(position).getOnclick());
		holder.zan.setText(list.get(position).getZan());
		if(TextUtils.equals(list.get(position).getZanIf(), "1")){//赞
			holder.zan_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.recommend_zan));
		}else{//不赞
			holder.zan_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.recommend_nozan));
		}
		
		holder.zan.setOnClickListener(this);
		holder.zan.setTag(position);

		return convertView;
	}

	final class ViewHolder {
		TextView title,des,time,onclick,zan;
		ImageView face,zan_iv;
	}

	@Override
	public void onClick(View v) {
		int posi=(Integer)v.getTag();
		if(v.getId()==R.id.item_recommend_zan){
//			onItemOnclick.OnItemClick(1, posi);
		}
		
	}
	
}
