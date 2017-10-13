package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.MyTripListAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.impl.MyTripOnItemOnClickListener;
import com.power.travel.xixuntravel.model.MyTripModel;
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
 * 我的行程
 * 
 * @author fan
 * 
 */
public class MyTripListActivity extends BaseActivity implements
        OnRefreshListener2<ListView>, MyTripOnItemOnClickListener {

	private ImageView back;
	private TextView title,title_send;
	private String data, TAG = "MyTripListActivity", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	private PullToRefreshListView mListView;
	List<MyTripModel> adapterList = new ArrayList<MyTripModel>();
	List<MyTripModel> adapterListMore = new ArrayList<MyTripModel>();
	MyTripListAdapter adapter;
	int page = 1,changePosi;
	DisplayMetrics dm;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new MyTripListAdapter(MyTripListActivity.this,
							adapterList, dm.widthPixels);
					adapter.setOnItemOnClick(MyTripListActivity.this);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page = page + 1;
			} else if (msg.what == 0) {// 失败
//				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
//				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {// 删除成功
				adapterList.remove(changePosi);
				adapter.notifyDataSetChanged();
			} else if (msg.what == -2) {// 删除失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 3) {//恢复成功
				changeData("1");
				adapter.notifyDataSetChanged();
			} else if (msg.what == -3) {// 恢复失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 4) {//取消成功
				changeData("0");
				adapter.notifyDataSetChanged();
			} else if (msg.what == -4) {// 取消失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && MyTripListActivity.this != null) {
				pd.dismiss();
			}
			if (MyTripListActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_mytriplist);
		MyApplication.getInstance().addActivity(this);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		initView();
		initListener();
		if (isConnect()) {
			getData(true);
		} else {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
		
//		mListView.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				if (firstVisibleItem >= 1) {
//				ToastUtil.showToast(getApplicationContext(), "firstVisibleItem >= 1");
//			} else {
//				ToastUtil.showToast(getApplicationContext(), "firstVisibleItem < 1");
//			}
//				
//			}
//		});
	}

	@SuppressLint("NewApi")
	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);

		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("我的行程");
		title_send= (TextView) findViewById(R.id.title_send);
		title_send.setVisibility(View.VISIBLE);
		title_send.setBackground(getResources().getDrawable(R.drawable.circle_sendtrips));
		mListView = (PullToRefreshListView) findViewById(R.id.mytrip_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
	}

	private void initListener() {
		back.setOnClickListener(this);
		title_send.setOnClickListener(this);
	}
	//修改状态
	private void changeData(String state){
		MyTripModel myTripModel=adapterList.get(changePosi);
		myTripModel.setState(state);
		adapterList.set(changePosi, myTripModel);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}else if(v==title_send){//发布行程
			startActivity(new Intent(this,
					PublicTripActivity.class));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	public void OnItemClick(int typeint, int position) {
		switch (typeint) {
		case 1:// 进入详情
			Intent intent = new Intent(this, TripDetailActivity.class);
			intent.putExtra("id", adapterList.get(position).getId());
			startActivity(intent);
			break;
		case 2:// 取消
			changePosi=position;
			cancelData(adapterList.get(position).getId());
			break;
		case 3:// 恢复
			changePosi=position;
			recoveryData(adapterList.get(position).getId());
			break;
		case 4:// 删除
			changePosi=position;
			delectData(adapterList.get(position).getId());
			break;
		default:
			break;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			if(!adapterList.isEmpty()){
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
			pd = ProgressDialogUtils.show(this, "加载数据...");
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
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
						adapterListMore = JSON.parseArray(arry.toString(),
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
	 * 删除
	 * @param delectid
	 */
	private void delectData(final String delectid) {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", delectid);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.delete_my_triplists;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "删除行程提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "删除行程返回的数据" + jsonj.toString());

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
	/**
	 * 恢复
	 * @param delectid
	 */
	private void recoveryData(final String delectid) {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", delectid);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.recovery_my_triplists;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "恢复行程提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "恢复行程返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

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
	
	/**
	 * 取消
	 * @param delectid
	 */
	private void cancelData(final String delectid) {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", delectid);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.cancel_my_triplists;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "取消行程提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "取消行程返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

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

}
