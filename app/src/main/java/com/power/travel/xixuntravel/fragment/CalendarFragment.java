/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.power.travel.xixuntravel.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.CalendarMainActivity;
import com.power.travel.xixuntravel.adapter.CalendarGridViewAdapter;
import com.power.travel.xixuntravel.event.CalendarEvent;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.Utils;

public class CalendarFragment extends Fragment {
	public static final String ARG_PAGE = "page",ARG_TIME="time";

	private int mPageNumber;

	private Calendar mCalendar;

	private CalendarGridViewAdapter calendarGridViewAdapter;
	
	private TextView tvMonth;
	private String month,Start,End;
	public static Fragment create(int pageNumber,String startData,String endData) {
		CalendarFragment fragment = new CalendarFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		args.putString("startData", startData);
		args.putString("endData", endData);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		Start=getArguments().getString("startData");
		End=getArguments().getString("endData");
		mCalendar = Utils.getSelectCalendar(mPageNumber);
		month=String.valueOf(mCalendar.get(Calendar.YEAR))+"-"+String.valueOf(mCalendar.get(Calendar.MONTH) + 1);
		calendarGridViewAdapter = new CalendarGridViewAdapter(getActivity(),
				mCalendar);
		Date startData=null, endData=null;
		
		if (!TextUtils.isEmpty(Start)) {
			try {
				startData = StringUtils.ConverToDate(Start);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.e("收到的数据错误开始", e.toString());
			}
		}
		if (!TextUtils.isEmpty(End)) {
			try {
				endData = StringUtils.ConverToDate(End);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.e("收到的数据错误结束", e.toString());
			}
		}
		calendarGridViewAdapter.Updata(startData, endData);
//		LogUtil.e("收到的数据", Start+"====="+End);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.calendar_view, container, false);
		GridView titleGridView = (GridView) rootView
				.findViewById(R.id.gridview);
		titleGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 取消GridView自带item单机效果
		TitleGridAdapter titleAdapter = new TitleGridAdapter(getActivity());
		initGridView(titleGridView, titleAdapter);
		GridView calendarView = (GridView) rootView
				.findViewById(R.id.calendarView);
		initGridView(calendarView, calendarGridViewAdapter);
		tvMonth = (TextView) rootView.findViewById(R.id.tv_month_2);
		tvMonth.setText(month);
		calendarView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 取消GridView自带item单机效果
		calendarView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < parent.getCount(); i++) {
					if ((i % 7) == 6) {

					} else if ((i % 7) == 0) {

					} else {
						parent.getChildAt(i).setBackgroundColor(
								Color.TRANSPARENT);
					}
				}
				Date start=null,end = null;
				start=CalendarMainActivity.getStartDate();
				end= CalendarMainActivity.getEndDate();
				Date nowDate = StringUtils.getNowDate();
				if(start==null){//都为空
					start = (Date)calendarGridViewAdapter.getItem(position);
					if (start.getTime() < nowDate.getTime()){
						Toast.makeText(getActivity().getApplicationContext(),"请选择当前日期之后的时间",Toast.LENGTH_SHORT).show();
					}else {
						CalendarMainActivity.setStartDate(start);
						calendarGridViewAdapter.Updata(start, end);
					}
				}else if(start!=null&&end==null){//开始有 结束为空
					end = (Date)calendarGridViewAdapter.getItem(position);
					if (end.getTime() < nowDate.getTime() && end.getTime() < start.getTime()){
						Toast.makeText(getActivity().getApplicationContext(),"请选择当前日期之后的时间",Toast.LENGTH_SHORT).show();
					}else {
						CalendarMainActivity.setEndDate(end);
						calendarGridViewAdapter.Updata(start, end);
						EventBus.getDefault().post(new CalendarEvent(CalendarEvent.REFRESH));
					}
				}else if(start!=null&&end!=null){//都不为空
					start = (Date)calendarGridViewAdapter.getItem(position);
					if (start.getTime() < nowDate.getTime()){
						Toast.makeText(getActivity().getApplicationContext(),"请选择当前日期之后的时间",Toast.LENGTH_SHORT).show();
					}else {
						CalendarMainActivity.setStartDate(start);
						end=null;
						if (end.getTime() < nowDate.getTime() && end.getTime() < start.getTime()){
							Toast.makeText(getActivity().getApplicationContext(),"请选择当前日期之后的时间",Toast.LENGTH_SHORT).show();
						}else {
							CalendarMainActivity.setEndDate(end);
							calendarGridViewAdapter.Updata(CalendarMainActivity.startDate, CalendarMainActivity.endDate);
						}
					}
				}
			}
		});
		return rootView;
	}
	
	 public static String ConverToString(Date date){
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	          
	        return df.format(date);  
	    }  

	private void initGridView(GridView gridView, BaseAdapter adapter) {
		gridView = setGirdView(gridView);
		gridView.setAdapter(adapter);// 设置菜单Adapter
	}

	@SuppressWarnings("deprecation")
	private GridView setGirdView(GridView gridView) {
		gridView.setNumColumns(7);// 设置每行列数
		gridView.setGravity(Gravity.CENTER_VERTICAL);// 位置居中
		gridView.setVerticalSpacing(1);// 垂直间隔
		gridView.setHorizontalSpacing(1);// 水平间隔
		gridView.setBackgroundColor(getResources().getColor(
				R.color.calendar_background));

		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		gridView.setPadding(x, 0, 0, 0);// 居中

		return gridView;
	}

	public class TitleGridAdapter extends BaseAdapter {

		int[] titles = new int[] { R.string.Sun, R.string.Mon, R.string.Tue,
				R.string.Wed, R.string.Thu, R.string.Fri, R.string.Sat };

		private Activity activity;

		// construct
		public TitleGridAdapter(Activity a) {
			activity = a;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return titles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout iv = new LinearLayout(activity);
			TextView txtDay = new TextView(activity);
			txtDay.setFocusable(false);
			txtDay.setBackgroundColor(Color.TRANSPARENT);
			iv.setOrientation(LinearLayout.VERTICAL);

			txtDay.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			int i = (Integer) getItem(position);
			txtDay.setTextColor(Color.GRAY);
			Resources res = getResources();

			if (i == R.string.Sat) {

			} else if (i == R.string.Sun) {

			} else {
			}
			txtDay.setText((Integer) getItem(position));
			iv.addView(txtDay, lp);
			return iv;
		}
	}

}
