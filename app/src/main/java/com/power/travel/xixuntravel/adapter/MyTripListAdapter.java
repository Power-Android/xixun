package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.content.SharedPreferences;
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

/**
 * 我的行程
 * 
 * @author fan
 * 
 */
public class MyTripListAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	SharedPreferences sp;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<MyTripModel> list = new ArrayList<MyTripModel>();
	int mScreentWidth;
	MyTripOnItemOnClickListener onItemOnClick;

	public MyTripListAdapter(Context context, List<MyTripModel> list,
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
				.displayer(new RoundedBitmapDisplayer(100))// 设置用户加载图片task(这里是圆角图片显示)
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
			convertView = inflater.inflate(R.layout.item_mytriplist_layout,
					null, false);

			sp=context.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);


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
			holder.item_trip_dath = (TextView) convertView
					.findViewById(R.id.item_trip_dath);
			holder.item_go_where = (TextView) convertView
					.findViewById(R.id.item_go_where);
			holder.item_alltrip_ifyueban = (TextView) convertView
					.findViewById(R.id.item_alltrip_ifyueban);
			holder.item_alltrip_ifyuecar = (TextView) convertView
					.findViewById(R.id.item_alltrip_ifyuecar);
			// 整个item的水平滚动层
			holder.itemHorizontalScrollView = (HorizontalScrollView) convertView
					.findViewById(R.id.hsv);
			holder.actionLayout = convertView.findViewById(R.id.ll_action);
			holder.recover = (Button) convertView.findViewById(R.id.recover);
			holder.cancel = (Button) convertView.findViewById(R.id.cancel);
			holder.delect = (Button) convertView.findViewById(R.id.delet);
			// 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
			holder.normalItemContentLayout = convertView.findViewById(R.id.ll_content);
			LayoutParams lp = holder.normalItemContentLayout.getLayoutParams();
			lp.width = mScreentWidth;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(list.get(position).getArr_img())) {//设置头像
			/*imageLoader.displayImage(
					list.get(position).getArr_img().split(",")[0], holder.face,
					options, animateFirstListener);*/
			imageLoader.displayImage(sp.getString(XZContranst.face, ""),holder.face,options,animateFirstListener);
		}

//		holder.title.setText(list.get(position).getTitle());
		holder.title.setText(sp.getString(XZContranst.nickname,""));
		Date start_data = StringUtils.getData(list.get(position).getStarttime());
		Date end_data = StringUtils.getData(list.get(position).getEndtime());
		int gapCount = StringUtils.getGapCount(start_data, end_data);
		holder.item_trip_dath.setText("旅行天数"+gapCount+"天");
		/*holder.time.setText(StringUtils.getStrTimeMonthDay(list.get(position)
				.getStarttime())
				+ "-"
				+ StringUtils.getStrTimeMonthDay(list.get(position)
						.getEndtime()));*/

		holder.time.setText("出发时间："+StringUtils.getStrTimeYMD(list.get(position).getStarttime()));
//		holder.content.setText(list.get(position).getInfo());//目的地，写死了 地点西藏，注释打开即可
		holder.item_go_where.setText("出发地点："+ list.get(position).getAddress());

		if (TextUtils.equals(list.get(position).getIs_at(),"1")){//约伴
			holder.item_alltrip_ifyueban.setVisibility(View.VISIBLE);
		}else {
			holder.item_alltrip_ifyueban.setVisibility(View.INVISIBLE);
		}

		if (TextUtils.equals(list.get(position).getIs_carpool(),"1")){
			holder.item_alltrip_ifyuecar.setVisibility(View.VISIBLE);
		}else {
			holder.item_alltrip_ifyuecar.setVisibility(View.INVISIBLE);
		}


		if (TextUtils.equals(list.get(position).getState(), "1")) {// 正常
			holder.recover.setVisibility(View.GONE);
			holder.cancel.setVisibility(View.VISIBLE);
			holder.canceltype.setVisibility(View.GONE);
		} else if (TextUtils.equals(list.get(position).getState(), "0")) {// 取消
			holder.recover.setVisibility(View.VISIBLE);
			holder.cancel.setVisibility(View.GONE);
			holder.canceltype.setVisibility(View.VISIBLE);
		}
		holder.recover.setOnClickListener(this);
		holder.recover.setTag(position);
		holder.cancel.setOnClickListener(this);
		holder.cancel.setTag(position);
		holder.delect.setOnClickListener(this);
		holder.delect.setTag(position);

		holder.itemHorizontalScrollView.setOnClickListener(this);

		// 设置监听事件
		convertView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

				case MotionEvent.ACTION_UP:

					// 获得ViewHolder
					ViewHolder viewHolder = (ViewHolder) v.getTag();

					// 获得HorizontalScrollView滑动的水平方向值.
					int scrollX = viewHolder.itemHorizontalScrollView
							.getScrollX();

					// 获得操作区域的长度
					int actionLayoutWidth = viewHolder.actionLayout.getWidth();

					// 注意使用smoothScrollTo,这样效果看起来比较圆滑,不生硬
					// 如果水平方向的移动值<操作区域的长度的一半,就复原
					if (scrollX < actionLayoutWidth / 2 && scrollX > 0) {
						viewHolder.itemHorizontalScrollView
								.smoothScrollTo(0, 0);
					} else if (scrollX == 0) {// 没有移动距离实现单机操作
						onItemOnClick.OnItemClick(1, position);
					} else {// 否则的话显示操作区域
						viewHolder.itemHorizontalScrollView.smoothScrollTo(
								actionLayoutWidth, 0);
					}
					return true;

				}
				return false;
			}
		});

		// 这里防止删除一条item后,ListView处于操作状态,直接还原
		if (holder.itemHorizontalScrollView.getScrollX() != 0) {
			holder.itemHorizontalScrollView.scrollTo(0, 0);
		}

		return convertView;
	}

	final class ViewHolder {
		TextView title, time, content,canceltype, item_trip_dath,item_go_where, item_alltrip_ifyueban, item_alltrip_ifyuecar;
		ImageView face;
		View normalItemContentLayout;
		HorizontalScrollView itemHorizontalScrollView;
		View actionLayout;
		Button recover, cancel, delect;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		if (v.getId() == R.id.recover) {// 恢复
			onItemOnClick.OnItemClick(3, position);
		} else if (v.getId() == R.id.cancel) {// 取消
			onItemOnClick.OnItemClick(2, position);
		} else if (v.getId() == R.id.delet) {// 删除
			onItemOnClick.OnItemClick(4, position);
		}

	}

}
