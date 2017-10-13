package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.Search_MemberAdapter;
import com.power.travel.xixuntravel.adapter.Search_TravelAdapter;
import com.power.travel.xixuntravel.adapter.Search_TripAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.model.AllTripModel;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.MemberModel;
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
 * 搜索查看更多
 * 
 * @author fan
 * 
 */
public class SearchMoreListActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView title;
	private String data, TAG = "SearchMoreListActivity", info, mobile,content;
	private ProgressDialogUtils pd;
	private PullToRefreshListView mListView;
	List<AllTravelModel> travelList = new ArrayList<AllTravelModel>();
	List<AllTravelModel> travelListMore = new ArrayList<AllTravelModel>();
	Search_TravelAdapter travelAdapter;
	List<AllTripModel> tripList = new ArrayList<AllTripModel>();
	List<AllTripModel> tripListMore = new ArrayList<AllTripModel>();
	Search_TripAdapter tripAdapter;
	List<MemberModel> memberList = new ArrayList<MemberModel>();
	List<MemberModel> memberListMore = new ArrayList<MemberModel>();
	Search_MemberAdapter memberAdapter;;
	int page = 1,type;
	SharedPreferences sp;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				if (page == 1) {
					switch (type) {
					case 1://游记
						travelList.addAll(travelListMore);
						if(travelList!=null&&travelList.size()>0){
							travelAdapter = new Search_TravelAdapter(SearchMoreListActivity.this,
									travelList);
							mListView.setAdapter(travelAdapter);
						}
						break;
					case 2://行程
						tripList.addAll(tripListMore);
						if(tripList!=null&&tripList.size()>0){
							tripAdapter = new Search_TripAdapter(SearchMoreListActivity.this,
									tripList);
							mListView.setAdapter(tripAdapter);
						}
						break;
					case 3://用户
						memberList.addAll(memberListMore);
						if(memberList!=null&&memberList.size()>0){
							memberAdapter = new Search_MemberAdapter(SearchMoreListActivity.this,
									memberList);
							mListView.setAdapter(memberAdapter);
						}
						break;
					default:
						break;
					}
				} else {
					switch (type) {
					case 1:
						travelList.addAll(travelListMore);
						travelAdapter.notifyDataSetChanged();
						break;
					case 2:
						tripList.addAll(tripListMore);
						tripAdapter.notifyDataSetChanged();
						break;
					case 3:
						memberList.addAll(memberListMore);
						memberAdapter.notifyDataSetChanged();
						break;
					default:
						break;
					}
					
				}
				page=page+1;
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && SearchMoreListActivity.this != null) {
				pd.dismiss();
			}
			if (SearchMoreListActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_searchmorelist);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		initGetIntent();
		getData(true);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				switch (type) {
				case 1:
					Intent intent=new Intent(SearchMoreListActivity.this,TravelDetailActivity.class);
					intent.putExtra("id", travelList.get(position-1).getId());
					startActivity(intent);
					break;
				case 2://
					Intent intent2=new Intent(SearchMoreListActivity.this,TripDetailActivity.class);
					intent2.putExtra("id", tripList.get(position-1).getId());
					startActivity(intent2);
					break;
				case 3://
					MasterModel mMasterModel=new MasterModel();
					mMasterModel.setId(memberList.get(position-1).getId());
					mMasterModel.setFace(memberList.get(position-1).getFace());
					mMasterModel.setSex(memberList.get(position-1).getSex());
					mMasterModel.setNickname(memberList.get(position-1).getNickname());
					mMasterModel.setAddress(memberList.get(position-1).getAddress());
					mMasterModel.setSignature(memberList.get(position-1).getSignature());
					mMasterModel.setIf_driver(memberList.get(position-1).getIf_driver());
					mMasterModel.setIf_guide(memberList.get(position-1).getIf_guide());
					Intent intent3=new Intent(SearchMoreListActivity.this,UserCenterActivity.class);
					intent3.putExtra("model", mMasterModel);
					startActivity(intent3);
					break;

				default:
					break;
				}
			}
		});
	}

	private void initGetIntent() {
		Intent intent=getIntent();
		content=intent.getStringExtra("content");
		type=intent.getExtras().getInt("type");
		
		switch (type) {
		case 1:
			title.setText("游记");
			break;
		case 2://
			title.setText("行程");
			break;
		case 3://
			title.setText("用户");
			break;
		}
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		
		mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
//		mListView.setMode(Mode.BOTH);
		mListView.setMode(Mode.DISABLED);
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
//		if(isConnect()){
//			if(!adapterList.isEmpty()){
//				adapterList.clear();
//				adapter.notifyDataSetChanged();
//			}
//			page=1;
//			getData(false);
//		}else{
//			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
//		}
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
					data.put("mid", sp.getString(XZContranst.id, ""));
					data.put("content", content);
					 data.put("page", page);
					data.put("type", type);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.search;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "搜索提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "搜索返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONObject js = jsonj.getJSONObject("data");
						switch (type) {
						case 1:
							JSONArray travelarry = js.optJSONArray("travels");
							if (travelarry!=null) {
								travelListMore = JSON.parseArray(travelarry.toString(),
										AllTravelModel.class);
							}
							break;
						case 2:
							JSONArray triparry = js.optJSONArray("trip");
							if (triparry!=null) {
								tripListMore = JSON.parseArray(triparry.toString(),
										AllTripModel.class);
							}
							break;
						case 3:
							JSONArray memberarry = js.optJSONArray("member");
							if (memberarry!=null) {
								memberListMore = JSON.parseArray(memberarry.toString(),
										MemberModel.class);
							}
							break;
						default:
							break;
						}

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
