package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.Friend;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.net.UploadUtil;
import com.power.travel.xixuntravel.utils.CameraUtil;
import com.power.travel.xixuntravel.utils.DialogUtils;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * 绑定
 * 
 * @author fan
 * 
 */
/*public class Regist_BangDingActivity extends BaseActivity implements
        OnCheckedChangeListener, UserInfoProvider {*/
public class Regist_BangDingActivity extends BaseActivity implements
		OnCheckedChangeListener {

	private ImageView back;
	private TextView title, regist_getcode, regist_check_tv;
	private EditText regist_account, regist_code;
	private Button regist_next;
	private CheckBox mCheckBox;
	private RoundImageView regist_head;
	ArrayList<String> List = new ArrayList<String>();
	private String data, TAG = "RegistActivity", info, headportrait,openid,type,touxiang,nicheng;
	private ProgressDialogUtils pd;
	private TimeCount time;// 重新获取倒计时
	Bitmap photo;
	File file1;
	SharedPreferences sp,spLocation;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private java.util.List<Friend> userIdList;
	private Intent intent1;


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
//				ToastUtil.showToast(getApplicationContext(), info);
				time.start();// 开始计时
//				regist_code.setText(List.get(0) + List.get(1) + List.get(2)
//						+ List.get(3) + List.get(4) + List.get(5));
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况

			} else if (msg.what == 9) {// 上传头像
//				regist_head.setImageBitmap(photo);
				uploadHead();
			} else if (msg.what == 11) {// 上传头像成功
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration
						.createDefault(Regist_BangDingActivity.this));
				/**
				 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
				 */
				imageLoader.displayImage(headportrait, regist_head,
						options, animateFirstListener);
//				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -11) {// 上传头像失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {// 展示头像
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration
						.createDefault(Regist_BangDingActivity.this));
				imageLoader.displayImage(touxiang, regist_head,
						options, animateFirstListener);
			} else if(msg.what==3){//完成绑定
				Intent intent = new Intent();
				setResult(301, intent);
				finish();
			} else if(msg.what==-3){//绑定是失败
				Intent intent = new Intent(Regist_BangDingActivity.this,ChuangJianPwdActivity.class);
				intent.putExtra("openid",openid);
				intent.putExtra("type",type);
				intent.putExtra("mobile",regist_account.getText().toString());
				intent.putExtra("nickname",nicheng);
				intent.putExtra("face",touxiang);
				startActivityForResult(intent,5);
				finish();
			}
			if (pd != null && Regist_BangDingActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_regist_bangding);
		MyApplication.getInstance().addActivity(this);

		initView();
		initGetIntentData();
