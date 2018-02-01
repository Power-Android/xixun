package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.UserCenterTravelAdapter;
import com.power.travel.xixuntravel.adapter.UserCenterTripListAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.Friend;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.model.MyTripModel;
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
import io.rong.imlib.model.UserInfo;

/**
 * 用户个人中心
 * @author fan
 *
 */
public class UserCenterActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back,usercenter_head,usercenter_sex,usercenter_ifguide,usercenter_ifdriver,title_iv;
	private TextView title,usercenter_name,usercenter_id,usercenter_sign,usercenter_distance;
	private String TAG="UserCenterActivity",info;
	private RadioGroup radioGroup;
	private RadioButton left,right;
	SharedPreferences sp;
	private ProgressDialogUtils pd;
	MasterModel mMasterModel;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private int listtype = 1;// 1约伴（行程）2游记
	private PullToRefreshListView tripListView,travelListView;
	List<MyTripModel> TripList = new ArrayList<MyTripModel>();
	UserCenterTripListAdapter TripAdapter;
	int page = 1,travelpage = 1;
	DisplayMetrics dm;
	List<MyTravelModel> TravelList = new ArrayList<MyTravelModel>();
	UserCenterTravelAdapter TravelAdapter;
	private String mid;

//	LinearLayout yueban_listview_bg;
	
	private Handler handler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				if (page == 1) {
//					yueban_listview_bg.setVisibility(View.GONE);
					TripAdapter = new UserCenterTripListAdapter(UserCenterActivity.this, TripList,dm.widthPixels);
					tripListView.setAdapter(TripAdapter);
					
				} else {
					TripAdapter.notifyDataSetChanged();
				}
				page = page + 1;
			} else if (msg.what == 0) {// 失败
				tripListView.setBackground(getResources().getDrawable(R.drawable.edittext_graybg));;
				ToastUtil
						.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil
						.showToast(getApplicationContext(), info);
			} else if (msg.what ==2) {//获取用户信息正确
				setData();
			}else if (msg.what ==-2) {//获取用户信息失败
				ToastUtil
				.showToast(getApplicationContext(), info);
			}else if (msg.what == 3){//关注
				ToastUtil.showToast(getApplicationContext(), "关注成功");
				praise_iv.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
				getData();
			}else if (msg.what == -3){
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 4){
				ToastUtil.showToast(getApplicationContext(),"取消关注");
				praise_iv.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
				getData();
			}else if (msg.what == -4){
				ToastUtil.showToast(UserCenterActivity.this,info);
			}
			if (pd != null && UserCenterActivity.this != null) {
				pd.dismiss();
			}
			if (UserCenterActivity.this != null && tripListView != null) {
				tripListView.onRefreshComplete();
			}

		}
	};

	private Handler travelhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				if (travelpage == 1) {
					TravelAdapter = new UserCenterTravelAdapter(UserCenterActivity.this,
							TravelList);
					travelListView.setAdapter(TravelAdapter);
				} else {
					TravelAdapter.notifyDataSetChanged();
				}
				travelpage = travelpage + 1;
			} else if (msg.what == 0) {// 失败
				ToastUtil
						.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil
						.showToast(getApplicationContext(), info);

			}
			if (pd != null && UserCenterActivity.this != null) {
				pd.dismiss();
			}
			if (UserCenterActivity.this != null && travelListView != null) {
				travelListView.onRefreshComplete();
			}

		}
	};
	private ImageView praise_iv;
	private String guanzhu_mid = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_usercenter);
		MyApplication.getInstance().addActivity(this);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		init();
		initListener();
		initGetIntent();
		getData();
