package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.AgeAdapter;
import com.power.travel.xixuntravel.adapter.Area_onlyAdapter;
import com.power.travel.xixuntravel.adapter.CarAdapter;
import com.power.travel.xixuntravel.adapter.CarTypeAdapter;
import com.power.travel.xixuntravel.adapter.CarageAdapter;
import com.power.travel.xixuntravel.adapter.SexAdapter;
import com.power.travel.xixuntravel.model.AgeModel;
import com.power.travel.xixuntravel.model.AreaModel;
import com.power.travel.xixuntravel.model.CarModel;
import com.power.travel.xixuntravel.model.SexModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class FiltrateTwoActivity extends AppCompatActivity implements OnClickListener {

    int clickType = 1;
    private LinearLayout zuche_lay;
    private TextView zuche_pinpai;
    private TextView zuche_chexing;
    private TextView zuche_cheling;
    private TextView zuche_zujin;
    private TextView zuche_shi;
    private TextView zuche_qu;
    private Button filtarate_reset;
    private Button filtarate_sure;
    private List<CarModel> adapterList = new ArrayList<CarModel>();
    private List<CarModel> CarTypeList = new ArrayList<CarModel>();
    private List<AreaModel> zujinList = new ArrayList<AreaModel>();

    private ProgressDialogUtils pd;
    private ListView carListView;
    CarTypeAdapter pinpaiAdapter, chexingAdapter;
    CarageAdapter carageAdapter;
    private PopupWindow popupWindow;
    private String pinpai, pinpaiid, chexing, chexingid;
    private String TAG = "FiltrateTwoActivity", info, headportrait,province, city,
            city_id, country, country_id, brand, models, age, money, area;
    private List<AreaModel> provinceList = new ArrayList<AreaModel>();
    private List<AreaModel> cityList = new ArrayList<AreaModel>();
    private List<AreaModel> countyList = new ArrayList<AreaModel>();
    Area_onlyAdapter mAreaAdapter, cityAdapter, countyAdapter;
    private ListView province_list;
    Area_onlyAdapter1 mAreaAdapter1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {//
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 2) {// 上传图片成功

            } else if (msg.what == -2) {// 上传图片失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 3) {// 成功
                initmPopupWindowView1(clickType);
                switch (clickType) {
                    case 2:
                        popupWindow.showAsDropDown(zuche_shi, 0, 0);
                        break;
                    case 3:
                        popupWindow.showAsDropDown(zuche_qu, 0, 0);
                        break;
                    default:
                        break;
                }
            } else if (msg.what == -3) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 4) {//发布成功
                ToastUtil.showToast(getApplicationContext(), info);
                onBackPressed();
            } else if (msg.what == -4) {// 发布失败
                ToastUtil.showToast(getApplicationContext(), info);
            }else if (msg.what == 5) {// 成功
                initmPopupWindowView(clickType);
                switch (clickType) {
                    case 1:
                        popupWindow.showAsDropDown(zuche_pinpai, 0, 0);
                        break;
                    case 2:
                        popupWindow.showAsDropDown(zuche_chexing, 0, 0);
                        break;
                    default:
                        break;
                }
            } else if (msg.what == -5) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 6) {
                initmPopupWindowView2(1);
                popupWindow.showAsDropDown(zuche_zujin,0,0);
            }

            if (pd != null && FiltrateTwoActivity.this != null) {
                pd.dismiss();
            }

        }
    };


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_filtrate_two);

        //设置宽度，不然有间隙
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        init();
        initgetIntent();

    }

    /**
     * 设置位置
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.BOTTOM;

        getWindowManager().updateViewLayout(view, lp);
    }

    public void onBackPressed() {
        finish();
        this.overridePendingTransition(R.anim.alpha_exit, R.anim.top_to_bottom);
        super.onBackPressed();
    }

    private void init() {
        zuche_lay = (LinearLayout) findViewById(R.id.filtrate_yueban_layout);
        zuche_pinpai = (TextView) findViewById(R.id.zuche_pinpai);
        zuche_chexing = (TextView) findViewById(R.id.zuche_chexing);
        zuche_cheling = (TextView) findViewById(R.id.zuche_cheling);
        zuche_zujin = (TextView) findViewById(R.id.zuche_zujin);
        zuche_shi = (TextView) findViewById(R.id.zuche_shi);
        zuche_qu = (TextView) findViewById(R.id.zuche_qu);
        filtarate_reset = (Button)findViewById(R.id.filtarate_reset);
        filtarate_sure = (Button)findViewById(R.id.filtarate_sure);

        zuche_lay.setOnClickListener(this);
        zuche_pinpai.setOnClickListener(this);
        zuche_chexing.setOnClickListener(this);
        zuche_cheling.setOnClickListener(this);
        zuche_zujin.setOnClickListener(this);
        zuche_shi.setOnClickListener(this);
        zuche_qu.setOnClickListener(this);
        filtarate_reset.setOnClickListener(this);
        filtarate_sure.setOnClickListener(this);
    }
    private void initgetIntent() {
        Intent data=getIntent();
        brand = data.getStringExtra("brand");
        models = data.getStringExtra("models");
        age = data.getStringExtra("age");
        money = data.getStringExtra("money");
        LogUtil.e(TAG,money+"22222222");
        city = data.getStringExtra("city");
        area = data.getStringExtra("area");

        if(!TextUtils.isEmpty(brand)){
            zuche_pinpai.setText(brand);
        }
        if(!TextUtils.isEmpty(models)){
            zuche_chexing.setText(models);
        }
        if(!TextUtils.isEmpty(age)){
            zuche_cheling.setText(age);
        }
        if (!TextUtils.isEmpty(money)){
            zuche_zujin.setText(money);
            LogUtil.e(TAG,money+"3333333");
        }
        if (!TextUtils.isEmpty(city)){
            zuche_shi.setText(city);
        }
        if (!TextUtils.isEmpty(area)){
            zuche_qu.setText(area);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == zuche_pinpai) {// 品牌
            clickType = 1;
//            if (adapterList.isEmpty()) {// 第一次
                getData("0");
//            } else {
//                initmPopupWindowView(clickType);
//                popupWindow.showAsDropDown(zuche_pinpai, 0, 0);
//            }
        } else if (view == zuche_chexing) {// 车型
            if (TextUtils.isEmpty(pinpai)) {
                ToastUtil.showToast(getApplicationContext(), "请选择品牌！");
            } else {
                clickType = 2;
//                if (CarTypeList.isEmpty()) {
                    getData(pinpaiid);
//                } else {
//                    initmPopupWindowView(clickType);
//                    popupWindow.showAsDropDown(zuche_chexing, 0, 0);
//                }
            }
        } else if (view == zuche_cheling) {// 车龄
            clickType = 3;
            initmPopupWindowView(clickType);
            popupWindow.showAsDropDown(zuche_cheling, 0, 0);
        }else if (view == zuche_shi) {// 市
                clickType = 2;
                if (cityList.isEmpty()) {
                    getData1("28");
                } else {
                    initmPopupWindowView1(clickType);
                    popupWindow.showAsDropDown(zuche_shi, 0, 0);
                }
            } else if (view == zuche_qu) {// 区
            if (TextUtils.isEmpty(city)) {
                ToastUtil.showToast(getApplicationContext(), "请选择市！");
            } else {
                clickType = 3;
                if (countyList.isEmpty()) {
                    getData1(city_id);
                } else {
                    initmPopupWindowView1(clickType);
                    popupWindow.showAsDropDown(zuche_qu, 0, 0);
                }
            }
        }else if (view == zuche_zujin){
            clickType = 1;
            if (zujinList.isEmpty()){
                getData2();
            }else {
                initmPopupWindowView2(1);
                popupWindow.showAsDropDown(zuche_zujin,0,0);
            }
        }else if (view == filtarate_reset){
            adapterList.clear();
            brand = "";
            zuche_pinpai.setText("品牌");
            CarTypeList.clear();
            models = "";
            zuche_chexing.setText("车型");
            age = "";
            zuche_cheling.setText("车龄");
            zujinList.clear();
            money = "";
            zuche_zujin.setText("租金");
            provinceList.clear();
            city = "";
            zuche_shi.setText("市");
            cityList.clear();
            area = "";
            zuche_qu.setText("区");

        }else if (view == filtarate_sure){
            Intent intent = getIntent();
                intent.putExtra("brand",brand);
                intent.putExtra("models",models);
                intent.putExtra("age",age);
                LogUtil.e(TAG,age+"aaaaaaaaaa");
                intent.putExtra("money",money);
                LogUtil.e(TAG,money+"555555555");
                intent.putExtra("city",city);
                intent.putExtra("area",area);
            setResult(101, intent);
            onBackPressed();
        }
    }

    // 清除车类别
    private void clearList(int type) {
        switch (type) {
            case 1:// 清理 city country
                adapterList.clear();
                zuche_pinpai.setText("品牌");
                break;
            case 2:// 清理 country
                CarTypeList.clear();
                zuche_chexing.setText("车型");
                break;
            case 3:
                zuche_cheling.setText("车龄");
                break;
            default:
                break;
        }
    }

    /**
     * 获取品牌车型
     */
    private void getData(final String upid) {
        pd = ProgressDialogUtils.show(this, "加载数据...");
        pd.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("upid", upid);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.Car;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "品牌车型提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "品牌车型返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    org.json.JSONArray arry = jsonj.getJSONArray("data");

                    switch (clickType) {
                        case 1:
                            adapterList = JSON.parseArray(arry.toString(),
                                    CarModel.class);
                            break;
                        case 2:
                            CarTypeList = JSON.parseArray(arry.toString(),
                                    CarModel.class);
                            break;
                        default:
                            break;
                    }
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

    /**
     * 获取省市区
     */
    private void getData1(final String upid) {
        pd = ProgressDialogUtils.show(this, "加载数据...");
        pd.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("upid", upid);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.Service_province;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "省市区信息提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "省市区信息返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    org.json.JSONArray arry = jsonj.getJSONArray("data");

                    switch (clickType) {
                        case 1:
                            provinceList = JSON.parseArray(arry.toString(),
                                    AreaModel.class);
                            break;
                        case 2:
                            provinceList = JSON.parseArray(arry.toString(),
                                    AreaModel.class);
                            break;
                        case 3:
                            cityList = JSON.parseArray(arry.toString(),
                                    AreaModel.class);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    handler.sendEmptyMessage(3);
                } else if (TextUtils.equals(status, "0")) {
                    handler.sendEmptyMessage(-3);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    /**
     * 获取租金区间
     */
    private void getData2(){
        pd = ProgressDialogUtils.show(this, "加载数据...");
        pd.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("type", "1");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.rentdata;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "租金提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "租金返回的数据" + jsonj.toString());

                    status = jsonj.getString("msg");
                    info = jsonj.getString("info");
                    org.json.JSONArray arry = jsonj.getJSONArray("data");
                    zujinList = JSON.parseArray(arry.toString(), AreaModel.class);

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    handler.sendEmptyMessage(6);
                } else if (TextUtils.equals(status, "0")) {
                    handler.sendEmptyMessage(-6);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    /**
     * 省市区 pop
     *
     * @param type
     */
    private void initmPopupWindowView1(final int type) {

        View customView = getLayoutInflater().inflate(
                R.layout.item_province2_layout, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        // popupWindow.setAnimationStyle(R.style.AnimationFade);
        // popwindow获取焦点
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SignPopupWindow弹出窗体的背景
        // popupWindow.setBackgroundDrawable(dw);

        // 自定义view添加触摸事件
        customView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 单击内部空白区域，pop消失
                 */
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    return true;
                }
                /**
                 * 单击pop外部区域，pop消失
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }

                return false;
            }

        });
        switch (type) {
            case 2:// 省
                province_list = (ListView) customView.findViewById(R.id.list_province);
//                mAreaAdapter = new Area_onlyAdapter(this, provinceList);
//                province_list.setAdapter(mAreaAdapter);
//                province_list = (ListView) customView.findViewById(R.id.list_city);
                cityAdapter = new Area_onlyAdapter(this, provinceList);
                province_list.setAdapter(cityAdapter);
                break;
            case 3:// 市
                province_list = (ListView) customView.findViewById(R.id.list_city);
//                cityAdapter = new Area_onlyAdapter(this, cityList);
//                province_list.setAdapter(cityAdapter);
//                province_list = (ListView) customView.findViewById(R.id.list_country);
                countyAdapter = new Area_onlyAdapter(this, cityList);
                province_list.setAdapter(countyAdapter);
                break;
            /*case 3:// 区
                province_list = (ListView) customView
                        .findViewById(R.id.list_country);
                countyAdapter = new Area_onlyAdapter(this, countyList);
                province_list.setAdapter(countyAdapter);
                break;*/
            default:
                break;
        }
        province_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
        province_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (type) {
                    case 2://
                        city = provinceList.get(position).getName();
                        city_id = provinceList.get(position).getId();
                        zuche_shi.setText(city);
                        break;
                    case 3:
                        area = cityList.get(position).getName();
                        zuche_qu.setText(area);
                        break;
                    default:
                        break;
                }
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    /**
     * 品牌车型
     *
     * @param type
     */
    private void initmPopupWindowView(final int type) {

        View customView = getLayoutInflater().inflate(
                R.layout.item_cartype_layout, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        // popupWindow.setAnimationStyle(R.style.AnimationFade);
        // popwindow获取焦点
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SignPopupWindow弹出窗体的背景
        // popupWindow.setBackgroundDrawable(dw);

        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 单击内部空白区域，pop消失
                 */
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    return true;
                }
                /**
                 * 单击pop外部区域，pop消失
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }

                return false;
            }

        });
        switch (type) {
            case 1:// 品牌
                carListView = (ListView) customView.findViewById(R.id.list_pinpai);
                pinpaiAdapter = new CarTypeAdapter(FiltrateTwoActivity.this,
                        adapterList);
                carListView.setAdapter(pinpaiAdapter);
                break;
            case 2:// 车型
                carListView = (ListView) customView.findViewById(R.id.list_chexing);
                chexingAdapter = new CarTypeAdapter(FiltrateTwoActivity.this,
                        CarTypeList);
                carListView.setAdapter(chexingAdapter);
                break;
            case 3:
                carListView = (ListView) customView.findViewById(R.id.list_carage);
                carageAdapter = new CarageAdapter(FiltrateTwoActivity.this,
                        XZContranst.caragelist);
                carListView.setAdapter(carageAdapter);
                break;
            default:
                break;
        }

        carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (type) {
                    case 1:
                        pinpai = adapterList.get(position).getName();
                        pinpaiid = adapterList.get(position).getId();
                        brand = adapterList.get(position).getName();
                        zuche_pinpai.setText(brand);
                        break;
                    case 2://
                        chexing = CarTypeList.get(position).getName();
                        chexingid = CarTypeList.get(position).getId();
                        models = CarTypeList.get(position).getName();
                        zuche_chexing.setText(models);
                        break;
                    case 3://
                        zuche_cheling.setText(XZContranst.caragelist[position]);
                        age = XZContranst.caragelist[position];
                        break;
                    default:
                        break;
                }
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    private void initmPopupWindowView2(final int type) {

        View customView = getLayoutInflater().inflate(
                R.layout.item_province2_layout, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        // popupWindow.setAnimationStyle(R.style.AnimationFade);
        // popwindow获取焦点
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SignPopupWindow弹出窗体的背景
        // popupWindow.setBackgroundDrawable(dw);

        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 单击内部空白区域，pop消失
                 */
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    return true;
                }
                /**
                 * 单击pop外部区域，pop消失
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }

                return false;
            }

        });
        province_list = (ListView) customView.findViewById(R.id.list_province);
        mAreaAdapter1 = new Area_onlyAdapter1(this, zujinList);
        province_list.setAdapter(mAreaAdapter1);
        province_list.setSelector(new ColorDrawable(Color.TRANSPARENT));


        province_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                money = zujinList.get(position).getValue();
                zuche_zujin.setText(money);
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    public class Area_onlyAdapter1 extends BaseAdapter implements OnClickListener {

        private LayoutInflater inflater;
        Context context;
        int mPosition;

        private List<AreaModel> list = new ArrayList<AreaModel>();

        public Area_onlyAdapter1(Context context, List<AreaModel> list) {
            super();
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_area_only_layout, null, false);

                holder.title = (TextView) convertView
                        .findViewById(R.id.area_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(list.get(position).getValue());


            return convertView;
        }

        final class ViewHolder {
            TextView title;
        }

        @Override
        public void onClick(View v) {

        }
    }
}
