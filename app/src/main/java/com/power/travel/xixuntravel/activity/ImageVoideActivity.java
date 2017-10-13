package com.power.travel.xixuntravel.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.event.MyTravelDetailEvent;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.EventMessage;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.video.BaseActivity;
import com.power.travel.xixuntravel.video.SurfaceVideoView;
import com.yixia.weibo.sdk.util.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import de.greenrobot.event.EventBus;
import io.rong.eventbus.ThreadMode;

/***
 * 我的游记视频展示
 * 
 * 
 */
public class ImageVoideActivity extends BaseActivity implements SurfaceVideoView.OnPlayStateListener, OnErrorListener,
		OnPreparedListener, OnClickListener, OnCompletionListener, OnInfoListener {

	private ArrayList<String> mList_pic = new ArrayList<String>();

	private ImageView back,title_iv;
	private TextView title,content,praise_type,comment_type;
	MyTravelModel travelModel;
	private String TAG="ImageVoideActivity",info;
	SharedPreferences sp;
	private ProgressDialogUtils pd;
	
	/** 播放控件 */
	private SurfaceVideoView mVideoView;
	private View mLoading;
	private boolean mNeedResume;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1){////成功
				EventBus.getDefault().post(new MyTravelDetailEvent(MyTravelDetailEvent.REFRESH));
				finish();
			}else if(msg.what==0){//失败
				ToastUtil.showToast(getApplicationContext(), info);
			}else if(msg.what==-1){//其他
				ToastUtil.showToast(getApplicationContext(), info);
			}else if (msg.what == 2) {// 成功点赞
				ToastUtil.showToast(getApplicationContext(), info);
				if (TextUtils.equals(travelModel.getZanIf(), "1")) {// 赞变不赞
					travelModel.setZanIf("0");
					travelModel.setZan(String.valueOf(Integer
							.valueOf(travelModel.getZan()) - 1));
					ToastUtil.showToast(ImageVoideActivity.this, "取消成功");
					detail_zan.setText("赞");

				} else {// 不赞变赞
					travelModel.setZanIf("1");
					travelModel.setZan(String.valueOf(Integer
							.valueOf(travelModel.getZan()) + 1));
					ToastUtil.showToast(ImageVoideActivity.this, "点赞成功");
					detail_zan.setText("取消");
				}
			}else if (msg.what == -2) {// 失败点赞
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && ImageVoideActivity.this != null) {
				pd.dismiss();
			}
		}
	};
	private TextView detail_zan;
	private TextView detail_comment;
	private LinearLayout tiaozhuan_ll;

	// ArrayList<Fragment> listFragment = new ArrayList<Fragment>();

	public static Intent newIntent(Context mContext,
                                   MyTravelModel model, ArrayList<String> pic_List, int position) {
		Intent i = new Intent(mContext, ImageVoideActivity.class);
		i.putStringArrayListExtra("pic_list", pic_List);
		i.putExtra("position", position);
		i.putExtra("model", model);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_video_layout);
		MyApplication.getInstance().addActivity(this);
		//注册
		EventBus.getDefault().register(this);
		getData();// 接收参数
		initView();// 初始化控件
		setData();
 	}

	private void setData() {
		content.setText(travelModel.getContent());
		praise_type.setText(travelModel.getZan());
		comment_type.setText(travelModel.getComment_count());
	}

	private void getData() {
		mList_pic = getIntent().getStringArrayListExtra("pic_list");
	
		travelModel=(MyTravelModel)getIntent().getExtras().getSerializable("model");
	}



	private void initView() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		title_iv= (ImageView) findViewById(R.id.title_iv);
		title_iv.setImageDrawable(getResources().getDrawable(R.drawable.traveldetail_delect));
		title_iv.setVisibility(View.VISIBLE);
		title_iv.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("我的游记");
		content= (TextView) findViewById(R.id.detail_content);
		praise_type= (TextView) findViewById(R.id.detail_zan_type);
		comment_type= (TextView) findViewById(R.id.detail_comment_type);
		
		mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
		mLoading = findViewById(R.id.loading);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);

		mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this)* 4 / 3;

		detail_zan = findViewById(R.id.detail_zan);
		detail_comment = findViewById(R.id.detail_comment);
		tiaozhuan_ll = findViewById(R.id.tiaozhuan_ll);
		detail_comment.setOnClickListener(this);
		detail_zan.setOnClickListener(this);
		tiaozhuan_ll.setOnClickListener(this);
		
//		if (TextUtils.isEmpty(travelModel.getVideo())) {
//			ToastUtil.showToast(getApplicationContext(), "没有数据");
//			finish();
//			return;
//		}
		if (!TextUtils.isEmpty(travelModel.getVideo())) {
			mVideoView.setVideoPath(travelModel.getVideo());
		}
		
	}

	@Override
	public void onClick(View v) {
		if(v==back){
			finish();
		}
		if(v==title_iv){//删除
			delectData();
		}
		if (v == detail_zan){
			if (sp.getBoolean(XZContranst.if_login, false)) {
				praise(travelModel.getId());
			} else {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
		}
		if (v == detail_comment){
			Intent intent = new Intent(ImageVoideActivity.this,
					TravelDetailActivity.class);
			intent.putExtra("id", travelModel.getId());
			startActivity(intent);
		}
		if (v == tiaozhuan_ll){
			Intent intent = new Intent(ImageVoideActivity.this,
					TravelDetailActivity.class);
			intent.putExtra("id", travelModel.getId());
			startActivity(intent);
		}
	}

	/**
	 * 赞
	 */
	private void praise(final String id) {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("travels_id", id);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.all_triavelpraise;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "赞提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "赞返回的数据" + jsonj.toString());

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
	
	@Override
	public void onResume() {
		super.onResume();
		if (mVideoView != null && mNeedResume) {
			mNeedResume = false;
			if (mVideoView.isRelease())
				mVideoView.reOpen();
			else
				mVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mVideoView != null) {
			if (mVideoView.isPlaying()) {
				mNeedResume = true;
				mVideoView.pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		 EventBus.getDefault().unregister(this);
		if (mVideoView != null) {
			mVideoView.release();
			mVideoView = null;
		}
		super.onDestroy();
	}



	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
		mVideoView.start();
//		new Handler().postDelayed(new Runnable() {
//
//			@SuppressWarnings("deprecation")
//			@Override
//			public void run() {
//				if (DeviceUtils.hasJellyBean()) {
//					mVideoView.setBackground(null);
//				} else {
//					mVideoView.setBackgroundDrawable(null);
//				}
//			}
//		}, 300);
		mLoading.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {//跟随系统音量	
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			mVideoView.dispatchKeyEvent(this, event);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onStateChanged(boolean isPlaying) {
//		mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!isFinishing()) {
			//播放失败
		}
		finish();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (!isFinishing())
			mVideoView.reOpen();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			//音频和视频数据不正确
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (!isFinishing())
				mVideoView.pause();
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!isFinishing())
				mVideoView.start();
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
			if (DeviceUtils.hasJellyBean()) {
				mVideoView.setBackground(null);
			} else {
				mVideoView.setBackgroundDrawable(null);
			}
			break;
		}
		return false;
	}


	/**
	 * 删除
	 */
	private void delectData() {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", travelModel.getId());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.delete_my_travel;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "删除游记提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "删除游记返回的数据" + jsonj.toString());

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
}
