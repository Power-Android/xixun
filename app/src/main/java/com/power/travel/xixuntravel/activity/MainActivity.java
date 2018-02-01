package com.power.travel.xixuntravel.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.power.travel.xixuntravel.BuildConfig;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.fragment.PersionalCenterFragment;
import com.power.travel.xixuntravel.fragment.RecommendFragment;
import com.power.travel.xixuntravel.fragment.ServiceFragment;
import com.power.travel.xixuntravel.fragment.YueBanFragment;
import com.power.travel.xixuntravel.model.Friend;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.recordvideo.RecordVideoActivity;
import com.power.travel.xixuntravel.utils.Dialog_Update;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.UserData;
import com.power.travel.xixuntravel.utils.XZContranst;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;


public class MainActivity extends BaseActivity implements RongIM.UserInfoProvider {

	private long exitTime = 0;
	private LinearLayout tab_yueban_layout, tab_recomment_layout,
			tab_service_layout, tab_persionalcenter_layout;
	private ImageView tab_yueban_iv, tab_recomment_iv, tab_service_iv,
			tab_persionalcenter_iv, tab_public;
	private TextView tab_yueban_tv, tab_recomment_tv, tab_service_tv,
			tab_persionalcenter_tv;

	private FragmentManager fragmentmanager;// 管理fragmentmanager
	private FragmentTransaction transaction;// 开启一个Fragment事务

	YueBanFragment mYueBanFragment;

	RecommendFragment mRecommendFragment;
	ServiceFragment mServiceFragment;
	PersionalCenterFragment mPersionalCenterFragment;

