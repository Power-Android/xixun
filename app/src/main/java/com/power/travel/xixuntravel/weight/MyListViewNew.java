package com.power.travel.xixuntravel.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/*
 * @author antasu
 * 
 * */

public class MyListViewNew extends ListView {
	public MyListViewNew(Context context) {
		super(context);
	}

	public MyListViewNew(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListViewNew(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}