package com.power.travel.xixuntravel.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.power.travel.xixuntravel.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2017/9/9.
 */

public class ChatFragment extends Fragment implements View.OnClickListener {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter; // 将tab页面持久在内存中
    private Fragment mConversationList;
    private Fragment mConversationFragment = null;
    private List<Fragment> mFragment = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.chat_pager);

        initView();
        return view;
    }

    private void initView() {

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
        /**
         *
         * 进入融云
         *
         */
        //TODO 进入会话列表
        /*HashMap<String, Boolean> hashMap = new HashMap<>();
        //会话类型 以及是否聚合显示
        hashMap.put(Conversation.ConversationType.PRIVATE.getName(),false);
        hashMap.put(Conversation.ConversationType.PUSH_SERVICE.getName(),true);
        hashMap.put(Conversation.ConversationType.SYSTEM.getName(),true);
        RongIM.getInstance().startConversationList(getActivity(),hashMap);*/
    }

    private Fragment initConversationList() {
        // appendQueryParameter 对具体的会话列表做展示
        if (mConversationFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationList")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")     // 设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
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
}