	SharedPreferences sp;
	private String mycenter = "mycenter";
	PopupWindow popupWindow;
	String TAG="MainActivity",newestversion,newesturi,newestdescription;
	private List<Friend> userIdList;
	private Dialog_Update dialog_Update;
	private int listtype=1;// 1约伴（行程）2游记

	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 可以更新软件
				if(MainActivity.this!=null){
					UserData.newapkcode = newestversion;
					UserData.apkUrl=newesturi;
					
					String title="检测到最新版本,立即更新吗?";
					String[] strArr=newestdescription.split(";");
					int i=strArr.length;
					ArrayList<String> list=new ArrayList<String>();
					for(int j=0;j<i;j++){
						list.add(strArr[j]);
					}
					dialog_Update=new Dialog_Update(MainActivity.this,title,list);
					dialog_Update.show();
				}
			} else{
				
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyApplication.getInstance().addActivity(this);
		initView();
		try {
			EventBus.getDefault().register(this);
		} catch (Exception e) {
			Log.e("", "EventBus注册错误" + e.toString());
		}
		if (sp.getBoolean(XZContranst.if_login, false)) {
			LogUtil.e(TAG, "token得知"+sp.getString(XZContranst.token, ""));
			if(!TextUtils.isEmpty(sp.getString(XZContranst.token, ""))){
				connectRongServer(sp.getString(XZContranst.token, ""));
			}
		}
		setTabSelection(0);//进入页面后显示约伴fragment
		

		GetVersion();
		
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		tab_yueban_layout = (LinearLayout) findViewById(R.id.tab_yueban_layout);
		tab_yueban_layout.setOnClickListener(this);
		tab_recomment_layout = (LinearLayout) findViewById(R.id.tab_recomment_layout);
		tab_recomment_layout.setOnClickListener(this);
		tab_service_layout = (LinearLayout) findViewById(R.id.tab_service_layout);
		tab_service_layout.setOnClickListener(this);
		tab_persionalcenter_layout = (LinearLayout) findViewById(R.id.tab_persionalcenter_layout);
		tab_persionalcenter_layout.setOnClickListener(this);
		tab_public = (ImageView) findViewById(R.id.tab_public);
		tab_public.setOnClickListener(this);
		tab_yueban_iv = (ImageView) findViewById(R.id.tab_yueban_iv);
		tab_yueban_tv = (TextView) findViewById(R.id.tab_yueban_tv);
		tab_recomment_iv = (ImageView) findViewById(R.id.tab_recomment_iv);
		tab_recomment_tv = (TextView) findViewById(R.id.tab_recomment_tv);
		tab_service_iv = (ImageView) findViewById(R.id.tab_service_iv);
		tab_service_tv = (TextView) findViewById(R.id.tab_service_tv);
		tab_persionalcenter_iv = (ImageView) findViewById(R.id.tab_persionalcenter_iv);
		tab_persionalcenter_tv = (TextView) findViewById(R.id.tab_persionalcenter_tv);
		fragmentmanager = getSupportFragmentManager();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == tab_yueban_layout) {
			setTabSelection(0);
		} else if (v == tab_recomment_layout) {
			setTabSelection(1);
		} else if (v == tab_service_layout) {
			setTabSelection(2);
		} else if (v == tab_persionalcenter_layout) {
			if (sp.getBoolean(XZContranst.if_login, false)) {
				setTabSelection(3);
			} else {
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra("type", mycenter);
				startActivityForResult(intent, 1);
			}
		} else if (v == tab_public) {// 发布
			if (sp.getBoolean(XZContranst.if_login, false)) {
				initPopupWindow();
				popupWindow.showAtLocation(tab_public, Gravity.BOTTOM, 0, 0);
				// new mPopupWindow(this, tab_public);
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			switch (requestCode) {
			case 1:// 从我的中心调到登陆
				if (TextUtils.equals(data.getStringExtra("type"), mycenter)) {
					setTabSelection(3);
				}
				break;

			default:
				break;
			}
		}
	}

	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	 /*if(keyCode== KeyEvent.KEYCODE_BACK){
	 	this.overridePendingTransition(R.anim.alpha_exit,R.anim.top_to_bottom);
	 	Log.e("..........", "结束。。。。。。。。");
	 	this.finish();
	 	return true;
	 	}else{
	 		return super.onKeyDown(keyCode, event);
	 	}*/
		 if (keyCode == KeyEvent.KEYCODE_BACK
				 && event.getAction() == KeyEvent.ACTION_DOWN) {
			 if (System.currentTimeMillis() - exitTime > 2000) {
				 ToastUtil.showToast(MainActivity.this, "再按一次退出程序");
				 exitTime = System.currentTimeMillis();
			 }else{
				 MyApplication.getInstance().exit();
			 }
			 return true;
		 }
		 return super.onKeyDown(keyCode, event);
	 }

	/**
	 * 处理tab
	 * 
	 * @param index
	 */
	@SuppressWarnings("deprecation")
	private void setTabSelection(int index) {
		clearSelection();// 每次加载前先清理上次的选中状
		transaction = fragmentmanager.beginTransaction();// 开启一个Fragment事务
		hideFragments(transaction);// 隐藏所有fragment,防止多个fragment同时出现
		switch (index) {
		case 0:// 选中第一个
			tab_yueban_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.tab_yueban_select));
			tab_yueban_tv.setTextColor(this.getResources().getColor(
					R.color.tab_text_select_color));
			if (mYueBanFragment == null) {// 为空，创建一个并添加到界面
				mYueBanFragment = new YueBanFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("key", listtype);  
				mYueBanFragment.setArguments(bundle);  
				transaction.add(R.id.tab_content, mYueBanFragment);
			} else {// 不为空，直接显示在界面
				transaction.show(mYueBanFragment);
			}
			break;
		case 1:// 第二个
			tab_recomment_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.tab_recomment_select));
			tab_recomment_tv.setTextColor(this.getResources().getColor(
					R.color.tab_text_select_color));
			if (mRecommendFragment == null) {// 为空，创建一个并添加到界面
				mRecommendFragment = new RecommendFragment();
				transaction.add(R.id.tab_content, mRecommendFragment);
			} else {// 不为空，直接显示在界面
				transaction.show(mRecommendFragment);
			}
			break;

		case 2:// 第三个
			tab_service_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.tab_service_select));
			tab_service_tv.setTextColor(this.getResources().getColor(
					R.color.tab_text_select_color));
			if (mServiceFragment == null) {// 为空，创建一个并添加到界面
				mServiceFragment = new ServiceFragment();
				transaction.add(R.id.tab_content, mServiceFragment);
			} else {// 不为空，直接显示在界面
				transaction.show(mServiceFragment);
			}
			break;

		case 3:// 第4个

			tab_persionalcenter_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.tab_persionalcenter_select));
			tab_persionalcenter_tv.setTextColor(this.getResources().getColor(
					R.color.tab_text_select_color));
			if (mPersionalCenterFragment == null) {// 为空，创建一个并添加到界面
				mPersionalCenterFragment = new PersionalCenterFragment();
				transaction.add(R.id.tab_content, mPersionalCenterFragment);
			} else {// 不为空，直接显示在界面
				transaction.show(mPersionalCenterFragment);
			}
			break;
		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}//

	/**
	 * 清理tab上次的选中状态
	 */
	private void clearSelection() {
		tab_yueban_iv.setImageDrawable(getResources().getDrawable(
				R.drawable.tab_yueban_normal));
		tab_yueban_tv.setTextColor(this.getResources().getColor(
				R.color.tab_text_normal_color));
		tab_recomment_iv.setImageDrawable(getResources().getDrawable(
				R.drawable.tab_recomment_normal));
		tab_recomment_tv.setTextColor(this.getResources().getColor(
				R.color.tab_text_normal_color));
		tab_service_iv.setImageDrawable(getResources().getDrawable(
				R.drawable.tab_service_normal));
		tab_service_tv.setTextColor(this.getResources().getColor(
				R.color.tab_text_normal_color));
		tab_persionalcenter_iv.setImageDrawable(getResources().getDrawable(
				R.drawable.tab_persionalcenter_normal));
		tab_persionalcenter_tv.setTextColor(this.getResources().getColor(
				R.color.tab_text_normal_color));
	}//

	/**
	 * 隐藏所有fragment
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (mYueBanFragment != null) {
			transaction.hide(mYueBanFragment);
		}
		if (mRecommendFragment != null) {
			transaction.hide(mRecommendFragment);
		}
		if (mServiceFragment != null) {
			transaction.hide(mServiceFragment);
		}

		if (mPersionalCenterFragment != null) {
			transaction.hide(mPersionalCenterFragment);
		}
	}

	// 选择行程，游记，还是短视频
	private void initPopupWindow() {
		View customView = getLayoutInflater().inflate(
				R.layout.popupwindow_main_public, null, false);
		// 初始化 pop 设施宽高
		popupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置弹出效果
		popupWindow.setAnimationStyle(R.style.PingjiaFade);
		// 获取焦点,无法单击父类，只能单击pop
		popupWindow.setFocusable(true);
		// 可以输入法
		popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		// 防止虚拟软键盘被弹出菜单遮住
		popupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x30000000);
		// 设置SignPopupWindow弹出窗体的背景
		popupWindow.setBackgroundDrawable(dw);

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
		ImageView trip = (ImageView) customView
				.findViewById(R.id.public_trip_iv);
		final ImageView travel = (ImageView) customView
				.findViewById(R.id.public_travel_iv);
		/*短视频*/
		ImageView vedio = (ImageView) customView
				.findViewById(R.id.public_vedio);

		vedio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 23) {
					int checkCallPhonePermission1 = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
					int checkCallPhonePermission2 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
					int checkCallPhonePermission3 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
					if (checkCallPhonePermission1 != PackageManager.PERMISSION_GRANTED && checkCallPhonePermission2 != PackageManager.PERMISSION_GRANTED
							&& checkCallPhonePermission3 != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 123);
						return;
					}
				}
				popupWindow.dismiss();
				startActivity(new Intent(MainActivity.this,RecordVideoActivity.class));
			}
		});

		trip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				startActivity(new Intent(MainActivity.this,
						PublicTripActivity.class));

			}
		});

		travel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
