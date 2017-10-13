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
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.net.UploadUtil;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.video.BaseActivity;
import com.power.travel.xixuntravel.video.SurfaceVideoView;
import com.yixia.weibo.sdk.util.DeviceUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.power.travel.xixuntravel.net.HttpUrl.video;


public class Video_sendActivity extends BaseActivity
		implements SurfaceVideoView.OnPlayStateListener, OnErrorListener,
        OnPreparedListener, OnClickListener, OnCompletionListener,
        OnInfoListener {
	public static final String TAG = "Video_sendActivity";
	String info, videourl;
	private TextView title, send;
	private ImageView back;
	RelativeLayout fl_videoView_parent;
	private EditText sendvideo_edit;
	File sourceVideoFile;
	SharedPreferences sp;
	private ProgressDialogUtils pd;

	/** 播放控件 */
	private SurfaceVideoView mVideoView;

	/** 播放路径 */
	private String mPath;
	/** 视频截图路径 */
	private String mCoverPath;

	/** 是否�?��回复播放 */
	private boolean mNeedResume;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功
				ToastUtil.showToast(getApplicationContext(), info);
				onBackPressed();
			} else if (msg.what == 10) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -2) {// 上传图片失败
				ToastUtil.showToast(getApplicationContext(), info);
			}
			if (pd != null && Video_sendActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 2) {// 上传视频
				upload();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.playvideo);
		MyApplication.getInstance().addActivity(this);
		init();
	}

	private void init() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "提交数据...");
		mPath = getIntent().getExtras().getString("videoPath");
		sourceVideoFile = new File(mPath);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("发布视频");
		sendvideo_edit = (EditText) findViewById(R.id.sendvideo_edit);
		send = (TextView) findViewById(R.id.send);
		send.setOnClickListener(this);

		/*fl_videoView_parent = (RelativeLayout) findViewById(R.id.fl_videoView_parent);
		
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		LinearLayout.LayoutParams reparams = (LinearLayout.LayoutParams) fl_videoView_parent
				.getLayoutParams();
		reparams.height = screenW;
		fl_videoView_parent.setLayoutParams(reparams);*/


		mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);

		mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this);
		mVideoView.getLayoutParams().width = DeviceUtils.getScreenWidth(this);
		if (TextUtils.isEmpty(mPath)) {
			ToastUtil.showToast(getApplicationContext(), "数据错误");
			finish();
			return;
		}
		mVideoView.setVideoPath(mPath);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back) {
			finish();
		} else if (v.getId() == R.id.send) {// 发布
			if (validate()) {
				uploadpic();
			}
		}else if(v.getId() == R.id.videoview){
			Intent intent=new Intent(this,VideoPlayerActivity.class);
			intent.putExtra("videoPath", mPath);
			startActivity(intent);
		}
	}

	private boolean validate() {
		if (TextUtils.isEmpty(sendvideo_edit.getText().toString())) {
			ToastUtil.showToast(getApplicationContext(), "请输入文字描述");
			return false;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// Intent intent = new Intent(Video_sendActivity.this,
			// VideoActivity.class);
			// startActivity(intent);
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	// 上传图片
	private void uploadpic() {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = HttpUrl.video;

				Map<String, String> word = new HashMap<String, String>();

				Map<String, File> files = new HashMap<String, File>();
				if (sourceVideoFile != null && sourceVideoFile.exists()) {
					files.put("file_box1", sourceVideoFile);
				}

				String request = UploadUtil.uploadFile(word, files, url);

				JSONObject jsonj = null;
				String code = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "上传图片返回的数据" + jsonj.toString());

					code = jsonj.getString("status");// 1成功
					info = jsonj.getString("msg");
					videourl = jsonj.optString("img");// 图片网络地址 全称

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

	// 发布游记
	private void upload() {
		// pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("content", sendvideo_edit.getText().toString());
					data.put("video", videourl);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.add;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "发布游记提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "发布游记返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(10);
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
		// new Handler().postDelayed(new Runnable() {
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public void run() {
		// if (DeviceUtils.hasJellyBean()) {
		// mVideoView.setBackground(null);
		// } else {
		// mVideoView.setBackgroundDrawable(null);
		// }
		// }
		// }, 300);
		// mLoading.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {// 跟随系统音量�?
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			mVideoView.dispatchKeyEvent(this, event);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onStateChanged(boolean isPlaying) {

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!isFinishing()) {
			// 播放失败
		}
		finish();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
//		ToastUtil.showToast(getApplicationContext(), "播放完毕");
		if (!isFinishing())
			mVideoView.reOpen();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			// 音频和视频数据不正确
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

}
