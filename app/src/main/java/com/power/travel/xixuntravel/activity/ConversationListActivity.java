package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseRongCloudActivity;
import com.power.travel.xixuntravel.fragment.ChatFragment;
import com.power.travel.xixuntravel.model.Friend;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.weight.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;


public class ConversationListActivity extends BaseRongCloudActivity implements View.OnClickListener{
    private SharedPreferences sp;
    private static final String TAG = ConversationListActivity.class.getSimpleName();
    private String mTargetId;//对方ID
    private String info;
    MasterModel mMasterModel;
    private List<Friend> userIdList;
    private String nickname;
    private String face;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        tv_title.setText("聊天列表");
//        RongIM.setUserInfoProvider(this,true);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLeftClick(View v) {
        super.onLeftClick(v);
    }

   /* @Override
    public UserInfo getUserInfo(String s) {
        LogUtil.e("会话列表：",s);
        mTargetId = s;
        getData(s);
        if (userInfo != null){
            return new UserInfo(s,nickname,Uri.parse(face));
        }
        return  null;
    }

    private void getData(final String s) {
//		pd.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                JSONObject data = new JSONObject();
                try {
                    data.put("mid",s);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.index;
                String json = StringUtils.setJSON(data);

                LogUtil.e("会话列表", "获取信息提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e("会话列表", "获取信息返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");

                    JSONObject djson = jsonj.getJSONObject("data");
                    mMasterModel= JSON.parseObject(djson.toString(), MasterModel.class);

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("会话列表", "解析错误" + e.toString());
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

    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what ==2) {//获取用户信息正确
                nickname = mMasterModel.getNickname();
                face = mMasterModel.getFace();
                userIdList = new ArrayList<>();
                userIdList.add(new Friend(mTargetId, nickname, face));
                userInfo = new UserInfo(mMasterModel.getId(), mMasterModel.getNickname(), Uri.parse(mMasterModel.getFace()));
            }else if (msg.what ==-2) {//获取用户信息失败
                ToastUtil
                        .showToast(getApplicationContext(), info);
            }
        }
    };*/
}
