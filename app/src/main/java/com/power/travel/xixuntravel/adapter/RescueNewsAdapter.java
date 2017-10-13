package com.power.travel.xixuntravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.RescueNewsModel;

import java.util.ArrayList;
import java.util.List;


/**
 * 救援知识
 * @author fan
 *
 */
public class RescueNewsAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
//	int maxDescripLine = 0;
	
	List<RescueNewsModel> list=new ArrayList<RescueNewsModel>();
	
	public RescueNewsAdapter(Context context, List<RescueNewsModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
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
	
	public void setUpdate(int mPosition){
		this.mPosition=mPosition;
		super.notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_rescuenews_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.title);
			holder.title_layout = (RelativeLayout) convertView
					.findViewById(R.id.title_layout);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_rescuenews_content);
			holder.right = (ImageView) convertView
					.findViewById(R.id.item_rescuenews_right);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title_layout.setOnClickListener(this);
		holder.title_layout.setTag(position);
		
		holder.title.setText(list.get(position).getTitle());
		holder.content.setText(list.get(position).getDescription());
		/*if(mPosition==position){
			holder.content.setVisibility(View.VISIBLE);
			holder.right.setImageDrawable(context.getResources().getDrawable(R.drawable.down));
		}else{
			holder.content.setVisibility(View.GONE);
			holder.right.setImageDrawable(context.getResources().getDrawable(R.drawable.right));
		}*/
//		holder.content.setHeight(holder.content.getLineHeight() * maxDescripLine);
		holder.title_layout.setTag(holder);
		holder.title_layout.setOnClickListener(new OnClickListener() {
			boolean isExpand;//是否已展开的状态
			@Override
			public void onClick(View v) {
				final ViewHolder viewHolder = (ViewHolder) v.getTag();
				isExpand = !isExpand;
				viewHolder.content.clearAnimation();//清楚动画效果
				final int deltaValue;//默认高度，即前边由maxLine确定的高度
				final int startValue = viewHolder.content.getHeight();//起始高度
				int durationMillis = 350;//动画持续时间
				if (isExpand) {
					/**
					 * 折叠动画
					 * 从实际高度缩回起始高度
					 */
//					deltaValue = viewHolder.content.getLineHeight() * viewHolder.content.getLineCount() - startValue;
					RotateAnimation animation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					animation.setDuration(durationMillis);
					animation.setFillAfter(true);
					viewHolder.right.startAnimation(animation);
					viewHolder.content.setVisibility(View.VISIBLE);
				} else {
					/**
					 * 展开动画
					 * 从起始高度增长至实际高度
					 */
//					deltaValue = viewHolder.content.getLineHeight() * maxDescripLine - startValue;
					RotateAnimation animation = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					animation.setDuration(durationMillis);
					animation.setFillAfter(true);
					viewHolder.right.startAnimation(animation);
					viewHolder.content.setVisibility(View.GONE);
				}
				/*Animation animation = new Animation() {
					protected void applyTransformation(float interpolatedTime, Transformation t) { //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
						viewHolder.content.setHeight((int) (startValue + deltaValue * interpolatedTime));
					}
				};
				animation.setDuration(durationMillis);
				viewHolder.content.startAnimation(animation);*/
			}
		});
		return convertView;
	}

	final class ViewHolder {
		TextView title,content;
		RelativeLayout title_layout;
		ImageView right;
	}

	@Override
	public void onClick(View v) {
		int posi=(Integer)v.getTag();
		if(v.getId()==R.id.title_layout){
			mPosition=posi;
			notifyDataSetChanged();
		}
	}


}
