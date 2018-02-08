package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.Area_onlyAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.AreaModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.net.UploadUtil;
import com.power.travel.xixuntravel.pic.Bimp;
import com.power.travel.xixuntravel.pic.FileUtils;
import com.power.travel.xixuntravel.pic.PhotoActivity;
import com.power.travel.xixuntravel.selectpic.SelectPicMainActivity;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.SaveBitmapUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.weight.MyGridView;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umeng.socialize.utils.DeviceConfig.context;


/**
 * 发布行程
 * 
 * @author fan
 * 
 */
public class PublicTripActivity extends BaseActivity implements OnCheckedChangeListener {

	private ImageView back;
	private TextView title, title_tv, trips_province, trips_city,
			trips_country,trip_time_tv;
	private MyGridView noScrollgridview;
	private EditText trips_title, trips_info,trips_start_ed;
	private CheckBox trips_yueban,trips_car,trips_pincar;
	private ListView province_list;
	private RelativeLayout trip_trip;
	private String startDate,endDate;
	private GridAdapter adapter;
	private String data, TAG = "PublicTripActivity", info, mobile,
			headportrait, province, province_id, city, city_id, country,
			country_id,is_at="0",is_car="0",is_carpool="0";
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	private int TAKE_PICTURE = 0x000000, clickType = 1,TAKE_CAMRO = 2;

	private String path = "",imageFileName = "";
	Bitmap photo;
	File file1,file2,file3,file4,file5,file6,file7,file8,file9;
	private PopupWindow popupWindow;
	private List<AreaModel> adapterList = new ArrayList<AreaModel>();
	private List<AreaModel> cityList = new ArrayList<AreaModel>();
	private List<AreaModel> countyList = new ArrayList<AreaModel>();
	Area_onlyAdapter mAreaAdapter, cityAdapter, countyAdapter;