//		setData();
		getTripData(true);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.tab_rb_yueban) {// 约伴
					left.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg_yueban_left));
					left.setTextColor(getResources().getColor(R.color.white));
					right.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
					right.setTextColor(getResources().getColor(R.color.black));
					tripListView.setVisibility(View.VISIBLE);
					travelListView.setVisibility(View.GONE);
					listtype = 1;
					if (TripList.isEmpty()) {
						getTripData(true);
					}
				} else if (checkedId == R.id.tab_rb_travel) {// 游记
					right.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg_yueban_right));
					right.setTextColor(getResources().getColor(R.color.white));
					left.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
					left.setTextColor(getResources().getColor(R.color.black));
					travelListView.setVisibility(View.VISIBLE);
					tripListView.setVisibility(View.GONE);
					listtype = 2;
					if (TravelList.isEmpty()) {
						getTravelData(true);
					}
				}

			}
		});
		
		tripListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent = new Intent(UserCenterActivity.this,
						TripDetailActivity.class);
				intent.putExtra("id", TripList.get(position - 1).getId());
				startActivity(intent);
			}
		});
		
		travelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent = new Intent(UserCenterActivity.this,
						TravelDetailActivity.class);
				intent.putExtra("id", TravelList.get(position - 1).getId());
				startActivity(intent);
			}
		});
	}
	private void init() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title_iv =(ImageView) findViewById(R.id.title_iv2);
		title_iv.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setText("个人中心");
		usercenter_head= (ImageView) findViewById(R.id.usercenter_head);
		usercenter_name= (TextView) findViewById(R.id.usercenter_name);
		usercenter_id= (TextView) findViewById(R.id.usercenter_id);
		usercenter_sex= (ImageView) findViewById(R.id.usercenter_sex);
		usercenter_ifguide= (ImageView) findViewById(R.id.usercenter_ifguide);
		usercenter_ifdriver= (ImageView) findViewById(R.id.usercenter_ifdriver);
		usercenter_sign= (TextView) findViewById(R.id.usercenter_sign);
		usercenter_distance= (TextView) findViewById(R.id.usercenter_distance);
		praise_iv = (ImageView) findViewById(R.id.praise_iv);
		praise_iv.setOnClickListener(this);
		
		options = new DisplayImageOptions.Builder()
//		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
//		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
//		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(false)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
		 .displayer(new RoundedBitmapDisplayer(10))//设置用户加载图片task(这里是圆角图片显示)
		.build();
		
		
		
		radioGroup=(RadioGroup)findViewById(R.id.usercenter_radiogroup);
		left=(RadioButton)findViewById(R.id.tab_rb_yueban);
		right=(RadioButton)findViewById(R.id.tab_rb_travel);
		tripListView = (PullToRefreshListView) findViewById(R.id.us_yueban_listview);
		tripListView.setMode(Mode.BOTH);
		tripListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
		// mListView.setMode(Mode.DISABLED);
		tripListView.setOnRefreshListener(this);
		tripListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
		travelListView = (PullToRefreshListView) findViewById(R.id.us_travel_listview);
		travelListView.setMode(Mode.BOTH);
		travelListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
		// mListView.setMode(Mode.DISABLED);
		travelListView.setOnRefreshListener(this);
		travelListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
