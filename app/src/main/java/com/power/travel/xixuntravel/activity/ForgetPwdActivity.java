package com.power.travel.xixuntravel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

/**
 * 忘记密码  获取验证码
 * @author fan
 *
 */
public class ForgetPwdActivity extends BaseActivity {

	private ImageView back;
	private TextView title,forgetpwd_getcode;
	private EditText forgetpwd_account,forgetpwd_code;
	private Button forgetpwd_next;
	ArrayList<String> List = new ArrayList<String>();
	private String data,TAG="RegistActivity",info;
	private ProgressDialogUtils pd;
	private TimeCount time;// 重新获取倒计时
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what==1){//成功
				ToastUtil.showToast(getApplicationContext(), info);
				time.start();// 开始计时
			}else if(msg.what==0){//失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if(msg.what==-1){//其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && ForgetPwdActivity.this != null) {
				pd.dismiss();
			}
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_forgetpwd);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		Random();
	}
	
	private void initView() {
		pd=ProgressDialogUtils.show(this, "获取数据...");
		back=(ImageView)findViewById(R.id.back);
		title=(TextView)findViewById(R.id.title);
		title.setText("找回密码");
		forgetpwd_getcode=(TextView)findViewById(R.id.forgetpwd_getcode);
		forgetpwd_account=(EditText)findViewById(R.id.forgetpwd_account);
		forgetpwd_code=(EditText)findViewById(R.id.forgetpwd_code);
		forgetpwd_next=(Button)findViewById(R.id.forgetpwd_next);
		time = new TimeCount(50000, 1000);// 构造CountDownTimer对象
	}

	private void initListener() {
		back.setOnClickListener(this);
		forgetpwd_getcode.setOnClickListener(this);
		forgetpwd_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v==back){
			finish();
		}else if(v==forgetpwd_getcode){//获取验证码
			if(TextUtils.isEmpty(forgetpwd_account.getText().toString())){
				forgetpwd_account.setFocusableInTouchMode(true);
				forgetpwd_account.requestFocus();
				forgetpwd_account.findFocus();
				showEnsure("请输入手机号!");
			}else{
				getCode();
			}
		}else if(v==forgetpwd_next){//下一步
			if(validate()){
				Intent intent = new Intent(this,
						Forget_SetPwdActivity.class);
				intent.putExtra("mobile", forgetpwd_account.getText().toString());
				startActivityForResult(intent, 1);
			}
		}
	}
	
	private boolean validate() {
		if (TextUtils.isEmpty(forgetpwd_account.getText().toString())) {
			forgetpwd_account.setFocusableInTouchMode(true);
			forgetpwd_account.requestFocus();
			forgetpwd_account.findFocus();
			showEnsure("请输入手机号!");
			return false;
		}

		if (TextUtils.isEmpty(forgetpwd_code.getText().toString())) {
			forgetpwd_code.setFocusableInTouchMode(true);
			forgetpwd_code.requestFocus();
			forgetpwd_code.findFocus();
			showEnsure("请输入验证码!");
			return false;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case 1:
			if(data!=null){
				Intent intent = new Intent();
				intent.putExtra("mobile", forgetpwd_account.getText().toString());
				intent.putExtra("password", data.getStringExtra("password"));
				setResult(101, intent);
				finish();
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 生成随机数
	 */
	private void Random() {
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			int randomInt = random.nextInt(9);
			System.out.println("生成的randomInt=" + randomInt);
			List.add(String.valueOf(randomInt));
		}
	}

	
	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			forgetpwd_getcode.setText("获取验证码");
			forgetpwd_getcode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			forgetpwd_getcode.setClickable(false);
			forgetpwd_getcode.setText( millisUntilFinished / 1000 + "秒"+"重新获取");
		}
	}
	
	// 获取短信验证码
		private void getCode() {
			pd.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					JSONObject data = new JSONObject();
					try {
						data.put("mobile", forgetpwd_account.getText().toString());
						data.put("code", List.get(0) + List.get(1) + List.get(2)
								+ List.get(3) + List.get(4) + List.get(5));
						data.put("type", "forgetpwd");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					String url = HttpUrl.sendmessages;
					String json = StringUtils.setJSON(data);

					LogUtil.e(TAG, "注册提交的数据" + json);
					String request = HttpClientPostUpload.Upload(json, url);

					JSONObject jsonj = null;
					String status = null;

					try {
						jsonj = new JSONObject(request);
						LogUtil.e(TAG, "注册返回的数据" + jsonj.toString());

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
