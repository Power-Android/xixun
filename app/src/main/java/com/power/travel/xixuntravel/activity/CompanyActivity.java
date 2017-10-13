package com.power.travel.xixuntravel.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 公司简介
 * @author fan
 *
 */
public class CompanyActivity extends BaseActivity {

	private ImageView back;
	private TextView title,company_content;
	private String info,TAG="CompanyActivity",content;
	private ProgressDialogUtils pd;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				company_content.setText(Html.fromHtml(content));
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && CompanyActivity.this != null) {
				pd.dismiss();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_company);
		MyApplication.getInstance().addActivity(this);
		
		initView();
		initListener();
		if(isConnect()){
			getData();
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
	}
	
	private void initView() {
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("公司简介");
		company_content = (TextView) findViewById(R.id.company_content);

	}



	private void initListener() {
		back.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v==back){
			finish();
		}
	}
	
	private void getData() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("catid", "5");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.Service_about;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "获取公司简介提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "获取公司简介返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					JSONObject js=jsonj.getJSONObject("data");
					content=js.getString("content");
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
