package com.power.travel.xixuntravel.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.Friend;
import com.power.travel.xixuntravel.model.MasterModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/9/9.
 */

public class ChatFragment extends Fragment implements View.OnClickListener,RongIM.UserInfoProvider{

    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter; // 将tab页面持久在内存中
    private Fragment mConversationList;
    private Fragment mConversationFragment = null;
    private List<Fragment> mFragment = new ArrayList<>();
    private String info;
    MasterModel mMasterModel;
    private List<Friend> userIdList;
    private UserInfo userInfo;
    private SharedPreferences sp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.chat_pager);
        sp = getActivity().getSharedPreferences("config", MODE_PRIVATE);

        initView();
        return view;
    }

    private void initView() {
        RongIM.setUserInfoProvider(this,true);

        mConversationList = initConversationList(); // 获取融云会话列表的对象

        mFragment.add(mConversationList);           // 加入会话列表
        // 配置ViewPager的适配器
        mFragmentPagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
    }

    private Fragment initConversationList() {
        // appendQueryParameter 对具体的会话列表做展示
        if (mConversationFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationList")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")     // 设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    //.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")    // 公共服务号
                    //.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")// 公共服务号
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")  // 设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")      // 设置私聊会是否聚合显示
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationFragment;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConversationFragment = null;
    }

    @Override
    public UserInfo getUserInfo(String s) {
        LogUtil.e("会话列表：",s);
        getData(s);
        /*if (userInfo != null){
            for (Friend i : userIdList) {
                if (!i.getUserId().equals(sp.getString(XZContranst.id, null))) {
                    Log.e("TAG", i.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(i.getUserId(),i.getUserName(),Uri.parse(i.getPortraitUri())));
                    return userInfo;
                }
            }
        }*/
        return null;
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
                String nickname = mMasterModel.getNickname();
                String face = mMasterModel.getFace();
                userIdList = new ArrayList<>();
                userIdList.add(new Friend(mMasterModel.getId(), nickname, face));
                userInfo = new UserInfo(mMasterModel.getId(), nickname, Uri.parse(face));
                LogUtil.e("会话列表",face);
                RongIM.getInstance().refreshUserInfoCache(userInfo);
            }else if (msg.what ==-2) {//获取用户信息失败

            }
        }
    };
}
