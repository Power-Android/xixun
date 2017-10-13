package com.power.travel.xixuntravel.selectpic;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.pic.Bimp;
import com.power.travel.xixuntravel.utils.CameraUtil;
import com.power.travel.xixuntravel.utils.SaveBitmapUtil;

import java.util.LinkedList;
import java.util.List;


public class MyAdapter extends CommonAdapter<String> {


	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();
	Context context;
	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
                     String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.context = context;
		this.mDirPath = dirPath;
		mSelectedImage= Bimp.mSelectedImage;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select,
				R.drawable.picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {
				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item)) {
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
					Bimp.drr.remove(CameraUtil.getAlbumDir() + "/" + item);
				} else{// 未选择该图片
					if(Bimp.drr.size()<9){
						mSelectedImage.add(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
						Bimp.drr.add(SaveBitmapUtil.SaveBitmap(context, mDirPath + "/" + item, item));
//						Bimp.drr.add(mDirPath + "/" + item);
					}else{
					}
				}
//				LogUtil.e("选择的图片数量", String.valueOf(Bimp.drr.size()));
//				LogUtil.e("选择的图片的名字", mDirPath+"\n" + "/" + item);
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}

}
