package com.power.travel.xixuntravel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.ViewspotDetailCommentAdapter;
import com.power.travel.xixuntravel.adapter.ViewspotDetailGridAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.ViewsportDetailCommentModel;
import com.power.travel.xixuntravel.model.ViewsportDetailModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyGridView;
import com.power.travel.xixuntravel.weight.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 景区详情
 * 
 * @author fan
 * 
 */
public class ViewsoptDetailActivity extends BaseActivity {

	private String id, TAG = "ViewsoptDetailActivity", info, star;
	private ImageView back, viewsport_daohang, viewsport_face, viewsport_star,
			viewsport_star2, viewsport_star3, viewsport_star4, viewsport_star5;
	private TextView search, viewsport_title, viewsport_label,
			viewsport_address, viewsport_title2, comment, description_more;
	private EditText comment_edit;
	private WebView mWebView;
	private DisplayMetrics dm;
	private LinearLayout.LayoutParams WebView_params;
	private MyGridView gridview;
	private MyListView comment_listview;
	SharedPreferences sp, spLocation;
	private ProgressDialogUtils pd;
	ViewsportDetailModel mViewsportDetailModel = new ViewsportDetailModel();
	DisplayImageOptions options, options2;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// 自己写的一个类
	ImageLoader imageLoader = ImageLoader.getInstance();
	List<ViewsportDetailCommentModel> list = new ArrayList<ViewsportDetailCommentModel>();
	ViewspotDetailCommentAdapter adapter;
	MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private Marker mMarker;
	BitmapDescriptor bdB;

	public static List<Activity> activityList = new LinkedList<Activity>();
	private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
	private String mSDCardPath = null;

	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				setData();
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {//
				comment_edit.getText().clear();
				ToastUtil.showToast(getApplicationContext(), info);
				getCommentData();
			} else if (msg.what == -2) {//
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 3) {// webview 高度改变
				description_more.setVisibility(View.GONE);
				WebView_params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				mWebView.setLayoutParams(WebView_params);
			}
			if (pd != null && ViewsoptDetailActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	private Handler handlercomment = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapter = new ViewspotDetailCommentAdapter(
						ViewsoptDetailActivity.this, list);
				comment_listview.setAdapter(adapter);
			} else if (msg.what == 0) {// 失败
				// ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				// ToastUtil.showToast(getApplicationContext(), info);

			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		SDKInitializer.initialize(getApplicationContext());
		activityList.add(this);
		setContentView(R.layout.activity_viewsportdetail);
		MyApplication.getInstance().addActivity(this);
		BNOuterLogUtil.setLogSwitcher(true);
		initView();
		initListener();
		if (initDirs()) {
			initNavi();
		}
		initGetIntent();
		getData(true);
		getCommentData();

	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		spLocation = this.getSharedPreferences(
				XZContranst.MAIN_SHARED_PREFERENCES_LO, Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		search = (TextView) findViewById(R.id.search);
		viewsport_daohang = (ImageView) findViewById(R.id.viewsport_daohang);
		viewsport_face = (ImageView) findViewById(R.id.viewsport_face);
		viewsport_title = (TextView) findViewById(R.id.viewsport_title);
		viewsport_label = (TextView) findViewById(R.id.viewsport_label);
		viewsport_address = (TextView) findViewById(R.id.viewsport_address);
		viewsport_title2 = (TextView) findViewById(R.id.viewsport_title2);
		mWebView = (WebView) findViewById(R.id.viewsport_description);
		dm = this.getResources().getDisplayMetrics();
		WebView_params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		WebView_params.height = dm.widthPixels / 2;
		mWebView.setLayoutParams(WebView_params);
		description_more = (TextView) findViewById(R.id.viewsport_description_more);
		gridview = (MyGridView) findViewById(R.id.image_gridView);
		comment_listview = (MyListView) findViewById(R.id.comment_listview);
		comment = (TextView) findViewById(R.id.viewspotdetail_comment);
		comment_edit = (EditText) findViewById(R.id.viewspotdetail_comment_edit);
		viewsport_star = (ImageView) findViewById(R.id.viewsport_star);
		viewsport_star2 = (ImageView) findViewById(R.id.viewsport_star2);
		viewsport_star3 = (ImageView) findViewById(R.id.viewsport_star3);
		viewsport_star4 = (ImageView) findViewById(R.id.viewsport_star4);
		viewsport_star5 = (ImageView) findViewById(R.id.viewsport_star5);

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.removeViewAt(1);// 取消logo
		mMapView.removeViewAt(2);// 隐藏比例尺
		mMapView.showZoomControls(false);// 隐藏缩放控件
		RelativeLayout.LayoutParams imagebtn_params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params.height = dm.widthPixels * 1 / 2;
		// imagebtn_params.width = dm.widthPixels;
		mMapView.setLayoutParams(imagebtn_params);
		mBaiduMap = mMapView.getMap();
		bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);

		options = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.imaleloadlogo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		// .cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.displayer(new RoundedBitmapDisplayer(5))// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		options2 = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.imaleloadlogo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		// .cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.displayer(new RoundedBitmapDisplayer(20))// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
	}

