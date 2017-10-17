package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.ImageViewListAdapter;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.app.SystemBarTintManager;
import com.power.travel.xixuntravel.model.AllTripModel;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.GradationScrollView;
import com.power.travel.xixuntravel.weight.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * 行程（约伴）详情
 * @author fan
 *
 */

public class TripDetailActivity extends FragmentActivity implements GradationScrollView.ScrollViewListener,OnClickListener {

	private ImageView back,sender_sex,sender_head,sender_head_bg,title_iv;
	private TextView title,sender_name,sender_sign,tripdetail_title,tripdetail_time,tripdetail_see,
	tripdetail_comment,tripdetail_content,tripdetail_sdtime,comment_num,youche_layout;
	private EditText tripdetail_comment_edit;
	private RelativeLayout sdtime_layout,yueban_layout,pinche_layout,
	addaddress_title;
	private LinearLayout comment_layout;
	private MyListView tripdetail_listview;
	ImageViewListAdapter adapter;
	private AllTripModel allTripModel;
	MasterModel mMasterModel;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	private String data, TAG = "TripDetailActivity", info, id;
	DisplayImageOptions options,options2;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayMetrics dm;
	private LinearLayout.LayoutParams imagebtn_params;
	private GradationScrollView scrollView;
	private int height;
	private String follow;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				//TODO 拿到follow字段判断是否关注，显示不同图片 2017年9月14日 19:12:02
				/*follow = allTripModel.getFollow();
                if (TextUtils.equals(follow,"0")){
                    item_alltrip_attentin.setText("关注");
                    item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
                }else {
                    item_alltrip_attentin.setText("已关注");
                    item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
                }*/
                setData();
				getData1();

			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 2) {// 成功关注
				ToastUtil.showToast(getApplicationContext(), "关注成功");
				item_alltrip_attentin.setText("已关注");
				item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
				getData1();
			}else if (msg.what == -2) {// 关注失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 3){
				ToastUtil.showToast(getApplicationContext(),"取消关注");
				item_alltrip_attentin.setText("关注");
				item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
				getData1();
			}else if (msg.what == 6){
				follow = allTripModel.getFollow();
				if (TextUtils.equals(follow,"0")){
					item_alltrip_attentin.setText("关注");
					item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
				}else {
					item_alltrip_attentin.setText("已关注");
					item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
				}
				setData1();
			}
			if (pd != null && TripDetailActivity.this != null) {
				pd.dismiss();
			}
		}
	};




	private RelativeLayout item_alltrip_attentin_layout;
	private ImageView item_alltrip_attentin_type;
	private TextView item_alltrip_attentin;
	private String mid;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		/*
		 * 去除界面顶部灰色部分项目名字的方法 局限性：在每个界面分别添加才管用
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
		requestWindowFeature(0);// 上下两行代码去掉项目名字
		setTranslucentStatus();
		setContentView(R.layout.activity_tripdetail);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		initListeners();
		initgetIntent();
	}

	private void setData1() {
		if (TextUtils.equals(mMasterModel.getFollow(),"1")){
			item_alltrip_attentin.setText("已关注");
			item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
		}else {
			item_alltrip_attentin.setText("关注");
			item_alltrip_attentin_type.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
		}
	}

	private void getData1() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid",mid);
					data.put("fid",sp.getString(XZContranst.id,null));
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
					mMasterModel= JSON.parseObject(djson.toString(), MasterModel.class);

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(6);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(-2);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
		super.onDestroy();
	}
	
	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("约伴详情");
		sender_head=(ImageView)findViewById(R.id.sender_head);
		sender_name= (TextView) findViewById(R.id.sender_name);
		sender_sex= (ImageView) findViewById(R.id.sender_sex);
		sender_sign= (TextView) findViewById(R.id.sender_sign);
		sender_head_bg= (ImageView) findViewById(R.id.sender_head_bg);
		sender_head_bg.setAlpha(0.7f);
		title_iv= (ImageView) findViewById(R.id.title_iv2);
		title_iv.setVisibility(View.VISIBLE);
		tripdetail_title= (TextView) findViewById(R.id.tripdetail_title);
		tripdetail_time= (TextView) findViewById(R.id.tripdetail_time);
		tripdetail_see= (TextView) findViewById(R.id.tripdetail_see);
		tripdetail_comment= (TextView) findViewById(R.id.tripdetail_comment);
		tripdetail_content= (TextView) findViewById(R.id.tripdetail_content);
		tripdetail_sdtime= (TextView) findViewById(R.id.tripdetail_sdtime);
		comment_num= (TextView) findViewById(R.id.tripdetail_comment_num);
		sdtime_layout=(RelativeLayout)findViewById(R.id.tripdetail_sdtime_layout);
		yueban_layout=(RelativeLayout)findViewById(R.id.tripdetail_yueban_layout);
		youche_layout=(TextView)findViewById(R.id.tripdetail_youche_layout);
		pinche_layout=(RelativeLayout)findViewById(R.id.tripdetail_pinche_layout);
		addaddress_title=(RelativeLayout)findViewById(R.id.addaddress_title);
		tripdetail_listview=(MyListView)findViewById(R.id.tripdetail_listview);
		comment_layout=(LinearLayout)findViewById(R.id.tripdetail_comment_layout);
		tripdetail_comment_edit=(EditText)findViewById(R.id.tripdetail_comment_edit);
		scrollView=(GradationScrollView)findViewById(R.id.scrollview);
		//新增关注
		item_alltrip_attentin_layout = (RelativeLayout) findViewById(R.id.item_alltrip_attentin_layout);
		item_alltrip_attentin_type =(ImageView) findViewById(R.id.item_alltrip_attentin_type);
		item_alltrip_attentin = (TextView)findViewById(R.id.item_alltrip_attentin);
		item_alltrip_attentin_layout.setOnClickListener(this);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(false)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
		.displayer(new RoundedBitmapDisplayer(100))//设置用户加载图片task(这里是圆角图片显示)
		.build();
		
		options2 = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(false)// 是否緩存都內存中
		.cacheOnDisc(true)// 是否緩存到sd卡上
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
//		.displayer(new RoundedBitmapDisplayer(100))//设置用户加载图片task(这里是圆角图片显示)
		.build();
		
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(TripDetailActivity.this));
	}

	private void initListener() {
		back.setOnClickListener(this);
		comment_layout.setOnClickListener(this);
		sender_head.setOnClickListener(this);
		title_iv.setOnClickListener(this);
		tripdetail_comment_edit.setOnClickListener(this);
	}
	
	/**
     * 获取顶部图片高度后，设置滚动监听
     */
    private void initListeners() {

        ViewTreeObserver vto = sender_head_bg.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	addaddress_title.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                height = addaddress_title.getHeight();

                scrollView.setScrollViewListener(TripDetailActivity.this);
            }
        });
    }

	private void initgetIntent() {
		Intent intent=getIntent();
		id=intent.getStringExtra("id");
		mid = intent.getStringExtra("mid");
//		allTripModel=(AllTripModel)intent.getExtras().getSerializable("model");
//		id = allTripModel.getId();
		getData();

	}
	
	/**
     * 滑动监听
     * @param scrollView
     * @param x
     * @param y
     * @param oldx
     * @param oldy
     */
	@Override
	public void onScrollChanged(GradationScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		 if (y <= 0) {   //设置标题的背景颜色
			 addaddress_title.setBackgroundColor(Color.argb((int) 0, 27,27,27));
	        } else if (y > 0 && y <= height) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
	            float scale = (float) y / height;
	            float alpha = (255 * scale);
//	            textView.setTextColor(Color.argb((int) alpha, 255,255,255));
	            addaddress_title.setBackgroundColor(Color.argb((int) alpha,  27,27,27));
	        } else {    //滑动到banner下面设置普通颜色
	        	addaddress_title.setBackgroundColor(Color.argb((int) 255,  27,27,27));
	        }
		
	}
	
	private void setData(){
		/**
		 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
		 */
		imageLoader.displayImage(allTripModel.getFace(), sender_head,
				options, animateFirstListener);
		
		/*imageLoader.displayImage(allTripModel.getFace(), sender_head_bg,
				options2, animateFirstListener);*/
		
		sender_name.setText(allTripModel.getNickname());
		if(TextUtils.equals(allTripModel.getSex(), "1")){
			sender_sex.setImageDrawable(getResources().getDrawable(R.drawable.my_boy));
		}else if(TextUtils.equals(allTripModel.getSex(), "2")){
			sender_sex.setImageDrawable(getResources().getDrawable(R.drawable.my_girle));
		}
		sender_sign.setText(allTripModel.getSignature());
		tripdetail_title.setText(allTripModel.getTitle());
		tripdetail_time.setText(StringUtils.getStrTimeMonthDaySF(allTripModel.getAddtime()));
		tripdetail_see.setText(allTripModel.getOnclick()+"浏览");
		tripdetail_comment.setText(allTripModel.getComm_count()+"评论");
		tripdetail_content.setText(allTripModel.getInfo());
		tripdetail_sdtime.setText(StringUtils.getStrTimeYMD2(allTripModel.getStarttime())+"-"+
				StringUtils.getStrTimeYMD2(allTripModel.getEndtime()));
		sdtime_layout.setVisibility(View.VISIBLE);
		if(TextUtils.equals(allTripModel.getIs_at(), "1")){
			yueban_layout.setVisibility(View.VISIBLE);
		}else{
			yueban_layout.setVisibility(View.GONE);
		}
		/*if(TextUtils.equals(allTripModel.getIs_car(), "1")){
			youche_layout.setVisibility(View.VISIBLE);
		}else{
			youche_layout.setVisibility(View.GONE);
		}*/
		youche_layout.setText(allTripModel.getAddress());
		if(TextUtils.equals(allTripModel.getIs_carpool(), "1")){
			pinche_layout.setVisibility(View.VISIBLE);
		}else{
			pinche_layout.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(allTripModel.getArr_img())){
			adapter=new ImageViewListAdapter(this, allTripModel.getArr_img());
			tripdetail_listview.setAdapter(adapter);
			scrollView.smoothScrollTo(0,0);//防止scrollView 跳到listview位置
		}
		comment_num.setText(allTripModel.getComm_count());
		comment_layout.setVisibility(View.VISIBLE);
	}
	@Override
	public void onClick(View v) {
		if (v == back) {
			finish();
		}else if(v==tripdetail_comment_edit){
			Intent intnet=new Intent(this,TripDetilCommentActivity.class);
			intnet.putExtra("id", id);
			startActivity(intnet);
		}else if(v==comment_layout){//评论
			Intent intnet=new Intent(this,TripDetilCommentActivity.class);
			intnet.putExtra("id", id);
			startActivity(intnet);
		}else if(v==sender_head){//头像
			MasterModel mMasterModel=new MasterModel();
			mMasterModel.setMid(allTripModel.getMid());
			Intent intent=new Intent(this,UserCenterActivity.class);
			intent.putExtra("model", mMasterModel);
			startActivity(intent);
		}else if(v==title_iv){//聊天
			if (sp.getBoolean(XZContranst.if_login, false)) {

				RongIM.getInstance().startPrivateChat(this,allTripModel.getMid() , allTripModel.getNickname());

			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
		}else if (v == item_alltrip_attentin_layout){//关注
			SharedPreferences sp;
			sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			if (sp.getBoolean(XZContranst.if_login, false)) {
				if (TextUtils.equals(mMasterModel.getFollow(),"1")){//关注变取消关注
					praise_cancel(allTripModel.getMid());

				}else {//点击关注
					praise(allTripModel.getMid());
				}
			}else {
				startActivity(new Intent(this, LoginActivity.class));
			}

		}
	}

	private void praise_cancel(final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
//					data.put("id", id);
					data.put("id", mMasterModel.getFollow_id());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.follow_cancel;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "取消关注提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "取消关注返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
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

	private void praise(final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("fid", id);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.followAdd;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "关注提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "关注返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
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

	private void getData() {
		pd = ProgressDialogUtils.show(this, "获取数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("id", id);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.all_tripdetail;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "详情提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "详情返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					JSONObject jsond=jsonj.getJSONObject("data");
					allTripModel= JSON.parseObject(jsond.toString(), AllTripModel.class);
					

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
	 * 设置状态栏背景状态 
	 */
	private void setTranslucentStatus() {
		setStatusBarTranslucent(true);
//		setNavigationBarTranslucent(true);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);// 状态栏的背景颜色(0表示无背景)  
		
//		tintManager.setNavigationBarTintEnabled(true);
//		tintManager.setNavigationBarTintResource(R.color.title_bar_bg);//导航栏背景颜色（0表示无背景）
	}

	/**
	 * 设置状态栏是否透明 
	 * 
	 * @param isTransparent
	 */
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
	}

}
