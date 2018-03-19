package com.power.travel.xixuntravel.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.AreaActivityTwo;
import com.power.travel.xixuntravel.activity.LoginActivity;
import com.power.travel.xixuntravel.activity.MasterListActivity;
import com.power.travel.xixuntravel.activity.SearchActivity;
import com.power.travel.xixuntravel.activity.YueBanActivity;
import com.power.travel.xixuntravel.adapter.RecyclerViewPagerAdapter;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.ClickImageView;
import com.power.travel.xixuntravel.weight.RecyclerViewPager;


/**
 * Created by Administrator on 2017/8/24.
 */

public class YueBanFragment extends Fragment implements View.OnClickListener {

    private LinearLayout addaddress_title;
    private TextView locaton_tv;
    SharedPreferences sp,spLocation;
    private String mycenter = "mycenter";
//    private ImageView iv_to_youji, iv_to_yueban, iv_to_daren;
    private String province_id = "", province;
    private LinearLayout title;
    protected RecyclerViewPager mRecyclerView;

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
        title = (LinearLayout)view. findViewById(R.id.title);
        title.setOnClickListener(this);
        addaddress_title.setOnClickListener(this);
        locaton_tv = (TextView)view.findViewById(R.id.locaton_tv);
        locaton_tv.setOnClickListener(this);
//        locaton_tv.setText(spLocation.getString(XZContranst.location_city, ""));
        locaton_tv.setText("全部");

        /*iv_to_youji = view.findViewById(R.id.iv_to_youji);
        iv_to_yueban =  view.findViewById(R.id.iv_to_yueban);
        iv_to_daren =  view.findViewById(R.id.iv_to_daren);

        iv_to_youji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), YueBanActivity.class);
                    intent.putExtra("typename", "yueban");
                    intent.putExtra("listtype", 2);
                    startActivity(intent);
                }
        });
        iv_to_yueban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), YueBanActivity.class);
                    intent.putExtra("typename", "yueban");
                    intent.putExtra("listtype", 1);
                    intent.putExtra("province_id",province_id);
                    startActivity(intent);
            }
        });
        iv_to_daren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(getActivity(), MasterListActivity.class));
            }
        });*/
        mRecyclerView = (RecyclerViewPager) view.findViewById(R.id.viewpager);
        setupView();
    }

    private void setupView(){
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setTriggerOffset(0.3f);
        mRecyclerView.setFlingFactor(0.3f);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(new RecyclerViewPagerAdapter.LayoutAdapter(getContext(), 4, province_id));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        registerListener();
    }

    private void registerListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int childCount = mRecyclerView.getChildCount();
                int width = mRecyclerView.getChildAt(0).getWidth();
                int padding = (mRecyclerView.getWidth() - width) / 2;

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    float rate = 0;
                    if (v.getTop() <= padding) {
                        if (v.getTop() >= padding - v.getHeight()) {
                            rate = (padding - v.getTop()) * 1f / v.getHeight();
                        } else {
                            rate = 1;
                        }
//                        v.setScaleX(1 - rate * 0.1f);
//                        v.setScaleY(1 - rate * 0.1f);
                    } else {
                        if (v.getTop() <= recyclerView.getHeight() - padding) {
                            rate = (recyclerView.getHeight() - padding - v.getTop()) * 1f / v.getHeight();
                        }
//                        v.setScaleX(0.9f + rate * 0.1f);
//                        v.setScaleY(0.9f + rate * 0.1f);
                    }
                }
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mRecyclerView.getChildCount() < 3) {
                    if (mRecyclerView.getChildAt(1) != null) {
                        View v1 = mRecyclerView.getChildAt(1);
                        v1.setScaleY(0.9f);
                    }
                } else {
                    if (mRecyclerView.getChildAt(0) != null) {
                        View v0 = mRecyclerView.getChildAt(0);
                        v0.setScaleY(0.9f);
                    }
                    if (mRecyclerView.getChildAt(2) != null) {
                        View v2 = mRecyclerView.getChildAt(2);
                        v2.setScaleY(0.9f);
                    }
                }
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
