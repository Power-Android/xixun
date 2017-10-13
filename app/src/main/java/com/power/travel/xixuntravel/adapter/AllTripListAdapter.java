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
import com.power.travel.xixuntravel.impl.AllTripOnItemOnClickListener;
import com.power.travel.xixuntravel.model.AllTripModel;
import com.power.travel.xixuntravel.utils.FlexibleRoundedBitmapDisplayer;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.annotation.SuppressLint;
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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 所有约伴(行程)
 * 
 * @author fan
 * 
 */
public class AllTripListAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options, options2;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<AllTripModel> list = new ArrayList<AllTripModel>();
	private DisplayMetrics dm;
	private RelativeLayout.LayoutParams imagebtn_params;
	private LinearLayout.LayoutParams layout_params;
	AllTripOnItemOnClickListener onItemOnClick;

	public AllTripListAdapter(Context context, List<AllTripModel> list) {
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
//				.displayer(new
//						RoundedBitmapDisplayer(20))// 设置用户加载图片task(这里是圆角图片显示)
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
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);//w h
		imagebtn_params.height = dm.widthPixels / 3 - 30;
		layout_params= new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout_params.height = dm.widthPixels / 2 - 20;
	}

	public void setOnItemOnClick(AllTripOnItemOnClickListener onItemOnClick) {
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

	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_alltrip_layout, null,
					false);

			holder.title = (TextView) convertView
					.findViewById(R.id.item_alltrip_title);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_alltrip_time);
			holder.see = (TextView) convertView
					.findViewById(R.id.item_alltrip_see);
			holder.comment = (TextView) convertView
					.findViewById(R.id.item_alltrip_comment);
			holder.ifyueban = (TextView) convertView
					.findViewById(R.id.item_alltrip_ifyueban);
			holder.ifyuecar = (TextView) convertView
					.findViewById(R.id.item_alltrip_ifyuecar);
			holder.ifyoucar = (TextView) convertView
					.findViewById(R.id.item_alltrip_ifyoucar);
			holder.sendname = (TextView) convertView
					.findViewById(R.id.item_alltrip_sendname);
			holder.pic = (ImageView) convertView
					.findViewById(R.id.item_alltrip_pic1);
			holder.attentin_type = (ImageView) convertView
					.findViewById(R.id.item_alltrip_attentin_type);
			holder.head = (ImageView) convertView
					.findViewById(R.id.item_alltrip_head);
			holder.sex = (ImageView) convertView
					.findViewById(R.id.item_alltrip_sex);
			holder.ifguide = (ImageView) convertView
					.findViewById(R.id.item_alltrip_ifguide);
			holder.ifdriver = (ImageView) convertView
					.findViewById(R.id.item_alltrip_ifdriver);
			holder.sendtime = (TextView) convertView
					.findViewById(R.id.item_alltrip_sendtime);
			holder.sendaddress = (TextView) convertView
					.findViewById(R.id.item_alltrip_sendaddress);
			holder.attention_layout = (LinearLayout) convertView
					.findViewById(R.id.item_alltrip_attentin_layout);
			holder.infolayout=(RelativeLayout)convertView
					.findViewById(R.id.item_alltrip_infolayout);
			holder.parent_layout= (LinearLayout) convertView
					.findViewById(R.id.item_alltrip_parent);
			holder.attentin = (TextView) convertView
					.findViewById(R.id.item_alltrip_attentin);
			holder.iv_layout = convertView.findViewById(R.id.iv_layout);
			holder.item_alltrip_pic1 = convertView.findViewById(R.id.item_alltrip_pic1);
			holder.item_alltrip_pic2 = convertView.findViewById(R.id.item_alltrip_pic2);
			holder.item_alltrip_pic3 = convertView.findViewById(R.id.item_alltrip_pic3);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

