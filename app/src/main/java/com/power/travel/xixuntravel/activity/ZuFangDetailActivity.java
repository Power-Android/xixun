package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.DialogUtils;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.weight.MyListView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZuFangDetailActivity extends BaseActivity {

    private ImageView back;
    private ProgressDialogUtils pd;
    SharedPreferences sp,spLocation;
    private TextView title;
    private TextView zuche_title_tv;
    private ImageView guanzhu_iv;
    private TextView jiage_tv;
    private TextView zuche_content_tv;
    private TextView mianji_tv;
    private TextView fangxing_tv;
    private TextView location_tv;
    private MyListView myListView;
    private TextView guanzhu_tv;
    private TextView tripdetail_comment_edit;
    MasterModel mMasterModel;
    private ScrollView scrollView;
    private String id,info;
    private String TAG = "ZuFangDetailActivity";
    private UMWeb umWeb;
    private UMImage image;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {// 成功
                String is_follow = mMasterModel.getIs_follow();
                if (TextUtils.equals(is_follow,"0")){
                    guanzhu_tv.setText("关注");
                    guanzhu_iv.setImageDrawable(getResources().getDrawable(R.drawable.praise_black));
                }else {
                    guanzhu_tv.setText("已关注");
                    guanzhu_iv.setImageDrawable(getResources().getDrawable(R.drawable.praise_red));
                }
            } else if (msg.what == 0) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == -1) {// 其他情况
                ToastUtil.showToast(getApplicationContext(), info);
            }else if (msg.what == 2) {// 成功关注
                ToastUtil.showToast(getApplicationContext(), info);
                getData();
            }else if (msg.what == -2) {// 关注失败
                ToastUtil.showToast(getApplicationContext(), info);
            }
            if (pd != null && ZuFangDetailActivity.this != null) {
                pd.dismiss();
            }
        }
    };
    private String contacts;
    private LinearLayout tripdetail_comment_layout2;
    private ImageView title_share;
    private LinearLayout call_phone_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zu_fang_detail);
        initView();
    }

    private void initView() {
        sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        title_share = findViewById(R.id.title_share);
        title_share.setVisibility(View.GONE);
        scrollView = findViewById(R.id.scrollView);
        zuche_title_tv = findViewById(R.id.zuche_title_tv);
        guanzhu_iv = findViewById(R.id.guanzhu_iv);
        guanzhu_tv = findViewById(R.id.guanzhu_tv);
        jiage_tv = findViewById(R.id.jiage_tv);
        zuche_content_tv = findViewById(R.id.zuche_content_tv);
        mianji_tv = findViewById(R.id.mianji_tv);
        fangxing_tv = findViewById(R.id.fangxing_tv);
        location_tv = findViewById(R.id.location_tv);
        myListView = findViewById(R.id.zuche_listview);
        call_phone_ll = findViewById(R.id.call_phone_ll);
        tripdetail_comment_edit = findViewById(R.id.tripdetail_comment_edit);
        tripdetail_comment_layout2 = findViewById(R.id.tripdetail_comment_layout2);

        title.setText("租房详情");

        pd = ProgressDialogUtils.show(this, "加载数据...");
        back.setOnClickListener(this);
        title_share.setOnClickListener(this);
        guanzhu_iv.setOnClickListener(this);
        tripdetail_comment_edit.setOnClickListener(this);
        tripdetail_comment_layout2.setOnClickListener(this);
        call_phone_ll.setOnClickListener(this);
        getIntentData();
    }

    private void getData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("id",id);
                    data.put("mid",sp.getString(XZContranst.id, null));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.rentalContent;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "租房详情提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "租房详情返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    if(TextUtils.equals(status, "1")){
                        JSONObject djson = jsonj.getJSONObject("data");
                        mMasterModel= JSON.parseObject(djson.toString(), MasterModel.class);
                    }
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

    private void getIntentData() {
        Intent intent=getIntent();
        mMasterModel=(MasterModel) intent.getExtras().getSerializable("model");
        setData();
    }

    private void setData() {
        zuche_title_tv.setText(mMasterModel.getTitle());
        jiage_tv.setText(mMasterModel.getMoney()+"元/天");
        zuche_content_tv.setText(mMasterModel.getContent());
        mianji_tv.setText("面积："+ mMasterModel.getMeasure()+"㎡");
        fangxing_tv.setText("房型："+ mMasterModel.getLayoutRoom()+mMasterModel.getLayoutOffice());
        location_tv.setText(mMasterModel.getPosition());
        if(!TextUtils.isEmpty(mMasterModel.getImg())){
            MyListAdapter adapter = new MyListAdapter(this, mMasterModel.getImg());
            myListView.setAdapter(adapter);
            scrollView.smoothScrollTo(0,0);//防止scrollView 跳到listview位置
        }
        id = mMasterModel.getId();
        contacts = mMasterModel.getContacts();

        image = new UMImage(this, R.drawable.logo);
        image.setThumb(image);

        /*//大小压缩
        image.compressStyle = UMImage.CompressStyle.SCALE;
        //质量压缩
        image.compressStyle = UMImage.CompressStyle.QUALITY;
        */
        String url = "https://www.baidu.com/";
        umWeb = new UMWeb(url);
        umWeb.setTitle("这是标题！！！");
        umWeb.setThumb(image);
        umWeb.setDescription("这是内容~~~~~~~~");

        getData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == back){
            finish();
        }else if (v == guanzhu_iv){
            getGuanZhu(true);
        }else if (v == call_phone_ll){
            showEnsure("拨打电话:"+contacts,contacts);
        } else if (v == tripdetail_comment_layout2){
            Intent intent = new Intent(ZuFangDetailActivity.this,ZuFangDetilCommentActivity.class);
            intent.putExtra("tid",mMasterModel.getId());
            startActivity(intent);
        }else if (v == tripdetail_comment_edit){
            Intent intent = new Intent(ZuFangDetailActivity.this,ZuFangDetilCommentActivity.class);
            intent.putExtra("tid",mMasterModel.getId());
            startActivity(intent);
        }else if (v == title_share){
            new ShareAction(ZuFangDetailActivity.this).setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,
                    SHARE_MEDIA.QQ,SHARE_MEDIA.SINA)
                    .withMedia(image)
                    .withMedia(umWeb)
                    .setCallback(umShareListener)
                    .open();
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtil.showToast(getApplicationContext(),"分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtil.showToast(getApplicationContext(),"分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtil.showToast(getApplicationContext(),"分享取消");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    protected void showEnsure(String message, final String phone){
        DialogUtils.showEnsure(this, message, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callIntent = new Intent(
                        Intent.ACTION_DIAL, Uri
                        .parse("tel:"+phone));
                startActivity(callIntent);
                dialog.dismiss();
            }
        });
    }

    private void getGuanZhu(boolean ifshow) {
        if (ifshow) {
            pd = ProgressDialogUtils.show(this, "加载数据...");
            pd.show();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("tid",id);
                    data.put("mid", sp.getString(XZContranst.id, null));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.rentalFollow;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "租房关注提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "租房关注返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
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

    public class MyListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        Context context;
        DisplayImageOptions options;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        ImageLoader imageLoader = ImageLoader.getInstance();
        String list ;
        private DisplayMetrics dm;
        private RelativeLayout.LayoutParams imagebtn_params;

        public MyListAdapter(Context context, String list) {
            super();
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.imaleloadlogo)
                    .showImageForEmptyUri(R.drawable.imaleloadlogo)
                    .showImageOnFail(R.drawable.imaleloadlogo)
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(10))
                    .build();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));

            dm = context.getResources().getDisplayMetrics();
            imagebtn_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imagebtn_params.height = dm.widthPixels;
        }

        public int getCount() {
            return list.split(",").length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_imageview_layout,
                        null, false);
                holder.face = (ImageView) convertView
                        .findViewById(R.id.item_tripdetail_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.face.setLayoutParams(imagebtn_params);
            if (!TextUtils.isEmpty(list)) {
                imageLoader.displayImage(list.split(",")[position],
                        holder.face, options,animateFirstListener);
            }

            return convertView;
        }

        final class ViewHolder {
            ImageView face;
        }
    }
}