//		MyApplication.getInstance().delectActivity(LoginActivity.class);
		initListener();
		Random();
	}

	private void initGetIntentData() {
		Intent intnet=getIntent();
		openid=intnet.getStringExtra("openid");
		type=intnet.getStringExtra("type");
		touxiang=intnet.getStringExtra("touxiang");
		nicheng=intnet.getStringExtra("nicheng");
//		Log.e(TAG,"传递的信息" + openid+"\n"+type+"\n"+touxiang+"\n"+nicheng);
		handler.sendEmptyMessage(2);
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		spLocation= this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "获取数据...");
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("绑定");
		regist_getcode = (TextView) findViewById(R.id.regist_getcode);
		regist_check_tv = (TextView) findViewById(R.id.regist_check_tv);
		regist_account = (EditText) findViewById(R.id.regist_account);
		regist_code = (EditText) findViewById(R.id.regist_code);
		regist_next = (Button) findViewById(R.id.regist_next);
		mCheckBox = (CheckBox) findViewById(R.id.regist_checkbox);
		regist_head = (RoundImageView) findViewById(R.id.regist_head);
		time = new TimeCount(50000, 1000);// 构造CountDownTimer对象
		
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
		regist_getcode.setOnClickListener(this);
		regist_next.setOnClickListener(this);
		regist_check_tv.setOnClickListener(this);
		mCheckBox.setOnCheckedChangeListener(this);
		regist_head.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == regist_getcode) {// 获取验证码
			if (TextUtils.isEmpty(regist_account.getText().toString())) {
				regist_getcode.setFocusableInTouchMode(true);
				regist_getcode.requestFocus();
				regist_getcode.findFocus();
				showEnsure("请输入手机号!");
			} else {
				if (isConnect()) {
					getCode();
				}else{
					ToastUtil.showToast(getApplicationContext(),getResources().getString(R.string.notnetwork));
				}
			}
		} else if (v == regist_next) {// 下一步
			if (validate()) {
				Login_Binding();
			}
		} else if (v == regist_check_tv) {// 协议
			startActivity(new Intent(this, Regist_AgreementActivity.class));
		} else if (v == regist_head) {// 头像
//			new PopupWindows(this, regist_head);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {

		if (v == mCheckBox) {
			// if(isChecked){
			// showDialog("查看协议");
			// }
		}
	}

	private boolean validate() {
		if (TextUtils.isEmpty(regist_account.getText().toString())) {
			regist_getcode.setFocusableInTouchMode(true);
			regist_getcode.requestFocus();
			regist_getcode.findFocus();
			showEnsure("请输入手机号!");
			return false;
		}

		if (TextUtils.isEmpty(regist_code.getText().toString())) {
			regist_code.setFocusableInTouchMode(true);
			regist_code.requestFocus();
			regist_code.findFocus();
			showEnsure("请输入验证码!");
			return false;
		}
		
//		if(TextUtils.isEmpty(headportrait)){
//			showEnsure("请上传头像!");
//			return false;
//		}
		return true;
	}

	/**
	 * 生成随机数
	 */
	private void Random() {
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			int randomInt = random.nextInt(9);
			System.out.println("生成的randomInt=" + randomInt);
			List.add(String.valueOf(randomInt));
		}
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			regist_getcode.setText("验证");
			regist_getcode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			regist_getcode.setClickable(false);
			regist_getcode.setText(millisUntilFinished / 1000 + "秒" + "重新获取");
		}
	}

	private void showDialog(String tipMessage) {
		DialogUtils.show(this, tipMessage,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
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
		case 4:
			Intent intent = new Intent();
			intent.putExtra("mobile", regist_account.getText().toString());
			intent.putExtra("password", data.getStringExtra("password"));
			setResult(101, intent);
			finish();
			break;

			case 5:
				if (data != null){
//					intent1 = data;
					handler.sendEmptyMessage(3);
				}

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				LogUtil.e(TAG, "创建文件错误" + e.toString());
			}

		}
	}

	// 获取短信验证码
	private void getCode() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mobile", regist_account.getText().toString());
					data.put("code", List.get(0) + List.get(1) + List.get(2)
							+ List.get(3) + List.get(4) + List.get(5));
					data.put("type", "binding");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.sendmessages;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "注册提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "注册返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

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
					headportrait = jsonj.optString("img");// 图片网络地址 全称
				

				} catch (Exception e) {
					LogUtil.e(TAG, "上传图片解析错误" + e.toString());
				}

				if (TextUtils.equals(code, "1")) {
					handler.sendEmptyMessage(11);
				} else {
					handler.sendEmptyMessage(-11);
				}

			}
		}).start();
	}
	
	/**
	 * 绑定
	 */
	private void Login_Binding() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("openid", openid);
					data.put("type", type);
					data.put("mobile", regist_account.getText().toString());
					data.put("nickname", nicheng);
					data.put("face", touxiang);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.openidBinding;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "绑定提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "绑定返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					JSONObject djson = jsonj.getJSONObject("data");
					if (TextUtils.equals(status, "1")) {
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
					handler.sendEmptyMessage(3);
				} else if (TextUtils.equals(status, "-1")) {
					handler.sendEmptyMessage(-3);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}
	
	/**
	 * 连接融云服务器
	 * 
//	 * @param token
	 */
	/*private void connectRongServer(String token) {
		LogUtil.e(TAG, "登陆用的token"+token);
		RongIM.connect(token, new ConnectCallback() {

			@Override
			public void onSuccess(String userId) {

//				Toast.makeText(getApplicationContext(), "connet server success",
//							Toast.LENGTH_SHORT).show();
				  Log.e("LoginActivity", "--onSuccess" + userId);
	                SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
	                editor.putString("loginid", userId);
	                editor.putBoolean("exit", false);
	                editor.putString("loginToken", sp.getString(XZContranst.token, ""));
	                editor.putString("loginnickname", sp.getString(XZContranst.nickname, ""));
	                editor.putString("loginPortrait", sp.getString(XZContranst.face, ""));
	                editor.apply();
	                userIdList = new ArrayList<Friend>();
	                //id  名字  头像 
	        		userIdList.add(new Friend(userId,sp.getString(XZContranst.nickname, ""),sp.getString(XZContranst.face, "")));
	        		*//**
	        		 * 设置当前用户信息，
	        		 * @param userInfo 当前用户信息
	        		 *//*
	        		RongIM.getInstance().setCurrentUserInfo(getUserInfo(userId));
	        		 *//**
	                 * 设置消息体内是否携带用户信息。
	                 * @param state 是否携带用户信息，true 携带，false 不携带。
	                 *//*
	                RongIM.getInstance().setMessageAttachedUserInfo(true);
	                RongIM.setUserInfoProvider(Regist_BangDingActivity.this, true);
			}

			@Override
			public void onError(ErrorCode errorCode) {
				
				Log.e(TAG,
						"connect failure errorCode is :" + errorCode.getValue());
			}

			@Override
			public void onTokenIncorrect() {
				Log.e(TAG, "token is error , please check token and appkey ");
			}
		});
	}
	
	@Override
	public UserInfo getUserInfo(String userId) {
		for (com.tibettraver.model.Friend i : userIdList) {
			if (i.getUserId().equals(userId)) {
				Log.e(TAG, i.getPortraitUri());
				return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
			}
		}
		return null;
	}*/

}
