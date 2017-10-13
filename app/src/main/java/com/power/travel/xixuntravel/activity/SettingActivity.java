package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.Map;

import io.rong.imkit.RongIM;

/**
 * 设置
 * @author fan
 *
 */
public class SettingActivity extends BaseActivity {

	private ImageView back;
	private TextView title;
	private Button exit;
	private String data,TAG="RegistActivity",info;
	private RelativeLayout my_clear_cache,my_about_us,my_voice_setting,my_changepwd_setting;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what==1){//成功
				ToastUtil.showToast(getApplicationContext(), info);
			}else if(msg.what==0){//失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if(msg.what==-1){//其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}
			pd.dismiss();
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_setting);
		MyApplication.getInstance().addActivity(this);
		
		initView();
		initListener();
		
	}
	
	private void initView() {
		sp=this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		back=(ImageView)findViewById(R.id.back);
		title=(TextView)findViewById(R.id.title);
		title.setText("设置");
		exit=(Button)findViewById(R.id.exit);
		my_clear_cache=(RelativeLayout)findViewById(R.id.my_clear_cache);
		my_about_us=(RelativeLayout)findViewById(R.id.my_about_us);
		my_voice_setting=(RelativeLayout)findViewById(R.id.my_voice_setting);
		my_changepwd_setting=(RelativeLayout)findViewById(R.id.my_changepwd_setting);
		
		if(sp.getBoolean(XZContranst.if_login, false)){
			exit.setVisibility(View.VISIBLE);
		}else{
			exit.setVisibility(View.GONE);
		}
	}

	private void initListener() {
		back.setOnClickListener(this);
		exit.setOnClickListener(this);
		my_clear_cache.setOnClickListener(this);
		my_about_us.setOnClickListener(this);
		my_voice_setting.setOnClickListener(this);
		my_changepwd_setting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v==back){
			finish();
		}else if(v==exit){//退出

			Editor edit=sp.edit();
			edit.clear();
			edit.commit();
			UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.QQ, authListener);
			exit.setVisibility(View.GONE);
			 //


//			MyApplication.getInstance().delectActivity(MainActivity.class);
			SharedPreferences.Editor editors = getSharedPreferences("configa", MODE_PRIVATE).edit();
			editors.putBoolean("shouquan", true);
			
			  SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
              editor.putBoolean("exit", true);
              editor.putString("loginToken", "");
              editor.putString("loginid", "");
              editor.putString("loginnickname", "");
              editor.putString("loginPortrait", "");

              editor.apply();


              RongIM.getInstance().logout();
//              MyApplication.getInstance().exit();
              finish();
  			startActivity(new Intent(this,MainActivity.class));
//              try {
//                  Thread.sleep(500);
//                  android.os.Process.killProcess(android.os.Process.myPid());
//              } catch (InterruptedException e) {
//                  e.printStackTrace();
//              }
			
		}else if(v==my_clear_cache){//清理缓存
			startActivity(new Intent(this,ClearCashActivity.class));
		}else if(v==my_about_us){//关于我们
			startActivity(new Intent(this,AboutUsActivity.class));
		}else if(v==my_voice_setting){//声音通知
			startActivity(new Intent(this,SetSoundActivity.class));
		}else if(v==my_changepwd_setting){//修改密码
			startActivity(new Intent(this,ChangePwdActivity.class));
		}
	}
	//取消授权
	UMAuthListener authListener = new UMAuthListener() {
		/**
		 * @desc 授权开始的回调
		 * @param platform 平台名称
		 */
		@Override
		public void onStart(SHARE_MEDIA platform) {
//					SocializeUtils.safeShowDialog(dialog);
		}

		/**
		 * @desc 授权成功的回调
		 * @param platform 平台名称
		 * @param action 行为序号，开发者用不上
		 * @param data 用户资料返回
		 */
		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//					SocializeUtils.safeCloseDialog(dialog);
//					Toast.makeText(mContext, "成功了", Toast.LENGTH_LONG).show();
//					notifyDataSetChanged();
		}

		/**
		 * @desc 授权失败的回调
		 * @param platform 平台名称
		 * @param action 行为序号，开发者用不上
		 * @param t 错误原因
		 */
		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			//	SocializeUtils.safeCloseDialog(dialog);
			//Toast.makeText(mContext, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
		}

		/**
		 * @desc 授权取消的回调
		 * @param platform 平台名称
		 * @param action 行为序号，开发者用不上
		 */
		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			//SocializeUtils.safeCloseDialog(dialog);
			Toast.makeText(SettingActivity.this, "取消了", Toast.LENGTH_LONG).show();
		}
	};
}
