package com.power.travel.xixuntravel.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.AreaActivityTwo;
import com.power.travel.xixuntravel.activity.LoginActivity;
import com.power.travel.xixuntravel.activity.MasterListActivity;
import com.power.travel.xixuntravel.activity.SearchActivity;
import com.power.travel.xixuntravel.activity.YueBanActivity;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.ClickImageView;


/**
 * Created by Administrator on 2017/8/24.
 */

public class YueBanFragment extends Fragment implements View.OnClickListener {

    private LinearLayout addaddress_title;
    private TextView locaton_tv;
    SharedPreferences sp,spLocation;
    private String mycenter = "mycenter";
    private ClickImageView iv_to_youji, iv_to_yueban, iv_to_daren;
    private String province_id = "", province;
    private LinearLayout title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_yueban_three, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        sp = getActivity().getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        spLocation= getActivity().getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
                Context.MODE_PRIVATE);

        addaddress_title=(LinearLayout)view.findViewById(R.id.title);
        title = view.findViewById(R.id.title);
        title.setOnClickListener(this);
        addaddress_title.setOnClickListener(this);
        locaton_tv = (TextView)view.findViewById(R.id.locaton_tv);
        locaton_tv.setOnClickListener(this);
//        locaton_tv.setText(spLocation.getString(XZContranst.location_city, ""));
        locaton_tv.setText("全部");

        iv_to_youji = (ClickImageView) view.findViewById(R.id.iv_to_youji);
        iv_to_yueban = (ClickImageView) view.findViewById(R.id.iv_to_yueban);
        iv_to_daren = (ClickImageView) view.findViewById(R.id.iv_to_daren);

        iv_to_youji.setOnClickListener(new ClickImageView.OnClickListener() {
            @Override
            public void onClick() {
                    Intent intent = new Intent(getActivity(), YueBanActivity.class);
                    intent.putExtra("typename", "yueban");
                    intent.putExtra("listtype", 2);
                    startActivity(intent);
                }
        });
        iv_to_yueban.setOnClickListener(new ClickImageView.OnClickListener() {
            @Override
            public void onClick() {
                    Intent intent = new Intent(getActivity(), YueBanActivity.class);
                    intent.putExtra("typename", "yueban");
                    intent.putExtra("listtype", 1);
                    intent.putExtra("province_id",province_id);
                    startActivity(intent);
            }
        });
        iv_to_daren.setOnClickListener(new ClickImageView.OnClickListener() {
            @Override
            public void onClick() {
                    startActivity(new Intent(getActivity(), MasterListActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.locaton_tv:
                Intent intent1 = new Intent(getActivity(),AreaActivityTwo.class);
                startActivityForResult(intent1,1);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(data!=null) {
                    province_id = data.getStringExtra("province_id");
                    province = data.getStringExtra("province");
                    locaton_tv.setText(province);
                }

        }
    }
}
