package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.net.UploadUtil;
import com.power.travel.xixuntravel.utils.CameraUtil;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.RoundImageView;
import com.power.travel.xixuntravel.widget.WheelView;
import com.power.travel.xixuntravel.widget.adapters.ArrayWheelAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人资料
 * 
 * @author fan
 * 
 */
public class UserInfoActivity extends BaseActivity {

	private ImageView back;
	private TextView title, addaddress_wheel_cancel,addaddress_wheel_title, addaddress_wheel_sure,
			sex_tv, age_tv, nickname_tv, account_tv, profession_tv, area_tv,
			signature_tv;
	private String data, TAG = "RegistActivity", info, headportrait,province, province_id, city, city_id, country,
			country_id;
	private int posi;
	private ProgressDialogUtils pd;
	private RelativeLayout head_layout, nickname_layout, account_layout,
			sex_layout, profession_layout, age_layout, area_layout,
			signature_layout;
	private RoundImageView info_head;
	Bitmap photo;
	File file1;
	private LinearLayout addaddress_wheel;
	private WheelView mProvince;// 省的WheelView控件
	private String[] mProvinceDatas;// 所有省
	private String mCurrentProviceName;// 当前省的名称
	SharedPreferences sp;

	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
			// ToastUtil.showToast(getApplicationContext(), info);
				setData();
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 9) {// 上传头像
//				info_head.setImageBitmap(photo);
				if (isConnect()) {
					uploadHead();
				}else{
					ToastUtil.showToast(getApplicationContext(),getResources().getString(R.string.notnetwork));
				}
				
			}else if (msg.what == -2) {// 上传头像失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 3) {// 修改信息成功
				ToastUtil.showToast(getApplicationContext(), info);
				editData();
			}else if (msg.what == -3) {// 修改信息失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && UserInfoActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 2) {// 上传头像成功
				if (isConnect()) {
					ImageLoader imageLoader = ImageLoader.getInstance();
					imageLoader.init(ImageLoaderConfiguration
							.createDefault(UserInfoActivity.this));
					/**
					 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
					 */
					imageLoader.displayImage(headportrait, info_head,
							options, animateFirstListener);
					
					posi=1;
					editInfo(1,headportrait);
				}else{
					ToastUtil.showToast(getApplicationContext(),getResources().getString(R.string.notnetwork));
				}
			}
			// if (pd != null && UserInfoActivity.this != null) {
			// pd.dismiss();
			// }
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_userinfo);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		if(isConnect()){
			getData();
		}else{
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
		
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "获取数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("个人资料");
		head_layout = (RelativeLayout) findViewById(R.id.head_layout);
		info_head = (RoundImageView) findViewById(R.id.info_head);
		nickname_layout = (RelativeLayout) findViewById(R.id.nickname_layout);
		account_layout = (RelativeLayout) findViewById(R.id.account_layout);
		sex_layout = (RelativeLayout) findViewById(R.id.sex_layout);
		profession_layout = (RelativeLayout) findViewById(R.id.profession_layout);
		age_layout = (RelativeLayout) findViewById(R.id.age_layout);
		area_layout = (RelativeLayout) findViewById(R.id.area_layout);
		signature_layout = (RelativeLayout) findViewById(R.id.signature_layout);
		sex_tv = (TextView) findViewById(R.id.sex_tv);
		age_tv = (TextView) findViewById(R.id.age_tv);
		nickname_tv = (TextView) findViewById(R.id.nickname_tv);
		account_tv = (TextView) findViewById(R.id.account_tv);
		profession_tv = (TextView) findViewById(R.id.profession_tv);
		area_tv = (TextView) findViewById(R.id.area_tv);
		signature_tv = (TextView) findViewById(R.id.signature_tv);

		options = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.imaleloadlogo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		// .cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				// .displayer(new
				// RoundedBitmapDisplayer(20))//设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	private void initListener() {
		back.setOnClickListener(this);
		head_layout.setOnClickListener(this);
		nickname_layout.setOnClickListener(this);
		account_layout.setOnClickListener(this);
		sex_layout.setOnClickListener(this);
		profession_layout.setOnClickListener(this);
		age_layout.setOnClickListener(this);
		area_layout.setOnClickListener(this);
		signature_layout.setOnClickListener(this);

	}
	
	private void setData() {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(UserInfoActivity.this));
		/**
		 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
		 */
		imageLoader.displayImage(sp.getString(XZContranst.face, ""), info_head,
				options, animateFirstListener);

		nickname_tv.setText(sp.getString(XZContranst.nickname, ""));
		account_tv.setText(sp.getString(XZContranst.username, ""));
		if (TextUtils.equals(sp.getString(XZContranst.sex, ""), "1")) {
			sex_tv.setText("男");
		} else if (TextUtils.equals(sp.getString(XZContranst.sex, ""), "2")) {
			sex_tv.setText("女");
		}
		profession_tv.setText(sp.getString(XZContranst.work, ""));
		age_tv.setText(sp.getString(XZContranst.age, ""));
