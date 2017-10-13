package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.power.travel.xixuntravel.adapter.MyTravelAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.event.MyTravelDetailEvent;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.ConfigApp;
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

import de.greenrobot.event.EventBus;

/**
 * 我的游记
 * 
 * @author fan
 * 
 */
public class MyTravelActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView title;
	private String data, TAG = "MyTravelActivity", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	private PullToRefreshListView mListView;
	List<MyTravelModel> adapterList = new ArrayList<MyTravelModel>();
	List<MyTravelModel> adapterListMore = new ArrayList<MyTravelModel>();
	MyTravelAdapter adapter;
	int page = 1;
	

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new MyTravelAdapter(MyTravelActivity.this, adapterList);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page=page+1;
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}else if (msg.what == 2) {//
				for(int i=0;i<adapterList.size();i++){
					if(TextUtils.equals(adapterList.get(i).getId(), ConfigApp.mDelectMyTravel.getTravelID())){
						adapterList.remove(i);
						adapter.notifyDataSetChanged();
						break;
					}
				}

			}
			if (pd != null && MyTravelActivity.this != null) {
				pd.dismiss();
			}
			if (MyTravelActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};
	private TextView title_tv;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_mytravel);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		
		try {
			EventBus.getDefault().register(this);
		} catch (Exception e) {
			Log.e(TAG, "EventBus注册错误" + e.toString());
		}
		
		if(isConnect()){
			getData(true);
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
		
		
	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title_tv = findViewById(R.id.title_tv);
		title_tv.setVisibility(View.VISIBLE);
		title_tv.setOnClickListener(this);
		title_tv.setText("发布游记");
		title.setText("我的游记");
		mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
//		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
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
		if (v == title_tv){
			startActivity(new Intent(MyTravelActivity.this,
					PublicTravelActivity.class));
		}
	}
	
	public void onEventMainThread(MyTravelDetailEvent event){
		switch (event.getType()) {
		case 1://删除
			handler.sendEmptyMessage(2);
			
			break;

		default:
			break;
		}
	}
	

	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
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
					data.put("mid", sp.getString(XZContranst.id, null));
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
						adapterListMore = JSON.parseArray(arry.toString(),
								MyTravelModel.class);
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
