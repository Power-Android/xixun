package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.AreaAdapter;
import com.power.travel.xixuntravel.adapter.AreaTwoAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.fragment.YueBanFragment;
import com.power.travel.xixuntravel.model.AreaModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AreaActivityTwo extends BaseActivity implements AdapterView.OnItemClickListener {

    private ImageView back;
    private TextView title;
    private ListView mListview;
    private List<AreaModel> adapterList = new ArrayList<AreaModel>();
    AreaTwoAdapter adapter;
    private String info, TAG = AreaActivity.class.getSimpleName(),province, province_id, city, city_id, country,
            country_id;
    SharedPreferences sp;
    private ProgressDialogUtils pd;
    int clickType = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {// 成功
                adapter = new AreaTwoAdapter(AreaActivityTwo.this, adapterList);
                mListview.setAdapter(adapter);
            } else if (msg.what == 0) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == -1) {//
                ToastUtil.showToast(getApplicationContext(), info);

            }
            if (pd != null && AreaActivityTwo.this != null) {
                pd.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_two);
        MyApplication.getInstance().addActivity(this);
        initView();

        if (isConnect()) {
            getData("1");
        } else {
            ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
        }
    }

    private void initView() {
        sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        pd = ProgressDialogUtils.show(this, "加载数据...");
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("省");
        mListview = (ListView) findViewById(R.id.area_listview);
        mListview.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == back) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        switch (clickType) {
            case 1:// 省
                province = adapterList.get(position).getName();
                province_id = adapterList.get(position).getId();
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(XZContranst.adress, province_id);
                edit.commit();
                Intent intent=new Intent();
                intent.putExtra("province", province);
                intent.putExtra("province_id",province_id);
                setResult(102, intent);
                finish();
                break;
        }
    }

    private void getData(final String upid) {
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
                adapterList.clear();
                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "省市区信息返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
                    org.json.JSONArray arry = jsonj.getJSONArray("data");
                    adapterList = JSON.parseArray(arry.toString(),
                            AreaModel.class);

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
