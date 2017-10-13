package com.power.travel.xixuntravel.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.power.travel.xixuntravel.R;

/**
 * ������  ��ʾ�����ֻ��ߴ����,,
 * @author fan
 *
 */
public class CollapsibleTextView extends LinearLayout implements
        OnClickListener {
	/** default text show max lines */
	private static final int DEFAULT_MAX_LINE_COUNT = 3;

	private static final int COLLAPSIBLE_STATE_NONE = 0;
	/** View����չ��״̬ **/  
	private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;  
	/** view����ʱ״̬ **/  
	private static final int COLLAPSIBLE_STATE_SPREAD = 2;

	private TextView desc;
	private TextView descOp;

	private String shrinkup;
	private String spread;
	/** ��ǰ���ڵ�״̬ */
	private int mState;
	private boolean flag;
	Context mContext;

	public CollapsibleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	@SuppressLint("NewApi")
	public CollapsibleTextView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
//		this.mContext = context;
//		initView();
	}

	public CollapsibleTextView(Context context) {
		this(context, null);
//		this.mContext = context;
//		initView();
	}

	void initView() {
		shrinkup = mContext.getString(R.string.desc_shrinkup);
		spread = mContext.getString(R.string.desc_spread);
		View view = inflate(mContext, R.layout.collapsible_textview, this);
		view.setPadding(0, -1, 0, 0);
		desc = (TextView) view.findViewById(R.id.desc_tv);
		descOp = (TextView) view.findViewById(R.id.desc_op_tv);
		descOp.setOnClickListener(this);
	}

	public final void setDesc(CharSequence charSequence, BufferType bufferType) {
//		byte[] bt=Base64.decode(String.valueOf(charSequence), Base64.DEFAULT);
//		try {
//			String str1=new String(bt,"utf-8");
//			desc.setText(str1, bufferType);
//			mState = COLLAPSIBLE_STATE_SPREAD;
//			requestLayout();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		desc.setText(charSequence, bufferType);
		mState = COLLAPSIBLE_STATE_SPREAD;
		requestLayout();
	}

	@Override
	public void onClick(View v) {
		flag = false;
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!flag) {
			flag = true;
			if (desc.getLineCount() <= DEFAULT_MAX_LINE_COUNT) {
				mState = COLLAPSIBLE_STATE_NONE;
				descOp.setVisibility(View.GONE);
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT + 1);
			} else {
				post(new InnerRunnable());
			}
		}
	}

	class InnerRunnable implements Runnable {
		@Override
		public void run() {
			if (mState == COLLAPSIBLE_STATE_SPREAD) {
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(spread);
				mState = COLLAPSIBLE_STATE_SHRINKUP;
			} else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
				desc.setMaxLines(Integer.MAX_VALUE);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(shrinkup);
				mState = COLLAPSIBLE_STATE_SPREAD;
			}
		}
	}

}