//		area_tv.setText(sp.getString(XZContranst.address, ""));
		if (!TextUtils.isEmpty(sp.getString(XZContranst.cityName, ""))){
			area_tv.setText(sp.getString(XZContranst.cityName, ""));
		}
		signature_tv.setText(sp.getString(XZContranst.signature, ""));
	}
	
	private void editData(){
		Editor edit = sp.edit();
		if(posi==1){
			edit.putString(XZContranst.face, headportrait);
		}else if(posi==3){
			sex_tv.setText(mCurrentProviceName);
			if(TextUtils.equals(mCurrentProviceName, "男")){
				edit.putString(XZContranst.sex, "1");
			}else if(TextUtils.equals(mCurrentProviceName, "女")){
				edit.putString(XZContranst.sex, "2");
			}
			
		}else if(posi==5){
			age_tv.setText(mCurrentProviceName);
			edit.putString(XZContranst.age, mCurrentProviceName);
		}else if(posi==6){
			area_tv.setText(province+city+country);
//			json.put("province", key);
//			json.put("city", key);
//			json.put("area", key);
//			json.put("address", key);
		}
		edit.commit();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == head_layout) {// 头像切换
			new PopupWindows(this, head_layout);
		} else if (v == nickname_layout) {// 昵称
			Jmup("昵称", 11);
		} else if (v == sex_layout) {// 性别
			new PopAgeOrSex(this, sex_layout, 1);
			mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
					mProvinceDatas));
		} else if (v == profession_layout) {// 职业
			Jmup("职业", 12);
		} else if (v == age_layout) {// 年龄
			new PopAgeOrSex(this, age_layout, 2);
			mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
					mProvinceDatas));
		} else if (v == area_layout) {// 地区
			Intent intent = new Intent(this, AreaActivity.class);
			startActivityForResult(intent, 14);
		} else if (v == signature_layout) {// 签名
			Intent intent = new Intent(this, SignatureActivity.class);
			intent.putExtra("name", sp.getString(XZContranst.signature, ""));
			startActivityForResult(intent, 13);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case 1:// 如果是直接从相册获取
			try {
				startPhotoZoom(data.getData());
			} catch (Exception e) {
			}

			break;

		case 2:// 如果是调用相机拍照时
			try {
				if (data != null){
					File temp = new File(Environment.getExternalStorageDirectory()
							+ "/xiaoma.jpg");
					startPhotoZoom(Uri.fromFile(temp));
				}

			} catch (Exception e) {
			}

			break;

		case 3:// 取得裁剪后的图片
			/**
			 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			 * 当前功能时，会报NullException，小马只 在这个地方加下，大家可以根据不同情况在合适的 地方做判断处理类似情况
			 */
			if (data != null) {
				setPicToView(data);
			}
			break;
		case 11:// 修改昵称
			nickname_tv.setText(sp.getString(XZContranst.nickname, ""));
			break;
		case 12:// 修改职业
			profession_tv.setText(sp.getString(XZContranst.work, ""));
			break;
		case 13:// 修改签名
			signature_tv.setText(sp.getString(XZContranst.signature, ""));
			break;
		case 14:// 修改省市区
			if(data!=null){
				province=data.getStringExtra("province");
				province_id=data.getStringExtra("province_id");
				city=data.getStringExtra("city");
				city_id=data.getStringExtra("city_id");
				country=data.getStringExtra("country");
				country_id=data.getStringExtra("country_id");
				area_tv.setText(province+city+country);
				editInfo(6,province+city+country);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void Jmup(String name, int type) {
		Intent intent = new Intent(this, UserInfo_ChangeActivity.class);
		intent.putExtra("name", name);
		intent.putExtra("type", type);
		startActivityForResult(intent, type);
	}

	// 选择相册还是拍照
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
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
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用相机

					// 指定拍照照片存放位置为pfile文件夹
					// createImageFile()下文的方法用来指定文件的路径以及名称

					// 下面这句指定调用相机拍照后的照片存储的路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(Environment
									.getExternalStorageDirectory(),
									"xiaoma.jpg")));
					startActivityForResult(intent, 2);
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_PICK, null);

					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, 1);
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

	// 选性别年龄
	public class PopAgeOrSex extends PopupWindow {

		public PopAgeOrSex(Context mContext, View parent, final int type) {

			View view = View.inflate(mContext, R.layout.item_popsexorage, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			addaddress_wheel = (LinearLayout) view
					.findViewById(R.id.addaddress_wheel_layout);
			addaddress_wheel.startAnimation(AnimationUtils.loadAnimation(
					mContext, R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();
			addaddress_wheel_title= (TextView) view
					.findViewById(R.id.addaddress_wheel_title);
			addaddress_wheel_cancel = (TextView) view
					.findViewById(R.id.addaddress_wheel_cancel);
			addaddress_wheel_sure = (TextView) view
					.findViewById(R.id.addaddress_wheel_sure);
			mProvince = (WheelView) view.findViewById(R.id.id_wheelview);
			// 添加change事件
			// 省市区各显示几个数据
			mProvince.setVisibleItems(5);
			if (type == 1) {// 性别
				addaddress_wheel_title.setText("性别");
				mProvinceDatas = new String[] { "男", "女" };
			} else {
				addaddress_wheel_title.setText("年龄");
				mProvinceDatas = new String[] { "0-20", "20-30", "30-40",
						"40-50", "50-100" };
			}

			addaddress_wheel_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					dismiss();
				}
			});
			addaddress_wheel_sure.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try {
						int pCurrent = mProvince.getCurrentItem();
						mCurrentProviceName = mProvinceDatas[pCurrent];
						
						if (isConnect()) {
							if (type == 1) {//
								posi=3;
								editInfo(posi, String.valueOf(pCurrent+1));
							} else {
								posi=5;
								editInfo(posi,mCurrentProviceName);
							}
							
						}else{
							ToastUtil.showToast(getApplicationContext(),getResources().getString(R.string.notnetwork));
						}
						

					} catch (Exception e) {
					}
					dismiss();
				}
			});
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	@SuppressWarnings("deprecation")
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			photo = extras.getParcelable("data");
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();// 字节数组流
				/*
				 * bitmap.compress(format, quality, stream) 第一个参数format为压缩的格式
				 * 可设置JPEG或PNG格式； 第二个参数quality为图像压缩比的值,0-100.0
				 * 意味着图片压缩率,100意味着高质量压缩 压缩率为0 第三个参数stream为输出流
				 */
				photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				/*
				 * 将图片写入CameraUtil.getAlbumDir()文件夹下，并重命名 new
				 * File(CameraUtil.getAlbumDir
				 * (),file.getName())包含了File的构造方法，指定了文件目录以及文件名称的实例化File对象
				 */
				FileOutputStream fos = new FileOutputStream(new File(
						CameraUtil.getAlbumDir(), "heading" + ".jpg"));
				int options = 100;
				/*
				 * baos.toByteArray()获得内循环充中的数据 如果大于120kb则再次压缩，做多压缩三次
				 */
				while (baos.toByteArray().length / 1024 > 50 && options != 10) {
					baos.reset();// reset()重新定位
					/*
					 * 压缩图片为 JPEG模式 ；压缩率0；输出流对象baos
					 */
					photo.compress(Bitmap.CompressFormat.JPEG, options, baos);
					options -= 30;
				}
				fos.write(baos.toByteArray());
				fos.close();
				fos.flush();
				fos.close();// 关闭文件输出流 先打开的后关闭
				// if (!bitmap.isRecycled()) {
				// bitmap.recycle();
				// Log.v("TAG", "保存图片释放biemap");
				// }

				handler.sendEmptyMessage(9);

			} catch (Exception e) {
				LogUtil.e("MyInfoActivity2", "创建文件错误" + e.toString());
			}

		}
	}

	private void getData() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.index;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "获取信息提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "获取信息返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					JSONObject djson = jsonj.getJSONObject("data");

					Editor edit = sp.edit();
					edit.putString(XZContranst.work, djson.getString("work"));
					edit.putString(XZContranst.face, djson.getString("face"));
					edit.putString(XZContranst.sex, djson.getString("sex"));
					edit.putString(XZContranst.nickname,
							djson.getString("nickname"));
					edit.putString(XZContranst.master,
							djson.getString("master"));
					edit.putString(XZContranst.id, djson.getString("id"));
