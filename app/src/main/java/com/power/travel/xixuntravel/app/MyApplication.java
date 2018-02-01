package com.power.travel.xixuntravel.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

public class MyApplication extends Application implements RongIMClient.OnReceiveMessageListener{
	public static final String PHONEREGX = "^((\\+86)|(86))?(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}";

	private static MyApplication instance,mApplication;
	private List<Activity> activityList = new ArrayList<Activity>();
	ImageLoader imageLoader = ImageLoader.getInstance();
	public boolean isRun;
	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	public static Context getContext() {
		return mApplication.getApplicationContext();
	}
	
	

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}
	
	public void clear() {
		for (Activity activity : activityList) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
			imageLoader.clearDiscCache();
			imageLoader.clearMemoryCache();
		}

	}

	public MyApplication() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication=this;
		MultiDex.install(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPoolSize(3)//
				.memoryCache(new WeakMemoryCache())
				.threadPriority(Thread.NORM_PRIORITY - 2)//
				.denyCacheImageMultipleSizesInMemory()//
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//
				.discCacheFileCount(40)//
				.tasksProcessingOrder(QueueProcessingType.LIFO)//
				.build();
		ImageLoader.getInstance().init(config);

		Config.DEBUG = true;
		UMShareAPI.get(this);
		PlatformConfig.setWeixin("wxd88a738cb740ae9d","dae6b185503646782d35570b09863019");
		PlatformConfig.setQQZone("101357534","72605552a02a54aa2f874cf77d198fa2");
		PlatformConfig.setSinaWeibo("4251148960","8c6984a8aebbec08a5fea5848d25cc5c","http://sns.whalecloud.com");
		RongIM.init(this);
		RongIM.setOnReceiveMessageListener(this);
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	@Override
	public boolean onReceived(Message message, int i) {
		Uri portraitUri = message.getContent().getUserInfo().getPortraitUri();
		String name = message.getContent().getUserInfo().getName();
		LogUtil.e("TAG","name："+name+"portraitUri："+portraitUri.toString());
		return false;
	}
}