	private void initListener() {
		back.setOnClickListener(this);
		comment.setOnClickListener(this);
		viewsport_daohang.setOnClickListener(this);
		description_more.setOnClickListener(this);
	}

	private void initGetIntent() {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
	}

	private void setData() {
		// 添加地图中心点
		if (!TextUtils.isEmpty(mViewsportDetailModel.getCoordinate_y())
				&& !TextUtils.isEmpty(mViewsportDetailModel.getCoordinate_x())) {
			LatLng latlng = new LatLng(Double.parseDouble(mViewsportDetailModel
					.getCoordinate_y()),
					Double.parseDouble(mViewsportDetailModel.getCoordinate_x()));
			MarkerOptions ooB = new MarkerOptions().position(latlng).icon(bdB)
					.zIndex(5);
			mMarker = (Marker) (mBaiduMap.addOverlay(ooB));
			mMarker.setTitle("");

			MapStatus mMapStatus = new MapStatus.Builder().target(latlng)
					.build();// .zoom(18)
			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			mBaiduMap.setMapStatus(mMapStatusUpdate); // 改变地图状态
		}

		if (!TextUtils.isEmpty(mViewsportDetailModel.getThumb())) {
			imageLoader.displayImage(
					HttpUrl.Url + mViewsportDetailModel.getThumb(),
					viewsport_face, options2, animateFirstListener);
		}
		viewsport_title.setText(mViewsportDetailModel.getTitle());
		viewsport_title2.setText(mViewsportDetailModel.getTitle());

		if (TextUtils.equals(mViewsportDetailModel.getStar(), "5")) {
			viewsport_star.setVisibility(View.VISIBLE);
			viewsport_star2.setVisibility(View.VISIBLE);
			viewsport_star3.setVisibility(View.VISIBLE);
			viewsport_star4.setVisibility(View.VISIBLE);
			viewsport_star5.setVisibility(View.VISIBLE);
			star = "AAAAA景区";
		} else if (TextUtils.equals(mViewsportDetailModel.getStar(), "4")) {
			viewsport_star.setVisibility(View.VISIBLE);
			viewsport_star2.setVisibility(View.VISIBLE);
			viewsport_star3.setVisibility(View.VISIBLE);
			viewsport_star4.setVisibility(View.VISIBLE);
			star = "AAAA景区";
		} else if (TextUtils.equals(mViewsportDetailModel.getStar(), "3")) {
			viewsport_star.setVisibility(View.VISIBLE);
			viewsport_star2.setVisibility(View.VISIBLE);
			viewsport_star3.setVisibility(View.VISIBLE);
			star = "AAA景区";
		} else if (TextUtils.equals(mViewsportDetailModel.getStar(), "2")) {
			viewsport_star.setVisibility(View.VISIBLE);
			viewsport_star2.setVisibility(View.VISIBLE);
			star = "AA景区";
		} else if (TextUtils.equals(mViewsportDetailModel.getStar(), "1")) {
			viewsport_star.setVisibility(View.VISIBLE);
			star = "A景区";
		} else {

		}
		if (!TextUtils.isEmpty(mViewsportDetailModel.getLabel())) {
			viewsport_label.setText(star + mViewsportDetailModel.getLabel());
		} else {
			viewsport_label.setText(star);
		}
		viewsport_address.setText("距您约" + mViewsportDetailModel.getApart()
				+ mViewsportDetailModel.getAddress());
		mWebView.loadDataWithBaseURL(null, mViewsportDetailModel.getContent(),
				"text/html", "utf-8", null);

		if (!TextUtils.isEmpty(mViewsportDetailModel.getArray_img())) {//
			String[] listpic = mViewsportDetailModel.getArray_img().split(",");
			if (listpic.length > 1) {
				gridview.setNumColumns(3);
			} else {
				gridview.setNumColumns(1);
			}
			gridview.setAdapter(new ViewspotDetailGridAdapter(listpic, this));
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == search) {//
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		} else if (v == comment) {// 评论
			SharedPreferences sp;
			sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			if (sp.getBoolean(XZContranst.if_login, false)) {
				if (TextUtils.isEmpty(comment_edit.getText().toString())) {
					ToastUtil.showToast(getApplicationContext(), "请输入评论内容！");
				} else {
					comment();
				}
			}else{
				startActivity(new Intent(this, LoginActivity.class));

			}

		} else if (v == viewsport_daohang) {// 导航
			if (!TextUtils.isEmpty(spLocation.getString(
					XZContranst.coordinate_x, ""))
					&& !TextUtils.isEmpty(spLocation.getString(
							XZContranst.coordinate_y, ""))) {
				if (BaiduNaviManager.isNaviInited()) {
					pd = ProgressDialogUtils.show(this, "初始化...");
					pd.show();
					routeplanToNavi(CoordinateType.BD09LL);
				}
			} else {
				ToastUtil.showToast(getApplicationContext(), "请获取当前位置！");
			}

		} else if (v == description_more) {// 查看更多

			handler.sendEmptyMessage(3);
		}
	}

	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	String authinfo = null;

