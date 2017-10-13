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
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.AreaAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.AreaModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 省市区
 * 
 * @author fan
 * 
 */
public class AreaActivity extends BaseActivity implements OnItemClickListener {

	private ImageView back;
	private TextView title;
	private ListView mListview;
	private List<AreaModel> adapterList = new ArrayList<AreaModel>();
	AreaAdapter adapter;
	private String info, TAG = AreaActivity.class.getSimpleName(),province, province_id, city, city_id, country,
			country_id;
	SharedPreferences sp;
	private ProgressDialogUtils pd;
	int clickType = 1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功
				switch (clickType) {
				case 1:// 省

					break;
				case 2:// 市
					title.setText("市");
					break;
				case 3:// 区
					title.setText("区");
					break;
				default:
					break;
				}
				adapter = new AreaAdapter(AreaActivity.this, adapterList);
				mListview.setAdapter(adapter);
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && AreaActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_area);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		if (isConnect()) {
			getData("1");
		} else {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}

		// mListview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// if(isConnect()){
		// getData(adapterList.get(position).getId());
		// }else{
		// ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		// }
		// }
		// });
	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("省");
		mListview = (ListView) findViewById(R.id.area_listview);
		mListview.setOnItemClickListener(this);
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

		switch (clickType) {
		case 1:// 省
			clickType=2;
			province=adapterList.get(position).getName();
			province_id=adapterList.get(position).getId();
			if (isConnect()) {
				getData(adapterList.get(position).getId());
			} else {
				ToastUtil
						.showToast(getApplicationContext(), XZContranst.no_net);
			}
			break;
		case 2:// 市
			clickType=3;
			city=adapterList.get(position).getName();
			city_id=adapterList.get(position).getId();
			if (isConnect()) {
				getData(adapterList.get(position).getId());
			} else {
				ToastUtil
						.showToast(getApplicationContext(), XZContranst.no_net);
			}
			break;
		case 3:// 区
			country=adapterList.get(position).getName();
			country_id=adapterList.get(position).getId();
			Intent intent=new Intent();
			intent.putExtra("province", province);
			intent.putExtra("province_id", province_id);
			intent.putExtra("city", city);
			intent.putExtra("city_id", city_id);
			intent.putExtra("country", country);
			intent.putExtra("country_id", country_id);
			setResult(101, intent);
			finish();
			break;
		default:
			break;
		}
	}

	private void getData(final String upid) {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("upid", upid);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.Service_province;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "省市区信息提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;
				adapterList.clear();
				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "省市区信息返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					org.json.JSONArray arry = jsonj.getJSONArray("data");
					adapterList = JSON.parseArray(arry.toString(),
							AreaModel.class);

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
