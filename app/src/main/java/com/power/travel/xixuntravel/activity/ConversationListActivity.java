package com.power.travel.xixuntravel.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseRongCloudActivity;
import com.power.travel.xixuntravel.model.Friend;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class ConversationListActivity extends BaseRongCloudActivity implements View.OnClickListener{

    private SharedPreferences sp;
    private static final String TAG = ConversationListActivity.class.getSimpleName();
    private String mTargetId;//对方ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        tv_title.setText("聊天列表");

        //TODO 获取对方name和头像

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLeftClick(View v) {
        super.onLeftClick(v);
    }

}