	/**
	 * 内部TTS播报状态回传handler
	 */
	private Handler ttsHandler = new Handler() {
		public void handleMessage(Message msg) {
			int type = msg.what;
			switch (type) {
			case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
				// showToastMsg("Handler : TTS play start");
				break;
			}
			case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
				// showToastMsg("Handler : TTS play end");
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 内部TTS播报状态回调接口
	 */
	private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

		@Override
		public void playEnd() {
			// showToastMsg("TTSPlayStateListener : TTS play end");
		}

		@Override
		public void playStart() {
			// showToastMsg("TTSPlayStateListener : TTS play start");
		}
	};

	public void showToastMsg(final String msg) {
		ViewsoptDetailActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void initNavi() {

		BNOuterTTSPlayerCallback ttsCallback = null;

		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
				new NaviInitListener() {
					@Override
					public void onAuthResult(int status, String msg) {
						if (0 == status) {
							authinfo = "key校验成功!";
						} else {
							authinfo = "key校验失败, " + msg;
						}
						ViewsoptDetailActivity.this
								.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										 Toast.makeText(getApplicationContext(),authinfo, Toast.LENGTH_LONG).show();
									}
								});
					}

					public void initSuccess() {
						// Toast.makeText(getApplicationContext(),
						// "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
						initSetting();
					}

					public void initStart() {
						// Toast.makeText(getApplicationContext(),
						// "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
					}

					public void initFailed() {
						Toast.makeText(getApplicationContext(), "百度导航引擎初始化失败",
								Toast.LENGTH_SHORT).show();
					}

				}, null, ttsHandler, null);

	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private void routeplanToNavi(CoordinateType coType) {
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;
		switch (coType) {
		case BD09LL: {
			// sNode = new BNRoutePlanNode(116.30784537597782,
			// 40.057009624099436, "百度大厦", null, coType);
			// eNode = new BNRoutePlanNode(116.40386525193937,
			// 39.915160800132085, "北京天安门", null, coType);
			sNode = new BNRoutePlanNode(
					Double.parseDouble(spLocation.getString(
							XZContranst.coordinate_x, "116.40386525193937")),
					Double.parseDouble(spLocation.getString(
							XZContranst.coordinate_y, "39.915160800132085")),
					spLocation.getString(XZContranst.location_country, ""),
					null, coType);
			eNode = new BNRoutePlanNode(
					Double.parseDouble(mViewsportDetailModel.getCoordinate_x()),
					Double.parseDouble(mViewsportDetailModel.getCoordinate_y()),
					mViewsportDetailModel.getTitle(), null, coType);
			break;
		}
		default:

		}
		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);

			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true,
					new DemoRoutePlanListener(sNode));
		}
	}

	public class DemoRoutePlanListener implements RoutePlanListener {

		private BNRoutePlanNode mBNRoutePlanNode = null;

		public DemoRoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

			for (Activity ac : activityList) {

				if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

					return;
				}
			}

			try {
				if (pd != null && ViewsoptDetailActivity.this != null) {
					pd.dismiss();
				}
			} catch (Exception e) {
				
			}
			Intent intent = new Intent(ViewsoptDetailActivity.this,
					BNDemoGuideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(ROUTE_PLAN_NODE,
					(BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);

		}

		@Override
		public void onRoutePlanFailed() {
			
			Toast.makeText(getApplicationContext(), "算路失败", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void initSetting() {
		BNaviSettingManager
				.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
		BNaviSettingManager
				.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
		BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
		BNaviSettingManager
				.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		BNaviSettingManager
				.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
	}

	private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

		@Override
		public void stopTTS() {
			
			Log.e("test_TTS", "stopTTS");
		}

		@Override
		public void resumeTTS() {
			
			Log.e("test_TTS", "resumeTTS");
		}

		@Override
		public void releaseTTSPlayer() {
			
			Log.e("test_TTS", "releaseTTSPlayer");
		}

		@Override
		public int playTTSText(String speech, int bPreempt) {
			
			Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

			return 1;
		}

		@Override
		public void phoneHangUp() {
			
			Log.e("test_TTS", "phoneHangUp");
		}

		@Override
		public void phoneCalling() {
			
			Log.e("test_TTS", "phoneCalling");
		}

		@Override
		public void pauseTTS() {
			
			Log.e("test_TTS", "pauseTTS");
		}

		@Override
		public void initTTSPlayer() {
			
			Log.e("test_TTS", "initTTSPlayer");
		}

		@Override
		public int getTTSState() {
			
			Log.e("test_TTS", "getTTSState");
			return 1;
		}
	};

	private void getData(boolean ifshow) {
		if (ifshow) {
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("scenic_id", id);
					data.put("coordinate_x",
							spLocation.getString(XZContranst.coordinate_x, ""));
					data.put("coordinate_y",
							spLocation.getString(XZContranst.coordinate_y, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.viewinfo;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "景区详情提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "景区详情返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONObject jsonobj = jsonj.getJSONObject("data");
						mViewsportDetailModel = JSON.parseObject(
								jsonobj.toString(), ViewsportDetailModel.class);
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
	 * 获取数据
	 */
	private void getCommentData() {
		// pd = ProgressDialogUtils.show(this, "获取数据...");
		// pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("sid", id);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.viewcommentlist;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "评论的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "评论返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					JSONArray jsonarry = jsonj.getJSONArray("data");
					if (!jsonarry.isNull(0)) {
						list = JSON.parseArray(jsonarry.toString(),
								ViewsportDetailCommentModel.class);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handlercomment.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "0")) {
					handlercomment.sendEmptyMessage(0);
				} else {
					handlercomment.sendEmptyMessage(-1);
				}

			}
		}).start();
	}

	/**
	 * 回复
	 */
	private void comment() {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("sid", id);
					data.put("content", comment_edit.getText().toString());
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.viewcomment;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "评论提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "评论返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

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
