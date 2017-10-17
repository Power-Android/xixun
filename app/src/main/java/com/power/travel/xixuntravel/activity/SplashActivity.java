package com.power.travel.xixuntravel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.app.SystemBarTintManager;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends Activity {
	String TAG="FirstActivity",coordinate_x,coordinate_y,location_pro,location_city,location_country;
	SharedPreferences sp,spLocation;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/*
		 * 去除界面顶部灰色部分项目名字的方法 局限性：在每个界面分别添加才管用
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
		requestWindowFeature(0);// 上下两行代码去掉项目名字
		// 防止锁屏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		/*
		 * 退出功能实现
		 */
		MyApplication.getInstance().addActivity(this);// 将本activity添加到**中一块结束掉
		super.onCreate(savedInstanceState);
//		SDKInitializer.initialize(getApplicationContext());
//		setTranslucentStatus();
		setContentView(R.layout.activity_first3);
	
		
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		initLocation();
		mLocationClient.start();
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
		spLocation= this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
				Context.MODE_PRIVATE);
		final Intent skip1 = new Intent(SplashActivity.this, MainActivity.class);
//		String path="http://xizanglvyou.ip115.enet360.com/Uploads/video/20160921/20160921134650VID_20160921_134639.mp4";
		String path="android.resource://"+this.getPackageName()+"/"+R.raw.opening;
//		String path="android.resource://com.tibettraver.activity/"+R.raw.opening;
		LogUtil.e("视频路径", path);
		Uri uri = Uri.parse(path);
		VideoView videoView = (VideoView)this.findViewById(R.id.videoview);
//		videoView.setMediaController(new MediaController(this));    
		videoView.setVideoURI(uri);    
		videoView.start();    
//		videoView.requestFocus();
		
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
            	startActivity(skip1);
            	finish();
            }    
		});  
		
		videoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				startActivity(skip1);
				finish();
				return false;
			}
		});
		 
		// 创建时间实例
		Timer delay = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if(sp.getBoolean(XZContranst.if_login, false)){//登录了
					startActivity(skip1);
				}else{//没有登录
					startActivity(skip1);
				}
				
				finish();//杀死 这个界面，这样按返回键的时候 就不会 返回这个界面了
			}
		};
		// 设置等待时间
//		delay.schedule(task, 1000 * 2);
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
                location_country= location.getCountry();
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
                location_country= location.getCountry();
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
            	 Log.e("BaiduLocationApiDem省市信息", location_pro+location_city+location_country);
            Log.e("BaiduLocationApiDem", sb.toString());
            mLocationClient.stop();
        }
	}
	
	/**
	 * 设置状态栏背景状态 
	 *//*
	private void setTranslucentStatus() {
		setStatusBarTranslucent(true);
//		setNavigationBarTranslucent(true);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.title_layout_bg_color);// 状态栏的背景颜色(0表示无背景)  
//		tintManager.setNavigationBarTintEnabled(true);
//		tintManager.setNavigationBarTintResource(R.color.title_bar_bg);//导航栏背景颜色（0表示无背景）
	}*/

	/**
	 * 设置状态栏是否透明
	 *
	 * @param isTransparent
	 *//*
	private void setStatusBarTranslucent(boolean isTransparent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& isTransparent) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			// 导航栏透明
			final int sBits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= sBits;
			win.setAttributes(winParams);
		}
	}*/

	/**
	 *设置导航栏是否透明 
	 * 
	 * @param isTransparent
	 *//*
	private void setNavigationBarTranslucent(boolean isTransparent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& isTransparent) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			// 导航栏透明 
			final int nBits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			winParams.flags |= nBits;
			win.setAttributes(winParams);
		}
	}*/


	
}
