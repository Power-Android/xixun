package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.HistoryWordsAdapter;
import com.power.travel.xixuntravel.adapter.Search_MemberAdapter;
import com.power.travel.xixuntravel.adapter.Search_TravelAdapter;
import com.power.travel.xixuntravel.adapter.Search_TripAdapter;
import com.power.travel.xixuntravel.adapter.TypeAdapter;
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
import com.power.travel.xixuntravel.weight.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索
 * 
 * @author fan
 * 
 */

public class SearchActivity extends BaseActivity {

	private ImageView back, search_delect;
	private TextView back_tv, history_clear, travelmore, tripmore, membermore,type_tv;
	private EditText content_ed;
	private String data, TAG = "SearchActivity", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	private LinearLayout travel_layout, trip_layout, member_layout;
	private MyListView travelListView, tripListView, memberListView;
	List<AllTravelModel> travelList = new ArrayList<AllTravelModel>();
	Search_TravelAdapter travelAdapter;
	List<AllTripModel> tripList = new ArrayList<AllTripModel>();
	Search_TripAdapter tripAdapter;
	List<MemberModel> memberList = new ArrayList<MemberModel>();
	Search_MemberAdapter memberAdapter;
	int typeID = 0;
	List<String> historyWordsList = new ArrayList<String>();
	MyListView history_listview;
	private LinearLayout history_layout;
	HistoryWordsAdapter historyAdapter;
	private PopupWindow popupWindow;
	ListView typeListView;
	TypeAdapter typeAdapter;
	String[] typelist={"全部","游记","行程","用户"};
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				if (history_layout.getVisibility() == 0) {
					history_layout.setVisibility(View.GONE);
				}
				if (travelList != null && travelList.size() > 0) {
					travel_layout.setVisibility(View.VISIBLE);
					travelAdapter = new Search_TravelAdapter(
							SearchActivity.this, travelList);
					travelListView.setAdapter(travelAdapter);
				}

				if (tripList != null && tripList.size() > 0) {
					trip_layout.setVisibility(View.VISIBLE);
					tripAdapter = new Search_TripAdapter(SearchActivity.this,
							tripList);
					tripListView.setAdapter(tripAdapter);
				}