//		holder.pic.setLayoutParams(imagebtn_params);
//		holder.pic.setScaleType(ScaleType.CENTER_CROP);
		holder.iv_layout.setLayoutParams(imagebtn_params);
		holder.infolayout.setLayoutParams(layout_params);
		if (!TextUtils.isEmpty(list.get(position).getArr_img())) {
			/*imageLoader.displayImage(
					list.get(position).getArr_img().split(",")[0], holder.pic,
					options, animateFirstListener);*/
				String[] listpic = list.get(position).getArr_img().split(",");
				holder.item_alltrip_pic1.setScaleType(ScaleType.CENTER_CROP);
				holder.item_alltrip_pic2.setScaleType(ScaleType.CENTER_CROP);
				holder.item_alltrip_pic3.setScaleType(ScaleType.CENTER_CROP);
				if (listpic.length == 1){
					imageLoader.displayImage(
							list.get(position).getArr_img().split(",")[0], holder.item_alltrip_pic1,
							options, animateFirstListener);
//					holder.item_iv2.setVisibility(View.INVISIBLE);
//					holder.item_iv3.setVisibility(View.INVISIBLE);
				}
				if (listpic.length == 2){
					imageLoader.displayImage(
							list.get(position).getArr_img().split(",")[0], holder.item_alltrip_pic1,
							options, animateFirstListener);
					imageLoader.displayImage(
							list.get(position).getArr_img().split(",")[1], holder.item_alltrip_pic2,
							options, animateFirstListener);
//					holder.item_iv3.setVisibility(View.INVISIBLE);
				}
				if (listpic.length >= 3){
					imageLoader.displayImage(
							list.get(position).getArr_img().split(",")[0], holder.item_alltrip_pic1,
							options, animateFirstListener);
					imageLoader.displayImage(
							list.get(position).getArr_img().split(",")[1], holder.item_alltrip_pic2,
							options, animateFirstListener);
					imageLoader.displayImage(
							list.get(position).getArr_img().split(",")[2], holder.item_alltrip_pic3,
							options, animateFirstListener);
				}
		}
		if (!TextUtils.isEmpty(list.get(position).getFace())) {
			imageLoader.displayImage(list.get(position).getFace(), holder.head,
					options2, animateFirstListener);
		}
		
		holder.title.setText(list.get(position).getTitle());
		holder.time.setText(StringUtils.getStrTimeMonthDay(list.get(position)
				.getStarttime()) + "出发，共"+ list.get(position).getDays()+"天");
		holder.see.setText("浏览" + list.get(position).getOnclick());
		holder.comment.setText("评论" + list.get(position).getComm_count());
		/**
		 * 关注已经隐藏，如有需要去布局文件打开即可
		 */
		/**
		 *                      江城子 . 程序员之歌
		 *
		 *                  十年生死两茫茫，写程序，到天亮。
		 *                      千行代码，Bug何处藏。
		 *                  纵使上线又怎样，朝令改，夕断肠。
		 *
		 *                  领导每天新想法，天天改，日日忙。
		 *                      相顾无言，惟有泪千行。
		 *                  每晚灯火阑珊处，夜难寐，加班狂。
		*/
		if (TextUtils.equals(list.get(position).getFollow(), "1")) {// 是否关注
			holder.attentin_type.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.trip_attention));
		} else {
			holder.attentin_type.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.my_travel_praise));
		}
		if (TextUtils.equals(list.get(position).getIs_at(), "1")) {
			holder.ifyueban.setVisibility(View.VISIBLE);
		} else {
			holder.ifyueban.setVisibility(View.INVISIBLE);
		}
		if (TextUtils.equals(list.get(position).getIs_carpool(), "1")) {//是否约车
			holder.ifyuecar.setVisibility(View.VISIBLE);
		} else {
			holder.ifyuecar.setVisibility(View.INVISIBLE);
			holder.ifyoucar.setVisibility(View.VISIBLE);
		}

		
		if(!TextUtils.isEmpty(list.get(position).getNickname())){
			if(list.get(position).getNickname().length()>2){
				holder.sendname.setText(list.get(position).getNickname().subSequence(0, 2)+"...");
			}else{
				holder.sendname.setText(list.get(position).getNickname());
			}
		}
		
		
		if (TextUtils.equals(list.get(position).getSex(), "1")) {// 男
			holder.sex.setImageDrawable(context.getResources().getDrawable(
					R.drawable.my_boy));
		} else if (TextUtils.equals(list.get(position).getSex(), "2")) {// 女
			holder.sex.setImageDrawable(context.getResources().getDrawable(
					R.drawable.my_girle));
		}
		if (TextUtils.equals(list.get(position).getIf_guide(), "1")) {// 是导游
			holder.ifguide.setVisibility(View.VISIBLE);
		} else {
			holder.ifguide.setVisibility(View.INVISIBLE);
		}
		/*if (TextUtils.equals(list.get(position).getIf_driver(), "1")) {// 是司机
			holder.ifdriver.setVisibility(View.VISIBLE);
		} else {
			holder.ifdriver.setVisibility(View.INVISIBLE);
		}*/
		if (TextUtils.equals(
				StringUtils.CountTime(list.get(position).getAddtime()), "0")) {// 今天
			holder.sendtime.setText("今天 "
					+ StringUtils.getStrTime(list.get(position).getAddtime()));
		} else if (TextUtils.equals(
				StringUtils.CountTime(list.get(position).getAddtime()), "1")) {// 昨天
			holder.sendtime.setText("昨天 "
					+ StringUtils.getStrTime(list.get(position).getAddtime()));
		} else {
			holder.sendtime.setText(StringUtils.getStrTimeMonthDay2(list.get(
					position).getAddtime())+"   "+StringUtils.getStrTime(list.get(position).getAddtime()));
		}
		if (TextUtils.equals(list.get(position).getProvinceName(),
				list.get(position).getCityName())) {
			// 直辖市
			holder.sendaddress.setText(list.get(position).getProvinceName());
		} else {// 非直辖市
			holder.sendaddress.setText(list.get(position).getProvinceName());
		}

		holder.attention_layout.setOnClickListener(this);
		holder.attention_layout.setTag(position);
		return convertView;
	}

	final class ViewHolder {
		TextView title, time, see, comment, ifyueban, ifyuecar, sendname,
				sendtime, sendaddress, ifyoucar, attentin;
		ImageView pic, attentin_type, sex, head, ifguide, ifdriver,item_alltrip_pic1,item_alltrip_pic2,item_alltrip_pic3;
		LinearLayout attention_layout,parent_layout,iv_layout;
		RelativeLayout infolayout;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.item_alltrip_attentin_layout) {
			int posi = (Integer) v.getTag();
			if (TextUtils.equals(list.get(posi).getFollow(), "1")) {// 关注 变为不关注
				onItemOnClick.OnItemClick(0, posi);
			} else {
				onItemOnClick.OnItemClick(1, posi);
			}
		}
	}

}
