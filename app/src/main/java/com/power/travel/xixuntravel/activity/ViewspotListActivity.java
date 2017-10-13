package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.ViewspotAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.ViewsportModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 景区
 * 
 * @author fan
 * 
 */
public class ViewspotListActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView search,bmapView_tv;
	private String data, TAG = "MasterListActivity", info, mobile;
	private ProgressDialogUtils pd;
	private PullToRefreshListView mListView;
	List<ViewsportModel> adapterList = new ArrayList<ViewsportModel>();
	List<ViewsportModel> adapterListMore = new ArrayList<ViewsportModel>();
	ViewspotAdapter adapter;
	int page = 1;
	SharedPreferences sp, spLocation;
	MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private Marker mMarker;
	BitmapDescriptor bdB;
	private InfoWindow mInfoWindow;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new ViewspotAdapter(ViewspotListActivity.this,
							adapterList);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page = page + 1;
				clearOverlay();
				initOverlay();
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && ViewspotListActivity.this != null) {
				pd.dismiss();
			}
			if (ViewspotListActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_viewsport);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		if (isConnect()) {
			getData(true);
		} else {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent = new Intent(ViewspotListActivity.this,
						ViewsoptDetailActivity.class);
				intent.putExtra("id", adapterList.get(position - 1).getId());
				startActivity(intent);
			}
		});

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				button.setText(marker.getTitle());
				button.setTextColor(getResources().getColor(R.color.black));
				button.setGravity(Gravity.CENTER_VERTICAL);
				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mBaiduMap.hideInfoWindow();
						Intent intent = new Intent(ViewspotListActivity.this,
								ViewsoptDetailActivity.class);
						intent.putExtra("id",
								String.valueOf(marker.getZIndex()));
						startActivity(intent);
					}
				});
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(button, ll, -47);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		spLocation = this.getSharedPreferences(
				XZContranst.MAIN_SHARED_PREFERENCES_LO, Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		search = (TextView) findViewById(R.id.search);
		mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.removeViewAt(1);// 取消logo
		mMapView.removeViewAt(2);// 隐藏比例尺
		mMapView.showZoomControls(false);// 隐藏缩放控件
		DisplayMetrics dm = getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams imagebtn_params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params.height = dm.widthPixels * 1 / 2;
		// imagebtn_params.width = dm.widthPixels;
		mMapView.setLayoutParams(imagebtn_params);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(10.0f);
		mBaiduMap.setMapStatus(msu);
		bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		bmapView_tv=(TextView)findViewById(R.id.bmapView_tv);
	}

	private void initListener() {
		back.setOnClickListener(this);
		search.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == search) {//
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * 添加
	 */
	public void initOverlay() {
		bmapView_tv.setText("共找到”景点“相关"+ String.valueOf(adapterList.size())+"结果");
		for (int i = 0; i < adapterList.size(); i++) {
			if (!TextUtils.isEmpty(adapterList.get(i).getCoordinate_y())
					&& !TextUtils.isEmpty(adapterList.get(i).getCoordinate_x())) {
				LatLng latlng = new LatLng(
						Double.parseDouble(adapterList.get(i).getCoordinate_y()),
						Double.parseDouble(adapterList.get(i).getCoordinate_x()));
				MarkerOptions ooB = new MarkerOptions().position(latlng)
						.icon(bdB)
						.zIndex(Integer.valueOf(adapterList.get(i).getId()));
				mMarker = (Marker) (mBaiduMap.addOverlay(ooB));
				mMarker.setTitle(adapterList.get(i).getTitle());
				if (i == 0) {
					MapStatus mMapStatus = new MapStatus.Builder().target(
							latlng).build();// .zoom(18)
					MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
							.newMapStatus(mMapStatus);
					mBaiduMap.setMapStatus(mMapStatusUpdate); // 改变地图状态
				}
			}

		}
	}

	/**
	 * 清除所有Overlay
	 */
	public void clearOverlay() {
		mBaiduMap.clear();
		if (mMarker != null) {
			mMarker = null;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			if (!adapterList.isEmpty()) {
				adapterList.clear();
				adapter.notifyDataSetChanged();
			}
			page = 1;
			getData(false);
		} else {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			getData(false);
		} else {
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
					data.put("coordinate_x",
							spLocation.getString(XZContranst.coordinate_x, ""));
					data.put("coordinate_y",
							spLocation.getString(XZContranst.coordinate_y, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.viewspot;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "常识提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "常识返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						adapterListMore = JSON.parseArray(arry.toString(),
								ViewsportModel.class);
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
}
