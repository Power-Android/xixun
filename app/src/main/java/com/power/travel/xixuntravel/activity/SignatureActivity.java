package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 个性签名
 * @author fan
 *
 */
public class SignatureActivity extends BaseActivity implements TextWatcher {

	private ImageView back;
	private TextView title,signature_number;
	private Button upload;
	private EditText signature_ed;
	private String info,TAG;
	SharedPreferences sp;
	private ProgressDialogUtils pd;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功
				Editor editor = sp.edit();
				editor.putString(XZContranst.signature, signature_ed.getText().toString());// 职位
				editor.commit();

				ToastUtil.showToast(getApplicationContext(), info);

				Intent intent = new Intent();
				intent.putExtra("name", signature_ed.getText().toString());
				setResult(101, intent);
				finish();
			} else if (msg.what == 10) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && SignatureActivity.this != null) {
				pd.dismiss();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_signature);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		initGetIntent();
	}
	
	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "提交数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("个性签名");
		upload=(Button)findViewById(R.id.upload);
		signature_ed=(EditText)findViewById(R.id.signature_ed);
		signature_number= (TextView) findViewById(R.id.signature_number);
	}

	private void initListener() {
		back.setOnClickListener(this);
		upload.setOnClickListener(this);
		signature_ed.addTextChangedListener(this);
	}
	private void initGetIntent() {
		Intent intent=getIntent();
		signature_ed.setText(intent.getStringExtra("name"));
		if(!TextUtils.isEmpty(intent.getStringExtra("name"))){
			signature_ed.setSelection(signature_ed.length());
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}else if(v==upload){//提交
			if (isConnect()) {
			editInfo(7, signature_ed.getText().toString());
			} else {
				ToastUtil.showToast(getApplicationContext(), getResources()
						.getString(R.string.notnetwork));
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// 更改后
	if(s.length()<=100){
		signature_number.setText(String.valueOf(s.length())+"/100");
	}else{
		ToastUtil.showToast(getApplicationContext(), "字数超过限制！");
	}
	}
	
	// 修改资料
		private void editInfo(final int i, final String key) {
			pd.show();
			new Thread(new Runnable() {

				@Override
				public void run() {

					JSONObject data = StringUtils.setDataJSON(
							sp.getString(XZContranst.id, null), i, key,"","","");
					String url = HttpUrl.edit_info;
					String json = StringUtils.setJSON(data);

					LogUtil.e(TAG, "修改资料提交的数据" + json);
					String request = HttpClientPostUpload.Upload(json, url);

					JSONObject jsonj = null;
					String status = null;

					try {
						jsonj = new JSONObject(request);
						LogUtil.e(TAG, "修改资料返回的数据" + jsonj.toString());

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
