package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.AgeAdapter;
import com.power.travel.xixuntravel.adapter.GuideListAdapter;
import com.power.travel.xixuntravel.adapter.NationAdapter;
import com.power.travel.xixuntravel.adapter.SexAdapter;
import com.power.travel.xixuntravel.adapter.WorkageAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.AgeModel;
import com.power.travel.xixuntravel.model.GuideModel;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.NationModel;
import com.power.travel.xixuntravel.model.SexModel;
import com.power.travel.xixuntravel.model.WorkageModel;
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
 * 导游列表
 * 
 * @author fan
 * 
 */
public class GuideListActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView title, sex_tv,worktime_tv,age_tv,nation_tv;
	private String data, TAG = "MasterListActivity", info, mobile;
	private ProgressDialogUtils pd;
	private PullToRefreshListView mListView;
	List<GuideModel> adapterList = new ArrayList<GuideModel>();
	List<GuideModel> adapterListMore = new ArrayList<GuideModel>();
	GuideListAdapter adapter;
	int page = 1;
	SharedPreferences sp, spLocation;
	private String sex="",worktime="",age="",nation;
	private PopupWindow sexPopupWindow,workagePopupWindow,agePopupWindow,nationPopupWindow;
	private ListView list_sex,list_workage,list_age,list_nation;
	SexAdapter sexAdapter;
	List<SexModel> sexList = new ArrayList<SexModel>();
	WorkageAdapter workageAdapter;
	List<WorkageModel> workageList = new ArrayList<WorkageModel>();
	AgeAdapter ageAdapter;
	List<AgeModel> ageList = new ArrayList<AgeModel>();
	List<NationModel> nationList = new ArrayList<NationModel>();
	NationAdapter nationAdapter;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new GuideListAdapter(GuideListActivity.this,
							adapterList);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page = page + 1;
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 2) {//获取年龄驾龄成功

			}else if (msg.what == -2) {//获取年龄驾龄失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && GuideListActivity.this != null) {
				pd.dismiss();
			}
			if (GuideListActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_guide);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
	
		if (isConnect()) {
			getData(true);
		} else {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
		for(int i=0;i<3;i++){
			if(i==0){
				SexModel sexModel=new SexModel();
				sexModel.setId("");
				sexModel.setName("全部");
				sexList.add(sexModel);
			}else if(i==1){
				SexModel sexModel=new SexModel();
				sexModel.setId("2");
				sexModel.setName("女");
				sexList.add(sexModel);
			}else if(i==2){
				SexModel sexModel=new SexModel();
				sexModel.setId("1");
				sexModel.setName("男");
				sexList.add(sexModel);
			}
		
		}
		getWorkageData();
		getageData();
		getnationData();
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				MasterModel mMasterModel=new MasterModel();
				mMasterModel.setMid(adapterList.get(position-1).getMid());
				Intent intent=new Intent(GuideListActivity.this,UserCenterActivity.class);
				intent.putExtra("model", mMasterModel);
				startActivity(intent);
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
		title = (TextView) findViewById(R.id.title);
		title.setText("导游");
		mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
		sex_tv = (TextView) findViewById(R.id.sex);
		worktime_tv= (TextView) findViewById(R.id.worktime);
		age_tv= (TextView) findViewById(R.id.age);
		nation_tv= (TextView) findViewById(R.id.nation);
	}

	private void initListener() {
		back.setOnClickListener(this);
		sex_tv.setOnClickListener(this);
		worktime_tv.setOnClickListener(this);
		age_tv.setOnClickListener(this);
		nation_tv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == sex_tv) {//性别
			initmPopupWindowView();
			sexPopupWindow.showAsDropDown(sex_tv, 0, 0);
		}else if(v==worktime_tv){//工龄
			if(workageList.isEmpty()){
				getWorkageData();
			}else{
				initmWorkagePopupWindowView();
				workagePopupWindow.showAsDropDown(worktime_tv, 0, 0);
			}
		}else if(v==age_tv){//年龄
			if(ageList.isEmpty()){
				getageData();
			}else{
				initmagePopupWindowView();
				agePopupWindow.showAsDropDown(age_tv, 0, 0);
			}
		}else if(v==nation_tv){//
			if(nationList.isEmpty()){
				getnationData();
			}else{
				initmnationPopupWindowView();
				nationPopupWindow.showAsDropDown(nation_tv, 0, 0);
			}
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

	/**
	 * 性别 pop
	 */
	private void initmPopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_guidelist_select_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		sexPopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		sexPopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (sexPopupWindow != null && sexPopupWindow.isShowing()) {
					sexPopupWindow.dismiss();
					sexPopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					sexPopupWindow.dismiss();
					return true;
				}

				return false;
			}

		});

		list_sex = (ListView) customView.findViewById(R.id.list_sex);
		sexAdapter = new SexAdapter(GuideListActivity.this, sexList);
		list_sex.setAdapter(sexAdapter);

		list_sex.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				sex_tv.setText(sexList.get(position).getName());
				sex=sexList.get(position).getId();
				if (!adapterList.isEmpty()) {
					adapterList.clear();
					adapter.notifyDataSetChanged();
				}
				page = 1;
				getData(true);
				sexPopupWindow.dismiss();
				sexPopupWindow = null;
			}
		});
	}
	

	/**
	 * 工龄pop
	 */
	private void initmWorkagePopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_guidelist_select_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		workagePopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		workagePopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (workagePopupWindow != null && workagePopupWindow.isShowing()) {
					workagePopupWindow.dismiss();
					workagePopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					workagePopupWindow.dismiss();
					return true;
				}

				return false;
			}

		});

		list_workage = (ListView) customView.findViewById(R.id.list_workage);
		workageAdapter = new WorkageAdapter(GuideListActivity.this, workageList);
		list_workage.setAdapter(workageAdapter);

		list_workage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				worktime_tv.setText(workageList.get(position).getVal());
				worktime=workageList.get(position).getVal();
				if (!adapterList.isEmpty()) {
					adapterList.clear();
					adapter.notifyDataSetChanged();
				}
				page = 1;
				getData(true);
				workagePopupWindow.dismiss();
				workagePopupWindow = null;
			}
		});
	}
	
	/**
	 * 年龄pop
	 */
	private void initmagePopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_guidelist_select_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		agePopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		agePopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (agePopupWindow != null && agePopupWindow.isShowing()) {
					agePopupWindow.dismiss();
					agePopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					agePopupWindow.dismiss();
					return true;
				}

				return false;
			}

		});

		list_age = (ListView) customView.findViewById(R.id.list_age);
		ageAdapter = new AgeAdapter(GuideListActivity.this, ageList);
		list_age.setAdapter(ageAdapter);

		list_age.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				age_tv.setText(ageList.get(position).getVal());
				age=ageList.get(position).getVal();
				if (!adapterList.isEmpty()) {
					adapterList.clear();
					adapter.notifyDataSetChanged();
				}
				page = 1;
				getData(true);
				agePopupWindow.dismiss();
				agePopupWindow = null;
			}
		});
	}
	
	/**
	 * 民族pop
	 */
	private void initmnationPopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_guidelist_select_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		nationPopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		nationPopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (nationPopupWindow != null && nationPopupWindow.isShowing()) {
					nationPopupWindow.dismiss();
					nationPopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					nationPopupWindow.dismiss();
					return true;
				}

				return false;
			}

		});

		list_nation = (ListView) customView.findViewById(R.id.list_nation);
		nationAdapter = new NationAdapter(GuideListActivity.this, nationList);
		list_nation.setAdapter(nationAdapter);

		list_nation.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				nation_tv.setText(nationList.get(position).getName());
				nation=nationList.get(position).getName();
				if (!adapterList.isEmpty()) {
					adapterList.clear();
					adapter.notifyDataSetChanged();
				}
				page = 1;
				getData(true);
				nationPopupWindow.dismiss();
				nationPopupWindow = null;
			}
		});
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
					data.put("sex", sex);
					data.put("worktime", worktime);
					data.put("age", age);
					data.put("nation", nation);
					data.put("coordinate_x",
							spLocation.getString(XZContranst.coordinate_x, ""));
					data.put("coordinate_y",
							spLocation.getString(XZContranst.coordinate_y, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.guide;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "导游提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "导游返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						adapterListMore = JSON.parseArray(arry.toString(),
								GuideModel.class);
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
	 * 获取工龄
	 */
	private void getWorkageData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "1");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.age;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "年龄 驾龄获取提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "年龄 驾龄获取返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						workageList = JSON.parseArray(arry.toString(),
								WorkageModel.class);
					}

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
	 * 获取年龄
	 */
	private void getageData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "2");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.age;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "年龄 驾龄获取提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "年龄 驾龄获取返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						ageList = JSON.parseArray(arry.toString(),
								AgeModel.class);
					}

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
	 * 获取民族
	 */
	private void getnationData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "1");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.nation;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "民族获取提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "民族获取返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						nationList = JSON.parseArray(arry.toString(),
								NationModel.class);
					}

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