//		yueban_listview_bg=(LinearLayout)findViewById(R.id.us_yueban_listview_bg);
	}
	
	private void initListener() {
		back.setOnClickListener(this);
		title_iv.setOnClickListener(this);
	}
	
	private void initGetIntent() {
		Intent intent=getIntent();
		if (intent.getStringExtra("guanzhu_mid")!= null){
			guanzhu_mid = intent.getStringExtra("guanzhu_mid");
			mid = guanzhu_mid;
		}else {
			mMasterModel=(MasterModel) intent.getExtras().getSerializable("model");
			mid = mMasterModel.getMid();
		}
	}
	
	private void setData(){
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		/**
		 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
		 */
		imageLoader.displayImage(mMasterModel.getFace(), usercenter_head,
				options, animateFirstListener);
		
		usercenter_name.setText(mMasterModel.getNickname());
		if(mMasterModel.getUsername().length()>6){
			usercenter_id.setText("ID："+mMasterModel.getUsername().substring(0, 3)
					+"****"+mMasterModel.getUsername().substring(mMasterModel.getUsername().length()-4, mMasterModel.getUsername().length()));
		}
		
		if(TextUtils.equals(mMasterModel.getSex(), "1")){
			usercenter_sex.setImageDrawable(getResources().getDrawable(R.drawable.my_boy));
		}else if(TextUtils.equals(mMasterModel.getSex(), "2")){
			usercenter_sex.setImageDrawable(getResources().getDrawable(R.drawable.my_girle));
		}
		if (TextUtils.equals(mMasterModel.getIf_driver(),"2")){
			usercenter_ifdriver.setVisibility(View.VISIBLE);
		}
		if (TextUtils.equals(mMasterModel.getIf_guide(),"2")){
			usercenter_ifguide.setVisibility(View.VISIBLE);
		}
		if (TextUtils.equals(mMasterModel.getFollow(),"1")){
			praise_iv.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
		}else {
			praise_iv.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
		}
		if (mMasterModel.getCityName() != null){
			usercenter_distance.setText(mMasterModel.getCityName());
		}

		usercenter_sign.setText(mMasterModel.getSignature());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v==back){//
			finish();
		}
		if (v == title_iv){
			/**
			 * 进入融云
			 */
//			RongIM.getInstance().setCurrentUserInfo(new UserInfo(mMasterModel.getId(),mMasterModel.getNickname(), Uri.parse(mMasterModel.getFace())));
//			LogUtil.e("rongyunlog","融云打印："+mMasterModel.getId()+mMasterModel.getNickname()+mMasterModel.getFace());
//			RongIM.getInstance().setMessageAttachedUserInfo(true);
//			List<Friend> list = new ArrayList<>();
//			list.add(new Friend(mMasterModel.getId(),mMasterModel.getNickname(),mMasterModel.getFace()));
			/*final List<Friend> list = new ArrayList<>();
			list.add(new Friend(mMasterModel.getId(),mMasterModel.getNickname(),mMasterModel.getFace()));
			RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
				@Override
				public UserInfo getUserInfo(String s) {
					for (Friend i : list) {
						if (i.getUserId().equals(mMasterModel.getId())) {
							Log.e(TAG, "---------返回的用户信息-----------" + i.getPortraitUri());
							return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
						}
					}
					return null;
				}
			}, true);*/
			RongIM.getInstance().startPrivateChat(this,mMasterModel.getId() , mMasterModel.getNickname());
		}
		if (v==praise_iv){
			SharedPreferences sp;
			sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			if (sp.getBoolean(XZContranst.if_login, false)) {
				if (TextUtils.equals(mMasterModel.getFollow(),"1")){//关注变取消关注
					if (mid == null){
						praise_cancel(mMasterModel.getMid());
					}else {
						praise_cancel(mid);
					}
				}else {
					if (mid == null){
						praise(mMasterModel.getMid());
					}else {
						praise(mid);
					}
				}
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}

		}
	}

	private void praise_cancel(final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
//					data.put("id", id);
					data.put("id", mMasterModel.getFollow_id());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.follow_cancel;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "取消关注提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "取消关注返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
					}

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(4);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(-4);
				} else {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}

	private void praise(final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("fid", id);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.followAdd;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "关注提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "关注返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
					}

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
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
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			switch (listtype) {
			case 1:// 约伴
				if (!TripList.isEmpty()) {
					TripList.clear();
					TripAdapter.notifyDataSetChanged();
				}
				page = 1;
				getTripData(false);
				break;
			case 2:// 游记
				if (!TravelList.isEmpty()) {
					TravelList.clear();
					TravelAdapter.notifyDataSetChanged();
				}
				travelpage = 1;
				getTravelData(false);
				break;
			default:
				break;
			}

		} else {
			ToastUtil.showToast(getApplicationContext(),
					XZContranst.no_net);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			switch (listtype) {
			case 1:// 约伴
				getTripData(false);
				break;
			case 2:// 游记
				getTravelData(false);
				break;
			default:
				break;
			}

		} else {
			ToastUtil.showToast(getApplicationContext(),
					XZContranst.no_net);
		}
	}
	
	/**
	 * 获取行程
	 * @param ifshow
	 */
	private void getTripData(boolean ifshow) {
		if (ifshow) {
			pd = ProgressDialogUtils.show(this, "加载数据...");
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					if (mid == null){
						data.put("mid",mMasterModel.getId());
					}else {
						data.put("mid",mid);
					}
					data.put("page", page);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.my_triplists;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "我的行程提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "行程返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						TripList = JSON.parseArray(arry.toString(),
								MyTripModel.class);
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
	
	/**
	 * 获取游记
	 * @param ifshow
	 */
	private void getTravelData(boolean ifshow) {
		if (ifshow) {
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("page", travelpage);
					data.put("mid", mMasterModel.getId());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.my_lists;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "我的游记提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "我的游记返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if(TextUtils.equals(status, "1")){
						JSONArray arry = jsonj.getJSONArray("data");
						TravelList = JSON.parseArray(arry.toString(),
								MyTravelModel.class);
					}
					

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					travelhandler.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "0")) {
					travelhandler.sendEmptyMessage(0);
				} else {
					travelhandler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}
	
	private void getData() {
//		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					if (TextUtils.equals(mid,null)){
						data.put("mid",mMasterModel.getId());
					}else {
						data.put("mid",mid);
					}
					data.put("fid",sp.getString(XZContranst.id,null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.index;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "获取信息提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "获取信息返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					JSONObject djson = jsonj.getJSONObject("data");
					mMasterModel= JSON.parseObject(djson.toString(), MasterModel.class);
					
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
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