				if (memberList != null && memberList.size() > 0) {
					member_layout.setVisibility(View.VISIBLE);
					memberAdapter = new Search_MemberAdapter(
							SearchActivity.this, memberList);
					memberListView.setAdapter(memberAdapter);
				}

			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && SearchActivity.this != null) {
				pd.dismiss();
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();

		if (!TextUtils.isEmpty(sp.getString("historyWords", ""))) {// 有历史搜索记录
			history_layout.setVisibility(View.VISIBLE);
			for (int i = 0; i < sp.getString("historyWords", "").split(",").length; i++) {
				historyWordsList.add(sp.getString("historyWords", "")
						.split(",")[i]);
			}
			history_listview.setAdapter(historyAdapter);
		}

		history_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

				content_ed.setText(historyWordsList.get(position));
				getData();
			}
		});

		content_ed.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if (arg1 == EditorInfo.IME_ACTION_SEARCH
						|| (arg2 != null && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

					if (!TextUtils.isEmpty(content_ed.getText().toString())) {
						getData();
					} else {
						search_delect.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
		
		travelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent=new Intent(SearchActivity.this,TravelDetailActivity.class);
				intent.putExtra("id", travelList.get(position).getId());
				startActivity(intent);
			}
		});
		
		tripListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent=new Intent(SearchActivity.this,TripDetailActivity.class);
				intent.putExtra("id", tripList.get(position).getId());
				intent.putExtra("mid", tripList.get(position).getMid());
				startActivity(intent);
			}
		});
		
		memberListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				MasterModel mMasterModel=new MasterModel();
				mMasterModel.setId(memberList.get(position).getId());
				mMasterModel.setFace(memberList.get(position).getFace());
				mMasterModel.setSex(memberList.get(position).getSex());
				mMasterModel.setNickname(memberList.get(position).getNickname());
				mMasterModel.setAddress(memberList.get(position).getAddress());
				mMasterModel.setSignature(memberList.get(position).getSignature());
				mMasterModel.setIf_driver(memberList.get(position).getIf_driver());
				mMasterModel.setIf_guide(memberList.get(position).getIf_guide());
				Intent intent=new Intent(SearchActivity.this,UserCenterActivity.class);
				intent.putExtra("model", mMasterModel);
				startActivity(intent);
			}
		});
	}

	private void initView() {
		pd = ProgressDialogUtils.show(this, "加载数据...");
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		back = (ImageView) findViewById(R.id.back);
		search_delect = (ImageView) findViewById(R.id.search_shanchu);
		content_ed = (EditText) findViewById(R.id.search);
		travelListView = (MyListView) findViewById(R.id.travelListView);
		history_layout = (LinearLayout) findViewById(R.id.history_layout);
		history_listview = (MyListView) findViewById(R.id.history_listview);
		historyAdapter = new HistoryWordsAdapter(this, historyWordsList);
		history_clear = (TextView) findViewById(R.id.history_clear);
		travel_layout = (LinearLayout) findViewById(R.id.travel_layout);
		travelmore = (TextView) findViewById(R.id.travelmore);
		trip_layout = (LinearLayout) findViewById(R.id.trip_layout);
		tripListView = (MyListView) findViewById(R.id.tripListView);
		tripmore = (TextView) findViewById(R.id.tripmore);
		member_layout = (LinearLayout) findViewById(R.id.member_layout);
		memberListView = (MyListView) findViewById(R.id.memberListView);
		membermore = (TextView) findViewById(R.id.membermore);
		back_tv = (TextView) findViewById(R.id.back_tv);
		type_tv= (TextView) findViewById(R.id.type_tv);
	}

	private void initListener() {
		back.setOnClickListener(this);
		search_delect.setOnClickListener(this);
		history_clear.setOnClickListener(this);
		travelmore.setOnClickListener(this);
		tripmore.setOnClickListener(this);
		membermore.setOnClickListener(this);
		back_tv.setOnClickListener(this);
		type_tv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == back_tv) {
			finish();
		} else if (v == search_delect) {//
			content_ed.getText().clear();
			search_delect.setVisibility(View.INVISIBLE);
		} else if (v == history_clear) {// 清除历史记录
			Editor edit = sp.edit();
			edit.putString("historyWords", "");
			edit.commit();
			history_layout.setVisibility(View.GONE);
		} else if (v == travelmore) {// 游记查看更多
			Intent intent=new Intent(SearchActivity.this,SearchMoreListActivity.class);
			intent.putExtra("type", 1);
			intent.putExtra("content", content_ed.getText().toString());
			startActivity(intent);
		} else if (v == tripmore) {// 行程查看更多
			Intent intent=new Intent(SearchActivity.this,SearchMoreListActivity.class);
			intent.putExtra("type", 2);
			intent.putExtra("content", content_ed.getText().toString());
			startActivity(intent);
		} else if (v == membermore) {// 用户查看更多
			Intent intent=new Intent(SearchActivity.this,SearchMoreListActivity.class);
			intent.putExtra("type", 3);
			intent.putExtra("content", content_ed.getText().toString());
			startActivity(intent);
		}else if(v==type_tv){//类型
			initmPopupWindowView();
			popupWindow.showAsDropDown(type_tv, 0, 0);
		}
	}
	
	private void initmPopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_type_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		popupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		popupWindow.setFocusable(true);
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
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow.dismiss();
					return true;
				}

				return false;
			}

		});
		
		typeListView = (ListView) customView
					.findViewById(R.id.list_type);
		typeAdapter = new TypeAdapter(SearchActivity.this,
				typelist);
		typeListView.setAdapter(typeAdapter);


			typeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				type_tv.setText(typelist[position]);
				typeID=position;
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	private void getData() {
		search_delect.setVisibility(View.VISIBLE);
		if (travelList != null && travelList.size() > 0) {
			travel_layout.setVisibility(View.GONE);
			travelList.clear();
			travelAdapter.notifyDataSetChanged();
		}

		if (tripList != null && tripList.size() > 0) {
			trip_layout.setVisibility(View.GONE);
			tripList.clear();
			tripAdapter.notifyDataSetChanged();
		}
		if (memberList != null && memberList.size() > 0) {
			member_layout.setVisibility(View.GONE);
			memberList.clear();
			memberAdapter.notifyDataSetChanged();
		}

		String words = content_ed.getText().toString();
		Editor edit = sp.edit();
		if (!TextUtils.isEmpty(sp.getString("historyWords", ""))) {// 不为空
			boolean deng = false;
			for (int i = 0; i < historyWordsList.size(); i++) {
				if (TextUtils.equals(words, historyWordsList.get(i))) {
					deng = true;
				}
			}
			if (!deng) {
				edit.putString("historyWords", sp.getString("historyWords", "")
						+ "," + words);
			}
		} else {// 为空
			edit.putString("historyWords", words);
		}
		edit.commit();
		searchData(true);
	}

	private void searchData(boolean ifshow) {
		if (ifshow) {
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, ""));
					data.put("content", content_ed.getText().toString());
					// data.put("page", page);
					data.put("type", typeID);
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
						JSONArray travelarry = js.optJSONArray("travels");
						JSONArray triparry = js.optJSONArray("trip");
						JSONArray memberarry = js.optJSONArray("member");

						if (travelarry != null) {
							travelList = JSON.parseArray(travelarry.toString(),
									AllTravelModel.class);
						}
						if (triparry != null) {
							tripList = JSON.parseArray(triparry.toString(),
									AllTripModel.class);
						}

						if (memberarry != null) {
							memberList = JSON.parseArray(memberarry.toString(),
									MemberModel.class);
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
