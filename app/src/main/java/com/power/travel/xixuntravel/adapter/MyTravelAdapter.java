package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.ImageVoideActivity;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.weight.MyGridView;

/**
 * 我的游记
 * 
 * @author fan
 * 
 */
public class MyTravelAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;

	List<MyTravelModel> list = new ArrayList<MyTravelModel>();

	public MyTravelAdapter(Context context, List<MyTravelModel> list) {
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

	public void setUpdate(int mPosition) {
		this.mPosition = mPosition;
		super.notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {

			convertView = inflater.inflate(R.layout.item_mytravel_layout, null,
					false);
			holder = new ViewHolder();
			holder.day = (TextView) convertView
					.findViewById(R.id.item_mytravel_day);
			holder.month = (TextView) convertView
					.findViewById(R.id.item_mytravel_month);
			holder.gridview = (MyGridView) convertView
					.findViewById(R.id.image_gridView);
			holder.tuwen_text = (TextView) convertView
					.findViewById(R.id.image_text_name);
			holder.imgnum = (TextView) convertView
					.findViewById(R.id.image_text_num);
			holder.Videopic = (ImageView) convertView
					.findViewById(R.id.item_mytravel_pic);
			holder.videoplay = (ImageView) convertView
					.findViewById(R.id.item_mytravel_videoplay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MyTravelModel model = list.get(position);

//		if (TextUtils.isEmpty(holder.month.getText().toString())) {
			if (!TextUtils.isEmpty(list.get(position).getImg())) {// 图片不为空
					if (TextUtils.isEmpty(holder.imgnum.getText().toString())) {
						holder.gridview.setVisibility(View.VISIBLE);
						holder.imgnum.setVisibility(View.VISIBLE);
						holder.Videopic.setVisibility(View.GONE);
						holder.videoplay.setVisibility(View.GONE);
						holder.gridview.setTag(list.get(position).getImg());
						if(holder.gridview.getTag()!=null&&holder.gridview.getTag().equals(list.get(position).getImg())){
							String[] listpic = list.get(position).getImg().split(",");
							if (listpic.length > 1) {
								holder.gridview.setNumColumns(2);
							} else {
								holder.gridview.setNumColumns(1);
							}
							holder.gridview.setAdapter(new MyTravelGridAdapter(model,
									listpic, context));
							holder.imgnum.setText("共" + listpic.length + "张");
							LogUtil.e("加载数据", "图片数据。。。。。。。。");
						}
					}
			} else if (!TextUtils.isEmpty(list.get(position).getVideo())) {// 视频信息
				holder.gridview.setVisibility(View.GONE);
				holder.imgnum.setVisibility(View.GONE);
				holder.Videopic.setVisibility(View.VISIBLE);
				holder.videoplay.setVisibility(View.VISIBLE);
				holder.Videopic.setTag(list.get(position).getVideo());
				if(holder.Videopic.getTag()!=null&&holder.Videopic.getTag().equals(list.get(position).getVideo())){
				AsyncTaskImageLoad async = new AsyncTaskImageLoad(
						holder.Videopic);
				async.execute(list.get(position).getVideo());
				holder.videoplay.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.play_small));
				 LogUtil.e("加载数据", "视频数据。。。。。。。。");
				}
			}
//		}

		holder.day.setText(StringUtils.getStrTimeDay(list.get(position)
				.getAddtime()));
		holder.month.setText(StringUtils.getStrTimeMonth(list.get(position)
				.getAddtime()));
		
		// holder.tuwen_text.setDesc(list.get(position).getContent(),
		// BufferType.NORMAL);
		holder.tuwen_text.setText(list.get(position).getContent());

		holder.gridview.setTag(position);
		holder.Videopic.setTag(position);
		holder.Videopic.setOnClickListener(this);

		return convertView;
	}

	final class ViewHolder {
		TextView day, month, imgnum;
		MyGridView gridview;
		// CollapsibleTextView tuwen_text;
		TextView tuwen_text;
		ImageView Videopic, videoplay;
	}

	@Override
	public void onClick(View v) {
		int posi = (Integer) v.getTag();
		if (v.getId() == R.id.item_mytravel_pic) {
			MyTravelModel model = list.get(posi);
			ArrayList<String> mPic_list = null;
			Intent mIntent = ImageVoideActivity.newIntent(context, model,
					mPic_list, 0);
			context.startActivity(mIntent);
		}
	}

	public class AsyncTaskImageLoad extends AsyncTask<String, Integer, Bitmap> {

		private ImageView Image = null;

		public AsyncTaskImageLoad(ImageView img) {
			Image = img;
		}

		// 运行在子线程中
		protected Bitmap doInBackground(String... params) {
			try {

				Bitmap map = StringUtils.createVideoThumbnail(params[0], 60, 60);
				return map;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			if (Image != null && result != null) {
				Image.setImageBitmap(result);
			}

			super.onPostExecute(result);
		}
	}

}
