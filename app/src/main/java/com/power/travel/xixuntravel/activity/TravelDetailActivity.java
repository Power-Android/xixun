package com.power.travel.xixuntravel.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.power.travel.xixuntravel.adapter.TravelDetailCommentAdapter;
import com.power.travel.xixuntravel.adapter.TravelDetailGridAdapter;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.model.TravelDetailCommentModel;
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
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyGridView;
import com.power.travel.xixuntravel.weight.MyListView;
import com.yixia.weibo.sdk.util.DeviceUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 游记跟消息详情
 * 
 * @author fan
 * 
 */
public class TravelDetailActivity extends BaseActivity
		implements SurfaceVideoView.OnPlayStateListener, OnErrorListener,
        OnPreparedListener, OnClickListener, OnCompletionListener,
        OnInfoListener {

	private ImageView back, sender_head, praise_iv;
	private TextView title, sender_name, traveldetail_title, traveldetail_time,
			praise_tv, commentnum, comment;
	private EditText comment_edit;
	private LinearLayout praise_layout;
	private MyListView commentlistview;
	private MyGridView gridview;
	private String data, TAG = "TripDetailActivity", info, id, comentid = "";
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	ImageLoader imageLoader = ImageLoader.getInstance();
	private AllTravelModel allTravelModel;
	List<TravelDetailCommentModel> list = new ArrayList<TravelDetailCommentModel>();
	TravelDetailCommentAdapter adapter;
	 int tag;

	/** 播放控件 */
	private SurfaceVideoView mVideoView;
	private View mLoading;

	/** 是否回复播放 */
	private boolean mNeedResume;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				setData();
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == 2) {// 成功点赞
				ToastUtil.showToast(getApplicationContext(), info);
				if (TextUtils.equals(allTravelModel.getZanIf(), "1")) {// 赞变不赞
					allTravelModel.setZanIf("0");
					allTravelModel.setZan(String.valueOf(Integer
							.valueOf(allTravelModel.getZan()) - 1));
					praise_iv.setImageDrawable(getResources().getDrawable(
							R.drawable.traveldetail_nopraise));
					praise_tv.setText(allTravelModel.getZan());
					tag=1;

				} else {// 不赞变赞
					allTravelModel.setZanIf("1");
					allTravelModel.setZan(String.valueOf(Integer
							.valueOf(allTravelModel.getZan()) + 1));
					praise_tv.setText(allTravelModel.getZan());
					tag=2;
					praise_iv.setImageDrawable(getResources().getDrawable(
							R.drawable.trip_attention));
				}
			} else if (msg.what == -2) {// 失败点赞
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -3) {// 评论失败
				ToastUtil.showToast(getApplicationContext(), info);
			}

			if (pd != null && TravelDetailActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 3) {// 评论成功
				ToastUtil.showToast(getApplicationContext(), info);
				if (pd != null && TravelDetailActivity.this != null) {
					pd.dismiss();
				}
				if (list != null) {
					if (list.size() > 0) {
						list.clear();
						adapter.notifyDataSetChanged();
					}
				}

				getData(true);
			} else if (msg.what == 4) {// 评论后刷新成功
				if (pd != null && TravelDetailActivity.this != null) {
					pd.dismiss();
				}
				setData2();
			}
		};
	};
    private Intent intent;
    private String qubie;

    @Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_traveldetail);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
		initgetIntent();
		getData(false);

		commentlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				comentid = list.get(position).getId();
				comment_edit.setHint("回复" + list.get(position).getNickname());
			}
		});
	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("游记详情");
		sender_head = (ImageView) findViewById(R.id.sender_head);
		sender_name = (TextView) findViewById(R.id.sender_name);
		traveldetail_title = (TextView) findViewById(R.id.traveldetail_title);
		traveldetail_time = (TextView) findViewById(R.id.traveldetail_time);
		praise_tv = (TextView) findViewById(R.id.traveldetail_praise_tv);
		commentnum = (TextView) findViewById(R.id.traveldetail_commentnum);
		praise_layout = (LinearLayout) findViewById(R.id.traveldetail_praise_layout);
		praise_iv = (ImageView) findViewById(R.id.traveldetail_praise_iv);
		comment = (TextView) findViewById(R.id.traveldetail_comment);
		comment_edit = (EditText) findViewById(R.id.traveldetail_comment_edit);
		commentlistview = (MyListView) findViewById(R.id.traveldetail_commentlistview);
		gridview = (MyGridView) findViewById(R.id.image_gridView);

		mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
		mLoading = findViewById(R.id.loading);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.imaleloadlogo)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
				// .cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.displayer(new RoundedBitmapDisplayer(100))// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		imageLoader.init(ImageLoaderConfiguration
				.createDefault(TravelDetailActivity.this));
	}

	private void initListener() {
		back.setOnClickListener(this);
		praise_layout.setOnClickListener(this);
		comment.setOnClickListener(this);
		
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);
		sender_head.setOnClickListener(this);
	}

	private void initgetIntent() {
        intent = getIntent();
		id = intent.getStringExtra("id");
        qubie = intent.getStringExtra("qubie");
    }

	@Override
	public void onClick(View v) {
		if (v == back) {
			finish();
		} else if (v == praise_layout) {// 赞 取消赞
			if (sp.getBoolean(XZContranst.if_login, false)) {
				praise(id);
			} else {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
		} else if (v == comment) {// 评论
			if (TextUtils.isEmpty(comment_edit.getText().toString())) {
				ToastUtil.showToast(getApplicationContext(), "请输入评论信息！");
			} else {
				if (sp.getBoolean(XZContranst.if_login, false)) {
					comment();
				} else {
					Intent intent = new Intent(this, LoginActivity.class);
					startActivity(intent);
				}
			}
		}else if(v==sender_head){//头像
			MasterModel mMasterModel=new MasterModel();
			mMasterModel.setMid(allTravelModel.getMid());
			Intent intent=new Intent(this,UserCenterActivity.class);
			intent.putExtra("model", mMasterModel);
			startActivity(intent);
		}
	}

	// 第一次加载数据
	private void setData() {
		/**
		 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
		 */
		imageLoader.displayImage(allTravelModel.getFace(), sender_head,
				options, animateFirstListener);
		if (!TextUtils.isEmpty(allTravelModel.getImg())) {//
			gridview.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);
			String[] listpic = allTravelModel.getImg().split(",");
			if (listpic.length > 1) {
				gridview.setNumColumns(3);
			} else {
				gridview.setNumColumns(1);
			}
			gridview.setAdapter(new TravelDetailGridAdapter(listpic, this));
		} else if (!TextUtils.isEmpty(allTravelModel.getVideo())) {
			gridview.setVisibility(View.GONE);
			mVideoView.setVisibility(View.VISIBLE);
			mLoading.setVisibility(View.VISIBLE);

//			DisplayMetrics dm = getResources().getDisplayMetrics();
//			RelativeLayout.LayoutParams imagebtn_params = new RelativeLayout.LayoutParams(
//					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			imagebtn_params.width = dm.widthPixels;
//			imagebtn_params.height = dm.widthPixels;
//			mVideoView.setLayoutParams(imagebtn_params);
//			 LogUtil.e(TAG, "mSurfaceViewWidth:" + imagebtn_params.width + "...mSurfaceViewHeight:" + imagebtn_params.height);
			mVideoView.setVideoPath(allTravelModel.getVideo());
//			mVideoView.pauseDelayed(10);//定时暂停
		}

		sender_name.setText(allTravelModel.getNickname());
		traveldetail_title.setText(allTravelModel.getContent());
		if (TextUtils.equals(
				StringUtils.CountTime(allTravelModel.getAddtime()), "0")) {// 今天
			traveldetail_time.setText("今天 "
					+ StringUtils.getStrTime(allTravelModel.getAddtime()));
		} else if (TextUtils.equals(
				StringUtils.CountTime(allTravelModel.getAddtime()), "1")) {// 昨天
			traveldetail_time.setText("昨天 "
					+ StringUtils.getStrTime(allTravelModel.getAddtime()));
		} else {
			traveldetail_time.setText(StringUtils
					.getStrTimeMonthDay(allTravelModel.getAddtime()));
		}
		if (TextUtils.equals(allTravelModel.getZanIf(), "1")) {// 赞
			praise_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.trip_attention));
		} else {// 不赞
			praise_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.traveldetail_nopraise));
		}
		praise_tv.setText(allTravelModel.getZan());
		commentnum.setText(allTravelModel.getComment_count());

		adapter = new TravelDetailCommentAdapter(this, list);
		commentlistview.setAdapter(adapter);

		comment_edit.setHint("回复" + allTravelModel.getNickname());
	}

	// 评论后刷新数居
	private void setData2() {

		commentnum.setText(allTravelModel.getComment_count());
		adapter = new TravelDetailCommentAdapter(this, list);
		commentlistview.setAdapter(adapter);
		comentid = "";
		comment_edit.getText().clear();
		comment_edit.setHint("回复" + allTravelModel.getNickname());
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
		
		int vWidth = mp.getVideoWidth();   
        int vHeight = mp.getVideoHeight();   
//        if(vWidth > mVideoView.getWidth() || vHeight > mVideoView.getHeight()){   
//            //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放   
//  
//            float wRatio = (float)vWidth/(float)mVideoView.getWidth();   
//  
//            float hRatio = (float)vHeight/(float)mVideoView.getHeight();   
//            //选择大的一个进行缩放   
//            float ratio = Math.max(wRatio, hRatio);   
//            vWidth = (int)Math.ceil((float)vWidth/ratio);   
//           vHeight = (int)Math.ceil((float)vHeight/ratio);        
//        }
        
      if(vHeight > mVideoView.getWidth()){//视频的高大于手机的宽度
      //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放   

      float wRatio = (float)vWidth/(float)mVideoView.getWidth();   

      float hRatio = (float)vHeight/(float)mVideoView.getHeight();   
      //选择大的一个进行缩放   
      float ratio = Math.max(wRatio, hRatio);
      vWidth = (int) Math.ceil((float)vWidth/ratio);
     vHeight = (int) Math.ceil((float)vHeight/ratio);
  }
        
		RelativeLayout.LayoutParams imagebtn_params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imagebtn_params.width = vWidth;
		imagebtn_params.height = vHeight;
		mVideoView.setLayoutParams(imagebtn_params);
		
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
		 mLoading.setVisibility(View.GONE);
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

	/**
	 * 获取数据
	 */
	private void getData(final boolean ifcomment) {
		pd = ProgressDialogUtils.show(this, "获取数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("id", id);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.all_triaveldetail;
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
					JSONObject jsond = jsonj.getJSONObject("data");
					allTravelModel = JSON.parseObject(jsond.toString(),
							AllTravelModel.class);
					list = JSON.parseArray(jsond.getString("comment"),
							TravelDetailCommentModel.class);

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					if (ifcomment) {// 评论后刷新
						handler2.sendEmptyMessage(4);
					} else {
						handler.sendEmptyMessage(1);
					}

				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(-1);
				}

			}
		}).start();
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

	/**
	 * 回复
	 */
	private void comment() {
		pd = ProgressDialogUtils.show(this, "提交数据...");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("travels_id", id);
					data.put("content", comment_edit.getText().toString());
					data.put("comment_id", comentid);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.all_triavelcomment;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "评论提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "评论返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler2.sendEmptyMessage(3);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(-3);
				} else {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}
}
