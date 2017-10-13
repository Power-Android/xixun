package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.mob.tools.utils.UIHandler;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.Friend;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity implements Callback,
        OnClickListener {

	private ImageView back,login_qq,login_weixin,login_weibo;
	private TextView title, login_forgetpwd;
	private EditText login_account, regist_pwd;
	private Button login_login, login_regist;
	private String data, TAG = "LOG", info, mycenter,coordinate_x,coordinate_y
			,location_pro,location_city,location_country,openid,type,touxiang,nicheng;
	private ProgressDialogUtils pd;
	SharedPreferences sp,spLocation;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private List<Friend> userIdList;

	private static final int MSG_ACTION_CCALLBACK = 2;

	private SHARE_MEDIA[] list = {SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA,};

	private UMShareAPI umShareAPI;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
//				ToastUtil.showToast(getApplicationContext(), info);
				
//				if (TextUtils.isEmpty(mycenter)) {
					finish();
//				} else {
//					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//					intent.putExtra("typename", "mycenter");
//					startActivity(intent);
//					finish();
//				}
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 2) {//判断是否绑定
				Login_Openid();
			}else if (msg.what == 3) {//第三方登陆 已绑定
				
			}else if (msg.what == -3) {//第三方登陆未绑定
				Intent intent =new Intent(LoginActivity.this,Regist_BangDingActivity.class);
				intent.putExtra("openid", openid);
				intent.putExtra("type", type);
				intent.putExtra("touxiang", touxiang);
				intent.putExtra("nicheng", nicheng);
				startActivityForResult(intent, 3);
			}
			if (pd != null && LoginActivity.this != null) {
				pd.dismiss();
			}
		}
	};
	private SHARE_MEDIA share;
	private UMShareConfig config;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		ShareSDK.initSDK(this);
		setContentView(R.layout.activity_login);
		MyApplication.getInstance().addActivity(this);

		initUM();

		initView();
		initListener();
		initgetData();
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		initLocation();
		mLocationClient.start();


	}

	private void initUM() {
		umShareAPI = UMShareAPI.get(this);
		Config.DEBUG = true;
		PlatformConfig.setWeixin("wxd88a738cb740ae9d","dae6b185503646782d35570b09863019");
		PlatformConfig.setQQZone("101357534","72605552a02a54aa2f874cf77d198fa2");
		PlatformConfig.setSinaWeibo("4251148960","8c6984a8aebbec08a5fea5848d25cc5c","http://sns.whalecloud.com/sina2/callback");
		config = new UMShareConfig();
		config.isNeedAuthOnGetUserInfo(false);
//		config.isOpenShareEditActivity(true);
//		config.setShareToLinkedInFriendScope(UMShareConfig.LINKED_IN_FRIEND_SCOPE_ANYONE);
//		config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
//		umShareAPI.setShareConfig(config);
//		umShareAPI.getPlatformInfo(this,share,authListener);
	}

	UMAuthListener authListener = new UMAuthListener() {
		/**
		 * @desc 授权开始的回调
		 * @param share_media 平台名称
		 */
		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}
		/**
		 * @desc 授权成功的回调
		 * @param share_media 平台名称
		 * @param i 行为序号，开发者用不上
		 * @param map 用户资料返回
		 */
		@Override
		public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
			Message msg = new Message();
			msg.what = MSG_ACTION_CCALLBACK;
			msg.arg1 = 1;
			msg.arg2 = i;
			msg.obj = map;
			UIHandler.sendMessage(msg, LoginActivity.this);
