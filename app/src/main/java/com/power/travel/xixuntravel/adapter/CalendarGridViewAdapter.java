package com.power.travel.xixuntravel.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;

public class CalendarGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月
	Date startData, endData;
	// 根据改变的日期更新日历
	// 填充日历控件用
	private LayoutInflater inflater;

	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

		calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位

	}

	ArrayList<Date> titles;

	private ArrayList<Date> getDates() {

		UpdateStartDateForMonth();

		ArrayList<Date> alArrayList = new ArrayList<Date>();

		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		return alArrayList;
	}

	private Activity activity;
	Resources resources;

	public CalendarGridViewAdapter(Activity a, Calendar cal) {
		//, String startData,String endData
		calStartDate = cal;
		activity = a;
		inflater = LayoutInflater.from(activity);
		resources = activity.getResources();
		titles = getDates();

	}

	public void Updata(Date startData, Date endData) {
		this.startData = startData;
		this.endData = endData;
		notifyDataSetChanged();
	}

	public CalendarGridViewAdapter(Activity a) {
		activity = a;
		resources = activity.getResources();
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_month_layout, null,
					false);

			holder.month_month = (TextView) convertView
					.findViewById(R.id.month_month);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Date myDate = (Date) getItem(position);
		String time = StringUtils.ConverToString(titles.get(position));
		holder.month_month.setText(time);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);// 星期

		// 判断周六周日
		if (iDay == 7) {
			// 周六
			// iv.setBackgroundColor(resources.getColor(R.color.text_6));
		} else if (iDay == 1) {
			// 周日
			// iv.setBackgroundColor(resources.getColor(R.color.text_7));
		} else {

		}
		// 判断是否是当前月
		if (iMonth == iMonthViewCurrentMonth) {
			holder.month_month.setTextColor(resources.getColor(R.color.Text));
		} else {
			holder.month_month
					.setTextColor(resources.getColor(R.color.noMonth));
		}


		// 设置背景颜色结束
		if (startData != null && endData != null) {
			/*LogUtil.e("需要展示的数据", StringUtils.ConverToString(startData) + "\n"
					+ StringUtils.ConverToString(endData));*/
			if (startData.getTime() <= myDate.getTime()
					&& myDate.getTime() <= endData.getTime()) {
				LogUtil.e("开始时间==结束时间", StringUtils.ConverToString(myDate));
				holder.month_month.setBackgroundResource(R.drawable.circle_red);
				holder.month_month.setTextColor(resources
						.getColor(R.color.white));
			}
			if (TextUtils.equals(StringUtils.ConverToString2(myDate),
					StringUtils.ConverToString2(endData))) {
				
				holder.month_month.setBackgroundResource(R.drawable.circle_red);
				holder.month_month.setTextColor(resources
						.getColor(R.color.white));
			}
		}else if (startData != null && startData.getTime() == myDate.getTime()) {
			holder.month_month.setBackgroundResource(R.drawable.circle_red);
			holder.month_month.setTextColor(resources.getColor(R.color.white));
		} else if (endData != null && endData.getTime() == myDate.getTime()) {
			holder.month_month.setBackgroundResource(R.drawable.circle_red);
			holder.month_month.setTextColor(resources.getColor(R.color.white));
		} else {
			holder.month_month.setBackgroundResource(R.drawable.circle_white);
		}

		return convertView;
	}

	@SuppressWarnings("deprecation")
	private Boolean equalsDate(Date date1, Date date2) {
		if (date1.getYear() == date2.getYear()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}
	}

	final class ViewHolder {
		TextView month_month;
	}

}
