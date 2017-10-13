package com.power.travel.xixuntravel.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewSwitcherHelper {
    private Drawable mPosOff;
    private Drawable mPosOn;
    private ViewGroup viewGroup;
    private Context mContext;
    private int currentPos;

    public ViewSwitcherHelper(Context context, ViewGroup layout, int off, int on) {
        mContext = context;
        viewGroup = layout;
//        mPosOff = mContext.getResources().getDrawable(R.drawable.dot_red);
//        mPosOn = mContext.getResources().getDrawable(R.drawable.dot_gray); 
          mPosOff= mContext.getResources().getDrawable(off);
          mPosOn=mContext.getResources().getDrawable(on);
    }

    public void setViewSwitcherTip(int count, int current) {
        this.currentPos = current;

        for (int i = 0; i < count; i++) {
            ImageView mImageView = getPositionView(i == current);
            mImageView.setTag(i);
            viewGroup.addView(mImageView);
        }
    }

    private ImageView getPositionView(boolean isOn) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setImageDrawable(isOn ? mPosOn : mPosOff);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(5, 0, 5, 0);
        mImageView.setLayoutParams(mLayoutParams);
        return mImageView;
    }

    public void setCurrent(int current) {
        if (current >= viewGroup.getChildCount()) {
            return;
        }
        viewGroup.removeViewAt(currentPos);
        viewGroup.addView(getPositionView(false), currentPos);
        this.currentPos = current;
        viewGroup.removeViewAt(current);
        viewGroup.addView(getPositionView(true), current);
    }
}