//			LogUtil.e(TAG,"-----------DATA-----------"+map.toString());
			String temp = "";
			for (String key : map.keySet()) {
				temp = temp + key + " : " + map.get(key) + "\n";
			}
			LogUtil.e(TAG,temp);

			if (TextUtils.equals(type,"1")){//QQ登录
				JSONObject djson = new JSONObject(map);
				openid = map.get("openid");
				try {
					touxiang = djson.getString("iconurl");
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e(TAG,"touxiang======获取失败" + e.toString());
				}
				nicheng = map.get("name");
//				LogUtil.e(TAG,"--------------qq---------------" + nicheng);
			}
			if (TextUtils.equals(type,"2")){
				openid = map.get("openid");
				touxiang = map.get("iconurl");
				nicheng = map.get("screen_name");
//				LogUtil.e(TAG,"----id-----"+openid+"\n"+"----icon-----"+touxiang+"\n"+"-----name-----"+nicheng);
			}
			if (TextUtils.equals(type,"3")){
				openid = map.get("uid");
				touxiang = map.get("iconurl");
				nicheng = map.get("name");
			}
			handler.sendEmptyMessage(2);
		}
		/**
		 * @desc 授权失败的回调
		 * @param share_media 平台名称
		 * @param i 行为序号，开发者用不上
		 * @param throwable 错误原因
		 */
		@Override
		public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
			Message msg = new Message();
			msg.what = MSG_ACTION_CCALLBACK;
			msg.arg1 = 2;
			msg.arg2 = i;
			msg.obj = throwable;
			UIHandler.sendMessage(msg,LoginActivity.this);
		}
		/**
		 * @desc 授权取消的回调
		 * @param share_media 平台名称
		 * @param i 行为序号，开发者用不上
		 */
		@Override
		public void onCancel(SHARE_MEDIA share_media, int i) {
			Message msg = new Message();
			msg.what = MSG_ACTION_CCALLBACK;
			msg.arg1 = 3;
			msg.arg2 = i;
			UIHandler.sendMessage(msg, LoginActivity.this);
			Toast.makeText(LoginActivity.this, "取消登录了", Toast.LENGTH_LONG).show();
		}
	};

	private void initgetData() {
		Intent intnet = getIntent();
		if (intnet.hasExtra("type")) {
			mycenter = intnet.getStringExtra("type");
		}
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		spLocation= this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "正在登陆...");
		back = (ImageView) findViewById(R.id.back);
		login_qq= (ImageView) findViewById(R.id.login_qq);
		login_weixin=(ImageView)findViewById(R.id.login_weixin);
		login_weibo=(ImageView)findViewById(R.id.login_weibo);
		title = (TextView) findViewById(R.id.title);
		title.setText("登陆");
		login_login = (Button) findViewById(R.id.login_login);
		login_regist = (Button) findViewById(R.id.login_regist);
		login_account = (EditText) findViewById(R.id.login_account);
		regist_pwd = (EditText) findViewById(R.id.regist_pwd);
		login_forgetpwd = (TextView) findViewById(R.id.login_forgetpwd);
	}

	private void initListener() {
		back.setOnClickListener(this);
		login_login.setOnClickListener(this);
		login_regist.setOnClickListener(this);
		login_forgetpwd.setOnClickListener(this);
		login_qq.setOnClickListener(this);
		login_weixin.setOnClickListener(this);
		login_weibo.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == login_login) {// 登陆
			if (validate()) {
				Login();
			}
		} else if (v == login_regist) {// 注册
			startActivityForResult(new Intent(this, RegistActivity.class), 2);
		} else if (v == login_forgetpwd) {// 忘记密码
			startActivityForResult(new Intent(this, ForgetPwdActivity.class), 1);
		}else if(v==login_qq){//QQ登陆

			type="1";
			share = list[0];
			config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
			umShareAPI.setShareConfig(config);
			umShareAPI.getPlatformInfo(this,share,authListener);

		}else if(v==login_weixin){//微信
			type="2";
			share = list[1];
			config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
			umShareAPI.setShareConfig(config);
			umShareAPI.getPlatformInfo(this,share,authListener);
		}else if(v==login_weibo){//微博
			type="3";
			share = list[2];
			config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
			umShareAPI.setShareConfig(config);
			umShareAPI.getPlatformInfo(this,share,authListener);

			/*Platform weibo=ShareSDK.getPlatform(SinaWeibo.NAME);
			weibo.SSOSetting(false); // 设置false表示使用SSO授权方式
			weibo.setPlatformActionListener(this); // 设置分享事件回调
			
			if (weibo.isAuthValid()) {
				weibo.removeAccount(true);
			}
			if (weibo.isClientValid()) {
				System.out.println("安装了微博");
			} else {
				ToastUtil.showToast(getApplicationContext(), "没有安装微博");
				System.out.println("没有安装微博");
			}
			weibo.showUser(null)*/;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).getPlatformInfo(this,share, authListener);
		UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);

		switch (requestCode) {
			case 1:
				if (data != null) {
					login_account.setText(data.getStringExtra("mobile"));
					regist_pwd.setText(data.getStringExtra("password"));
					Login();
				}
				break;
			case 2:
				if (data != null) {
					login_account.setText(data.getStringExtra("mobile"));
					regist_pwd.setText(data.getStringExtra("password"));
					Login();
				}
				break;
			case 3://完成绑定后
				handler.sendEmptyMessage(1);
				break;
			default:
				break;
		}
	}

	private boolean validate() {
		if (TextUtils.isEmpty(login_account.getText().toString())) {
			login_account.setFocusableInTouchMode(true);
			login_account.requestFocus();
			login_account.findFocus();
			ToastUtil.showToast(getApplicationContext(), "请输入手机号!");
			// showEnsure("请输入手机号!");
			return false;
		}

		if (TextUtils.isEmpty(regist_pwd.getText().toString())) {
			regist_pwd.setFocusableInTouchMode(true);
			regist_pwd.requestFocus();
			regist_pwd.findFocus();
			ToastUtil.showToast(getApplicationContext(), "请输入密码!");
			// showEnsure("请输入密码!");
			return false;
		}
		if (!isConnect()) {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
			return false;
		}
		return true;
	}
	
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 0;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.arg1) {
			case 1: {
				// 成功
				Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT)
						.show();
			}
			break;
			case 2: {
				// 失败
				Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_SHORT).show();
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)
						|| "WechatTimelineNotSupportedException".equals(expName)
						|| "WechatFavoriteNotSupportedException".equals(expName)) {
					Toast.makeText(getApplicationContext(), "请安装微信客户端", Toast.LENGTH_SHORT).show();
				}
			}
			break;
			case 3: {
				// 取消
//				Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT)
//						.show();
			}
			break;
		}
		return false;
	}

	public class MyLocationListener implements BDLocationListener {
		 
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());//维度
            coordinate_y= String.valueOf(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());//经度
            coordinate_x= String.valueOf(location.getLongitude());
        	
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                location_pro=location.getProvince();
                location_city=location.getCity();
                location_country=location.getCountry();
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                location_pro=location.getProvince();
                location_city=location.getCity();
                location_country=location.getCountry();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            	sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                List<Poi> list = location.getPoiList();// POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }
                Editor edit = spLocation.edit();
                if(!TextUtils.isEmpty(coordinate_y)){
                	edit.putString(XZContranst.coordinate_y, coordinate_y);
                }
            	 if(!TextUtils.isEmpty(coordinate_x)){
            		 edit.putString(XZContranst.coordinate_x, coordinate_x);
                 }
            	 if(!TextUtils.isEmpty(location_pro)){
            		 edit.putString(XZContranst.location_pro,location_pro );
                 }
            	 if(!TextUtils.isEmpty(location_city)){
            		 edit.putString(XZContranst.location_city,location_city );
                 }
            	 if(!TextUtils.isEmpty(location_country)){
            		 edit.putString(XZContranst.location_country,location_country );
                 }
            	edit.commit();
            Log.e("BaiduLocationApiDem", sb.toString());
            mLocationClient.stop();
        }
	}

	private void Login() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mobile", login_account.getText().toString());
					data.put("password", regist_pwd.getText().toString());
					data.put("coordinate_x", spLocation.getString(XZContranst.coordinate_x, ""));
					data.put("coordinate_y", spLocation.getString(XZContranst.coordinate_y, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.login;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "登陆提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "登陆返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					JSONObject djson = jsonj.getJSONObject("data");

					Editor edit = sp.edit();
					edit.putBoolean(XZContranst.if_login, true);
					edit.putString(XZContranst.face, djson.optString("face"));
					edit.putString(XZContranst.sex, djson.optString("sex"));
					edit.putString(XZContranst.nickname,
							djson.optString("nickname"));
					edit.putString(XZContranst.if_guide,
							djson.optString("if_guide"));
					edit.putString(XZContranst.id, djson.optString("id"));
					edit.putString(XZContranst.username,
							djson.optString("username"));
					edit.putString(XZContranst.if_driver,
							djson.optString("if_driver"));
					edit.putString(XZContranst.signature,
							djson.optString("signature"));
					edit.putString(XZContranst.mobile,
							djson.optString("mobile"));
					edit.putString(XZContranst.weixin,
							djson.optString("weixin"));
					edit.putString(XZContranst.weibo,
							djson.optString("weibo"));
					edit.putString(XZContranst.token,
							djson.optString("token"));
					edit.commit();
//					connectRongServer(djson.optString("token"));
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
	
	/**
	 * 判断是否绑定
	 */
	private void Login_Openid() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("openid", openid);
					data.put("type", type);
//					data.put("coordinate_x", spLocation.getString(XZContranst.coordinate_x, ""));
//					data.put("coordinate_y", spLocation.getString(XZContranst.coordinate_y, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.openidLogin;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "判断绑定提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "判断绑定返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					if (TextUtils.equals(status, "1")) {
						JSONObject djson = jsonj.getJSONObject("data");
						Editor edit = sp.edit();
						edit.putBoolean(XZContranst.if_login, true);
						edit.putString(XZContranst.face, djson.optString("face"));
						edit.putString(XZContranst.sex, djson.optString("sex"));
						edit.putString(XZContranst.nickname,
								djson.optString("nickname"));
						edit.putString(XZContranst.if_guide,
								djson.optString("if_guide"));
						edit.putString(XZContranst.id, djson.optString("id"));
						edit.putString(XZContranst.username,
								djson.optString("username"));
						edit.putString(XZContranst.if_driver,
								djson.optString("if_driver"));
						edit.putString(XZContranst.signature,
								djson.optString("signature"));
						edit.putString(XZContranst.mobile,
								djson.optString("mobile"));
						edit.putString(XZContranst.weixin,
								djson.optString("weixin"));
						edit.putString(XZContranst.weibo,
								djson.optString("weibo"));
						edit.putString(XZContranst.token,
								djson.optString("token"));
						edit.commit();
//						connectRongServer(djson.optString("token"));
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "-1")) {
					handler.sendEmptyMessage(-3);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}
	
