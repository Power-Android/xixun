package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

public class ZuFangListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>{

    private ImageView back;
    private LinearLayout search;
    private TextView filtrate_tv;
    private PullToRefreshListView mListView;
    private ProgressDialogUtils pd;
    SharedPreferences sp,spLocation;
    List<MasterModel> adapterList = new ArrayList<MasterModel>();
    List<MasterModel> adapterListMore = new ArrayList<MasterModel>();
    int page = 1;
    private String data, TAG = "ZuFangListActivity", info;
    MasterAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {// 成功
                adapterList.addAll(adapterListMore);
                if (page == 1) {
                    adapter = new MasterAdapter(ZuFangListActivity.this, adapterList);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                page=page+1;
            } else if (msg.what == 0) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == -1) {//
//				ToastUtil.showToast(getApplicationContext(), info);

            }
            if (pd != null && ZuFangListActivity.this != null) {
                pd.dismiss();
            }
            if (ZuFangListActivity.this != null && mListView != null) {
                mListView.onRefreshComplete();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zu_fang_list);
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
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);
        filtrate_tv = findViewById(R.id.filtrate_tv);
        mListView = findViewById(R.id.yueche_listview);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);// 刷新加载更多都有
        pd = ProgressDialogUtils.show(this, "加载数据...");
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        filtrate_tv.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MasterModel masterModel=adapterList.get(position-1);
                Intent intent=new Intent(ZuFangListActivity.this,ZuFangDetailActivity.class);
                intent.putExtra("model", masterModel);
                startActivity(intent);
            }
        });
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
                    data.put("page", page);
                    data.put("x", spLocation.getString(XZContranst.coordinate_x, ""));
                    data.put("y", spLocation.getString(XZContranst.coordinate_y, ""));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.rentalList;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "租房列表提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "租房列表返回的数据" + jsonj.toString());

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

    public class MasterAdapter extends BaseAdapter implements View.OnClickListener {

        private LayoutInflater inflater;
        Context context;
        int mPosition;
        DisplayImageOptions options;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();// ???��???????
        ImageLoader imageLoader = ImageLoader.getInstance();
        List<MasterModel> list=new ArrayList<MasterModel>();
        SharedPreferences sp;
        ViewHolder holder = null;

        private String info;


        public MasterAdapter(Context context, List<MasterModel> list) {
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

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public void setUpdate(int mPosition){
            this.mPosition=mPosition;
            super.notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_yuechelist_layout, null, false);

                holder.nickname = (TextView) convertView
                        .findViewById(R.id.item_master_nickname);
                holder.title = (TextView) convertView
                        .findViewById(R.id.item_master_title);
                holder.distance = (TextView) convertView
                        .findViewById(R.id.item_master_distance);
                holder.face = (ImageView) convertView
                        .findViewById(R.id.item_master_face);
                holder.item_master_price = convertView.findViewById(R.id.item_master_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            imageLoader.displayImage(list.get(position).getFace(),
                    holder.face, options,animateFirstListener);
            holder.nickname.setText(list.get(position).getTitle());
            holder.title.setText(list.get(position).getLayoutRoom()+list.get(position).getLayoutOffice()+" | "+
                    list.get(position).getMeasure()+"㎡");
            holder.distance.setText(list.get(position).getPosition());
            holder.item_master_price.setText(list.get(position).getMoney()+"元/天");

            return convertView;
        }

        final class ViewHolder {
            TextView nickname,title,distance,item_master_price;
            ImageView face;
        }

        @Override
        public void onClick(View v) {
            int posi = (Integer) v.getTag();
        }
    }
}
