package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.AgeAdapter;
import com.power.travel.xixuntravel.adapter.Area_onlyAdapter;
import com.power.travel.xixuntravel.adapter.CarAdapter;
import com.power.travel.xixuntravel.adapter.SexAdapter;
import com.power.travel.xixuntravel.model.AgeModel;
import com.power.travel.xixuntravel.model.AreaModel;
import com.power.travel.xixuntravel.model.CarModel;
import com.power.travel.xixuntravel.model.SexModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


/**
 * 筛选
 * 
 * @author fan
 *
 */
public class FiltrateActivity extends AppCompatActivity implements OnClickListener{

	private LinearLayout ft_yueban_lay, ft_yueche_lay, trips_car;
	private ImageView ft_yueban_iv, ft_yueche_iv;
	private TextView ft_yueban_tv, ft_yueche_tv, trips_province, trips_city,
			trips_country, trips_sex, trips_age, trips_car_tv;
	int clickType = 1;
	String is_at, is_carpool;
	private List<AreaModel> provinceList = new ArrayList<AreaModel>();
	private List<AreaModel> cityList = new ArrayList<AreaModel>();
	private List<AreaModel> countyList = new ArrayList<AreaModel>();
	Area_onlyAdapter mAreaAdapter, cityAdapter, countyAdapter;
	private ListView province_list, list_sex, list_age, list_car;
	private String TAG = "FiltrateActivity", info, province, province_id, city,
			city_id, country, country_id, sex,sex_str, ageid,age_str, car, car_str;
	private ProgressDialogUtils pd;
	private PopupWindow popupWindow, sexPopupWindow, agePopupWindow, carPopupWindow;
	SexAdapter sexAdapter;
	List<SexModel> sexList = new ArrayList<SexModel>();
	List<CarModel> carList = new ArrayList<>();
	AgeAdapter ageAdapter;
	CarAdapter carAdapter;
	List<AgeModel> ageList = new ArrayList<AgeModel>();
	private Button filtarate_reset,filtarate_sure;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 3) {// 成功
				initmPopupWindowView(clickType);
				switch (clickType) {
				case 1:
					popupWindow.showAsDropDown(trips_province, 0, 0);
					break;
				case 2:
					popupWindow.showAsDropDown(trips_city, 0, 0);
					break;
				case 3:
					popupWindow.showAsDropDown(trips_country, 0, 0);
					break;
				default:
					break;
				}
			} else if (msg.what == -3) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {// 获取年龄驾龄成功

			} else if (msg.what == -2) {// 获取年龄驾龄失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && FiltrateActivity.this != null) {
				pd.dismiss();
			}
		}
	};
	private String is_car;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		/*
		 * 去除界面顶部灰色部分项目名字的方法 局限性：在每个界面分别添加才管用
		 */
//		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
//		requestWindowFeature(0);// 上下两行代码去掉项目名字

