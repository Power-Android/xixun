package com.power.travel.xixuntravel.adapter;

import java.util.ArrayList;
import java.util.List;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 搜索 游记的布局
 * @author fan
 *
 */
public class Search_TravelAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// �Լ�д��һ����
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<AllTravelModel> list=new ArrayList<AllTravelModel>();
	
	public Search_TravelAdapter(Context context, List<AllTravelModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
//		.showStubImage(R.drawable.imaleloadlogo)// ����ͼƬ�������ڼ���ʾ��ͼƬ
//		.showImageForEmptyUri(R.drawable.imaleloadlogo)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
//		.showImageOnFail(R.drawable.imaleloadlogo)// ����ͼƬ����/�������д���ʱ����ʾ��ͼƬ
//		.cacheInMemory(true)// �Ƿ񾏴涼�ȴ���
		.cacheOnDisc(true)// �Ƿ񾏴浽sd����
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//����ͼƬ����εı��뷽ʽ��ʾ
		.bitmapConfig(Bitmap.Config.RGB_565)//����ͼƬ�Ľ�������//
		 .displayer(new RoundedBitmapDisplayer(15))//�����û�����ͼƬtask(������Բ��ͼƬ��ʾ)
		.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
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
			convertView = inflater.inflate(R.layout.item_searchtravel_layout, null, false);
		
			holder.description = (TextView) convertView
					.findViewById(R.id.item_viewsport_description);
			holder.title = (TextView) convertView
					.findViewById(R.id.item_viewsport_title);
			
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_viewsport_face);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(list.get(position).getImg())) {//ͼƬ��Ϊ��
			imageLoader.displayImage(list.get(position).getImg().split(",")[0],
					holder.face, options,animateFirstListener);
		}
		
		
//		holder.description.setText(list.get(position).getDescription());
		holder.title.setText(list.get(position).getContent());
		
		return convertView;
	}

	final class ViewHolder {
		TextView description,title;
		ImageView face;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