	//请求访问外部存储
	private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
	//请求写入外部存储
	private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {// 上传图片成功
//				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -2) {// 上传图片失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 3) {// 成功
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
			} else if (msg.what == 4) {//发布成功
				ToastUtil.showToast(getApplicationContext(), info);
				onBackPressed();
			} else if (msg.what == -4) {// 发布失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			
			if (pd != null && PublicTripActivity.this != null) {
				pd.dismiss();
			}

		}
	};
	
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 2) {// 上传图片成功
				sendTrip();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_public_trips);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();

		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				boolean isOpen = imm.isActive();
				if (isOpen) {// 如果输入法打开 取消输入法
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

					inputMethodManager.hideSoftInputFromWindow(
							PublicTripActivity.this.getCurrentFocus()
									.getWindowToken(),

							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				// isOpen若返回true，则表示输入法打开
				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(PublicTripActivity.this, noScrollgridview);
				} else {
					Intent intent = new Intent(PublicTripActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("发起行程");
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setVisibility(View.VISIBLE);
		trips_province = (TextView) findViewById(R.id.trips_province);
		trips_city = (TextView) findViewById(R.id.trips_city);
		trips_country = (TextView) findViewById(R.id.trips_country);
		trip_time_tv= (TextView) findViewById(R.id.trip_time_tv);
		trips_title = (EditText) findViewById(R.id.trips_title);
		trips_info = (EditText) findViewById(R.id.trips_info);
		trips_start_ed= (EditText) findViewById(R.id.trips_start_ed);
		noScrollgridview = (MyGridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		noScrollgridview.setAdapter(adapter);
		trip_trip = (RelativeLayout) findViewById(R.id.trip_trip);
		trips_yueban=(CheckBox)findViewById(R.id.trips_public_yueban);
		trips_car=(CheckBox)findViewById(R.id.trips_public_car);
		trips_pincar=(CheckBox)findViewById(R.id.trips_public_pincar);
		Drawable drawable = this.getResources().getDrawable(R.drawable.checkbox_public);
		drawable.setBounds(0,0,120,60);
		trips_yueban.setCompoundDrawables(drawable,null,null,null);
		Drawable drawable1 = this.getResources().getDrawable(R.drawable.checkbox_public);
		drawable1.setBounds(0,0,120,60);
		trips_car.setCompoundDrawables(drawable1,null,null,null);
		Drawable drawable2 = this.getResources().getDrawable(R.drawable.checkbox_public);
		drawable2.setBounds(0,0,120,60);
		trips_pincar.setCompoundDrawables(drawable2,null,null,null);
	}

	private void initListener() {
		back.setOnClickListener(this);
		title_tv.setOnClickListener(this);
		trips_province.setOnClickListener(this);
		trips_city.setOnClickListener(this);
		trips_country.setOnClickListener(this);
		trip_trip.setOnClickListener(this);
		trips_yueban.setOnCheckedChangeListener(this);
		trips_car.setOnCheckedChangeListener(this);
		trips_pincar.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			onBackPressed();
		} else if (v == trips_province) {// 省
			clickType = 1;
			if (adapterList.isEmpty()) {// 第一次
				if (isConnect()) {
					getData("1");
				} else {
					ToastUtil.showToast(getApplicationContext(),
							XZContranst.no_net);
				}
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
		} else if (v == title_tv) {// 确认
			if (validate()) {
				uploadpic();
			}
		} else if (v == trip_trip) {// 行程 时间选择
			Intent intent=new Intent(this,CalendarMainActivity.class);
			intent.putExtra("startData", startDate);
			intent.putExtra("endData", endDate);
			startActivityForResult(intent, 11);
			overridePendingTransition(R.anim.bottom_to_top, R.anim.alpha_go);
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		if(v==trips_yueban){
			if(isChecked){
				is_at="1";
			}else{
				is_at="0";
			}
		}else if(v==trips_car){
			if(isChecked){
				is_car="1";
				if (trips_pincar.isChecked()){
					trips_pincar.setChecked(false);
					is_carpool = "0";
				}
			}else{
				is_car="0";
			}
		}else if(v==trips_pincar){//
			if(isChecked){
				is_carpool="1";
				if (trips_car.isChecked()){
					trips_car.setChecked(false);
					is_car = "0";
				}
			}else{
				is_carpool="0";
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			 if(requestCode==11){
				
				if(!TextUtils.isEmpty(data.getStringExtra("startDate"))
						&&!TextUtils.isEmpty(data.getStringExtra("endDate"))){
					startDate=data.getStringExtra("startDate");
					endDate=data.getStringExtra("endDate");
					LogUtil.e("返回的数据", startDate+"\n"+endDate);
					trip_time_tv.setText(startDate+"/"+endDate);	
				}
			}
		} else {
			if (requestCode == TAKE_PICTURE) {
				File file=new File(path);
				if (file.exists()) {
					if (Bimp.drr.size() < 9) {
//						Bimp.drr.add(path);
						Bimp.drr.add(SaveBitmapUtil.SaveBitmap(this, path,
								imageFileName));
					} else {
						ToastUtil.showToast(getApplicationContext(), "最多选择9张图片");
					}
				}
			}
		}
	}

	@Override
	protected void onResume() {
		// LogUtil.e(TAG, "刷新数据"+SelectPicList.drr.size());
		adapter.update();
		super.onResume();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Bimp.mSelectedImage.clear();
		Bimp.bmp.clear();
		Bimp.drr.clear();
		Bimp.max = 0;
		FileUtils.deleteDir();
		finish();
//		startActivity(new Intent(this,MyTripListActivity.class));
	}

	private void clearList(int type) {
		switch (type) {
		case 1:// 清理 city country
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

	private boolean validate() {
		if (TextUtils.isEmpty(trips_title.getText().toString())) {
			trips_title.setFocusableInTouchMode(true);
			trips_title.requestFocus();
			trips_title.findFocus();
			ToastUtil.showToast(getApplicationContext(), "请输入标题");
			return false;
		}
		
		if (TextUtils.isEmpty(trips_info.getText().toString())) {
			trips_info.setFocusableInTouchMode(true);
			trips_info.requestFocus();
			trips_info.findFocus();
			ToastUtil.showToast(getApplicationContext(), "请描述行程");
			return false;
		}
		if (Bimp.drr.size() ==0) {
			ToastUtil.showToast(getApplicationContext(), "请选择图片");
			return false;
		}
		
		if (TextUtils.isEmpty(province)) {
			ToastUtil.showToast(getApplicationContext(), "请选择省");
			return false;
		}
		if (TextUtils.isEmpty(city)) {
			ToastUtil.showToast(getApplicationContext(), "请选择市");
			return false;
		}
		
		if (TextUtils.isEmpty(trips_start_ed.getText().toString())) {
			trips_start_ed.setFocusableInTouchMode(true);
			trips_start_ed.requestFocus();
			trips_start_ed.findFocus();
			ToastUtil.showToast(getApplicationContext(), "请输入详细地址");
			return false;
		}
		if (TextUtils.isEmpty(startDate)) {
			ToastUtil.showToast(getApplicationContext(), "请选择行程");
			return false;
		}
		if (TextUtils.isEmpty(endDate)) {
			ToastUtil.showToast(getApplicationContext(), "请选择行程");
			return false;
		}
		if (!trips_car.isChecked() && !trips_pincar.isChecked()){
			ToastUtil.showToast(getApplicationContext(),"请选择有车否者拼车");
			return false;
		}
		return true;
	}

	// 选择相册还是拍照
	public class PopupWindows extends PopupWindow {

		public PopupWindows(final Context mContext, View parent) {

			final View view = View.inflate(mContext,
					R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (ContextCompat.checkSelfPermission(PublicTripActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
							!= PackageManager.PERMISSION_GRANTED){
						//申请WRITE_EXTERNAL_STORAGE权限
						ActivityCompat.requestPermissions(PublicTripActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
								WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
					}else {
						path = null;
						photo();
					}
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (ContextCompat.checkSelfPermission(PublicTripActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
							!= PackageManager.PERMISSION_GRANTED){
						//申请WRITE_EXTERNAL_STORAGE权限
						ActivityCompat.requestPermissions(PublicTripActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
								READ_EXTERNAL_STORAGE_REQUEST_CODE);
					}else {
						startActivity(new Intent(PublicTripActivity.this,
								SelectPicMainActivity.class));
					}
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				path = null;
				photo();
			} else {

			}
		} else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startActivity(new Intent(PublicTripActivity.this,
						SelectPicMainActivity.class));
			} else {

			}
		}
	}

	/**
	 * 省市区 pop
	 * 
	 * @param type
	 */
	private void initmPopupWindowView(final int type) {

		View customView = getLayoutInflater().inflate(
				R.layout.item_province_layout, null, false);
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
			mAreaAdapter = new Area_onlyAdapter(PublicTripActivity.this,
					adapterList);
			province_list.setAdapter(mAreaAdapter);
			break;
		case 2:// 市
			province_list = (ListView) customView.findViewById(R.id.list_city);
			cityAdapter = new Area_onlyAdapter(PublicTripActivity.this,
					cityList);
			province_list.setAdapter(cityAdapter);
			break;
		case 3:// 区
			province_list = (ListView) customView
					.findViewById(R.id.list_country);
			countyAdapter = new Area_onlyAdapter(PublicTripActivity.this,
					countyList);
			province_list.setAdapter(countyAdapter);
			break;
		default:
			break;
		}

		province_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				switch (type) {
				case 1:
					province = adapterList.get(position).getName();
					province_id = adapterList.get(position).getId();
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

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File pfile = null;
		try {
			pfile = createImageFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Uri uri;
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(this,getPackageName()+".FileProvider", pfile);
//			uri = Uri.fromFile(pfile);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
		} else {
			uri = Uri.fromFile(pfile);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
		}

		/*openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pfile));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);*/
	}

	protected File createImageFile() throws IOException {

		imageFileName = String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		/* File的构造方法，指定路径以及文件名称来实例化File对象 */
		File image = new File(getAlbumDir(), imageFileName);
		path = image.getCanonicalPath();// getCanonicalPath()获得绝对路径
		return image;
	}//

	public static File getAlbumDir() {
		// Environment.getExternalStorageDirectory() 获得sd卡
		File dir = new File(Environment.getExternalStorageDirectory(),
				getAlbunName());
		if (!dir.exists()) {// 文件夹不存在 创建文件
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取保存图片的文件夹名称
	 * 
	 * @return
	 */
	private static String getAlbunName() {

		return "myimage";
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {

		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item
		 */
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));

			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		public Bitmap drawableToBitmap(Drawable drawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		}

		Handler handlerimg = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							// Message message = new Message();
							// message.what = 1;
							handlerimg.sendEmptyMessage(1);
							LogUtil.e(TAG, "刷新获取相册的数据");
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;

								handlerimg.sendEmptyMessage(1);
								LogUtil.e(TAG, "刷新照相的数据");
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	// 上传图片
	private void uploadpic() {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = HttpUrl.uploadify;
				Map<String, String> word = new HashMap<String, String>();

				Map<String, File> files = new HashMap<String, File>();
				file1 = new File(Bimp.drr.get(0));
				if (file1 != null && file1.exists()) {
					files.put("file_box1", file1);
				}
			if (Bimp.drr.size() > 1) {
				file2 = new File(Bimp.drr.get(1));
				if (file2 != null && file2.exists()) {
					files.put("file_box2", file2);
				}
			}
			if (Bimp.drr.size() > 2) {
				file3 = new File(Bimp.drr.get(2));
				if (file3 != null && file3.exists()) {
					files.put("file_box3", file3);
				}
			}
			if (Bimp.drr.size() > 3) {
				file4 = new File(Bimp.drr.get(3));
				if (file4 != null && file4.exists()) {
					files.put("file_box4", file4);
				}
			}
			if (Bimp.drr.size() > 4) {
				file5 = new File(Bimp.drr.get(4));
				if (file5 != null && file5.exists()) {
					files.put("file_box5", file5);
				}
			}
			if (Bimp.drr.size() > 5) {
				file6 = new File(Bimp.drr.get(5));
				if (file6 != null && file6.exists()) {
					files.put("file_box6", file6);
				}
			}
			if (Bimp.drr.size() > 6) {
				file7 = new File(Bimp.drr.get(6));
				if (file7 != null && file7.exists()) {
					files.put("file_box7", file7);
				}
			}
			if (Bimp.drr.size() > 7) {
				file8 = new File(Bimp.drr.get(7));
				if (file8 != null && file8.exists()) {
					files.put("file_box8", file8);
				}
			}
			if (Bimp.drr.size() > 8) {
				file9 = new File(Bimp.drr.get(8));
				if (file9 != null && file9.exists()) {
					files.put("file_box9", file9);
				}
			}

				String request = UploadUtil.uploadFile(word, files, url);

				JSONObject jsonj = null;
				String code = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "上传图片返回的数据" + jsonj.toString());

					code = jsonj.getString("status");// 1成功
					info = jsonj.getString("msg");
					headportrait =  jsonj.optString("img");// 图片网络地址
																			// 全称

				} catch (Exception e) {
					LogUtil.e(TAG, "上传图片解析错误" + e.toString());
				}

				if (TextUtils.equals(code, "1")) {
					handler2.sendEmptyMessage(2);
				} else if (TextUtils.equals(code, "0")) {
					handler.sendEmptyMessage(-2);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
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
						adapterList = JSON.parseArray(arry.toString(),
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

	// 发布行程
	private void sendTrip() {
//		pd = ProgressDialogUtils.show(this, "提交数据...");
//		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("title", trips_title.getText().toString());
					data.put("info", trips_info.getText().toString());
					data.put("is_at",is_at);
					data.put("is_car", is_car);
					data.put("is_carpool",is_carpool);
					data.put("arr_img", headportrait);
					data.put("starttime",startDate);
					data.put("endtime",endDate);
					data.put("province",province_id);
					data.put("city",city_id);
					data.put("area",country_id);
					data.put("address", trips_start_ed.getText().toString());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.addTrip;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "发布行程提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "发布行程返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(4);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(-4);
				} else {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}

	

}
