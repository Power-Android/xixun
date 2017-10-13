package com.power.travel.xixuntravel.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/8/24.
 */

public class ClickImageView extends ImageView {
    public ClickImageView(Context context) {
        super(context);
    }

    public ClickImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClickImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.setColorFilter(0x33000000);
                /*重写触摸事件的方法，当图片被点击的时候*/
                mOnClickListener.onClick();
                return true;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                this.setColorFilter(null);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 定义点击接口
     */
    public interface OnClickListener{
        void onClick();
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }


}
