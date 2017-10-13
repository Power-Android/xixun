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
import com.power.travel.xixuntravel.activity.LoginActivity;
import com.power.travel.xixuntravel.model.GuideModel;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.rong.imkit.RongIM;

/**
 * 常识
 * @author fan
 *
 */
public class GuideListAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<GuideModel> list=new ArrayList<GuideModel>();
	
	public GuideListAdapter(Context context, List<GuideModel> list) {
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
			convertView = inflater.inflate(R.layout.item_guidelist_layout, null, false);
		
			holder.nickname = (TextView) convertView
					.findViewById(R.id.item_guide_nickname);
			holder.age = (TextView) convertView
					.findViewById(R.id.item_guide_age);
			holder.worktime = (TextView) convertView
					.findViewById(R.id.item_guide_worktime);
			holder.phone = (TextView) convertView
					.findViewById(R.id.item_guide_phone);
			holder.nation = (TextView) convertView
					.findViewById(R.id.item_guide_nation);
			holder.distance = (TextView) convertView
					.findViewById(R.id.item_guide_distance);
			holder.address = (TextView) convertView
					.findViewById(R.id.item_guide_address);
			holder.sex = (ImageView) convertView
					.findViewById(R.id.item_guide_sex);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_guide_face);
			holder.item_guide_chat = (ImageView) convertView
					.findViewById(R.id.item_guide_chat);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//list.get(position).getFace()
		imageLoader.displayImage(list.get(position).getFace(),
				holder.face, options,animateFirstListener);
		
		holder.nickname.setText(list.get(position).getNickname());
		holder.age.setText("年龄"+list.get(position).getAge()+"岁");
		holder.worktime.setText("工龄："+list.get(position).getWorktime()+"年");
		holder.phone.setText("电话："+list.get(position).getMobile());
		holder.nation.setText("民族："+list.get(position).getNation());
		holder.address.setText("所在地："+list.get(position).getAddress());
		holder.distance.setText(list.get(position).getApart());
		
		if(TextUtils.equals(list.get(position).getSex(), "1")){
			holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_boy));
		}else if(TextUtils.equals(list.get(position).getSex(), "2")){
			holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_girle));
		}

		holder.item_guide_chat.setOnClickListener(this);
		holder.item_guide_chat.setTag(position);
		return convertView;
	}

	final class ViewHolder {
		TextView nickname,age,worktime,phone,nation,address,distance;
		ImageView sex,face,item_guide_chat;
	}

	@Override
	public void onClick(View v) {

		int posi=(Integer)v.getTag();
		if(v.getId()==R.id.item_guide_chat){
			SharedPreferences sp;
			sp = context.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			if (sp.getBoolean(XZContranst.if_login, false)) {
				/**
				 * 进入融云
				 */
				 RongIM.getInstance().startPrivateChat(context,list.get(posi).getMid() , list.get(posi).getNickname());
				} else {
					context.startActivity(new Intent(context, LoginActivity.class));
				}
		}
	}


}
