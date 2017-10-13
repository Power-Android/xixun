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
import com.power.travel.xixuntravel.model.RouteModel;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 推荐路线
 * @author fan
 *
 */
public class RouteListAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	Context context;
	int mPosition;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// �Լ�д��һ����
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<RouteModel> list=new ArrayList<RouteModel>();
	private DisplayMetrics dm;
	private RelativeLayout.LayoutParams imagebtn_params;
	
	public RouteListAdapter(Context context, List<RouteModel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// ����ͼƬ�������ڼ���ʾ��ͼƬ
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
		.showImageOnFail(R.drawable.imaleloadlogo)// ����ͼƬ����/�������д���ʱ����ʾ��ͼƬ
//		.cacheInMemory(true)// �Ƿ񾏴涼�ȴ���
		.cacheOnDisc(true)// �Ƿ񾏴浽sd����
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//����ͼƬ����εı��뷽ʽ��ʾ
		.bitmapConfig(Bitmap.Config.RGB_565)//����ͼƬ�Ľ�������//
		 .displayer(new RoundedBitmapDisplayer(20))//�����û�����ͼƬtask(������Բ��ͼƬ��ʾ)
		.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		dm = context.getResources().getDisplayMetrics();
		 imagebtn_params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params.height = dm.widthPixels / 2;
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
			convertView = inflater.inflate(R.layout.item_rout_layout, null, false);
		
			holder.title = (TextView) convertView
					.findViewById(R.id.item_route_title);
			holder.face = (ImageView) convertView
					.findViewById(R.id.item_route_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.face.setLayoutParams(imagebtn_params);
		//http://xizanglvyou.ip115.enet360.com//Uploads//20160804//20160804175334heading.jpg
		imageLoader.displayImage(HttpUrl.Url+list.get(position).getThumb(),
				holder.face, options,animateFirstListener);
		
		holder.title.setText(list.get(position).getRoute());


		return convertView;
	}

	final class ViewHolder {
		TextView title;
		ImageView face;
	}

	@Override
	public void onClick(View v) {
		
		
	}


}
