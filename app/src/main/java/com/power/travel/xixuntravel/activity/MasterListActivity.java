package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * 地主达人
 * 
 * @author fan
 * 
 */
public class MasterListActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView title;
	private String data, TAG = "MasterListActivity", info, mobile;
	private ProgressDialogUtils pd;
	private PullToRefreshListView mListView;
	List<MasterModel> adapterList = new ArrayList<MasterModel>();
	List<MasterModel> adapterListMore = new ArrayList<MasterModel>();
	MasterAdapter adapter;
	int page = 1;
	SharedPreferences sp,spLocation;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new MasterAdapter(MasterListActivity.this, adapterList);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page=page+1;
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
//				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && MasterListActivity.this != null) {
				pd.dismiss();
			}
			if (MasterListActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_master);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		if(isConnect()){
			getData(true);
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				MasterModel mMasterModel=adapterList.get(position-1);
				Intent intent=new Intent(MasterListActivity.this,UserCenterActivity.class);
				intent.putExtra("model", mMasterModel);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
//		getData(true);
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		spLocation= this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("地主达人");
		mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
		
	}

	private void initListener() {
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(isConnect()){
			if(!adapterList.isEmpty()){
				adapterList.clear();
				adapter.notifyDataSetChanged();
			}
			page=1;
			getData(false);
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(isConnect()){
			getData(false);
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
	}

	private void getData(boolean ifshow) {
		if (ifshow) {
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("page", page);
					data.put("mid",sp.getString(XZContranst.id, null));
					data.put("coordinate_x", spLocation.getString(XZContranst.coordinate_x, ""));
					data.put("coordinate_y", spLocation.getString(XZContranst.coordinate_y, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.master;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "地主达人提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "地主达人返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if(TextUtils.equals(status, "1")){
						JSONArray arry = jsonj.getJSONArray("data");
						adapterListMore = JSON.parseArray(arry.toString(),
								MasterModel.class);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}

	public class MasterAdapter extends BaseAdapter implements View.OnClickListener {

		private LayoutInflater inflater;
		Context context;
		int mPosition;
		DisplayImageOptions options;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// ???��???????
		ImageLoader imageLoader = ImageLoader.getInstance();
		List<MasterModel> list=new ArrayList<MasterModel>();
		SharedPreferences sp;
		ViewHolder holder = null;

		private String info;


		public MasterAdapter(Context context, List<MasterModel> list) {
			super();
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder()
					.cacheOnDisc(true)// ???��sd????
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//??????????��????????
					.bitmapConfig(Bitmap.Config.RGB_565)//???????????????//
					.displayer(new RoundedBitmapDisplayer(20))//?????????????task(??????????????)
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


			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_master_layout, null, false);

				holder.nickname = (TextView) convertView
						.findViewById(R.id.item_master_nickname);
				holder.title = (TextView) convertView
						.findViewById(R.id.item_master_title);
				holder.address = (TextView) convertView
						.findViewById(R.id.item_master_address);
				holder.distance = (TextView) convertView
						.findViewById(R.id.item_master_distance);
				holder.sex = (ImageView) convertView
						.findViewById(R.id.item_master_sex);
				holder.face = (ImageView) convertView
						.findViewById(R.id.item_master_face);
				holder.item_master_chat = (ImageView) convertView
						.findViewById(R.id.item_master_chat);
				holder.item_master_attentin = (ImageView) convertView.findViewById(R.id.item_master_attentin);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			//list.get(position).getFace()
			imageLoader.displayImage(list.get(position).getFace(),
					holder.face, options,animateFirstListener);

			holder.nickname.setText(list.get(position).getNickname());
			holder.title.setText(list.get(position).getSignature());
			holder.address.setText(list.get(position).getAddress());
			holder.distance.setText(list.get(position).getApart());

			if (TextUtils.equals(list.get(position).getIs_follow(),"1")){
				holder.item_master_attentin.setImageDrawable(context.getResources().getDrawable(R.drawable.praise_red));
			}else {
				holder.item_master_attentin.setImageDrawable(context.getResources().getDrawable(R.drawable.praise_black));
			}
			holder.item_master_attentin.setOnClickListener(this);
			holder.item_master_attentin.setTag(position);

			if(TextUtils.equals(list.get(position).getSex(), "1")){
				holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_boy));
			}else if(TextUtils.equals(list.get(position).getSex(), "2")){
				holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_girle));
			}
			holder.item_master_chat.setOnClickListener(this);
			holder.item_master_chat.setTag(position);
			return convertView;
		}

		final class ViewHolder {
			TextView nickname,title,address,distance;
			ImageView sex,face,item_master_chat,item_master_attentin;
		}

		@Override
		public void onClick(View v) {
			int posi=(Integer)v.getTag();
			if(v.getId()==R.id.item_master_chat){
				SharedPreferences sp;
				sp = context.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
				if (sp.getBoolean(XZContranst.if_login, false)) {

					RongIM.getInstance().startPrivateChat(context,list.get(posi).getId() , list.get(posi).getNickname());

				} else {
					context.startActivity(new Intent(context, LoginActivity.class));
				}
			}
			if (v.getId() == R.id.item_master_attentin){
				SharedPreferences sp;
				sp = context.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
				if (sp.getBoolean(XZContranst.if_login, false)) {
					if (TextUtils.equals(list.get(posi).getIs_follow(),"0")){
						praise(list.get(posi).getMid());

					}
					if (TextUtils.equals(list.get(posi).getIs_follow(),"1")){
						praise_cancel(list.get(posi).getFollow_id());
					}
				}else {
					context.startActivity(new Intent(context, LoginActivity.class));
				}

			}
		}

		private void praise_cancel(final String id) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					JSONObject data = new JSONObject();
					try {
						data.put("id",id);

					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					String url = HttpUrl.follow_cancel;
					String json = StringUtils.setJSON(data);

					LogUtil.e("master", "取消关注提交的数据" + json);
					String request = HttpClientPostUpload.Upload(json, url);

					JSONObject jsonj = null;
					String status = null;

					try {
						jsonj = new JSONObject(request);
						LogUtil.e("msater", "取消关注返回的数据" + jsonj.toString());

						status = jsonj.getString("status");
						info = jsonj.getString("info");
						if (TextUtils.equals(status, "1")) {
							JSONArray arry = jsonj.getJSONArray("data");
						}

					} catch (JSONException e) {
						e.printStackTrace();
						LogUtil.e("master", "解析错误" + e.toString());
					}

					if (TextUtils.equals(status, "1")) {
						handler.sendEmptyMessage(3);
					} else if (TextUtils.equals(status, "0")) {
						handler.sendEmptyMessage(-3);
					} else {
						handler.sendEmptyMessage(-1);
					}
				}
			}).start();
		}



		private Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 2) {// 成功关注
					ToastUtil.showToast(context, "关注成功");
//					pd.show();
					mListView.setRefreshing(true);
				}else if (msg.what == -2) {// 关注失败
					ToastUtil.showToast(context, info);
				}else if (msg.what == 3){
					ToastUtil.showToast(context,"取消关注");
//					pd.show();
					mListView.setRefreshing(true);
				}
			}
		};

		private void praise(final String id) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					JSONObject data = new JSONObject();
					sp = context.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
							Context.MODE_PRIVATE);
					try {
						data.put("fid", id);
						data.put("mid",sp.getString(XZContranst.id,null));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					String url = HttpUrl.followAdd;
					String json = StringUtils.setJSON(data);

					LogUtil.e("master", "关注提交的数据" + json);
					String request = HttpClientPostUpload.Upload(json, url);

					JSONObject jsonj = null;
					String status = null;

					try {
						jsonj = new JSONObject(request);
						LogUtil.e("master", "关注返回的数据" + jsonj.toString());

						status = jsonj.getString("status");
						info = jsonj.getString("info");
						if (TextUtils.equals(status, "1")) {
							JSONArray arry = jsonj.getJSONArray("data");
						}

					} catch (JSONException e) {
						e.printStackTrace();
						LogUtil.e("master", "解析错误" + e.toString());
					}

					if (TextUtils.equals(status, "1")) {
						handler.sendEmptyMessage(2);
					} else if (TextUtils.equals(status, "0")) {
						handler.sendEmptyMessage(-2);
					} else {
						handler.sendEmptyMessage(-1);
					}
				}
			}).start();
		}


	}

}
