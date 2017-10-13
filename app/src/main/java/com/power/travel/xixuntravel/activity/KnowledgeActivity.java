package com.power.travel.xixuntravel.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.power.travel.xixuntravel.adapter.KnowledgeAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.KnowledgeModel;
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
 * 常识
 * 
 * @author fan
 * 
 */
public class KnowledgeActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView title;
	private String data, TAG = "KnowledgeActivity", info, mobile;
	private ProgressDialogUtils pd;
	private PullToRefreshListView mListView;
	List<KnowledgeModel> adapterList = new ArrayList<KnowledgeModel>();
	List<KnowledgeModel> adapterListMore = new ArrayList<KnowledgeModel>();
	KnowledgeAdapter adapter;
	int page = 1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new KnowledgeAdapter(KnowledgeActivity.this, adapterList);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page=page+1;
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && KnowledgeActivity.this != null) {
				pd.dismiss();
			}
			if (KnowledgeActivity.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_knowledge);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		if(isConnect()){
			getData(true);
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
	}

	private void initView() {
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("常识");
		mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
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
					data.put("catid", "9");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.knowledge;
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
					if(TextUtils.equals(status, "1")){
						JSONArray arry = jsonj.getJSONArray("data");
						adapterListMore = JSON.parseArray(arry.toString(),
								KnowledgeModel.class);
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
