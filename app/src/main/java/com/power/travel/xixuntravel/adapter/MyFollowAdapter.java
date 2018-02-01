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
import com.power.travel.xixuntravel.activity.MyFollowActivity;
import com.power.travel.xixuntravel.activity.UserCenterActivity;
import com.power.travel.xixuntravel.impl.MyFollowOnItemOnClickListener;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.PhoneModel;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 我的关注
 * 
 * @author fan
 * 
 */
public class MyFollowAdapter extends BaseAdapter implements OnClickListener,
        SectionIndexer {

	String TAG = "MyFollowAdapter";
	private LayoutInflater inflater, inflaterpop;
	Context context;
	private boolean ifCan;
	int mScreentWidth;
	List<MasterModel> adapterList = new ArrayList<MasterModel>();

	MyFollowOnItemOnClickListener OnItemListener;

	private List<PhoneModel> list = null;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类

	public MyFollowAdapter(Context context, List<PhoneModel> list,
						   int screenWidth, List<MasterModel> list1) {
		super();
		this.context = context;
		this.list = list;
		this.adapterList = list1;
		this.mScreentWidth = screenWidth;
		inflater = LayoutInflater.from(context);
		inflaterpop = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.imaleloadlogo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		// .cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.displayer(new RoundedBitmapDisplayer(100))
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<PhoneModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void OnItemListener(MyFollowOnItemOnClickListener OnItemListener) {
		this.OnItemListener = OnItemListener;
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

	public void setUpdate(boolean ifCan) {
		this.ifCan = ifCan;
		super.notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_myfollow_layout, null,
					false);
			holder.tvHead = (ImageView) convertView.findViewById(R.id.head_img);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
			holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			holder.item_ll = convertView.findViewById(R.id.item_ll);
			// 整个item的水平滚动层
			holder.itemHorizontalScrollView = (HorizontalScrollView) convertView
					.findViewById(R.id.hsv);
			holder.actionLayout = convertView.findViewById(R.id.ll_action);
			holder.shanchu = (Button) convertView.findViewById(R.id.button1);
			// 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
			holder.normalItemContentLayout = convertView
					.findViewById(R.id.ll_content);
			LayoutParams lp = holder.normalItemContentLayout.getLayoutParams();
			lp.width = mScreentWidth;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(list.get(position).getSortLetters());
		} else {
			holder.tvLetter.setVisibility(View.GONE);
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		/**
		 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
		 */
		imageLoader.displayImage(list.get(position).getImgSrc(), holder.tvHead,
				options, animateFirstListener);
		holder.tvTitle.setText(this.list.get(position).getName());

		holder.shanchu.setTag(position);
		holder.shanchu.setOnClickListener(this);
		holder.itemHorizontalScrollView.setOnClickListener(this);
		holder.item_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LogUtil.e(TAG,"hahahahaha"+position);
				Intent intent=new Intent(context,UserCenterActivity.class);
				intent.putExtra("guanzhu_mid",adapterList.get(position).getM_id());
				context.startActivity(intent);
			}
		});

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
						// OnItemListener.OnItemClick(2, position);
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
		ImageView tvHead;
		TextView tvLetter;
		TextView tvTitle;
		LinearLayout item_ll;
		View normalItemContentLayout;
		HorizontalScrollView itemHorizontalScrollView;
		View actionLayout;
		Button shanchu;
	}

	@Override
	public void onClick(View v) {
		int posi = (Integer) v.getTag();
		if (v.getId() == R.id.button1) {
			OnItemListener.OnItemClick(1, posi);
		}

	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
