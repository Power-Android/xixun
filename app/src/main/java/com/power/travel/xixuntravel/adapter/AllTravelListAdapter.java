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
import com.power.travel.xixuntravel.activity.ImageViewPagerActivity;
import com.power.travel.xixuntravel.activity.ImageViewPager_3Activity;
import com.power.travel.xixuntravel.activity.ImageVoideActivity;
import com.power.travel.xixuntravel.activity.ImageVoide_2Activity;
import com.power.travel.xixuntravel.activity.TravelDetailActivity;
import com.power.travel.xixuntravel.activity.UserCenterActivity;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyGridView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 全部游记
 * 
 * @author fan
 * 
 */
public class AllTravelListAdapter extends BaseAdapter implements
        OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options, options2;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<AllTravelModel> list = new ArrayList<AllTravelModel>();
	private AllTravelModel allTravelModel;
	private DisplayMetrics dm;
	private RelativeLayout.LayoutParams imagebtn_params,imagebtn_params1;
	private String TAG="AllTravelListAdapter";
	ArrayList<String> mPic_list = new ArrayList<String>();
	private LinearLayout.LayoutParams iv_params;

	public AllTravelListAdapter(Context context, List<AllTravelModel> list) {
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
//				 .displayer(new
//				 RoundedBitmapDisplayer(10))//设置用户加载图片task(这里是圆角图片显示)
				.build();

		options2 = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.imaleloadlogo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(false)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.displayer(new RoundedBitmapDisplayer(100))// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		dm = context.getResources().getDisplayMetrics();

		imagebtn_params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params.height = dm.widthPixels / 2;
		imagebtn_params1 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params1.height = dm.widthPixels / 3;
		iv_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		iv_params.width = dm.widthPixels / 3 - 10;
		iv_params.height = dm.widthPixels / 3;

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
			convertView = inflater.inflate(R.layout.item_alltravel_layout,
					null, false);

			holder.title = (TextView) convertView
					.findViewById(R.id.item_alltravel_title);
			holder.pic = (ImageView) convertView
					.findViewById(R.id.item_alltravel_pic);
			holder.pic2 = (ImageView) convertView
					.findViewById(R.id.item_alltravel_pic2);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_alltravel_head);
			holder.alltravel_video = (ImageView) convertView
					.findViewById(R.id.item_alltravel_video);
			holder.item_userimg = (LinearLayout)convertView.findViewById(R.id.item_userimg);
			holder.item_iv1 = (ImageView) convertView.findViewById(R.id.item_iv1);
			holder.item_iv2 = (ImageView) convertView.findViewById(R.id.item_iv2);
			holder.item_iv3 = (ImageView) convertView.findViewById(R.id.item_iv3);
			holder.iv_layout =(LinearLayout) convertView.findViewById(R.id.iv_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AllTravelModel model = list.get(position);
		holder.pic.setLayoutParams(imagebtn_params);
		holder.pic2.setLayoutParams(imagebtn_params);
		holder.iv_layout.setLayoutParams(imagebtn_params1);
		holder.item_iv1.setLayoutParams(iv_params);
		holder.item_iv2.setLayoutParams(iv_params);
		holder.item_iv3.setLayoutParams(iv_params);

		if (!TextUtils.isEmpty(list.get(position).getImg())){
			holder.iv_layout.setVisibility(View.VISIBLE);
			holder.pic.setVisibility(View.GONE);
			holder.pic2.setVisibility(View.GONE);
			holder.pic.setTag(list.get(position).getImg());
			holder.item_iv1.setTag(list.get(position).getImg());
			if(holder.pic.getTag()!=null&&holder.pic.getTag().equals(list.get(position).getImg())){
				String[] listpic = list.get(position).getImg().split(",");
				holder.item_iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
				holder.item_iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
				holder.item_iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
				if (listpic.length == 1){
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[0], holder.item_iv1,
							options, animateFirstListener);
//					holder.item_iv2.setVisibility(View.INVISIBLE);
//					holder.item_iv3.setVisibility(View.INVISIBLE);
				}
				if (listpic.length == 2){
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[0], holder.item_iv1,
							options, animateFirstListener);
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[1], holder.item_iv2,
							options, animateFirstListener);
//					holder.item_iv3.setVisibility(View.INVISIBLE);
				}
				if (listpic.length >= 3){
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[0], holder.item_iv1,
							options, animateFirstListener);
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[1], holder.item_iv2,
							options, animateFirstListener);
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[2], holder.item_iv3,
							options, animateFirstListener);
				}
			}
			holder.alltravel_video.setVisibility(View.GONE);
		}
			/*if (!TextUtils.isEmpty(list.get(position).getImg())) {// 图片信息
				holder.pic.setVisibility(View.VISIBLE);
				holder.pic2.setVisibility(View.GONE);
				holder.pic.setTag(list.get(position).getImg());
				holder.pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
				if(holder.pic.getTag()!=null&&holder.pic.getTag().equals(list.get(position).getImg())){
					imageLoader.displayImage(
							list.get(position).getImg().split(",")[0], holder.pic,
							options, animateFirstListener);
				}
				holder.alltravel_video.setVisibility(View.GONE);
//				LogUtil.e(TAG, "加载图片");
			}*/ else if (!TextUtils.isEmpty(list.get(position).getVideo())) {// 视频信息
				holder.alltravel_video.setVisibility(View.VISIBLE);
				holder.pic.setVisibility(View.GONE);
				holder.iv_layout.setVisibility(View.GONE);
				holder.pic2.setVisibility(View.VISIBLE);
				holder.pic2.setTag(list.get(position).getVideo());
				if(holder.pic2.getTag()!=null&&holder.pic2.getTag().equals(list.get(position).getVideo())){
					AsyncTaskImageLoad async = new AsyncTaskImageLoad(holder.pic2);
					async.execute(list.get(position).getVideo());
				}
//				LogUtil.e(TAG, "加载视频");
			}
//		}

		if (!TextUtils.isEmpty(list.get(position).getFace())) {
			imageLoader.displayImage(list.get(position).getFace(), holder.face,
					options2, animateFirstListener);
		}
		holder.title.setText(list.get(position).getContent());
		holder.pic.setTag(position);
		holder.iv_layout.setTag(position);
		holder.iv_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int poi = (Integer) view.getTag();
				String [] listpic = list.get(position).getImg().split(",");
				for (String str : listpic) {
					mPic_list.add(str);
				}
				Intent mIntent = ImageViewPager_3Activity.newIntent(context,list.get(position),mPic_list,poi);
				context.startActivity(mIntent);
				mPic_list.clear();
			}
		});
		holder.pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int poi = (Integer) view.getTag();
				String [] listpic = list.get(position).getImg().split(",");
				for (String str : listpic) {
					mPic_list.add(str);
				}
				Intent mIntent = ImageViewPager_3Activity.newIntent(context,list.get(position),mPic_list,poi);
				context.startActivity(mIntent);
				mPic_list.clear();
			}
		});
		holder.alltravel_video.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				int poi = (Integer) view.getTag();
				ArrayList<String> mPic_list = null;
				Intent mIntent = ImageVoide_2Activity.newIntent(context, list.get(position),
						mPic_list, 0);
				context.startActivity(mIntent);
			}
		});
		holder.item_userimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, TravelDetailActivity.class);
				intent.putExtra("id", list.get(position).getId());
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	final class ViewHolder {
		TextView title;
		ImageView pic,pic2, face, alltravel_video,item_iv1,item_iv2,item_iv3;
		LinearLayout item_userimg,iv_layout;
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
