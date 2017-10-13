package com.power.travel.xixuntravel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.event.MyTravelDetailEvent;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.ConfigApp;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.PhotoView;
import com.power.travel.xixuntravel.views.ViewSwitcherHelper;
import com.power.travel.xixuntravel.weight.HackyViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ImageViewPager_3Activity extends Activity implements View.OnClickListener {
    private ArrayList<String> mList_pic = new ArrayList<String>();

    HackyViewPager mViewPager;
    int mSeleteItem;
    ArrayList<View> views = new ArrayList<View>();
    LinearLayout mHeadDotLayout;
    ViewSwitcherHelper mViewSwitchHelper;
    private ImageView back,title_iv;
    private TextView title,content,praise_type,comment_type,detail_zan,detail_comment;
    AllTravelModel travelModel;

    private String TAG="ImageViewPagerActivity",info;
    SharedPreferences sp;
    private ProgressDialogUtils pd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){////成功
                ConfigApp.mDelectMyTravel.setTravelID(travelModel.getId());
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
                    ToastUtil.showToast(ImageViewPager_3Activity.this, "取消成功");
                    detail_zan.setText("赞");

                } else {// 不赞变赞
                    travelModel.setZanIf("1");
                    travelModel.setZan(String.valueOf(Integer
                            .valueOf(travelModel.getZan()) + 1));
                    ToastUtil.showToast(ImageViewPager_3Activity.this, "点赞成功");
                    detail_zan.setText("取消");
                }
            }else if (msg.what == -2) {// 失败点赞
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == -3) {// 评论失败
                ToastUtil.showToast(getApplicationContext(), info);
            }
            if (pd != null && ImageViewPager_3Activity.this != null) {
                pd.dismiss();
            }
        }
    };

    // ArrayList<Fragment> listFragment = new ArrayList<Fragment>();

    public static Intent newIntent(Context mContext, AllTravelModel model, ArrayList<String> pic_List, int position){
        Intent i = new Intent(mContext, ImageViewPager_3Activity.class);
        i.putStringArrayListExtra("pic_list", pic_List);
        i.putExtra("position", position);
        i.putExtra("model", model);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_pager_3);
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
        mSeleteItem = getIntent().getIntExtra("position", 0);
        travelModel=(AllTravelModel) getIntent().getExtras().getSerializable("model");
    }

    private void initView() {
        sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        mHeadDotLayout = (LinearLayout) findViewById(R.id.home_head_position);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        String addtime = travelModel.getAddtime();
        title.setText(StringUtils.getStrTimeMonthDaySF(addtime));
        content= (TextView) findViewById(R.id.detail_content);
        praise_type= (TextView) findViewById(R.id.detail_zan_type);
        comment_type= (TextView) findViewById(R.id.detail_comment_type);
        detail_zan = findViewById(R.id.detail_zan);
        detail_comment = findViewById(R.id.detail_comment);
        /**
         * 新增点击事件
         */
        praise_type.setOnClickListener(this);
        comment_type.setOnClickListener(this);
        detail_zan.setOnClickListener(this);
        detail_comment.setOnClickListener(this);

        int on = R.drawable.dot_bai;
        int off = R.drawable.dot_gray;

        mViewSwitchHelper = new ViewSwitcherHelper(this, mHeadDotLayout, off,
                on);
        mViewPager = (HackyViewPager) findViewById(R.id.viewpager);

//		Log.e("mlist_pic-viewpager---", mList_pic.size() + "");
        for (int i = 0; i < mList_pic.size(); i++) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.phone_view_layout, null);
            PhotoView imageView = (PhotoView) view.findViewById(R.id.photo_view_image);// photociew在此用到了
            // ImageView imageView = (ImageView) view
            // .findViewById(R.id.image_view_image);
            String image_url = mList_pic.get(i);
            ImageLoader.getInstance().displayImage(image_url, imageView);

            imageView.setTag(i);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//					Log.e("", "0000000-0000000----000000");
                    ImageViewPager_3Activity.this.finish();
                }
            });

            views.add(view);
        }

        mViewPager.setAdapter(new ViewPageAdpter());
        mViewPager.setCurrentItem(0);
        mViewSwitchHelper.setViewSwitcherTip(mList_pic.size(), 0);

        mViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        mViewSwitchHelper.setCurrent(i);
                        mViewPager.getCurrentItem();
                        mSeleteItem = i;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(v==back){
            finish();
        }
        if (v == praise_type){//进入detail
            Intent intent = new Intent(ImageViewPager_3Activity.this,
                    TravelDetailActivity.class);

            intent.putExtra("id", travelModel.getId());
            startActivity(intent);
        }
        if (v == comment_type){//进入detail
            Intent intent = new Intent(ImageViewPager_3Activity.this,
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
            Intent intent = new Intent(ImageViewPager_3Activity.this,
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

    /**
     * 返回键的监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // setBack();
            finish();
        }
        return false;
    }


    class ViewPageAdpter extends PagerAdapter // 装载view的适配器
    {
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) { // 销毁view
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public int getCount() { // view数量
            return views.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) { // 返回相应的view
            ((ViewPager) arg0).addView(views.get(arg1), 0);

            return views.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

    }
}
