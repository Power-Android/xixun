package com.power.travel.xixuntravel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.views.PhotoView;
import com.power.travel.xixuntravel.views.ViewSwitcherHelper;
import com.power.travel.xixuntravel.weight.HackyViewPager;

import java.util.ArrayList;

/***
 * 
 * 滑动展示用户选择图片 viewpager
 * 
 * newintent里接入，图片List数据，setadapter，做展示，
 * 
 */
public class ImageViewPager_2Activity extends Activity implements OnClickListener {

	private ArrayList<String> mList_pic = new ArrayList<String>();

	HackyViewPager mViewPager;
	// ViewPagerAdapter mViewPagerAdapter;
	// ViewPagerFragmentAdapter mViewAdapter;
	int mSeleteItem;
	ArrayList<View> views = new ArrayList<View>();
	LinearLayout mHeadDotLayout;
	ViewSwitcherHelper mViewSwitchHelper;
	private ImageView back;
	private TextView title;
//	MyTravelModel travelModel;
	 
 	// ArrayList<Fragment> listFragment = new ArrayList<Fragment>();

	public static Intent newIntent(Context mContext, ArrayList<String> pic_List, int position) {
		Intent i = new Intent(mContext, ImageViewPager_2Activity.class);
		i.putStringArrayListExtra("pic_list", pic_List);
		i.putExtra("position", position);
//		i.putExtra("model", model);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewpager_2_layout);
		MyApplication.getInstance().addActivity(this);
		getData();// 接收参数
		initView();// 初始化控件
 	}



	private void getData() {
		mList_pic = getIntent().getStringArrayListExtra("pic_list");
		mSeleteItem = getIntent().getIntExtra("position", 0);
//		travelModel=(MyTravelModel)getIntent().getExtras().getSerializable("model");
	}

	public void photoviewonlistener(View view) {
//		Log.e("", "0000000-0000000----000000");
		ImageViewPager_2Activity.this.finish();
		// imageView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// LogUtil.e("", "111111111111111-11111111111----111111111111");
		// ImageViewPagerActivity.this.finish();
		// return false;
		// }
		// });
		// imageView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// LogUtil.e("", "0000000-0000000----000000");
		// ImageViewPagerActivity.this.finish();
		// }
		// });
	}

	private void initView() {
		mHeadDotLayout = (LinearLayout) findViewById(R.id.home_head_position);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		back.setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.title);
		title.setText("");

		int on = R.drawable.dot_bai;
		int off = R.drawable.dot_gray;

		mViewSwitchHelper = new ViewSwitcherHelper(this, mHeadDotLayout, off,
				on);
		mViewPager = (HackyViewPager) findViewById(R.id.viewpager);

//		Log.e("mlist_pic-viewpager---", mList_pic.size() + "");
		for (int i = 0; i < mList_pic.size(); i++) {
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.phone_view_layout, null);
			PhotoView imageView = (PhotoView) view.findViewById(R.id.photo_view_image);// photociew在此用到了
			// ImageView imageView = (ImageView) view
			// .findViewById(R.id.image_view_image);
			String image_url = mList_pic.get(i);
			ImageLoader.getInstance().displayImage(image_url, imageView);

			imageView.setTag(i);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					Log.e("", "0000000-0000000----000000");
					ImageViewPager_2Activity.this.finish();
				}
			});

			views.add(view);
		}

		// mViewPagerAdapter = new ViewPagerAdapter(listViews);
		// mViewPagerAdapter = new ViewPagerAdapter(mList_pic);
		// mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setAdapter(new ViewPageAdpter());
		mViewPager.setCurrentItem(mSeleteItem);
		mViewSwitchHelper.setViewSwitcherTip(mList_pic.size(), mSeleteItem);

		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int i, float v, int i2) {

					}

					@Override
					public void onPageSelected(int i) {
						mViewSwitchHelper.setCurrent(i);
						mViewPager.getCurrentItem();
						mSeleteItem = i;
					}

					@Override
					public void onPageScrollStateChanged(int i) {
					}
				});

	}

	@Override
	public void onClick(View v) {
		if(v==back){
			finish();
		}
//		switch (v.getId()) {
		// case R.id.button_ok:// 刪除按钮监听
		// showDeleteDialog();
		// break;
		// case R.id.button_back:// back
		// setBack();
		// break;
//		default:
//			break;
//		}
	}

	/**
	 * 返回键的监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// setBack();
			finish();
		}
		return false;
	}

	// class ViewPagerAdapter extends PagerAdapter {
	// List<String> list;
	//
	// public ViewPagerAdapter(List<String> list) {
	// this.list = list;
	// }
	//
	// @Override
	// public int getCount() {
	//
	// if (list != null && list.size() > 0) {
	// return list.size();
	// } else {
	// return 0;
	// }
	// }
	//
	// @Override
	// public boolean isViewFromObject(View arg0, Object arg1) {
	// return arg0 == arg1;
	// }
	//
	// @Override
	// public void destroyItem(ViewGroup container, int position, Object object)
	// {
	// container.removeView((View) object);
	// }
	//
	// // @Override
	// // public Object instantiateItem(ViewGroup container, int position) {
	// // View imageLayout = getLayoutInflater().inflate(
	// // R.layout.phone_view_layout, container, false);
	// // PhotoView imageView = (PhotoView) imageLayout
	// // .findViewById(R.id.photo_view_image);// photociew在此用到了
	// //
	// // imageView.setId(position);
	// // String image_url = list.get(position);
	// // ImageLoader.getInstance().displayImage(image_url, imageView);
	// //
	// // LogUtil.e("", "11111111111-------------111111111111111111");
	// // imageView.setTag(position);
	// // imageView.setOnClickListener(new OnClickListener() {
	// //
	// // @Override
	// // public void onClick(View v) {
	// // LogUtil.e("", "0000000-0000000----000000");
	// // ImageViewPagerActivity.this.finish();
	// // }
	// // });
	// // ((ViewPager) container).addView(imageLayout, 0);
	// //
	// // return imageLayout;
	// // }
	//
	// @Override
	// public int getItemPosition(Object object) {
	// return POSITION_NONE;
	// }
	// }

	class ViewPageAdpter extends PagerAdapter // 装载view的适配器
	{
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) { // 销毁view
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public int getCount() { // view数量
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) { // 返回相应的view
			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