//		setFinishOnTouchOutside(true);

		setContentView(R.layout.item_filtrate_layout);
		//设置宽度，不然有间隙
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		init();
		initgetIntent();
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				SexModel sexModel = new SexModel();
				sexModel.setId("1");
				sexModel.setName("男");
				sexList.add(sexModel);
			} else if (i == 1) {
				SexModel sexModel = new SexModel();
				sexModel.setId("2");
				sexModel.setName("女");
				sexList.add(sexModel);
			}
		}
		for (int i  = 0; i < 2; i++){
			if (i == 0){
				CarModel carModel = new CarModel();
				carModel.setId("1");
				carModel.setName("有");
				carList.add(carModel);
			}else if (i == 1){
				CarModel carModel = new CarModel();
				carModel.setId("0");
				carModel.setName("无");
				carList.add(carModel);
			}
		}
		getageData();
	}

	/**
	 * 设置位置
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		View view = getWindow().getDecorView();
		WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
		lp.gravity = Gravity.BOTTOM;

		getWindowManager().updateViewLayout(view, lp);
	}

	private void init() {
		ft_yueban_lay = (LinearLayout) findViewById(R.id.filtrate_yueban_layout);
		ft_yueche_lay = (LinearLayout) findViewById(R.id.filtrate_yueche_layout);
		ft_yueban_iv = (ImageView) findViewById(R.id.filtrate_yueban_iv);
		ft_yueche_iv = (ImageView) findViewById(R.id.filtrate_yueche_iv);
		ft_yueban_tv = (TextView) findViewById(R.id.filtrate_yueban);
		ft_yueche_tv = (TextView) findViewById(R.id.filtrate_yueche);
		trips_province = (TextView) findViewById(R.id.trips_province);
		trips_city = (TextView) findViewById(R.id.trips_city);
		trips_country = (TextView) findViewById(R.id.trips_country);
		trips_sex = (TextView) findViewById(R.id.trips_sex);
		trips_age = (TextView) findViewById(R.id.trips_age);
		trips_car = (LinearLayout) findViewById(R.id.trips_car);
		filtarate_reset=(Button)findViewById(R.id.filtarate_reset);
		filtarate_sure=(Button)findViewById(R.id.filtarate_sure);
		trips_car_tv = (TextView) findViewById(R.id.trips_car_tv);


		ft_yueban_lay.setOnClickListener(this);
		ft_yueche_lay.setOnClickListener(this);
		trips_province.setOnClickListener(this);
		trips_city.setOnClickListener(this);
		trips_country.setOnClickListener(this);
		trips_sex.setOnClickListener(this);
		trips_age.setOnClickListener(this);
		trips_car.setOnClickListener(this);
		filtarate_reset.setOnClickListener(this);
		filtarate_sure.setOnClickListener(this);
		trips_car_tv.setOnClickListener(this);
	}
	
	private void initgetIntent() {
		Intent data=getIntent();
		is_at=data.getStringExtra("is_at");
		is_carpool=data.getStringExtra("is_carpool");
		province_id=data.getStringExtra("province_id");
		province=data.getStringExtra("province");
		city_id=data.getStringExtra("city_id");
		city=data.getStringExtra("city");
		country_id=data.getStringExtra("country_id");
		country=data.getStringExtra("country");
		sex=data.getStringExtra("sex");
		sex_str=data.getStringExtra("sex_str");
		ageid=data.getStringExtra("age");
		age_str=data.getStringExtra("age_str");
		car = data.getStringExtra("car");
		car_str = data.getStringExtra("car_str");
		is_car = data.getStringExtra("is_car");

		if(TextUtils.equals(is_at, "0")){
			is_at="1";
		}else if(TextUtils.equals(is_at, "1")){
			is_at="0";
		}

		if(TextUtils.equals(is_carpool, "0")){
			is_carpool="1";
		}else if(TextUtils.equals(is_carpool, "1")){
			is_carpool="0";
		}
		setYueban();
		setYueche();
		if(!TextUtils.isEmpty(province)){
			trips_province.setText(province);
		}
		if(!TextUtils.isEmpty(city)){
			trips_city.setText(city);
		}
		if(!TextUtils.isEmpty(country)){
			trips_country.setText(country);
		}
		if(!TextUtils.isEmpty(sex_str)){
			trips_sex.setText(sex_str);	
		}
		if(!TextUtils.isEmpty(age_str)){
			trips_age.setText(age_str);
		}
		if (!TextUtils.isEmpty(car_str)){
			trips_car_tv.setText(car_str);
		}
	}

	public void onBackPressed() {
		finish();
		this.overridePendingTransition(R.anim.alpha_exit, R.anim.top_to_bottom);
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		if (v == ft_yueban_lay) {// 筛选 约伴
			if(TextUtils.isEmpty(is_at)){
				is_at="0";
			}
			setYueban();
		} else if (v == ft_yueche_lay) {// 筛选 约车
			if(TextUtils.isEmpty(is_carpool)){
				is_carpool="0";
			}
			setYueche();
		} else if (v == trips_province) {// 省
			clickType = 1;
			if (provinceList.isEmpty()) {// 第一次
				getData("1");
			} else {
				clearList(1);
				initmPopupWindowView(clickType);
				popupWindow.showAsDropDown(trips_province, 0, 0);
			}

		} else if (v == trips_city) {// 市
			if (TextUtils.isEmpty(province)) {
				ToastUtil.showToast(getApplicationContext(), "请选择省！");
			} else {
				clickType = 2;
				if (cityList.isEmpty()) {
					getData(province_id);
				} else {
					clearList(2);
					initmPopupWindowView(clickType);
					popupWindow.showAsDropDown(trips_city, 0, 0);
				}
			}
		} else if (v == trips_country) {// 区
			if (TextUtils.isEmpty(city)) {
				ToastUtil.showToast(getApplicationContext(), "请选择市！");
			} else {
				clickType = 3;
				if (countyList.isEmpty()) {
					getData(city_id);
				} else {
					initmPopupWindowView(clickType);
					popupWindow.showAsDropDown(trips_country, 0, 0);
				}
			}
		} else if (v == trips_sex) {// 性别
			initmPopupWindowView();
			sexPopupWindow.showAsDropDown(trips_sex, 0, 0);
		} else if (v == trips_age) {// 年级
			if (ageList.isEmpty()) {
				getageData();
			} else {
				initmagePopupWindowView();
				agePopupWindow.showAsDropDown(trips_age, 0, 0);
			}
		}else if (v == trips_car_tv){//是否有车
			initCarPopupWindowView();
			carPopupWindow.showAsDropDown(trips_car_tv, 0 , 0);
		}
		else if(v==filtarate_reset){//重置
            trips_car.setVisibility(View.GONE);
			is_at="";
			setYueban();
			is_carpool="";
			setYueche();
			
			clearList(1);
			clearList(2);
			clearList(3);
			
			sex="";
			sex_str="";
			trips_sex.setText("性别");
			
			ageid="";
			age_str="";
			trips_age.setText("年龄");

			car = "";
			car_str = "";
			trips_car_tv.setText("有无");

		}else if(v==filtarate_sure){//确定
			Intent intent = getIntent();
			intent.putExtra("is_at", is_at);
			intent.putExtra("is_carpool", is_carpool);
			intent.putExtra("province_id", province_id);
			intent.putExtra("province", province);
			intent.putExtra("city_id", city_id);
			intent.putExtra("city", city);
			intent.putExtra("country_id", country_id);
			intent.putExtra("country", country);
			intent.putExtra("sex", sex);
			intent.putExtra("sex_str", sex_str);
			intent.putExtra("age", ageid);
			intent.putExtra("age_str", age_str);
			intent.putExtra("car", car);
			intent.putExtra("car_str", car_str);
			intent.putExtra("is_car",is_car);
			setResult(101, intent);
			onBackPressed();
		}
	}

	// 约伴单击效果
	@SuppressLint("NewApi")
	private void setYueban() {
		if(TextUtils.isEmpty(is_at)){//初始状态，隐藏勾选
			ft_yueban_iv.setVisibility(View.INVISIBLE);
			ft_yueban_tv.setTextColor(getResources().getColor(R.color.black));
			ft_yueban_lay.setBackground(getResources().getDrawable(
					R.drawable.tab_btn_filtrate_normalbg));
		}else{
			if(TextUtils.equals(is_at, "0")){//被点击状态，显示勾选，设置颜色背景
				ft_yueban_iv.setVisibility(View.VISIBLE);
				ft_yueban_tv.setTextColor(getResources().getColor(R.color.huang2));
				ft_yueban_lay.setBackground(getResources().getDrawable(
						R.drawable.tab_btn_filtrate_selectedbg));
				if (TextUtils.equals(is_carpool,"0")){
					ft_yueche_iv.setVisibility(View.INVISIBLE);
					ft_yueche_tv.setTextColor(getResources().getColor(R.color.black));
					ft_yueche_lay.setBackground(getResources().getDrawable(
							R.drawable.tab_btn_filtrate_normalbg));
					trips_car.setVisibility(View.GONE);
				}
			}
			else if(TextUtils.equals(is_at, "1")){//显示状态，隐藏勾选，背景颜色
				is_at = "0";
				ft_yueban_iv.setVisibility(View.INVISIBLE);
				ft_yueban_tv.setTextColor(getResources().getColor(R.color.black));
				ft_yueban_lay.setBackground(getResources().getDrawable(
						R.drawable.tab_btn_filtrate_normalbg));
			}
		}
		
	}

	// 约车单击效果
	@SuppressLint("NewApi")
	private void setYueche() {
		if(TextUtils.isEmpty(is_carpool)){
			ft_yueche_iv.setVisibility(View.INVISIBLE);
			ft_yueche_tv.setTextColor(getResources().getColor(R.color.black));
			ft_yueche_lay.setBackground(getResources().getDrawable(
					R.drawable.tab_btn_filtrate_normalbg));
		}else{
			if(TextUtils.equals(is_carpool, "0")){
				ft_yueche_iv.setVisibility(View.VISIBLE);
				ft_yueche_tv.setTextColor(getResources().getColor(R.color.huang2));
				ft_yueche_lay.setBackground(getResources().getDrawable(
						R.drawable.tab_btn_filtrate_selectedbg));
				trips_car.setVisibility(View.VISIBLE);
				if (TextUtils.equals(is_at,"0")){
					is_at = "";
					ft_yueban_iv.setVisibility(View.INVISIBLE);
					ft_yueban_tv.setTextColor(getResources().getColor(R.color.black));
					ft_yueban_lay.setBackground(getResources().getDrawable(
							R.drawable.tab_btn_filtrate_normalbg));
				}
			}
			else if(TextUtils.equals(is_carpool, "1")){
				is_carpool = "0";
				ft_yueche_iv.setVisibility(View.INVISIBLE);
				ft_yueche_tv.setTextColor(getResources().getColor(R.color.black));
				ft_yueche_lay.setBackground(getResources().getDrawable(
						R.drawable.tab_btn_filtrate_normalbg));
			}
		}
		
	}

	// 清除省市区
	private void clearList(int type) {
		switch (type) {
		case 1:// 清理 city country
			provinceList.clear();
			province_id = "";
			province = "";
			trips_province.setText("省");

			cityList.clear();
			city = "";
			city_id = "";
			trips_city.setText("市");

			countyList.clear();
			country_id = "";
			country = "";
			trips_country.setText("区");
			break;
		case 2:// 清理 country
			countyList.clear();
			country_id = "";
			country = "";
			trips_country.setText("区");
			break;
		default:
			break;
		}
	}

	/**
	 * 省市区 pop
	 * 
	 * @param type
	 */
	private void initmPopupWindowView(final int type) {

		View customView = getLayoutInflater().inflate(
				R.layout.item_province2_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		popupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		popupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

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
					return true;
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
		switch (type) {
		case 1:// 省
			province_list = (ListView) customView
					.findViewById(R.id.list_province);
			mAreaAdapter = new Area_onlyAdapter(this, provinceList);
			province_list.setAdapter(mAreaAdapter);
			break;
		case 2:// 市
			province_list = (ListView) customView.findViewById(R.id.list_city);
			cityAdapter = new Area_onlyAdapter(this, cityList);
			province_list.setAdapter(cityAdapter);
			break;
		case 3:// 区
			province_list = (ListView) customView
					.findViewById(R.id.list_country);
			countyAdapter = new Area_onlyAdapter(this, countyList);
			province_list.setAdapter(countyAdapter);
			break;
		default:
			break;
		}
		province_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
		province_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				switch (type) {
				case 1:
					province = provinceList.get(position).getName();
					province_id = provinceList.get(position).getId();
					trips_province.setText(province);
					break;
				case 2://
					city = cityList.get(position).getName();
					city_id = cityList.get(position).getId();
					trips_city.setText(city);
					break;
				case 3:
					country = countyList.get(position).getName();
					country_id = countyList.get(position).getId();
					trips_country.setText(country);
					break;
				default:
					break;
				}
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	/**
	 * 性别 pop
	 */
	private void initmPopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_filtrate_sex_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		sexPopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		sexPopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (sexPopupWindow != null && sexPopupWindow.isShowing()) {
					sexPopupWindow.dismiss();
					sexPopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					sexPopupWindow.dismiss();
					return true;
				}

				return false;
			}

		});

		list_sex = (ListView) customView.findViewById(R.id.list_sex);
		list_sex.setSelector(new ColorDrawable(Color.TRANSPARENT));
		sexAdapter = new SexAdapter(FiltrateActivity.this, sexList);
		list_sex.setAdapter(sexAdapter);

		list_sex.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				trips_sex.setText(sexList.get(position).getName());
				sex = sexList.get(position).getId();
				sex_str=sexList.get(position).getName();
				sexPopupWindow.dismiss();
				sexPopupWindow = null;
			}
		});
	}

	/**
	 * 年龄pop
	 */
	private void initmagePopupWindowView() {

		View customView = getLayoutInflater().inflate(
				R.layout.item_filtrate_age_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		agePopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		agePopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (agePopupWindow != null && agePopupWindow.isShowing()) {
					agePopupWindow.dismiss();
					agePopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					agePopupWindow.dismiss();
					return true;
				}

				return false;
			}

		});

		list_age = (ListView) customView.findViewById(R.id.list_age);
		list_age.setSelector(new ColorDrawable(Color.TRANSPARENT));
		ageAdapter = new AgeAdapter(FiltrateActivity.this, ageList);
		list_age.setAdapter(ageAdapter);

		list_age.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				trips_age.setText(ageList.get(position).getVal());
				age_str=ageList.get(position).getVal();
				ageid = ageList.get(position).getId();

				agePopupWindow.dismiss();
				agePopupWindow = null;
			}
		});
	}

	/**
	 * 获取年龄
	 */
	private void getageData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "2");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.age;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "年龄 驾龄获取提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "年龄 驾龄获取返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						ageList = JSON.parseArray(arry.toString(),
								AgeModel.class);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(2);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(-2);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}
	/**
	 * 是否有车pop
	 */
	private void initCarPopupWindowView(){
		View customView = getLayoutInflater().inflate(
				R.layout.item_filtrate_car_layout, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		carPopupWindow = new PopupWindow(customView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// popwindow获取焦点
		carPopupWindow.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SignPopupWindow弹出窗体的背景
		// popupWindow.setBackgroundDrawable(dw);

		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**
				 * 单击内部空白区域，pop消失
				 */
				if (carPopupWindow != null && carPopupWindow.isShowing()) {
					carPopupWindow.dismiss();
					carPopupWindow = null;
					return true;
				}
				/**
				 * 单击pop外部区域，pop消失
				 */
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					carPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		list_car = (ListView) customView.findViewById(R.id.list_car);
		list_car.setSelector(new ColorDrawable(Color.TRANSPARENT));
		carAdapter = new CarAdapter(FiltrateActivity.this,carList);
		list_car.setAdapter(carAdapter);

		list_car.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				trips_car_tv.setText(carList.get(position).getName());
				car = carList.get(position).getId();
				car_str = carList.get(position).getName();
				carPopupWindow.dismiss();
				carPopupWindow = null;
			}
		});
	}

	/**
	 * 获取省市区
	 */
	private void getData(final String upid) {
		pd = ProgressDialogUtils.show(this, "加载数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("upid", upid);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.Service_province;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "省市区信息提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "省市区信息返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					org.json.JSONArray arry = jsonj.getJSONArray("data");

					switch (clickType) {
					case 1:
						provinceList = JSON.parseArray(arry.toString(),
								AreaModel.class);
						break;
					case 2:
						cityList = JSON.parseArray(arry.toString(),
								AreaModel.class);
						break;
					case 3:
						countyList = JSON.parseArray(arry.toString(),
								AreaModel.class);
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(3);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(-3);
				} else {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}
}
