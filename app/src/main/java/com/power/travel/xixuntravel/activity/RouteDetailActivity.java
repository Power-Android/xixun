package com.power.travel.xixuntravel.activity;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 推荐详情
 * @author fan
 *
 */
public class RouteDetailActivity extends BaseActivity {

	private ImageView back, route_pic;
	private TextView title, route_title;
	private WebView mWebView;
	private String data, TAG = "RouteDetailActivity", info, id, biaoti, thumb,
			content, onclick;
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
				// ToastUtil.showToast(getApplicationContext(), info);
				route_title.setText(biaoti);
				mWebView.loadDataWithBaseURL(null, content, "text/html",
						"utf-8", null);
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration
						.createDefault(RouteDetailActivity.this));
				/**
				 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
				 */
				imageLoader.displayImage(HttpUrl.Url + thumb, route_pic,
						options, animateFirstListener);
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			}
			pd.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_routedetail);
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
		pd = ProgressDialogUtils.show(this, "获取数据...");
		back = (ImageView) findViewById(R.id.back);
		route_pic = (ImageView) findViewById(R.id.route_pic);
		title = (TextView) findViewById(R.id.title);
		title.setText("推荐详情");
		mWebView = (WebView) findViewById(R.id.content);
		route_title = (TextView) findViewById(R.id.route_title);

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

	}

	private void initgetIntent() {
		id = getIntent().getStringExtra("id");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}
	}

	private void getData() {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("catid", "17");
					// data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", id);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.RouteDetails;
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
}