//				new PopupWindows(MainActivity.this, travel);
				startActivity(new Intent(MainActivity.this,
						PublicTravelActivity.class));

			}
		});

		travel.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				popupWindow.dismiss();
				startActivity(new Intent(MainActivity.this,
						PublicTravelActivity.class));
				return true;
			}
		});
	}
	
	private void GetVersion() {

		new Thread(new Runnable() {//getChildFragmentManager()
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("title", getVersionCode());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				String url= HttpUrl.download;
				
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "自动更新提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);
				
				JSONObject jsonj = null;
				String status = null;
				
				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "自动更新返回的数据" + jsonj.toString());

					status = jsonj.getString("status");// 1成功
					if (TextUtils.equals(status, "1")) {
						JSONObject djson = jsonj.getJSONObject("data");
						newestversion=djson.getString("title");
						newesturi=djson.getString("download");
						newestdescription=djson.getString("content");
					}else{
//						Message = jsonj.getString("resultdescription");
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "解析错误" + e.toString());
				}
				
				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
	}
	
	/**
	 * 获取软件版本号
	 * 
	 * @return
	 */
	public String getVersionCode() {
		int versionCode=0;
//		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			/*versionCode = getPackageManager().getPackageInfo(
					"com.power.travel.xixuntravel", 0).versionCode;*/
			versionCode = BuildConfig.VERSION_CODE;
		/*} catch (NameNotFoundException e) {
			e.printStackTrace();
//			Log.i(TAG, "getVersionName"+e.toString());
		}*/
		return String.valueOf(versionCode);
	}

	private void connectRongServer(String token){
		RongIM.connect(token, new RongIMClient.ConnectCallback() {
			@Override
			public void onTokenIncorrect() {
				Log.e(TAG, "--onTokenIncorrect");
			}

			@Override
			public void onSuccess(String userid) {
				Log.e(TAG, "--onSuccess" + userid);
				SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
				editor.putString("loginid", userid);
				editor.putBoolean("exit", false);
				editor.putString("loginToken", sp.getString(XZContranst.token, ""));
				editor.putString("loginnickname", sp.getString(XZContranst.nickname, ""));
				editor.putString("loginPortrait", sp.getString(XZContranst.face, ""));
				editor.apply();
				userIdList = new ArrayList<Friend>();
				//id  名字  头像
				userIdList.add(new Friend(userid,sp.getString(XZContranst.nickname, ""),sp.getString(XZContranst.face, "")));
				/**
				 * 设置当前用户信息，
				 * @param userInfo 当前用户信息
				 */
//				RongIM.getInstance().setCurrentUserInfo(new UserInfo(userid,sp.getString(XZContranst.nickname, ""),Uri.parse(sp.getString(XZContranst.face, ""))));
				/**
				 * 设置消息体内是否携带用户信息。
				 * @param state 是否携带用户信息，true 携带，false 不携带。
				 */
//				RongIM.getInstance().setMessageAttachedUserInfo(true);

				RongIM.setUserInfoProvider(MainActivity.this, true);

			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {

				Log.e(TAG, "--onError" + errorCode);
			}
		});
	}

	@Override
	public UserInfo getUserInfo(String userId) {

		for (Friend i : userIdList) {
			if (i.getUserId().equals(userId)) {
				Log.e(TAG, "---------返回的用户信息-----------" + i.getPortraitUri());
				return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
			}
		}
		return null;
	}
}
