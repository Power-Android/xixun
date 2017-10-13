package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 推荐详情
 * @author fan
 *
 */
public class RecommendDetailActivity extends BaseActivity {

	private ImageView back, route_pic,zan_iv;
	private TextView title, route_title,onclick_tv,zan_tv;
	private WebView mWebView;
	private LinearLayout zan_layout;
	private String data, TAG = "RouteDetailActivity", info, id, biaoti, thumb,
			content, onclick,zanIf,zan;
	private int zannum;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private DisplayMetrics dm;
	private LinearLayout.LayoutParams imagebtn_params;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				if(TextUtils.equals(zanIf, "1")){//赞
					zan_iv.setImageDrawable(getResources().getDrawable(R.drawable.recommend_zan));
				}else{//不赞
					zan_iv.setImageDrawable(getResources().getDrawable(R.drawable.recommend_nozan));
				}
				zannum= Integer.valueOf(zan);
				route_title.setText(biaoti);
				onclick_tv.setText("阅读  "+onclick);
				zan_tv.setText(zan);
				if (content != null){
					mWebView.loadDataWithBaseURL(null, content, "text/html",
							"utf-8", null);
				}else {
					mWebView.setVisibility(View.GONE);
				}

				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration
						.createDefault(RecommendDetailActivity.this));
				/**
				 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
				 */
				imageLoader.displayImage(HttpUrl.Url + thumb, route_pic,
						options, animateFirstListener);
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 2) {// 成功点赞
				ToastUtil
				.showToast(getApplicationContext(), info);
				if(TextUtils.equals(zanIf, "1")){//赞变不赞
					zanIf="0";
					zannum=zannum-1;
					zan= String.valueOf(zannum);
					zan_iv.setImageDrawable(getResources().getDrawable(R.drawable.recommend_nozan));
					zan_tv.setText(String.valueOf(zannum));
				}else{//不赞变赞
					zanIf="1";
					zannum=zannum+1;
					zan= String.valueOf(zannum);
					zan_iv.setImageDrawable(getResources().getDrawable(R.drawable.recommend_zan));
					zan_tv.setText(String.valueOf(zannum));
				}
			}else if (msg.what == -2) {// 失败点赞
				ToastUtil
				.showToast(getApplicationContext(), info);
			}
			pd.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_recommenddetail);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		initgetIntent();

		if (isConnect()) {
			getData();
		} else {
			ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
		}
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		
		back = (ImageView) findViewById(R.id.back);
		route_pic = (ImageView) findViewById(R.id.route_pic);
		title = (TextView) findViewById(R.id.title);
		title.setText("推荐详情");
		mWebView = (WebView) findViewById(R.id.content);
		route_title = (TextView) findViewById(R.id.route_title);
		onclick_tv = (TextView) findViewById(R.id.recommenddetail_onclick_tv);
		zan_tv = (TextView) findViewById(R.id.recommenddetail_zan_tv);
		zan_layout=(LinearLayout)findViewById(R.id.recommenddetail_zan_layout);
		zan_iv= (ImageView) findViewById(R.id.recommenddetail_zan_iv);

		dm = getResources().getDisplayMetrics();
		imagebtn_params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imagebtn_params.height = dm.widthPixels/3;
		route_pic.setLayoutParams(imagebtn_params);

		options = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.imaleloadlogo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载解码过程中错误时候显示的图片
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
		zan_layout.setOnClickListener(this);
	}

	private void initgetIntent() {
		id = getIntent().getStringExtra("id");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}else if(v==zan_layout){//赞 取消赞
			if (isConnect()) {
				if(sp.getBoolean(XZContranst.if_login, false)){
					praise(id);
				}else{
					startActivity(new Intent(this,LoginActivity.class));
				}
			} else {
				ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
			}
		}
	}

	private void getData() {
		pd = ProgressDialogUtils.show(this, "获取数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("catid", "2");
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", id);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.recommendDetails;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "推荐详情提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "推荐详情返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

					JSONObject djson = jsonj.getJSONObject("data");
					biaoti = djson.getString("title");
					content = djson.getString("content");
					onclick = djson.getString("onclick");
					thumb = djson.getString("thumb");
					zanIf= djson.optString("zanIf");
					zan= djson.getString("zan");
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
	
	private void praise(final String id){
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("aid", id);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.recommendZan;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "推荐赞提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "推荐赞返回的数据" + jsonj.toString());

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

}
