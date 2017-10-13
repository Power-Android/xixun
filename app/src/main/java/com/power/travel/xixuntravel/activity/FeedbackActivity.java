package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * 意见反馈
 * 
 * @author fan
 * 
 */
public class FeedbackActivity extends BaseActivity {

	private ImageView back;
	private TextView title;
	private Button upload;
	private EditText mEditText;
	private String info, TAG = "LOG";
	SharedPreferences sp;
	private ProgressDialogUtils pd;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功
				ToastUtil.showToast(getApplicationContext(), info);
				finish();
			} else if (msg.what == 10) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && FeedbackActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_feedback);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "提交数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("意见与反馈");
		upload = (Button) findViewById(R.id.upload);
		mEditText = (EditText) findViewById(R.id.feedback_content);
	}

	private void initListener() {
		back.setOnClickListener(this);
		upload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == upload) {// 提交
			if (validate()) {
				upload();
			}
		}
	}

	private boolean validate() {

		if (TextUtils.isEmpty(mEditText.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请提出你宝贵的建议");
			return false;
		}

		if (!isConnect()) {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
			return false;
		}
		return true;
	}

	// 修改资料
	private void upload() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("content", mEditText.getText().toString());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.Service_feedback;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "意见反馈提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "意见反馈返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(10);
				} else {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}
}
