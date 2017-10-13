package com.power.travel.xixuntravel.activity;

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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.NationModel;
import com.power.travel.xixuntravel.model.WorkageModel;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 申请导游
 * 
 * @author fan
 * 
 */
public class Apply_guideActivity extends BaseActivity implements
        OnCheckedChangeListener {

	private ImageView back, guide_card, identity_card;
	private TextView title, guideworkage_tv, guidename_tv, guidenation_tv;
	private EditText feedback_content;
	private RelativeLayout guidename_layout, guidenation_layout,
			guideworkage_layout;
	private CheckBox apply_guide_sex;
	private Button apply_guide;
	private String info, TAG = "Apply_guideActivity", pic_iv, uploadType, sex="2";
	// , guide_card_iv,identity_card_iv
	private boolean pic1 = false;
	private boolean pic2 = false;
	SharedPreferences sp;
	private ProgressDialogUtils pd;

	private TextView addaddress_wheel_cancel, addaddress_wheel_title,
			addaddress_wheel_sure;
	private WheelView mProvince;// 省的WheelView控件
	private String[] mProvinceDatas;// 工龄
	private String mCurrentProviceName;// 选择的工龄
	Bitmap photo;
	File file1, file2;
	List<WorkageModel> workageList = new ArrayList<WorkageModel>();
	List<NationModel> nationList = new ArrayList<NationModel>();

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
				} else {
					identity_card.setImageBitmap(photo);
				}

//				if (isConnect()) {
//					uploadHead();
//				} else {
//					ToastUtil.showToast(getApplicationContext(), getResources()
//							.getString(R.string.notnetwork));
//				}
			} else if (msg.what == 2) {// 上传头像成功
				uploadInfo();
			// ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -2) {// 上传头像失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 3) {// 申请导游成功
				Editor edit = sp.edit();
				edit.putString(XZContranst.if_guide, "2");
				edit.commit();
				ToastUtil.showToast(getApplicationContext(), info);
				finish();
			} else if (msg.what == -3) {// 申请导游失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && Apply_guideActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_applyguide);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		// getWorkageData();
		// getnationData();
	}

	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "加载数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("成为导游");
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
		apply_guide = (Button) findViewById(R.id.apply_guide);
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
		apply_guide.setOnClickListener(this);
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
		} else if (v == guide_card) {// 导游证
			uploadType = "guide";
			new PopupWindows(this, guide_card);
		} else if (v == identity_card) {// 身份证
			uploadType = "identity";
			new PopupWindows(this, identity_card);
		} else if (v == apply_guide) {// 提交信息
			if (validate()) {
				if (isConnect()) {
//					uploadInfo();
					uploadHead();
				} else {
					ToastUtil.showToast(getApplicationContext(), getResources()
							.getString(R.string.notnetwork));
				}
			}
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
			ToastUtil.showToast(getApplicationContext(), "请选择工龄!");
			return false;
		}
		if (TextUtils.isEmpty(guidenation_tv.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请输入民族!");
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
			ToastUtil.showToast(getApplicationContext(), "请上传导游证!");
			return false;
		}
		if (!pic2) {
			ToastUtil.showToast(getApplicationContext(), "请上传身份证!");
			return false;
		}
		// if (TextUtils.isEmpty(guide_card_iv)) {
		// ToastUtil.showToast(getApplicationContext(), "请上传导游证!");
		// return false;
		// }
		// if (TextUtils.isEmpty(identity_card_iv)) {
		// ToastUtil.showToast(getApplicationContext(), "请上传身份证!");
		// return false;
		// }
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
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/xiaoma.jpg");
				startPhotoZoom(Uri.fromFile(temp));
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
				} else {
					fos = new FileOutputStream(new File(
							CameraUtil.getAlbumDir(), "heading2" + ".jpg"));
					pic2=true;
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
			// addaddress_wheel = (LinearLayout) view
			// .findViewById(R.id.addaddress_wheel_layout);
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
				// }
				Map<String, String> word = new HashMap<String, String>();

				Map<String, File> files = new HashMap<String, File>();
				if (file1 != null && file1.exists()) {
					files.put("file_box1", file1);
				}

				if (file2 != null && file2.exists()) {
					files.put("file_box2", file2);
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
					// } else {
					// identity_card_iv = jsonj.optString("img");// 图片网络地址全称
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

	// 申请导游
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
					data.put("nation", guidenation_tv.getText().toString());
					data.put("other", feedback_content.getText().toString());
					data.put("upload_data", pic_iv);
					// data.put("upload_data",guide_card_iv+","+identity_card_iv);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.apply_guide;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "申请导游提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "申请导游返回的数据" + jsonj.toString());

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

	/**
	 * 获取工龄
	 */
	private void getWorkageData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "1");
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
						workageList = JSON.parseArray(arry.toString(),
								WorkageModel.class);
						mProvinceDatas = new String[arry.length()];
						for (int i = 0; i < arry.length(); i++) {
							JSONObject temp = (JSONObject) arry.get(i);
							mProvinceDatas[i] = temp.getString("val");

						}
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
	 * 获取民族
	 */
	private void getnationData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "1");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.nation;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "民族获取提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "民族获取返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						nationList = JSON.parseArray(arry.toString(),
								NationModel.class);
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

}
