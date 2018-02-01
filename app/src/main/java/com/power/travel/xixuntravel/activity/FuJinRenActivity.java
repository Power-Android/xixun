package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;
import com.power.travel.xixuntravel.widget.WheelView;
import com.power.travel.xixuntravel.widget.adapters.ArrayWheelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FuJinRenActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener2<ListView> {

    private ImageView back;
    private TextView title;
    private ProgressDialogUtils pd;
    private PullToRefreshListView mListView;
    SharedPreferences sp,spLocation;
    private String data, TAG = "FuJinRenActivity",info;
    List<MasterModel> adapterList = new ArrayList<MasterModel>();
    List<MasterModel> adapterListMore = new ArrayList<MasterModel>();
    int page = 1;
    FujinrenAdapter adapter;
    private LinearLayout addaddress_wheel;
    private TextView  addaddress_wheel_cancel,addaddress_wheel_title, addaddress_wheel_sure;
    private WheelView mProvince;// 省的WheelView控件
    private String[] mProvinceDatas;// 所有省
    private String mCurrentProviceName;// 当前省的名称
    private int posi;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {// 成功
                if(!adapterList.isEmpty()){
                    adapterList.clear();
                }
                adapterList.addAll(adapterListMore);
                if (page == 1) {
                    adapter = new FujinrenAdapter(FuJinRenActivity.this, adapterList);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                page=page+1;
            } else if (msg.what == 0) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == -1) {//
				ToastUtil.showToast(getApplicationContext(), info);
            }
            if (pd != null && FuJinRenActivity.this != null) {
                pd.dismiss();
            }
            if (FuJinRenActivity.this != null && mListView != null) {
                mListView.onRefreshComplete();
            }
        }
    };
    private ImageView title_dian;
    private String sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu_jin_ren);
        MyApplication.getInstance().addActivity(this);
        initView();
        if(isConnect()){
            getData(true);
        }else{
            ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
        }
    }

    private void initView() {
        sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        spLocation= this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
                Context.MODE_PRIVATE);
        pd = ProgressDialogUtils.show(this, "加载数据...");
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        title_dian = findViewById(R.id.title_dian);
        title_dian.setVisibility(View.VISIBLE);
        title.setText("附近人");
        mListView = (PullToRefreshListView) findViewById(R.id.fujinren_listview);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MasterModel masterModel=adapterList.get(position-1);
                Intent intent=new Intent(FuJinRenActivity.this,UserCenterActivity.class);
                intent.putExtra("model", masterModel);
                startActivity(intent);
            }
        });
        initListener();
    }

    private void initListener() {
        back.setOnClickListener(this);
        title_dian.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == back) {
            finish();
        }else if (v == title_dian){
            new PopAgeOrSex(this, title_dian, 1);
            mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
                    mProvinceDatas));
        }
    }

    // 选性别年龄
    public class PopAgeOrSex extends PopupWindow {

        public PopAgeOrSex(Context mContext, View parent, final int type) {

            View view = View.inflate(mContext, R.layout.item_popsexorage, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            addaddress_wheel = (LinearLayout) view
                    .findViewById(R.id.addaddress_wheel_layout);
            addaddress_wheel.startAnimation(AnimationUtils.loadAnimation(
                    mContext, R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();
            addaddress_wheel_title= (TextView) view
                    .findViewById(R.id.addaddress_wheel_title);
            addaddress_wheel_cancel = (TextView) view
                    .findViewById(R.id.addaddress_wheel_cancel);
            addaddress_wheel_sure = (TextView) view
                    .findViewById(R.id.addaddress_wheel_sure);
            mProvince = (WheelView) view.findViewById(R.id.id_wheelview);
            // 添加change事件
            // 省市区各显示几个数据
            mProvince.setVisibleItems(5);
            if (type == 1) {// 性别
                addaddress_wheel_title.setText("");
                mProvinceDatas = new String[] { "只看男生", "只看女生", "查看全部" };
            }

            addaddress_wheel_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    dismiss();
                }
            });
            addaddress_wheel_sure.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        int pCurrent = mProvince.getCurrentItem();
                        mCurrentProviceName = mProvinceDatas[pCurrent];

                        if (isConnect()) {
                            if (type == 1) {//
                                LogUtil.e(TAG,pCurrent+"--------");
                                if (TextUtils.equals("0",pCurrent+"")){
                                    sex = "1";
                                }
                                if (TextUtils.equals("1",pCurrent+"")){
                                    sex = "2";
                                }
                                if (TextUtils.equals("2",pCurrent+"")){
                                    sex = "0";
                                }
                                getData(true);
                            }
                        }else{
                            ToastUtil.showToast(getApplicationContext(),getResources().getString(R.string.notnetwork));
                        }
                    } catch (Exception e) {
                    }
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if(isConnect()){
            if(!adapterList.isEmpty()){
                adapterList.clear();
                adapter.notifyDataSetChanged();
            }
            page=1;
            getData(false);
        }else{
            ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if(isConnect()){
            getData(false);
        }else{
            ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
        }
    }

    private void getData(boolean ifshow) {
        if (ifshow) {
            pd.show();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("x", spLocation.getString(XZContranst.coordinate_x, ""));
                    data.put("y", spLocation.getString(XZContranst.coordinate_y, ""));
                    data.put("sex",sex);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.nearbyPeople;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "地主达人提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "地主达人返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    if(TextUtils.equals(status, "1")){
                        JSONArray arry = jsonj.getJSONArray("data");
                        adapterListMore = JSON.parseArray(arry.toString(),
                                MasterModel.class);
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

    public class FujinrenAdapter extends BaseAdapter implements View.OnClickListener{

        private LayoutInflater inflater;
        Context context;
        List<MasterModel> list=new ArrayList<>();
        DisplayImageOptions options;
        ImageLoader imageLoader = ImageLoader.getInstance();
        ViewHolder holder = null;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();


        public FujinrenAdapter(Context context, List<MasterModel> list){
            super();
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
            options = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(20))
                    .build();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_fujinren_layout, null, false);

                holder.nickname = (TextView) convertView
                        .findViewById(R.id.item_master_nickname);
                holder.distance = (TextView) convertView
                        .findViewById(R.id.item_master_distance);
                holder.sex = (ImageView) convertView
                        .findViewById(R.id.item_master_sex);
                holder.face = (ImageView) convertView
                        .findViewById(R.id.item_master_face);
                holder.content = (TextView) convertView
                        .findViewById(R.id.item_content_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            imageLoader.displayImage(list.get(position).getFace(),
                    holder.face, options,animateFirstListener);

            holder.nickname.setText(list.get(position).getNickname());
            holder.content.setText(list.get(position).getSignature());
            holder.distance.setText(list.get(position).getDistance());
            if(TextUtils.equals(list.get(position).getSex(),"1")){
                holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_boy));
            }else if(TextUtils.equals(list.get(position).getSex(), "2")){
                holder.sex.setImageDrawable(context.getResources().getDrawable(R.drawable.my_girle));
            }else if (TextUtils.isEmpty(list.get(position).getSex())){
                holder.sex.setVisibility(View.GONE);
            }

            return convertView;
        }

        final class ViewHolder {
            TextView nickname,title,distance,content;
            ImageView face,sex;
        }
    }
}
