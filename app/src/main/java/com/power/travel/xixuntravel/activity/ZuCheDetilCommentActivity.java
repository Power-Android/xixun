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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.TripDetailCommentAdapter;
import com.power.travel.xixuntravel.adapter.ZuCheCommentAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.impl.AllTripDetailComment_replayOnItemOnClickListener;
import com.power.travel.xixuntravel.model.TripDetailCommentModel;
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

public class ZuCheDetilCommentActivity extends BaseActivity  implements
        PullToRefreshBase.OnRefreshListener2<ListView>,AllTripDetailComment_replayOnItemOnClickListener {

    private ImageView back;
    private TextView title,comment;
    private EditText comment_edit;
    private RelativeLayout comment_layout;
    private String data, TAG = "KnowledgeActivity", info, comentID="0",tid;
    private ProgressDialogUtils pd;
    SharedPreferences sp;
    private PullToRefreshListView mListView;
    List<TripDetailCommentModel> adapterList = new ArrayList<TripDetailCommentModel>();
    ZuCheCommentAdapter adapter;
    int page = 1;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {// 成功
                if (page == 1) {
                    adapter = new ZuCheCommentAdapter(ZuCheDetilCommentActivity.this, adapterList);
                    adapter.setOnItemClick(ZuCheDetilCommentActivity.this);
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
            if (pd != null && ZuCheDetilCommentActivity.this != null) {
                pd.dismiss();
            }
            if (ZuCheDetilCommentActivity.this != null && mListView != null) {
                mListView.onRefreshComplete();
            }
        }
    };

    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 3) {// 评论成功
                ToastUtil.showToast(getApplicationContext(), info);
                if (pd != null && ZuCheDetilCommentActivity.this != null) {
                    pd.dismiss();
                }
                if(adapterList!=null){
                    if(adapterList.size()>0){
                        adapterList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
                comment_edit.getText().clear();
                page=1;
                getData(true);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zu_che_detil_comment);

        MyApplication.getInstance().addActivity(this);

        initView();
        initListener();
        initgetIntent();
        if(isConnect()){
            getData(true);
        }else{
            ToastUtil.showToast(getApplicationContext(), XZContranst.no_net);
        }
    }

    private void initView() {
        sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        pd = ProgressDialogUtils.show(this, "加载数据...");
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        title.setText("评论");
        mListView = (PullToRefreshListView) findViewById(R.id.knowledge_listview);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        // mListView.setMode(Mode.DISABLED);
        mListView.setOnRefreshListener(this);
        comment_layout=(RelativeLayout)findViewById(R.id.tripldetail_comment_layout);
        comment= (TextView) findViewById(R.id.tripldetail_comment);
        comment_edit=(EditText)findViewById(R.id.tripldetail_comment_edit);
    }

    private void initListener() {
        back.setOnClickListener(this);
        comment.setOnClickListener(this);
    }

    private void initgetIntent() {
        Intent intent=getIntent();
        tid=intent.getStringExtra("tid");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == back) {
            finish();
        }else if(v==comment){//评论
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
        }
    }

    @Override
    public void OnItemClick(int typeint, int position) {
        switch (typeint) {
            case 1:
                comentID=adapterList.get(position).getId();
                comment_edit.setHint("回复"+ adapterList.get(position).getNickname());
                break;

            default:
                break;
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
                    data.put("tid", tid);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.carRentalComment;
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
                    if(TextUtils.equals(status, "1")){
                        JSONArray arry = jsonj.getJSONArray("data");
                        adapterList = JSON.parseArray(arry.toString(),
                                TripDetailCommentModel.class);
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
                    data.put("mid", sp.getString(XZContranst.id, null));
                    data.put("tid", tid);
                    data.put("content", comment_edit.getText().toString());
                    data.put("comment_id", comentID);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.carRentalCommentAdd;
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
