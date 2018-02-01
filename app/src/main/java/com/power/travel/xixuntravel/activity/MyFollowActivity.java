package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.MyFollowAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.impl.MyFollowOnItemOnClickListener;
import com.power.travel.xixuntravel.model.FollowModel;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.PhoneModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.CharacterParser;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.PinyinComparator;
import com.power.travel.xixuntravel.utils.PinyinUtils;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.weight.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 我的关注
 * 
 * @author fan
 * 
 */
public class MyFollowActivity extends BaseActivity implements MyFollowOnItemOnClickListener {
	private ImageView back;
	private TextView title, dialog;
	private String data, TAG = "MyFollowActivity", info, mobile;
	private ProgressDialogUtils pd;
	private int delectID;
	ListView mListView;
	List<MasterModel> adapterList = new ArrayList<MasterModel>();
	SharedPreferences sp;
	SideBar sideBar;
	MyFollowAdapter adapter;
	 DisplayMetrics dm;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<PhoneModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功
				SourceDateList = filledData(adapterList);
				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new MyFollowAdapter(MyFollowActivity.this, SourceDateList,dm.widthPixels,adapterList);
				adapter.OnItemListener(MyFollowActivity.this);
				mListView.setAdapter(adapter);
			} else if (msg.what == 10) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {//取消关注成功
				SourceDateList.remove(delectID);
				adapter.notifyDataSetChanged();
			}else if (msg.what == -2) {//
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && MyFollowActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_follow);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		getData();
	}

	private void initView() {
		 dm = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
	
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("关注");
		mListView = (ListView) findViewById(R.id.follow_listview);
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
	}

	private void initListener() {
		back.setOnClickListener(this);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mListView.setSelection(position);
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}
	}
	
	@Override
	public void OnItemClick(int typeint, int position) {
		switch (typeint) {
		case 1://取消关注
			delectID=position;
			LogUtil.e(TAG,"------------"+SourceDateList.get(position).getUserID());
			Follow_cancel(SourceDateList.get(position).getUserID());
			break;

		default:
			break;
		}
		
	}

	/**
	 * 为ListView填充数据
	 * 
	 *
	 * @return
	 */
	private List<PhoneModel> filledData(List<MasterModel> list) {
		List<PhoneModel> mSortList = new ArrayList<PhoneModel>();

		for (int i = 0; i < list.size(); i++) {
			PhoneModel sortModel = new PhoneModel();
			sortModel.setImgSrc(list.get(i).getFace());
			sortModel.setName(list.get(i).getNickname());
//			sortModel.setUserID(list.get(i).getId());
//			sortModel.setUserID(list.get(i).getFid());
			sortModel.setUserID(list.get(i).getFollow_id());

			// 汉字转换成拼音
			/*String pinyin = characterParser.getSelling(list.get(i)
					.getNickname());*/
			String pinyin = PinyinUtils.getPingYin(list.get(i)
					.getNickname());
			String sortString = null;
			if(!TextUtils.isEmpty(list.get(i).getNickname())){
				 sortString = pinyin.substring(0, 1).toUpperCase();
			}else{
				sortString="#";
			}
			
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	//获取数据
	private void getData() {
		pd = ProgressDialogUtils.show(this, "加载数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.my_follow;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "关注列表提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "关注列表返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						adapterList = JSON.parseArray(arry.toString(),
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
	
	// 取消关注
		private void Follow_cancel(final String fid) {
			pd = ProgressDialogUtils.show(this, "提交数据...");
			pd.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					JSONObject data = new JSONObject();
					try {
						data.put("mid", sp.getString(XZContranst.id, null));
						data.put("id", fid);
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