//	/**
//	 * 连接融云服务器
//	 *
//	 * @param token
//	 */
//	private void connectRongServer(String token) {
//		LogUtil.e(TAG, "登陆用的token"+token);
//		RongIM.connect(token, new ConnectCallback() {
//
//			@Override
//			public void onSuccess(String userId) {
//
////				Toast.makeText(getApplicationContext(), "connet server success",
////							Toast.LENGTH_SHORT).show();
//				  Log.e("LoginActivity", "--onSuccess" + userId);
//	                SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
//	                editor.putString("loginid", userId);
//	                editor.putBoolean("exit", false);
//	                editor.putString("loginToken", sp.getString(XZContranst.token, ""));
//	                editor.putString("loginnickname", sp.getString(XZContranst.nickname, ""));
//	                editor.putString("loginPortrait", sp.getString(XZContranst.face, ""));
//	                editor.apply();
//	                userIdList = new ArrayList<Friend>();
//	                //id  名字  头像
//	        		userIdList.add(new Friend(userId,sp.getString(XZContranst.nickname, ""),sp.getString(XZContranst.face, "")));
//	        		/**
//	        		 * 设置当前用户信息，
//	        		 * @param userInfo 当前用户信息
//	        		 */
//	        		RongIM.getInstance().setCurrentUserInfo(getUserInfo(userId));
//	        		 /**
//	                 * 设置消息体内是否携带用户信息。
//	                 * @param state 是否携带用户信息，true 携带，false 不携带。
//	                 */
//	                RongIM.getInstance().setMessageAttachedUserInfo(true);
//	                RongIM.setUserInfoProvider(LoginActivity.this, true);
//			}
//
//			@Override
//			public void onError(ErrorCode errorCode) {
//
//				Log.e(TAG,
//						"connect failure errorCode is :" + errorCode.getValue());
//			}
//
//			@Override
//			public void onTokenIncorrect() {
//				Log.e(TAG, "token is error , please check token and appkey ");
//			}
//		});
//	}
//
//	@Override
//	public UserInfo getUserInfo(String userId) {
//		for (com.tibettraver.model.Friend i : userIdList) {
//			if (i.getUserId().equals(userId)) {
//				Log.e(TAG, i.getPortraitUri());
//				return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
//			}
//		}
//		return null;
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UMShareAPI.get(this).release();
	}
}
