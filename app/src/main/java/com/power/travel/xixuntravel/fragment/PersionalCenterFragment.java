package com.power.travel.xixuntravel.fragment;


import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.Apply_driverActivity;
import com.power.travel.xixuntravel.activity.Apply_guideActivity;
import com.power.travel.xixuntravel.activity.LoginActivity;
import com.power.travel.xixuntravel.activity.MessageActivity;
import com.power.travel.xixuntravel.activity.MyFollowActivity;
import com.power.travel.xixuntravel.activity.MyTravelActivity;
import com.power.travel.xixuntravel.activity.MyTripListActivity;
import com.power.travel.xixuntravel.activity.SettingActivity;
import com.power.travel.xixuntravel.activity.UserInfoActivity;
import com.power.travel.xixuntravel.model.FollowModel;
import com.power.travel.xixuntravel.model.MessageModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.DialogUtils;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.views.AnimateFirstDisplayListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;


public class PersionalCenterFragment extends Fragment implements OnClickListener {

	private ImageView back,title_iv,my_sex;
	private TextView title,my_name,my_id,my_apply_guide_type,my_apply_driver_type,my_message,
	my_attention;
	TextView title_iv_num;
	private RelativeLayout my_setting,info_layout,my_apply_guide,my_apply_driver,my_travel,my_trip;
	private ImageView my_head;
	private String data, TAG = "PersionalCenterFragment", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;

	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	List<MessageModel> adapterList = new ArrayList<MessageModel>();
	List<FollowModel> adapterList_follow = new ArrayList<FollowModel>();

	int page = 1;
	private int weiducount;
	private String followcount;
	private int msg_count;


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {
//				ToastUtil.showToast(getActivity().getApplicationContext(), info);
				int msg_totall = weiducount+msg_count;
				my_message.setText("消息"+ msg_totall);
				my_attention.setText("关注"+ followcount);
			} else if (msg.what == 0) {
				ToastUtil.showToast(getActivity().getApplicationContext(), info);
			} else if (msg.what == -1) {
				ToastUtil.showToast(getActivity().getApplicationContext(), info);
			} else if (msg.what == 2) {
				setData();
			}
			pd.dismiss();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view=inflater.inflate(R.layout.fragment_persionalcenter, container, false);

		initView(view);
		initListener();
		getData(true);
		initUnreadCountListener();

