package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.event.CalendarEvent;
import com.power.travel.xixuntravel.fragment.CalendarFragment;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import de.greenrobot.event.EventBus;

/**
 * 日历
 */
public class CalendarMainActivity extends FragmentActivity {

	private ViewPager viewPager;
	private TextView tvMonth;
	private String month;
	private String start,end;
	 public static Date startDate,endDate;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * 去除界面顶部灰色部分项目名字的方法 局限性：在每个界面分别添加才管用
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
		requestWindowFeature(0);// 上下两行代码去掉项目名字
		setContentView(R.layout.activity_calendarmain);
		start=getIntent().getStringExtra("startData");
		end=getIntent().getStringExtra("endData");
		tvMonth = (TextView) this.findViewById(R.id.tv_month);
		month = Calendar.getInstance().get(Calendar.YEAR)
				+ "-"
				+ Utils.LeftPad_Tow_Zero(Calendar.getInstance().get(
						Calendar.MONTH) + 1);
		tvMonth.setText(month);
		viewPager = (ViewPager) this.findViewById(R.id.viewpager);
		final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter();
		viewPager.setAdapter(screenSlidePagerAdapter);
		viewPager.setCurrentItem(500);
		
		try {
			EventBus.getDefault().register(this);
		} catch (Exception e) {
			Log.e("", "EventBus注册错误" + e.toString());
		}
	
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				Calendar calendar = Utils.getSelectCalendar(position);
				month = calendar.get(Calendar.YEAR)
						+ "-"
						+ Utils.LeftPad_Tow_Zero(calendar.get(Calendar.MONTH) + 1);
				tvMonth.setText(month);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}
	
	public void onEventMainThread(CalendarEvent event){
		switch (event.getType()) {
		case 1://
			onBackPressed();
			break;

		default:
			break;
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}


	public void onBackPressed() {
		 backSet();
		super.onBackPressed();
	}
	
	private void backSet(){
		Intent intent=getIntent();
		if(startDate!=null){
			intent.putExtra("startDate", ConverToString(startDate));
		}
		if(endDate!=null){
			intent.putExtra("endDate", ConverToString(endDate));
		}
		setResult(101, intent);
		finish();
		this.overridePendingTransition(R.anim.alpha_exit,R.anim.top_to_bottom);
	}


	 public static String ConverToString(Date date){
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	          
	        return df.format(date);  
	    }


	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// TODO Auto-generated method stub
			return CalendarFragment.create(position, CalendarMainActivity.this.start,end);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 1000;
		}
		
		public ScreenSlidePagerAdapter() {
			super(getSupportFragmentManager());
		}
	}


	public static Date getStartDate() {
		return startDate;
	}


	public static void setStartDate(Date startDate) {
		CalendarMainActivity.startDate = startDate;
	}


	public static Date getEndDate() {
		return endDate;
	}


	public static void setEndDate(Date endDate) {
			CalendarMainActivity.endDate = endDate;
	}
	
	
}
