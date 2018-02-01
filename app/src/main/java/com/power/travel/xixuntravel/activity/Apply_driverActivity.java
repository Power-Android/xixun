package com.power.travel.xixuntravel.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.power.travel.xixuntravel.adapter.CarTypeAdapter;
import com.power.travel.xixuntravel.adapter.CarageAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.CarModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.net.UploadUtil;
import com.power.travel.xixuntravel.utils.CameraUtil;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.widget.WheelView;
import com.power.travel.xixuntravel.widget.adapters.ArrayWheelAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 申请司机
 * 
 * @author fan
 * 
 */
public class Apply_driverActivity extends BaseActivity implements
        OnCheckedChangeListener {

	private ImageView back, guide_card, identity_card, car_photo;
	private TextView title, guideworkage_tv, guidename_tv, guidenation_tv,
			pinpai_tv, chexing_tv, carage_tv;
	private EditText feedback_content;
	private RelativeLayout guidename_layout, guidenation_layout,
			guideworkage_layout;
	private CheckBox apply_guide_sex;
	private Button apply_guide;
	private String info, TAG = "Apply_driverActivity", uploadType, sex="2", pic_iv;
	// , guide_card_iv,identity_card_iv,car_iv
	private boolean pic1 = false;
	private boolean pic2 = false;
	private boolean pic3 = false;
	SharedPreferences sp;
	private ProgressDialogUtils pd;
	 private Uri photoUri;
	public static final int PHOTO_REQUEST_TAKEPHOTO=2;

	private LinearLayout addaddress_wheel;
	private TextView addaddress_wheel_cancel, addaddress_wheel_title,
			addaddress_wheel_sure;
	private WheelView mProvince;// 省的WheelView控件
	private String[] mProvinceDatas;// 所有省
	private String mCurrentProviceName;// 当前省的名称
	Bitmap photo;
	File file1,file2,file3;
	private PopupWindow popupWindow;
	int clickType = 1;
	private List<CarModel> adapterList = new ArrayList<CarModel>();
	private List<CarModel> CarTypeList = new ArrayList<CarModel>();
	private ListView carListView;
	CarTypeAdapter pinpaiAdapter, chexingAdapter;
	CarageAdapter carageAdapter;
	private String pinpai, pinpaiid, chexing, chexingid;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功

			} else if (msg.what == 10) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 9) {// 展示头像
				if (TextUtils.equals(uploadType, "guide")) {
					guide_card.setImageBitmap(photo);
				} else if (TextUtils.equals(uploadType, "identity")) {
					identity_card.setImageBitmap(photo);
				} else {
					car_photo.setImageBitmap(photo);
				}

				// if (isConnect()) {
				// uploadHead();
				// } else {
				// ToastUtil.showToast(getApplicationContext(), getResources()
				// .getString(R.string.notnetwork));
				// }
			} else if (msg.what == 2) {// 上传头像成功
				uploadInfo();
				// ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -2) {// 上传头像失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 3) {// 申请司机成功
				Editor edit = sp.edit();
				edit.putString(XZContranst.if_driver, "2");
				edit.commit();
				ToastUtil.showToast(getApplicationContext(), info);
				finish();
			} else if (msg.what == -3) {// 申请司机失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 4) {// 成功
				initmPopupWindowView(clickType);
				switch (clickType) {
				case 1:
					popupWindow.showAsDropDown(pinpai_tv, 0, 0);
					break;
				case 2:
					popupWindow.showAsDropDown(chexing_tv, 0, 0);
					break;
				default:
					break;
				}
			} else if (msg.what == -4) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && Apply_driverActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_applydriver);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("成为司机");
		guidename_layout = (RelativeLayout) findViewById(R.id.guidename_layout);
		guidenation_layout = (RelativeLayout) findViewById(R.id.guidenation_layout);
		guideworkage_layout = (RelativeLayout) findViewById(R.id.guideworkage_layout);
		guideworkage_tv = (TextView) findViewById(R.id.guideworkage_tv);
		guidename_tv = (TextView) findViewById(R.id.guidename_tv);
		guidenation_tv = (TextView) findViewById(R.id.guidenation_tv);
		apply_guide_sex = (CheckBox) findViewById(R.id.apply_guide_sex);
		Drawable drawable = this.getResources().getDrawable(R.drawable.checkbox_sex);
		drawable.setBounds(0,0,120,60);
		apply_guide_sex.setCompoundDrawables(drawable,null,null,null);
		feedback_content = (EditText) findViewById(R.id.feedback_content);
		guide_card = (ImageView) findViewById(R.id.guide_card);
		identity_card = (ImageView) findViewById(R.id.identity_card);
		car_photo = (ImageView) findViewById(R.id.car_photo);
		apply_guide = (Button) findViewById(R.id.apply_guide);
		pinpai_tv = (TextView) findViewById(R.id.pinpai);
		chexing_tv = (TextView) findViewById(R.id.chexing);
		carage_tv = (TextView) findViewById(R.id.carage);
	}

	private void initListener() {
		back.setOnClickListener(this);
		guidename_layout.setOnClickListener(this);
		guidenation_layout.setOnClickListener(this);
		guidenation_tv.setOnClickListener(this);
		guideworkage_layout.setOnClickListener(this);
		apply_guide_sex.setOnCheckedChangeListener(this);
		guide_card.setOnClickListener(this);
		identity_card.setOnClickListener(this);
		car_photo.setOnClickListener(this);
		apply_guide.setOnClickListener(this);
		pinpai_tv.setOnClickListener(this);
		chexing_tv.setOnClickListener(this);
		carage_tv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == guidename_layout) {// 姓名
			jump(XZContranst.apply_guide_name, "姓名");
		} else if (v == guidenation_layout) {// 民族
			jump(XZContranst.apply_guide_nation, "民族");
		} else if (v == guideworkage_layout) {// 工龄
			new PopWorkAge(this, guideworkage_layout);
			mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
					mProvinceDatas));
		} else if (v == guide_card) {// 司机证
			uploadType = "guide";
			new PopupWindows(this, guide_card);
		} else if (v == identity_card) {// 身份证
			uploadType = "identity";
			new PopupWindows(this, identity_card);
		} else if (v == car_photo) {// 汽车照片
			uploadType = "car";
			new PopupWindows(this, identity_card);
		} else if (v == apply_guide) {// 提交信息
			if (validate()) {
				if (isConnect()) {
					// uploadInfo();
					uploadHead();
				} else {
					ToastUtil.showToast(getApplicationContext(), getResources()
							.getString(R.string.notnetwork));
				}
			}
		} else if (v == pinpai_tv) {// 品牌
			clickType = 1;
//			if (adapterList.isEmpty()) {// 第一次
				getData("0");
//			} else {
//				initmPopupWindowView(clickType);
//				popupWindow.showAsDropDown(pinpai_tv, 0, 0);
//			}
		} else if (v == chexing_tv) {// 车型
			if (TextUtils.isEmpty(pinpai)) {
				ToastUtil.showToast(getApplicationContext(), "请选择品牌！");
			} else {
				clickType = 2;
//				if (CarTypeList.isEmpty()) {
					getData(pinpaiid);
//				} else {
//					initmPopupWindowView(clickType);
//					popupWindow.showAsDropDown(chexing_tv, 0, 0);
//				}
			}
		} else if (v == carage_tv) {// 车龄
			clickType = 3;
			initmPopupWindowView(clickType);
			popupWindow.showAsDropDown(carage_tv, 0, 0);
		}
	}

	private boolean validate() {
		if (TextUtils.isEmpty(guidename_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请输入姓名!");
			return false;
		}
		if (apply_guide_sex.isChecked()) {
			sex = "2";
		} else {
			sex = "1";
		}
		if (TextUtils.isEmpty(guideworkage_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请选择驾龄!");
			return false;
		}
		if (TextUtils.isEmpty(guidenation_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请输入民族!");
			return false;
		}
		if (TextUtils.isEmpty(pinpai_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请选择品牌!");
			return false;
		}

		if (TextUtils.isEmpty(chexing_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请选择车型!");
			return false;
		}

		if (TextUtils.isEmpty(carage_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请选择车龄!");
			return false;
		}
		if (TextUtils.isEmpty(feedback_content.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请输入备注!");
			feedback_content.setFocusableInTouchMode(true);
			feedback_content.requestFocus();
			feedback_content.findFocus();
			return false;
		}
		if (!pic1) {
			ToastUtil.showToast(getApplicationContext(), "请上传驾驶证!");
			return false;
		}
		if (!pic2) {
			ToastUtil.showToast(getApplicationContext(), "请上传身份证!");
			return false;
		}
		if (!pic3) {
			ToastUtil.showToast(getApplicationContext(), "请上传汽车照片!");
			return false;
		}
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {

	}

	private void jump(int i, String str) {
		Intent intent = new Intent(this, UserInfo_setActivity.class);
		intent.putExtra("name", str);
		startActivityForResult(intent, i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case XZContranst.apply_guide_name:
			if (data != null) {
				guidename_tv.setText(data.getStringExtra("name"));
			}
			break;
		case XZContranst.apply_guide_nation:
			if (data != null) {
				guidenation_tv.setText(data.getStringExtra("name"));
			}
			break;
		case 1:// 如果是直接从相册获取
			try {
				startPhotoZoom(data.getData());
			} catch (Exception e) {
			}

			break;

		case 2:// 如果是调用相机拍照时
			try {
				Uri uri=null;

				if (data != null && data.getData() != null) {
					uri = data.getData();
				}

				if (uri == null) {
					if (photoUri != null) {
						uri = photoUri;
					}

					startPhotoZoom(uri);
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
		default:
			break;
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
				FileOutputStream fos;

				if (TextUtils.equals(uploadType, "guide")) {
					fos = new FileOutputStream(new File(
							CameraUtil.getAlbumDir(), "heading1" + ".jpg"));
					pic1=true;
				} else if (TextUtils.equals(uploadType, "identity")) {
					fos = new FileOutputStream(new File(
							CameraUtil.getAlbumDir(), "heading2" + ".jpg"));
					pic2=true;
				} else {
					fos = new FileOutputStream(new File(
							CameraUtil.getAlbumDir(), "heading3" + ".jpg"));
					pic3=true;
				}
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

	// 选性别年龄
	public class PopWorkAge extends PopupWindow {

		public PopWorkAge(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.item_popsexorage, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			addaddress_wheel = (LinearLayout) view
					.findViewById(R.id.addaddress_wheel_layout);
			// addaddress_wheel.startAnimation(AnimationUtils.loadAnimation(
			// mContext, R.anim.push_bottom_in_2));

			setAnimationStyle(R.style.PingjiaFade);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();
			addaddress_wheel_title = (TextView) view
					.findViewById(R.id.addaddress_wheel_title);
			addaddress_wheel_cancel = (TextView) view
					.findViewById(R.id.addaddress_wheel_cancel);
			addaddress_wheel_sure = (TextView) view
					.findViewById(R.id.addaddress_wheel_sure);
			mProvince = (WheelView) view.findViewById(R.id.id_wheelview);
			// 添加change事件
			// 省市区各显示几个数据
			mProvince.setVisibleItems(5);

			addaddress_wheel_title.setText("工龄");
			mProvinceDatas = new String[] { "1", "2", "3", "4", "5" };

			// 自定义view添加触摸事件
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					/**
					 * 单击内部空白区域，pop消失
					 */
					// if (popupwindow != null && popupwindow.isShowing()) {
					// dismiss();
					// return true;
					// }
					/**
					 * 单击pop外部区域，pop消失
					 */
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						dismiss();
						return true;
					}
					return false;
				}

			});

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
						guideworkage_tv.setText(mCurrentProviceName);

					} catch (Exception e) {
					}
					dismiss();
				}
			});
		}
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
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					SimpleDateFormat timeStampFormat = new SimpleDateFormat(
							"yyyy_MM_dd_HH_mm_ss");
					String filename = timeStampFormat.format(new Date());
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, filename);

					photoUri = getContentResolver().insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

					intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

					startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
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

	/**
	 * 品牌车型
	 * 
	 * @param type
	 */
	private void initmPopupWindowView(final int type) {

		View customView = getLayoutInflater().inflate(
				R.layout.item_cartype_layout, null, false);
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
		case 1:// 品牌
			carListView = (ListView) customView.findViewById(R.id.list_pinpai);
			pinpaiAdapter = new CarTypeAdapter(Apply_driverActivity.this,
					adapterList);
			carListView.setAdapter(pinpaiAdapter);
			break;
		case 2:// 车型
			carListView = (ListView) customView.findViewById(R.id.list_chexing);
			chexingAdapter = new CarTypeAdapter(Apply_driverActivity.this,
					CarTypeList);
			carListView.setAdapter(chexingAdapter);
			break;
		case 3:
			carListView = (ListView) customView.findViewById(R.id.list_carage);
			carageAdapter = new CarageAdapter(Apply_driverActivity.this,
					XZContranst.caragelist);
			carListView.setAdapter(carageAdapter);
			break;
		default:
			break;
		}

		carListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				switch (type) {
				case 1:
					pinpai = adapterList.get(position).getName();
					pinpaiid = adapterList.get(position).getId();
					pinpai_tv.setText(pinpai);
					break;
				case 2://
					chexing = CarTypeList.get(position).getName();
					chexingid = CarTypeList.get(position).getId();
					chexing_tv.setText(chexing);
					break;
				case 3://
					carage_tv.setText(XZContranst.caragelist[position]);
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
	 * 获取品牌车型
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

				String url = HttpUrl.Car;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "品牌车型提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "品牌车型返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					org.json.JSONArray arry = jsonj.getJSONArray("data");

					switch (clickType) {
					case 1:
						adapterList = JSON.parseArray(arry.toString(),
								CarModel.class);
						break;
					case 2:
						CarTypeList = JSON.parseArray(arry.toString(),
								CarModel.class);
						break;
					default:
						break;
					}
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

	// 上传头像
	private void uploadHead() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				String url = HttpUrl.uploadify;
				// if(photoPath!=null){
				file1 = new File("/sdcard/xizang/heading1.jpg");
				file2 = new File("/sdcard/xizang/heading2.jpg");
				file3 = new File("/sdcard/xizang/heading3.jpg");
				// }
				Map<String, String> word = new HashMap<String, String>();

				Map<String, File> files = new HashMap<String, File>();
				if (file1 != null && file1.exists()) {
					files.put("file_box1", file1);
				}
				if (file2 != null && file2.exists()) {
					files.put("file_box2", file2);
				}
				if (file3 != null && file3.exists()) {
					files.put("file_box3", file3);
				}

				String request = UploadUtil.uploadFile(word, files, url);

				JSONObject jsonj = null;
				String code = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "上传图片返回的数据" + jsonj.toString());

					code = jsonj.getString("status");// 1成功
					info = jsonj.getString("msg");
					pic_iv = jsonj.optString("img");// 图片网络地址全称
					// if (TextUtils.equals(uploadType, "guide")) {
					// guide_card_iv = jsonj.optString("img");// 图片网络地址全称
					// } else if (TextUtils.equals(uploadType, "identity")){
					// identity_card_iv = jsonj.optString("img");// 图片网络地址全称
					// }else{
					// car_iv = jsonj.optString("img");// 图片网络地址全称
					// }

				} catch (Exception e) {
					LogUtil.e(TAG, "上传图片解析错误" + e.toString());
				}

				if (TextUtils.equals(code, "1")) {
					handler.sendEmptyMessage(2);
				} else if (TextUtils.equals(code, "0")) {
					handler.sendEmptyMessage(-2);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}

	// 申请司机
	private void uploadInfo() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, ""));
					data.put("name", guidename_tv.getText().toString());
					data.put("sex", sex);
					data.put("worktime", guideworkage_tv.getText().toString());
					data.put("car_brand", pinpai);
					data.put("car_models", chexing);
					data.put("car_age", "5");
					data.put("nation", guidenation_tv.getText().toString());
					data.put("other", feedback_content.getText().toString());
					data.put("upload_data", pic_iv);
					// data.put("upload_data",guide_card_iv+","+identity_card_iv+","+car_iv);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.apply_driver;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "申请司机提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "申请司机返回的数据" + jsonj.toString());

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
