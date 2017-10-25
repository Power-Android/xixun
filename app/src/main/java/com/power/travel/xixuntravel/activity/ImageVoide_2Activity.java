package com.power.travel.xixuntravel.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.event.MyTravelDetailEvent;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.model.MyTravelModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.video.SurfaceVideoView;
import com.yixia.weibo.sdk.util.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ImageVoide_2Activity extends Activity implements SurfaceVideoView.OnPlayStateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {

    private ArrayList<String> mList_pic = new ArrayList<String>();

    private ImageView back;
    private TextView content,praise_type,comment_type,detail_zan,detail_comment,title;
    AllTravelModel travelModel;
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
                    getData(false);
                } else {// 不赞变赞
                    getData(false);
                }
            }else if (msg.what == -2) {// 失败点赞
                ToastUtil.showToast(getApplicationContext(), info);
            }else if (msg.what == 4){
                String zan = travelModel.getZan();
                praise_type.setText(zan);
                String is_zan = travelModel.getZanIf();
                if (TextUtils.equals(is_zan, "1")) {// 赞变不赞
                    detail_zan.setText("取消");
                }else {// 不赞变赞
                    detail_zan.setText("赞");
                }
            }
            if (pd != null && ImageVoide_2Activity.this != null) {
                pd.dismiss();
            }
        }
    };

    // ArrayList<Fragment> listFragment = new ArrayList<Fragment>();

    public static Intent newIntent(Context mContext,
                                   AllTravelModel model, ArrayList<String> pic_List, int position) {
        Intent i = new Intent(mContext, ImageVoide_2Activity.class);
        i.putStringArrayListExtra("pic_list", pic_List);
        i.putExtra("position", position);
        i.putExtra("model", model);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_voide_2);
        MyApplication.getInstance().addActivity(this);
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
        travelModel=(AllTravelModel) getIntent().getExtras().getSerializable("model");
        String zan = travelModel.getZan();
    }

    private void initView() {
        sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        String addtime = travelModel.getAddtime();
        title.setText(StringUtils.getStrTimeMonthDaySF(addtime));
        content= (TextView) findViewById(R.id.detail_content);
        praise_type= (TextView) findViewById(R.id.detail_zan_type);
        comment_type= (TextView) findViewById(R.id.detail_comment_type);
        detail_zan = (TextView)findViewById(R.id.detail_zan);
        detail_comment =(TextView) findViewById(R.id.detail_comment);

        praise_type.setOnClickListener(this);
        comment_type.setOnClickListener(this);
        detail_zan.setOnClickListener(this);
        detail_comment.setOnClickListener(this);

        mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
        mLoading = findViewById(R.id.loading);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);

        mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this)* 4 / 3;

        if (!TextUtils.isEmpty(travelModel.getVideo())) {
            mVideoView.setVideoPath(travelModel.getVideo());
        }

    }

    @Override
    public void onClick(View v) {
        if(v==back){
            finish();
        }
        if (v == praise_type){
            Intent intent = new Intent(ImageVoide_2Activity.this,
                    TravelDetailActivity.class);
            intent.putExtra("id", travelModel.getId());
            startActivity(intent);
        }
        if (v == comment_type){
            Intent intent = new Intent(ImageVoide_2Activity.this,
                    TravelDetailActivity.class);
            intent.putExtra("id", travelModel.getId());
            startActivity(intent);
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
            Intent intent = new Intent(ImageVoide_2Activity.this,
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
        getData(false);
    }

    private void getData(final boolean ifcomment) {
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
                    travelModel = JSON.parseObject(jsond.toString(),
                            AllTravelModel.class);

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    handler.sendEmptyMessage(4);

                } else if (TextUtils.equals(status, "0")) {
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(-1);
                }

            }
        }).start();
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
        mVideoView.start();;
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

}
