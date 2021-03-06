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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 修改密码
 */
public class ChangePwdActivity extends BaseActivity {

	private ImageView back;
	private TextView title;
	private Button regist_upload;
	private EditText regist_oldpwd,regist_setpwd, regist_setpwd2;
	private String data, TAG = "Forget_SetPwdActivity", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				ToastUtil.showToast(getApplicationContext(), info);
	              finish();
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && ChangePwdActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_changepwd);
		MyApplication.getInstance().addActivity(this);

//		initGetIntent();
		initView();
		initListener();
	}

//	private void initGetIntent() {
//		Intent intent = getIntent();
//		if (intent.hasExtra("mobile")) {
//			mobile = intent.getStringExtra("mobile");
//		}
//	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "提交数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("修改密码");
		regist_upload = (Button) findViewById(R.id.regist_upload);
		regist_oldpwd= (EditText) findViewById(R.id.regist_oldpwd);
		regist_setpwd = (EditText) findViewById(R.id.regist_setpwd);
		regist_setpwd2 = (EditText) findViewById(R.id.regist_setpwd2);
	}

	private void initListener() {
		back.setOnClickListener(this);
		regist_upload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == regist_upload) {
			if (validate()) {
				upload();
			}
		}
	}

	/**
	 * 是否是纯数字或者纯英文
	 * @param psd
	 * @return
	 */
	public static boolean ispsd(String psd) {
		Pattern p = Pattern
				.compile("^[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]");
		Matcher m = p.matcher(psd);

		return m.matches();
	}

	private boolean validate() {
		String pwd = regist_setpwd.getText().toString();

		if (ispsd(pwd) == false){
			regist_setpwd.setFocusableInTouchMode(true);
			regist_setpwd.requestFocus();
			regist_setpwd.findFocus();
			showEnsure("密码格式不正确！");
			return false;
		}

		/*Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(pwd);
		Pattern o=Pattern.compile("[a-zA-Z]");
		Matcher n =o.matcher(pwd);
		if(m.matches() ){
			regist_setpwd.setFocusableInTouchMode(true);
			regist_setpwd.requestFocus();
			regist_setpwd.findFocus();
			showEnsure("密码格式不正确！");
			return false;
		}
		if(n.matches() == false){
			regist_setpwd.setFocusableInTouchMode(true);
			regist_setpwd.requestFocus();
			regist_setpwd.findFocus();
			LogUtil.e(TAG,"------------------------------------");
			showEnsure("密码格式不正确！");
			return false;
		}*/
		if (TextUtils.isEmpty(regist_oldpwd.getText().toString())) {
			regist_oldpwd.setFocusableInTouchMode(true);
			regist_oldpwd.requestFocus();
			regist_oldpwd.findFocus();
			showEnsure("请输入新密码!");
			return false;
		}
		if (TextUtils.isEmpty(regist_setpwd.getText().toString())) {
			regist_setpwd.setFocusableInTouchMode(true);
			regist_setpwd.requestFocus();
			regist_setpwd.findFocus();
			showEnsure("请输入新密码!");
			return false;
		}
		if (TextUtils.isEmpty(regist_setpwd2.getText().toString())) {
			regist_setpwd2.setFocusableInTouchMode(true);
			regist_setpwd2.requestFocus();
			regist_setpwd2.findFocus();
			showEnsure("请确认新密码!");
			return false;
		}
		if (!TextUtils.equals(regist_setpwd.getText().toString(),
				regist_setpwd2.getText().toString())) {
			regist_setpwd2.setFocusableInTouchMode(true);
			regist_setpwd2.requestFocus();
			regist_setpwd2.findFocus();
			regist_setpwd2.getText().clear();
			showEnsure("两次密码不相等!");
			return false;
		}
		if (regist_setpwd.getText().toString().trim().length() < 6 && regist_setpwd.getText().toString().trim().length() > 16){
			regist_setpwd2.setFocusableInTouchMode(true);
			regist_setpwd2.requestFocus();
			regist_setpwd2.findFocus();
			showEnsure("请输入6-16位密码");
			return false;
		}
		if (!isConnect()) {
			ToastUtil.showToast(getApplication(),
					getResources().getString(R.string.notnetwork));
			return false;
		}
		return true;
	}

	private void upload() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, ""));
					data.put("OldPass", regist_oldpwd.getText().toString());
					data.put("NewPass", regist_setpwd.getText()
							.toString());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.changepwd;
				String json= StringUtils.setJSON(data);
				
				LogUtil.e(TAG, "找回密码提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "找回密码返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
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