//					edit.putString(XZContranst.coordinate_x,
//							djson.getString("coordinate_x"));
//					edit.putString(XZContranst.coordinate_y,
//							djson.getString("coordinate_y"));
					edit.putString(XZContranst.username,
							djson.getString("username"));
					edit.putString(XZContranst.province,djson.getString("province"));
					edit.putString(XZContranst.city, djson.getString("city"));
					edit.putString(XZContranst.area, djson.getString("area"));
					edit.putString(XZContranst.provinceName,djson.getString("provinceName"));
					edit.putString(XZContranst.cityName, djson.getString("cityName"));
					edit.putString(XZContranst.areaName, djson.getString("areaName"));
					edit.putString(XZContranst.address,
							djson.getString("address"));
					edit.putString(XZContranst.age, djson.getString("age"));
					edit.putString(XZContranst.if_guide,
							djson.getString("if_guide"));
					edit.putString(XZContranst.if_driver,
							djson.getString("if_driver"));
					edit.putString(XZContranst.signature,
							djson.getString("signature"));
					edit.putString(XZContranst.mobile,
							djson.getString("mobile"));
					edit.commit();
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

	// 上传头像
	private void uploadHead() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				String url = HttpUrl.uploadify;
				// if(photoPath!=null){
				file1 = new File("/sdcard/xizang/heading.jpg");
				// }
				Map<String, String> word = new HashMap<String, String>();

				Map<String, File> files = new HashMap<String, File>();
				if (file1 != null && file1.exists()) {
					files.put("file_box1", file1);
				}

				String request = UploadUtil.uploadFile(word, files, url);

				JSONObject jsonj = null;
				String code = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "上传图片返回的数据" + jsonj.toString());

					code = jsonj.getString("status");// 1成功
					info = jsonj.getString("msg");
					headportrait =jsonj.optString("img");// 图片网络地址全称

				} catch (Exception e) {
					LogUtil.e(TAG, "上传图片解析错误" + e.toString());
				}

				if (TextUtils.equals(code, "1")) {
					handler2.sendEmptyMessage(2);
				} else if (TextUtils.equals(code, "0")) {
					handler.sendEmptyMessage(-2);
				}else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}
	
	//修改资料
	private void editInfo(final int i,final String key){
		if(i!=1){
			pd.show();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				JSONObject data =StringUtils.setDataJSON(sp.getString(XZContranst.id, null), i, key,
						province_id,city_id,country_id);
				String url = HttpUrl.edit_info;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "修改资料提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "修改资料返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					
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
