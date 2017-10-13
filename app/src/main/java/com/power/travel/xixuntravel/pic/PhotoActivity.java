package com.power.travel.xixuntravel.pic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.power.travel.xixuntravel.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片详情
 * @author fan
 *
 */
public class PhotoActivity extends Activity {

	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	public int max;

	RelativeLayout photo_relativeLayout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		photo_relativeLayout.setBackgroundColor(0x70000000);

		for (int i = 0; i < Bimp.bmp.size(); i++) {
			bmp.add(Bimp.bmp.get(i));
		}
		for (int i = 0; i < Bimp.drr.size(); i++) {
			drr.add(Bimp.drr.get(i));
		}
		max = Bimp.max;

		Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
		photo_bt_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				finish();
			}
		});
		Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
		photo_bt_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listViews.size() == 1) {
					Bimp.bmp.clear();
					Bimp.drr.clear();
					Bimp.max = 0;
					FileUtils.deleteDir();
					finish();
				} else {
					String newStr = drr.get(count).substring(
							drr.get(count).lastIndexOf("/") + 1,
							drr.get(count).lastIndexOf("."));
					bmp.remove(count);
					drr.remove(count);
					del.add(newStr);
					max--;
					pager.removeAllViews();
					listViews.remove(count);
					adapter.setListViews(listViews);
					adapter.notifyDataSetChanged();
				}
			}
		});
		Button photo_bt_enter = (Button) findViewById(R.id.photo_bt_enter);
		photo_bt_enter.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Bimp.bmp = bmp;
				Bimp.drr = drr;
				Bimp.max = max;
				for(int i=0;i<del.size();i++){				
					FileUtils.delFile(del.get(i)+".JPEG"); 
				}
				finish();
			}
		});

		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < bmp.size(); i++) {
			initListViews(bmp.get(i));//
		}

		adapter = new MyPageAdapter(listViews);// 閺嬪嫰锟絘dapter
		pager.setAdapter(adapter);// 鐠佸墽鐤嗛柅鍌炲帳閸ｏ拷
		Intent intent = getIntent();
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		ImageView img = new ImageView(this);// 閺嬪嫰锟絫extView鐎电锟�
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		listViews.add(img);// 濞ｈ濮瀡iew
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 妞ょ敻娼伴柅澶嬪閸濆秴绨查崙鑺ユ殶
			count = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 濠婃垵濮╂稉顓滐拷閵嗗倶锟�

		}

		public void onPageScrollStateChanged(int arg0) {// 濠婃垵濮╅悩鑸碉拷锟�锟斤拷锟�

		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 妞ゅ灚锟�

		public MyPageAdapter(ArrayList<View> listViews) {// 閺嬪嫰锟介崙鑺ユ殶
															// 閸掓繂顬婇崠鏉ewpager閻ㄥ嫭妞傞崐娆戠舶閻ㄥ嫪绔存稉顏堬拷?闂堬拷
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 閼奉亜绻侀崘娆戞畱娑擄拷閲滈弬瑙勭《閻劍娼靛ǎ璇插閺佺増锟�
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 鏉╂柨娲栭弫浼村櫤
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 闁匡拷鐦塿iew鐎电锟�
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 鏉╂柨娲杤iew鐎电锟�
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
