package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.power.travel.xixuntravel.adapter.AllTravelListAdapter;
import com.power.travel.xixuntravel.adapter.AllTripListAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.impl.AllTripOnItemOnClickListener;
import com.power.travel.xixuntravel.model.AllTravelModel;
import com.power.travel.xixuntravel.model.AllTripModel;
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

import java.util.ArrayList;
import java.util.List;


public class YueBanActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, AllTripOnItemOnClickListener {

    private ImageView back;
    private LinearLayout radioGroup,layout_top;
    private Button left, right,top_left, top_right;
    private int listtype = 1;// 1约伴（行程）2游记
    private ProgressDialogUtils pd;
    SharedPreferences sp, spLocation;
    PullToRefreshListView mListView, travelListView;
    View trip_head_img,trip_head_btn;
    List<AllTripModel> adapterList = new ArrayList<AllTripModel>();
    List<AllTripModel> adapterListMore = new ArrayList<AllTripModel>();
    AllTripListAdapter adapter;
    List<AllTravelModel> travelList = new ArrayList<AllTravelModel>();
    List<AllTravelModel> travelListMore = new ArrayList<AllTravelModel>();
    AllTravelListAdapter travelAdapter;
    int page = 1, travelpage = 1, changePosi;
    private String TAG = "YueBanFragment", info, province, province_id, city,
            city_id, country, country_id, adlogo, adurl, adname,sex,sex_str,age,age_str
            , is_at, is_carpool;
    private TextView filtrate_tv, search;
    private ImageView ad_iv;
//	private TextView locaton_tv;

    DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (pd != null && YueBanActivity.this != null) {
                pd.dismiss();
            }
            if (YueBanActivity.this != null && mListView != null) {
                mListView.onRefreshComplete();
            }
            if (msg.what == 1) {// 成功
                if (page == 1) {
                    adapterList.addAll(adapterListMore);
                    adapter = new AllTripListAdapter(YueBanActivity.this, adapterList);
                    adapter.setOnItemOnClick(YueBanActivity.this);
                    mListView.setAdapter(adapter);
//					scroll.getRefreshableView().smoothScrollTo(0,0);//防止scrollView 跳到listview位置
                } else {
                    adapterList.addAll(adapterListMore);
                    adapter.notifyDataSetChanged();
                }
//				LogUtil.e(TAG, "数据的个数"+adapterList.size());
                page = page + 1;
            } else if (msg.what == 0) {// 失败
                ToastUtil.showToast(YueBanActivity.this, info);
            } else if (msg.what == -1) {//
                ToastUtil.showToast(YueBanActivity.this, info);
            } else if (msg.what == 4) {// 关注 成功
                changeData("1");
                adapter.notifyDataSetChanged();
            } else if (msg.what == -4) {// 关注 失败
                ToastUtil.showToast(YueBanActivity.this, info);
            } else if (msg.what == 5) {// 取消关注 成功
                changeData("0");
                adapter.notifyDataSetChanged();
            } else if (msg.what == -5) {// 取消关注 失败
                ToastUtil.showToast(YueBanActivity.this, info);
            }

        }
    };

    private Handler travelhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (pd != null && YueBanActivity.this != null) {
                pd.dismiss();
            }
            if (YueBanActivity.this != null && travelListView != null) {
                travelListView.onRefreshComplete();
            }
            if (msg.what == 1) {// 成功
                if (travelpage == 1) {
                    travelList.addAll(travelListMore);
                    travelAdapter = new AllTravelListAdapter(YueBanActivity.this,
                            travelList);
                    travelListView.setAdapter(travelAdapter);
//					scroll.getRefreshableView().smoothScrollTo(0,0);//防止scrollView 跳到listview位置
                } else {
                    travelList.addAll(travelListMore);
                    travelAdapter.notifyDataSetChanged();
                }
                travelpage = travelpage + 1;
            } else if (msg.what == 0) {// 失败
                ToastUtil
                        .showToast(YueBanActivity.this, info);
            } else if (msg.what == -1) {//
                ToastUtil
                        .showToast(YueBanActivity.this, info);

            }
        }
    };

    private Handler adHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 2) {// 加载广告
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration
                        .createDefault(YueBanActivity.this));
                /**
                 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
                 */
                imageLoader.displayImage(HttpUrl.Url + adlogo, ad_iv, options,
                        animateFirstListener);
            }

        }
    };

    private void changeData(String follow) {
        AllTripModel model = adapterList.get(changePosi);
        model.setFollow(follow);
        adapterList.set(changePosi, model);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yue_ban);
        initView();
        initListener();
        listtype = getIntent().getIntExtra("listtype",1);
        getADInfo();
        switch (listtype) {
            case 1:
                setLeft();
                mListView.setVisibility(View.VISIBLE);
                getData(false);
                break;
            case 2:
                setRight();
                break;
            default:
                break;
        }
        //约伴
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(YueBanActivity.this,
                        TripDetailActivity.class);
                intent.putExtra("id", adapterList.get(position-3).getId());
                intent.putExtra("mid",adapterList.get(position-3).getMid());
                startActivity(intent);
            }
        });
        //游记
        /*travelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(YueBanActivity.this,
                        TravelDetailActivity.class);
                intent.putExtra("id", travelList.get(position-3).getId());
                startActivity(intent);
            }
        });*/

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 2) {
                    layout_top.setVisibility(View.VISIBLE);
                    trip_head_btn.setVisibility(View.GONE);
                } else {
                    layout_top.setVisibility(View.GONE);
                    trip_head_btn.setVisibility(View.VISIBLE);
                }

            }

        });

        travelListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 2) {
                    layout_top.setVisibility(View.VISIBLE);
                    trip_head_btn.setVisibility(View.GONE);
                } else {
                    layout_top.setVisibility(View.GONE);
                    trip_head_btn.setVisibility(View.VISIBLE);
                }

            }

        });
    }

    @SuppressLint("NewApi")
    private void setLeft() {
        left.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_selectedbg_yueban_left));
        right.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_normallbg));
        top_left.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_selectedbg_yueban_left));
        top_right.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_normallbg));
        travelListView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        listtype = 1;

    }

    @SuppressLint("NewApi")
    private void setRight() {
        left.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_normallbg));
        right.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_selectedbg_yueban_right));
        top_left.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_normallbg));
        top_right.setBackground(getResources().getDrawable(
                R.drawable.tab_btn_selectedbg_yueban_right));
        mListView.setVisibility(View.GONE);
        travelListView.setVisibility(View.VISIBLE);
        listtype = 2;

        if (travelList.isEmpty()) {
            getTravelData(true);
        }
    }

    /**
     * 获取游记
     *
     * @param ifshow
     */
    private void getTravelData(boolean ifshow) {
        if (ifshow) {
            pd.show();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("page", travelpage);
                    data.put("mid", sp.getString(XZContranst.id, null));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.all_triavellists;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "游记提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "游记返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    if (TextUtils.equals(status, "1")) {
                        JSONArray arry = jsonj.getJSONArray("data");
                        travelListMore = JSON.parseArray(arry.toString(),
                                AllTravelModel.class);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    travelhandler.sendEmptyMessage(1);
                } else if (TextUtils.equals(status, "0")) {
                    travelhandler.sendEmptyMessage(0);
                } else {
                    travelhandler.sendEmptyMessage(-1);
                }

            }
        }).start();
    }

    /**
     * 获取约伴（行程）
     *
     * @param ifshow
     */
    private void getData(boolean ifshow) {
        if (ifshow) {
            pd.show();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("mid", sp.getString(XZContranst.id, null));
                    data.put("page", page);
                    data.put("is_at", is_at);
                    data.put("is_carpool", is_carpool);
                    data.put("province", province_id);
                    data.put("city", city_id);
                    data.put("area", country_id);
                    data.put("sex", sex);
                    data.put("age", age);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.all_triplists;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "约伴提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "行程返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    if (TextUtils.equals(status, "1")) {
                        JSONArray arry = jsonj.getJSONArray("data");
                        adapterListMore = JSON.parseArray(arry.toString(),
                                AllTripModel.class);
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

    private void initListener() {
        back.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        top_left.setOnClickListener(this);
        top_right.setOnClickListener(this);
        filtrate_tv.setOnClickListener(this);
        search.setOnClickListener(this);
        ad_iv.setOnClickListener(this);
    }

    private void initView() {
        sp = YueBanActivity.this.getSharedPreferences(
                XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        spLocation = YueBanActivity.this.getSharedPreferences(
                XZContranst.MAIN_SHARED_PREFERENCES_LO, Context.MODE_PRIVATE);
        pd = ProgressDialogUtils.show(YueBanActivity.this, "加载数据...");
        back = (ImageView) findViewById(R.id.back);

        mListView = (PullToRefreshListView) findViewById(R.id.yueban_listview);
        mListView.setOnRefreshListener(this);
//		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
//		mListView.setFocusable(false);//防止加载数据时界面跳转到listview或者geidview位置
        // scroll.setMode(Mode.DISABLED);//什么也没有
        // scroll.setMode(Mode.PULL_FROM_START);//只有刷新
        mListView.setMode(PullToRefreshBase.Mode.BOTH);// 刷新加载更多都有
        trip_head_img= View.inflate(YueBanActivity.this, R.layout.item_head_img_layout, null);
        trip_head_btn= View.inflate(YueBanActivity.this, R.layout.item_head_layout, null);

        ad_iv = (ImageView) trip_head_img.findViewById(R.id.ad_iv);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams imagebtn_params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imagebtn_params.height = dm.widthPixels * 1 / 2;
        imagebtn_params.width = dm.widthPixels;
        ad_iv.setLayoutParams(imagebtn_params);
        radioGroup = (LinearLayout) trip_head_btn.findViewById(R.id.yueban_radiogroup);
        left = (Button) trip_head_btn.findViewById(R.id.tab_rb_yueban);
        right = (Button) trip_head_btn.findViewById(R.id.tab_rb_travel);

        mListView.getRefreshableView().addHeaderView(trip_head_img);
        mListView.getRefreshableView().addHeaderView(trip_head_btn);
        layout_top= (LinearLayout) findViewById(R.id.yueban_top);
        travelListView = (PullToRefreshListView) findViewById(R.id.travel_listview);
//		travelListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
        travelListView.setOnRefreshListener(this);
        travelListView.setMode(PullToRefreshBase.Mode.BOTH);// 刷新加载更多都有
        travelListView.getRefreshableView().addHeaderView(trip_head_img);
        travelListView.getRefreshableView().addHeaderView(trip_head_btn);
        filtrate_tv = (TextView) findViewById(R.id.filtrate_tv);
        search = (TextView) findViewById(R.id.search);
        top_left= (Button) findViewById(R.id.left_top);
        top_right= (Button) findViewById(R.id.right_top);
//		locaton_tv = (TextView) view.findViewById(R.id.locaton_tv);
//		locaton_tv.setText(spLocation.getString(XZContranst.location_city, ""));

        options = new DisplayImageOptions.Builder()
                // .showStubImage(R.drawable.imaleloadlogo)// 设置图片在下载期间显示的图片
                // .showImageForEmptyUri(R.drawable.imaleloadlogo)//
                // 设置图片Uri为空或是错误的时候显示的图片
                // .showImageOnFail(R.drawable.imaleloadlogo)// 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(false)// 是否緩存都內存中
                .cacheOnDisc(true)// 是否緩存到sd卡上
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                .displayer(new RoundedBitmapDisplayer(5))// 设置用户加载图片task(这里是圆角图片显示)
                .build();
    }

    private void getADInfo() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("type", "2");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.ad;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "广告提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "广告返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    if (TextUtils.equals(status, "1")) {
                        JSONObject jsonobj = jsonj.getJSONObject("data");
                        adlogo = jsonobj.getString("logo");
                        adurl = jsonobj.getString("url");
                        adname = jsonobj.getString("name");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    adHandler.sendEmptyMessage(2);
                }
                // else if (TextUtils.equals(status, "0")) {
                // travelhandler.sendEmptyMessage(0);
                // } else {
                // travelhandler.sendEmptyMessage(-1);
                // }

            }
        }).start();

    }


    @Override
    public void onClick(View v) {
        if (v == back) {
//			addaddress_title.setVisibility(View.VISIBLE);
//			first_layout.setVisibility(View.VISIBLE);
//			list_data_layout.setVisibility(View.GONE);
            finish();
        } else if (v == filtrate_tv) {// 筛选
            //
            Intent intent = new Intent(YueBanActivity.this, FiltrateActivity.class);
            intent.putExtra("is_at", is_at);
            intent.putExtra("is_carpool", is_carpool);
            intent.putExtra("province_id", province_id);
            intent.putExtra("province", province);
            intent.putExtra("city_id", city_id);
            intent.putExtra("city", city);
            intent.putExtra("country_id", country_id);
            intent.putExtra("country", country);
            intent.putExtra("sex", sex);
            intent.putExtra("sex_str", sex_str);
            intent.putExtra("age", age);
            intent.putExtra("age_str", age_str);
            startActivityForResult(intent, 11);
            this.overridePendingTransition(R.anim.bottom_to_top,
                    R.anim.alpha_go);
        } else if (v == search) {// 搜索
            Intent intent = new Intent(YueBanActivity.this, SearchActivity.class);
            startActivity(intent);
        }  else if (v == ad_iv) {//广告
//            if (!TextUtils.isEmpty(adurl)) {
                Intent intent = new Intent(YueBanActivity.this, WebViewActivity.class);
                intent.putExtra("url", adurl);
                intent.putExtra("name", adname);
                startActivity(intent);
//            }
        }else if (v == left) {// 约伴
            setLeft();
            if (adapterList.isEmpty()) {
                getData(true);
            }
        } else if (v == right) {// 游记
            setRight();
        }else if (v == top_left) {// 约伴
            setLeft();
            if (adapterList.isEmpty()) {
                getData(true);
            }
        } else if (v == top_right) {// 游记
            setRight();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==11){
                is_at=data.getStringExtra("is_at");
                is_carpool=data.getStringExtra("is_carpool");
                province_id=data.getStringExtra("province_id");
                province=data.getStringExtra("province");
                city_id=data.getStringExtra("city_id");
                city=data.getStringExtra("city");
                country_id=data.getStringExtra("country_id");
                country=data.getStringExtra("country");
                sex=data.getStringExtra("sex");
                sex_str=data.getStringExtra("sex_str");
                age=data.getStringExtra("age");
                age_str=data.getStringExtra("age_str");
                setLeft();
//				scroll.getRefreshableView().smoothScrollTo(0,0);//防止scrollView 跳到listview位置
                if (!adapterList.isEmpty()) {
                    adapterList.clear();
                    adapter.notifyDataSetChanged();
                }
                page=1;
                getData(true);
            }
        }
    }

    @Override
    public void OnItemClick(int typeint, int position) {
        if (sp.getBoolean(XZContranst.if_login, false)) {
            changePosi = position;
            // typeint1关注0取消关注
            switch (typeint) {
                case 0:
                    // Follow_cancel(adapterList.get(position).getId());
                    break;
                case 1:
                    Follow(adapterList.get(position).getMid());
                    break;
                default:
                    break;
            }
        } else {
            startActivity(new Intent(YueBanActivity.this, LoginActivity.class));
        }
    }

    // 关注
    private void Follow(final String fid) {
        pd = ProgressDialogUtils.show(YueBanActivity.this, "提交数据...");
        pd.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("mid", sp.getString(XZContranst.id, null));
                    data.put("fid", fid);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.followAdd;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "关注提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "关注返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    handler.sendEmptyMessage(4);
                } else if (TextUtils.equals(status, "0")) {
                    handler.sendEmptyMessage(-4);
                } else {
                    handler.sendEmptyMessage(-1);
                }

            }
        }).start();
    }

    // 取消关注
    private void Follow_cancel(final String fid) {
        pd = ProgressDialogUtils.show(YueBanActivity.this, "提交数据...");
        pd.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("mid", sp.getString(XZContranst.id, null));
                    data.put("id", fid);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.follow_cancel;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "取消关注提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "取消关注返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    handler.sendEmptyMessage(5);
                } else if (TextUtils.equals(status, "0")) {
                    handler.sendEmptyMessage(-5);
                } else {
                    handler.sendEmptyMessage(-1);
                }

            }
        }).start();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (isConnect()) {
            switch (listtype) {
                case 1:// 约伴
                    if (!adapterList.isEmpty()) {
                        adapterList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    page = 1;
                    getData(false);
                    break;
                case 2:// 游记
                    if (!travelList.isEmpty()) {
                        travelList.clear();
                        travelAdapter.notifyDataSetChanged();
                    }
                    travelpage = 1;
                    getTravelData(false);
                    break;
                default:
                    break;
            }

        } else {
            ToastUtil.showToast(YueBanActivity.this.getApplicationContext(),
                    XZContranst.no_net);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (isConnect()) {
            switch (listtype) {
                case 1:// 约伴
                    getData(false);
                    break;
                case 2:// 游记
                    getTravelData(false);
                    break;
                default:
                    break;
            }

        } else {
            ToastUtil.showToast(YueBanActivity.this.getApplicationContext(),
                    XZContranst.no_net);
        }
    }
}
