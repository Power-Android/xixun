package com.power.travel.xixuntravel.utils;

import android.os.Build;

public interface XZContranst {
	public static final String MAIN_SHARED_PREFERENCES = "com_tibettraver_userinfo";// 数据库名
	public static final String MAIN_SHARED_PREFERENCES_LO = "com_tibettraver_userinfo_location";// 数据库名
	public static final String if_login="if_login";//是否登录  参数名
	public static String face="face";//头像  参数名
	public static String sex="sex";//性别  参数名
	public static String nickname="nickname";//昵称  参数名
	public static String if_guide="if_guide";//是否是导游  参数名 0否 1是 2审核中
	public static String id="id";//聊天用id  参数名
	public static String username="username";//用户账号
	public static String if_driver="if_driver";//是否是司机 0否 1是 2审核中
	public static String signature="signature";//签名
	public static String mobile="mobile";//手机号
	public static String weixin="weixin";//微信
	public static String weibo="weibo";//微博
	public static String master="master";//
	public static String province="province";//
	public static String city="city";//
	public static String area="area";//
	public static String provinceName="provinceName";//
	public static String cityName="cityName";//
	public static String areaName="areaName";//
	public static String address="address";////详细地址
	public static String coordinate_x="coordinate_x";//
	public static String coordinate_y="coordinate_y";//
	public static String location_pro="location_pro";//
	public static String location_city="location_city";//
	public static String location_country="location_country";
	public static String work="work";//职位
	public static String age="age";//年级
	public static String token="token";//融云token
	public static String zhendong="zhendong";//
	public static String sound="sound";//
	public static String adress="adress";//筛选地址
	
	public static int apply_guide_name=10;
	public static int apply_guide_nation=11;
	
	public static String lastPage="已经是最后一页了！";//列表加载最后一页使用
	public static String devicename= Build.MODEL;
	public static String deviceos= Build.VERSION.RELEASE;
	public static String addcar="恭喜您，商品已添加至购物车！";
	public static String no_net="网络连接失败， 请确认网络连接!";
	
	public static String[] caragelist={"1","2","3","4","5","6","7","8","9","10"
		,"11","12","13","14","15","16","17","18","19","20",
		"21","22","23","24","25","26","27","28","29","30"};

}
