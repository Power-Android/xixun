package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.MessageModel;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyGridView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 消息
 * @author fan
 *
 */
public class MessageAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<MessageModel> list=new ArrayList<MessageModel>();
	
	public MessageAdapter(Context context, List<MessageModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		 .cacheInMemory(false)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
		.displayer(new RoundedBitmapDisplayer(10))// 设置用户加载图片task(这里是圆角图片显示)
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
			convertView = inflater.inflate(R.layout.item_message_layout, null, false);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_message_face);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_message_time);
			holder.title = (TextView) convertView
					.findViewById(R.id.item_message_nicheng);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_message_title);
			holder.message_type= (TextView) convertView
					.findViewById(R.id.item_message_type);
			holder.ifguide= (ImageView) convertView
					.findViewById(R.id.item_message_ifguide);
			holder.ifdriver= (ImageView) convertView
					.findViewById(R.id.item_message_ifdriver);
			holder.item_message_pic= (ImageView) convertView
					.findViewById(R.id.item_message_pic);
			holder.item_message_videoplay= (ImageView) convertView
					.findViewById(R.id.item_message_videoplay);
			holder.gridview = (MyGridView) convertView
					.findViewById(R.id.image_gridView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		imageLoader.displayImage(
				list.get(position).getFace(), holder.face,options, animateFirstListener);
		
		
		holder.title.setText(list.get(position).getNickname());
		holder.content.setText(list.get(position).getContent());
		
		if(TextUtils.equals(list.get(position).getState(), "0")){//未读
			holder.message_type.setVisibility(View.VISIBLE);
			if(TextUtils.equals(list.get(position).getType(), "1")){//约伴
				holder.message_type.setText("来源于约伴");
			}else{//游记
				holder.message_type.setText("来源于游记");
			}
		}else{//已读
			holder.message_type.setVisibility(View.GONE);
		}
		
		if(TextUtils.equals(list.get(position).getIf_guide(), "1")){//
			holder.ifguide.setVisibility(View.VISIBLE);
		}else{
			holder.ifguide.setVisibility(View.GONE);
		}
		if(TextUtils.equals(list.get(position).getIf_driver(), "1")){//
			holder.ifdriver.setVisibility(View.VISIBLE);
		}else{
			holder.ifdriver.setVisibility(View.GONE);
		}
		
//		if(TextUtils.isEmpty(holder.time.getText().toString())){
			if(!TextUtils.isEmpty(list.get(position).getData())){
				try {
					JSONObject json=new JSONObject(list.get(position).getData());
					String img=json.optString("arr_img");
					 if (!TextUtils.isEmpty(img)) {//图片不为空
							holder.gridview.setVisibility(View.VISIBLE);
							holder.item_message_pic.setVisibility(View.GONE);
							holder.item_message_videoplay.setVisibility(View.GONE);
							holder.gridview.setTag(img);
							if(holder.gridview.getTag()!=null&&holder.gridview.getTag().equals(img)){
								String[] listpic=img.split(",");
								if(listpic.length>1){
									holder.gridview.setNumColumns(2);
								}else{
									holder.gridview.setNumColumns(1);
								}
								holder.gridview.setAdapter(new MessageGridAdapter(list.get(position).getCid(),list.get(position).getType(),listpic, context));
							}
						}else{//视频不为空
							holder.gridview.setVisibility(View.GONE);
							holder.item_message_pic.setVisibility(View.VISIBLE);
							holder.item_message_videoplay.setVisibility(View.VISIBLE);
							holder.item_message_pic.setTag(json.optString("video"));
							if(holder.item_message_pic.getTag()!=null&&holder.item_message_pic.getTag().equals(json.optString("video"))){
								AsyncTaskImageLoad async = new AsyncTaskImageLoad(
										holder.item_message_pic);
								async.execute(json.optString("video"));
							}
							holder.item_message_videoplay.setImageDrawable(context.getResources()
									.getDrawable(R.drawable.play_smalltoo));
						}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//		}
		
		holder.time.setText(StringUtils.getStrTimeMonthDaySF(list.get(position).getAddtime()));

		return convertView;
	}

	final class ViewHolder {
		TextView time,title,content,message_type;
		ImageView face,ifguide,ifdriver,item_message_pic,item_message_videoplay;
		MyGridView gridview;
	}

	@Override
	public void onClick(View v) {
		
		
	}
	
	public class AsyncTaskImageLoad extends AsyncTask<String, Integer, Bitmap> {

		private ImageView Image = null;

		public AsyncTaskImageLoad(ImageView img) {
			Image = img;
		}

		// 运行在子线程中
		protected Bitmap doInBackground(String... params) {
			try {

				Bitmap map = StringUtils
						.createVideoThumbnail(params[0], 60, 60);
				return map;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			if (Image != null && result != null) {
				Image.setImageBitmap(result);
			}

			super.onPostExecute(result);
		}
	}



}
