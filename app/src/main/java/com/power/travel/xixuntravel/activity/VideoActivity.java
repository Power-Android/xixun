package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;


/**
 * 视频录制
 * 
 * @author fan
 * 
 */
public class VideoActivity extends BaseActivity implements OnClickListener,
		SurfaceHolder.Callback, OnTouchListener {
	private static final String TAG = "VideoActivity";
	private TextView start, title;
	private ImageView back;
	private SurfaceView mSurfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean isRecording = false;
	private MediaRecorder mediarecorder;
	private Camera camera;
	private Camera.Parameters params;
	private File file;
	private int BitRate = 5;
	private int displayOrientation = 90;
	private long time = 11;
	private TextView time_tv;
	private LinearLayout ll_time;
	private Handler handler;
	private String vid_name;
	private ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		MyApplication.getInstance().addActivity(this);
		makeDirs();
		initView();
//		List<Size> preSizeList=	new ArrayList<Size>();
//		preSizeList=params.getSupportedPreviewSizes();
//		if(preSizeList.size()>0){
//			for(int i=0;i<preSizeList.size();i++){
//				LogUtil.e(TAG, preSizeList.get(i).toString());
//			}
//
//		}
	}

	private void initView() {
		start = (TextView) this.findViewById(R.id.start_record);
		start.setOnTouchListener(this);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("拍视频");
		time_tv = (TextView) findViewById(R.id.time);
		ll_time = (LinearLayout) findViewById(R.id.ll_time);
		mProgress = (ProgressBar) findViewById(R.id.update_progress);
		handler = new Handler();

		mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		LinearLayout.LayoutParams imagebtn_params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		imagebtn_params.height = dm.widthPixels;//*4/3
//		imagebtn_params.width = dm.widthPixels;
		imagebtn_params.height = 480;//*4/3
		imagebtn_params.width = 720;
		mSurfaceView.setLayoutParams(imagebtn_params);
		// WindowManager wm = (WindowManager)
		// this.getSystemService(Context.WINDOW_SERVICE);
		// DisplayMetrics dm = new DisplayMetrics();
		// wm.getDefaultDisplay().getMetrics(dm);
		// int screenW = dm.widthPixels;
		//
		// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		// mSurfaceView.getLayoutParams();
		// params.width = screenW*3/4;
		// params.height = screenW;
		// params.gravity = Gravity.TOP;
		// mSurfaceView.setLayoutParams(params);

		// mSurfaceView.setOnTouchListener(this);

		surfaceHolder = mSurfaceView.getHolder();// 閸欐牕绶県older
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setKeepScreenOn(true);
		mSurfaceView.setFocusable(true);
		surfaceHolder.addCallback(this); // holder閸旂姴鍙嗛崶鐐剁殶閹恒儱褰?

	}

	private Runnable timeRun = new Runnable() {
		@Override
		public void run() {
			if (time != 0) {
				time--;
				ll_time.setVisibility(View.VISIBLE);
				mProgress.setProgress(10 * (10 - (int) time));
				time_tv.setText(timeFormat((int) time));
				handler.postDelayed(timeRun, 1000);
			} else {// 缁撴潫
				stop();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (!(saveVideoPath.equals(""))) {

							File reName = new File(saveVideoPath);
							File f = new File(getSDPath(VideoActivity.this)
									+ "append.mp4");
							f.renameTo(reName);
							if (reName.exists()) {
								f.delete();
								new File(currentVideoFilePath).delete();
							}

						}
						Intent intent = new Intent(VideoActivity.this,
								Video_sendActivity.class);
						Bundle bundle = new Bundle();
						if (saveVideoPath.equals("")) {
							bundle.putString("videoPath", currentVideoFilePath);

						} else {
							bundle.putString("videoPath", saveVideoPath);
						}
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}
				}).start();
			}
		}
	};

	/**
	 * 閺冨爼妫块弽鐓庣础閸栵拷
	 *
	 * @param timeMs
	 * @return
	 */
	public String timeFormat(int timeMs) {
		int totalSeconds = timeMs;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.getDefault());
		if (hours > 0) {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private String getModifyTime() {
		// long l = file.lastModified();
		long l = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date(l);
		String s = format.format(date);
		return s;
	}

	/**
	 * 瀵拷婵缍嶉崚锟?
	 */

	String currentVideoFilePath;

	@SuppressLint("NewApi")
	protected void start() {
		vid_name = "VID_" + getModifyTime() + ".mp4";

		file = new File(getSDPath(this) + vid_name);
		if (file.exists()) {
			// 婵″倹鐏夐弬鍥︽鐎涙ê婀敍灞藉灩闂勩倕鐣犻敍灞剧川缁?桨鍞惍浣风箽鐠囦浇顔曟径鍥︾瑐閸欘亝婀佹稉锟芥稉顏勭秿闂婅櫕鏋冩禒锟?
			// file.delete();
		}
		camera.stopPreview();
		camera.unlock();

		mediarecorder = new MediaRecorder();// 閸掓稑缂搈ediarecorder鐎电钖?

		mediarecorder.setCamera(camera);
		// 鐠佸墽鐤嗚ぐ鏇炲煑鐟欏棝顣跺┃鎰礋Camera(閻╁憡婧?
		mediarecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		// // 瑜版洖鍎氶弮瀣祮90鎼达拷
		mediarecorder.setOrientationHint(displayOrientation);
		// mediaRecorder.setVideoSource(VideoSource.CAMERA);
		// 鐠佸墽鐤嗚ぐ鏇炲煑鐎瑰本鍨氶崥搴ゎ潒妫版垹娈戠亸浣筋棅閺嶇厧绱HREE_GPP娑擄拷3gp.MPEG_4娑撶皟p4
		mediarecorder.setOutputFormat(OutputFormat.MPEG_4);
		// // 鐠佸墽鐤嗚ぐ鏇炲煑閻ㄥ嫯顫嬫０鎴犵椽閻弓263 h264
		mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 鐠佸墽鐤嗙憴鍡涱暥瑜版洖鍩楅惃鍕瀻鏉堛劎宸奸妴鍌氱箑妞ょ粯鏂侀崷銊啎缂冾喚绱惍浣告嫲閺嶇厧绱￠惃鍕倵闂堫澁绱濋崥锕?灟閹躲儵鏁?
//		mediarecorder.setVideoSize(width, height)640, 480
		mediarecorder.setVideoSize(720,480);// 瑙嗛灏哄
		mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// 鐠佸墽鐤嗘妯垮窛闁插繐缍嶉崚锟?閺?懓褰夌紓鏍垳闁喓宸?
		// mediarecorder.setVideoEncodingBitRate(BitRate * 1024 * 512);
		// 璁剧疆瑙嗛杈撳嚭鐨勬牸寮忓拰缂栫爜
		CamcorderProfile mProfile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_480P);
		if (mProfile.videoBitRate > 2 * 640 * 480) {
			mediarecorder.setVideoEncodingBitRate(2 * 640 * 480);
		} else {
			mediarecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
		}
		// 鐠佸墽鐤嗚ぐ鏇炲煑閻ㄥ嫯顫嬫０鎴濇姎閻滃洢锟藉倸绻?い缁樻杹閸︺劏顔曠純顔剧椽閻礁鎷伴弽鐓庣础閻ㄥ嫬鎮楅棃顫礉閸氾箑鍨幎銉╂晩
		// mediarecorder.setVideoFrameRate(30);//瑙嗛甯ч鐜?
		mediarecorder.setVideoFrameRate(mProfile.videoFrameRate);

		mediarecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
		// 鐠佸墽鐤嗙憴鍡涱暥閺傚洣娆㈡潏鎾冲毉閻ㄥ嫯鐭惧锟?

		currentVideoFilePath = getSDPath(VideoActivity.this) + vid_name;
		mediarecorder.setOutputFile(currentVideoFilePath);// 淇濆瓨璺緞
		try {
			// 閸戝棗顦妴浣哥磻婵拷
			mediarecorder.prepare();
			mediarecorder.start();// 瀵拷婵鍩㈣ぐ锟?
			isRecording = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mediarecorder.setOnErrorListener(new OnErrorListener() {

			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				// 閸欐垹鏁撻柨娆掝嚖閿涘苯浠犲銏犵秿閸掞拷
				try {
					if (mediarecorder != null) {
						mediarecorder.setOnErrorListener(null);
						mediarecorder.stop();
						mediarecorder.reset();
						mediarecorder.release();
						mediarecorder = null;
						isRecording = false;
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}

			}
		});
		start.setBackgroundResource(R.drawable.circle_video_normal);
		handler.post(timeRun);

	}

	protected void stop() {
		if (isRecording) {
			// 婵″倹鐏夊锝呮躬瑜版洖鍩楅敍灞戒粻濮濄垹鑻熼柌濠冩杹鐠у嫭绨?
			try {
				if (mediarecorder != null) {
					mediarecorder.setOnErrorListener(null);
					mediarecorder.setPreviewDisplay(null);

					mediarecorder.stop();
					mediarecorder.reset();
					mediarecorder.release();
					mediarecorder = null;
					isRecording = false;
					handler.removeCallbacks(timeRun);
					start.setBackgroundResource(R.drawable.circle_video_normal);
					// camera.lock();
					// if (camera != null) {
					// camera.release();
					// }
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	boolean isPause = false;
	String saveVideoPath = "";
	private boolean record = true;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_record:
			if (record) {

				record = false;
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						start();
					}
				});

			} else {
				// start.setImageResource(R.drawable.record_start);
				if (isPause) {
					Intent intent = new Intent(VideoActivity.this,
							Video_sendActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("videoPath", saveVideoPath);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();

				} else {
					stop();
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (!(saveVideoPath.equals(""))) {

								File reName = new File(saveVideoPath);
								File f = new File(getSDPath(VideoActivity.this)
										+ "append.mp4");
								f.renameTo(reName);
								if (reName.exists()) {
									f.delete();
									new File(currentVideoFilePath).delete();
								}

							}
							Intent intent = new Intent(VideoActivity.this,
									Video_sendActivity.class);
							Bundle bundle = new Bundle();
							if (saveVideoPath.equals("")) {
								bundle.putString("videoPath",
										currentVideoFilePath);

							} else {
								bundle.putString("videoPath", saveVideoPath);
							}
							intent.putExtras(bundle);
							startActivity(intent);
							finish();
						}
					}).start();
				}
			}
			break;
		case R.id.back:
			stop();
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			record = false;
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					start();
				}
			});

			break;
		case MotionEvent.ACTION_UP:
			if (time < 10) {
				stop();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (!(saveVideoPath.equals(""))) {

							File reName = new File(saveVideoPath);
							File f = new File(getSDPath(VideoActivity.this)
									+ "append.mp4");
							f.renameTo(reName);
							if (reName.exists()) {
								f.delete();
								new File(currentVideoFilePath).delete();
							}

						}
						Intent intent = new Intent(VideoActivity.this,
								Video_sendActivity.class);
						Bundle bundle = new Bundle();
						if (saveVideoPath.equals("")) {
							bundle.putString("videoPath", currentVideoFilePath);

						} else {
							bundle.putString("videoPath", saveVideoPath);
						}
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}
				}).start();
			} else {
				time = 11;
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (isPause) {
				stop();
				new Thread(new Runnable() {
					@Override
					public void run() {

						File reName = new File(saveVideoPath);
						File f = new File(getSDPath(VideoActivity.this)
								+ "append.mp4");
						f.renameTo(reName);
						if (reName.exists()) {
							f.delete();
							new File(currentVideoFilePath).delete();
						}
						Intent intent = new Intent(VideoActivity.this,
								Video_sendActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("videoPath", saveVideoPath);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}
				}).start();

			} else {
				finish();
			}

			break;
		default:
			break;
		}
		return true;
	}

	public static String getSDPath(Context context) {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 閸掋倖鏌噑d閸椻剝妲搁崥锕?摠閸︼拷
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else if (!sdCardExist) {

			// Toast.makeText(context, "SD閸椻?绗夌?妯烘躬",
			// Toast.LENGTH_SHORT).show();

		}
		File eis = new File(sdDir.toString() + "/Video/");
		try {
			if (!eis.exists()) {
				eis.mkdir();
			}
		} catch (Exception e) {

		}
		return sdDir.toString() + "/Video/";

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 鐏忓攧older閿涘矁绻栨稉鐚lder娑撳搫绱戞慨瀣躬oncreat闁插矂娼伴崣鏍х繁閻ㄥ埅older閿涘苯鐨㈢?鍐ゴ缂佹獨urfaceHolder
		Log.d(TAG, "surfaceCreated");

		if (null == camera) {
			camera = Camera.open();

			// try {
			try {

				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}

			camera();
			camera.startPreview();

		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
		Log.d(TAG, "surfaceChanged");

		camera();
		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			params.set("orientation", "portrait");
			camera.setDisplayOrientation(90);//设置图像怎么在手机内显示
			ToastUtil.showToast(getApplicationContext(), "90");
		} else {
			params.set("orientation", "landscape");
			camera.setDisplayOrientation(0);
			ToastUtil.showToast(getApplicationContext(), "0");
		}
	}

	private void camera() {
		try {
			camera.stopPreview();
			params = camera.getParameters();
//			params.setPreviewSize(width, height)
			params.setPreviewSize(720, 480);
			params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			camera.setParameters(params);
			camera.startPreview();

			List<Size> preSizeList=	new ArrayList<Size>();
			preSizeList=params.getSupportedPreviewSizes();
			if(preSizeList.size()>0){
				for(int i=0;i<preSizeList.size();i++){
					LogUtil.e(TAG, "允许的height"
				+ String.valueOf(preSizeList.get(i).height)+",width"+ String.valueOf(preSizeList.get(i).width));
				}
			}
			DisplayMetrics dm = getResources().getDisplayMetrics();
			LinearLayout.LayoutParams imagebtn_params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			imagebtn_params.height = dm.widthPixels;//*4/3
			imagebtn_params.width = dm.widthPixels;
			LogUtil.e(TAG, "屏幕的width"+ String.valueOf(dm.widthPixels)+",height"+dm.heightPixels);
			getOptimalPreviewSize(preSizeList,720,480);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//选取与SurfaceView的高的差值最小的
	public Camera.Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        LogUtil.e(TAG,"返回的width"+optimalSize.width+",返回的height"+optimalSize.height);
        return optimalSize;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int ort = getResources().getConfiguration().orientation;
		if (ort == Configuration.ORIENTATION_PORTRAIT) {
			displayOrientation = 90;
			ToastUtil.showToast(getApplicationContext(), "displayOrientation=90");
		} else if (ort == Configuration.ORIENTATION_LANDSCAPE) {
			displayOrientation = 0;
			ToastUtil.showToast(getApplicationContext(), "displayOrientation=0");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// surfaceDestroyed閻ㄥ嫭妞傞崐娆忔倱閺冭泛顕挒陇顔曠純顔昏礋null
		try {
			if (mediarecorder != null) {

				mediarecorder.setOnErrorListener(null);
				mediarecorder.setPreviewDisplay(null);
				mediarecorder.stop();
				mediarecorder.reset();
				mediarecorder.release();
				mediarecorder = null;
				camera.lock();
			}
			camera.stopPreview();
			camera.release();
			camera = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		// finish();
	}

	public void makeDirs() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 閸掋倖鏌噑d閸椻剝妲搁崥锕?摠閸︼拷
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else if (!sdCardExist) {
		}

		File dirs_v = new File(sdDir + "/Video");

		if (!dirs_v.exists())
			dirs_v.mkdirs();

	}
}