		return view;
	}

	private void initUnreadCountListener() {

		final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION,
				Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
				Conversation.ConversationType.PUBLIC_SERVICE};

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
			}
		}, 500);
	}

	public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
		@Override
		public void onMessageIncreased(int count) {
			msg_count = count;
			getUserData();
		}
	};


	@Override
	public void onResume() {
		super.onResume();
		getUserData();
		handler.sendEmptyMessage(2);
	}
	private void initView(View view) {
		sp=getActivity().getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(getActivity(), "获取数据...");
		back = (ImageView)view.findViewById(R.id.back);
		back.setVisibility(View.GONE);
		title_iv= (ImageView)view.findViewById(R.id.title_iv);
		title_iv.setVisibility(View.VISIBLE);
		my_sex= (ImageView)view.findViewById(R.id.my_sex);
		title = (TextView)view.findViewById(R.id.title);
		title.setText("我的");
		my_setting=(RelativeLayout)view.findViewById(R.id.my_setting);
		info_layout=(RelativeLayout)view.findViewById(R.id.info_layout);
		my_apply_guide=(RelativeLayout)view.findViewById(R.id.my_apply_guide);
		my_apply_driver=(RelativeLayout)view.findViewById(R.id.my_apply_driver);
		my_travel=(RelativeLayout)view.findViewById(R.id.my_travel);
		my_trip=(RelativeLayout)view.findViewById(R.id.my_trip);
		my_head=(ImageView)view.findViewById(R.id.my_head);
		my_name= (TextView)view.findViewById(R.id.my_name);
		my_id= (TextView)view.findViewById(R.id.my_id);
		my_apply_guide_type= (TextView)view.findViewById(R.id.my_apply_guide_type);
		my_apply_driver_type= (TextView)view.findViewById(R.id.my_apply_driver_type);
		my_message= (TextView)view.findViewById(R.id.my_message);
		my_attention= (TextView)view.findViewById(R.id.my_attention);
		title_iv_num= (TextView)view.findViewById(R.id.title_iv_num);
		options = new DisplayImageOptions.Builder()
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		 .displayer(new RoundedBitmapDisplayer(100))
		.build();
	}
	private void initListener() {
		my_setting.setOnClickListener(this);
		info_layout.setOnClickListener(this);
		my_apply_guide.setOnClickListener(this);
		my_apply_driver.setOnClickListener(this);
		my_travel.setOnClickListener(this);
		my_message.setOnClickListener(this);
		my_trip.setOnClickListener(this);
		my_attention.setOnClickListener(this);
		title_iv.setOnClickListener(this);
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
					data.put("mid", sp.getString(XZContranst.id, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.message;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "消息提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "消息返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if(TextUtils.equals(status, "1")){
						JSONArray arry = jsonj.getJSONArray("data");
						adapterList = JSON.parseArray(arry.toString(),
								MessageModel.class);
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

		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.my_follow;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "关注列表提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "关注列表返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						adapterList_follow = JSON.parseArray(arry.toString(),FollowModel.class);
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

	private void getUserData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				String url = HttpUrl.index;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "用户基本信息提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "用户基本信息返回的数据" + jsonj.toString());
					status = jsonj.getString("status");
					weiducount = jsonj.getInt("weiducount");
					followcount = jsonj.getString("followcount");

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

	private void setData(){
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		
		imageLoader.displayImage(sp.getString(XZContranst.face, ""), my_head,
				options, animateFirstListener);

		my_name.setText(sp.getString(XZContranst.nickname, null));
		if(!TextUtils.isEmpty(sp.getString(XZContranst.username, null))){
			my_id.setText("ID:"+sp.getString(XZContranst.username, null));
		}

		if(TextUtils.equals(sp.getString(XZContranst.sex, null), "1")){
			my_sex.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.my_boy));
		}else if(TextUtils.equals(sp.getString(XZContranst.sex, null), "2")){
			my_sex.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.my_girle));
		}

		if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "2")){//审核中
			my_apply_guide_type.setVisibility(View.VISIBLE);
		}else if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "0")){//没有审核
			my_apply_guide_type.setVisibility(View.GONE);
		}else if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "1")){//是导游
			my_apply_guide_type.setVisibility(View.VISIBLE);
            my_apply_guide_type.setText("已是导游");
		}else if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "3")){//
            my_apply_guide_type.setVisibility(View.VISIBLE);
            my_apply_guide_type.setText("审核被驳回");
        }

		if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "2")){//审核中
			my_apply_driver_type.setVisibility(View.VISIBLE);
		}else if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "0")){//没有审核
			my_apply_driver_type.setVisibility(View.GONE);
		}else if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "1")){//是导游
			my_apply_driver_type.setVisibility(View.VISIBLE);
            my_apply_driver_type.setText("已是司机");
		}else if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "3")){
            my_apply_driver_type.setVisibility(View.VISIBLE);
            my_apply_driver_type.setText("审核被驳回");
        }

	}

	@Override
	public void onClick(View v) {
		if(v==my_setting){//设置
			startActivity(new Intent(getActivity(),SettingActivity.class));
		}else if(v==info_layout){//用户信息
			jump(UserInfoActivity.class);
		}else if(v==my_apply_guide){//成为导游
			if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "2")){
				ToastUtil.showToast(getActivity().getApplicationContext(), "资料审核中！");
			}else if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "0")){//没有审核
				jump(Apply_guideActivity.class);
			}else if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "1")){//是导游
				ToastUtil.showToast(getActivity().getApplicationContext(), "成为导游！");
			}else if(TextUtils.equals(sp.getString(XZContranst.if_guide, ""), "3")){
                jump(Apply_guideActivity.class);
            }
		}else if(v==my_apply_driver){//成为司机
			if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "2")){
				ToastUtil.showToast(getActivity().getApplicationContext(), "资料审核中！");
			}else if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "0")){//没有审核
				jump(Apply_driverActivity.class);
			}else if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "1")){//是导游
				ToastUtil.showToast(getActivity().getApplicationContext(), "成为司机！");
			}else if(TextUtils.equals(sp.getString(XZContranst.if_driver, ""), "2")){
                jump(Apply_driverActivity.class);
            }
		}else if(v==my_travel){//我的游记
			startActivity(new Intent(getActivity(),MyTravelActivity.class));
		}else if(v==my_message){//消息
			startActivity(new Intent(getActivity(),MessageActivity.class));
		}else if(v==my_trip){//我的行程
			startActivity(new Intent(getActivity(),MyTripListActivity.class));
		}else if(v==my_attention){//关注
			startActivity(new Intent(getActivity(),MyFollowActivity.class));
		}else if(v==title_iv){//会话列表
			/**
			 *
			 * 进入融云
			 *
			 */
			//TODO 进入会话列表
			HashMap<String, Boolean> hashMap = new HashMap<>();
			//会话类型 以及是否聚合显示
			hashMap.put(Conversation.ConversationType.PRIVATE.getName(),false);
			hashMap.put(Conversation.ConversationType.PUSH_SERVICE.getName(),true);
			hashMap.put(Conversation.ConversationType.SYSTEM.getName(),true);
			RongIM.getInstance().startConversationList(getActivity(),hashMap);

		}
	}



	private void jump(Class<?> class1){
		if(sp.getBoolean(XZContranst.if_login, false)){
			startActivity(new Intent(getActivity(),class1));
		}else{
			startActivity(new Intent(getActivity(),LoginActivity.class));
		}
	}

	protected void showDialog(String tipMessage){
		DialogUtils.show(getActivity(), tipMessage, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
	}

}
