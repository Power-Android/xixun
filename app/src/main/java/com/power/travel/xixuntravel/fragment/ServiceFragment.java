package com.power.travel.xixuntravel.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.ContactUsActivity;
import com.power.travel.xixuntravel.activity.DriverListActivity;
import com.power.travel.xixuntravel.activity.GuideListActivity;
import com.power.travel.xixuntravel.activity.HospitalActivity;
import com.power.travel.xixuntravel.activity.KnowledgeActivity;
import com.power.travel.xixuntravel.activity.MasterListActivity;
import com.power.travel.xixuntravel.activity.PoliceActivity;
import com.power.travel.xixuntravel.activity.RentCarActivity;
import com.power.travel.xixuntravel.activity.RescueActivity;
import com.power.travel.xixuntravel.activity.RouteDetailActivity;
import com.power.travel.xixuntravel.activity.ViewspotListActivity;
import com.power.travel.xixuntravel.adapter.MenuGridViewAdapter;
import com.power.travel.xixuntravel.adapter.RouteListAdapter;
import com.power.travel.xixuntravel.model.RouteModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.weight.MyGridView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceFragment extends Fragment implements OnClickListener,
        OnItemClickListener, OnRefreshListener2<ListView> {

	private ImageView back;
	private TextView title;
	MenuGridViewAdapter adapter;
	private MyGridView gridview;
	private PullToRefreshListView mListView;
	List<RouteModel> adapterList = new ArrayList<RouteModel>();
	List<RouteModel> adapterListMore = new ArrayList<RouteModel>();
	RouteListAdapter mAdapter;

	private String data, TAG = "ServiceFragment", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	int page = 1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					mAdapter = new RouteListAdapter(getActivity(), adapterList);
					mListView.setAdapter(mAdapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page = page + 1;
			} else if (msg.what == 0) {// 失败
				ToastUtil
						.showToast(getActivity().getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil
						.showToast(getActivity().getApplicationContext(), info);
			}
			if (pd != null && ServiceFragment.this != null) {
				pd.dismiss();
			}
			if (ServiceFragment.this != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_service, container,
				false);

		init(view);
		initListener();
		if (isConnect()) {
			getData(true);
		} else {
			ToastUtil.showToast(getActivity().getApplicationContext(),
					XZContranst.no_net);
		}
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent= new Intent(getActivity(),RouteDetailActivity.class);
				intent.putExtra("id", adapterList.get(position-1).getId());
				startActivity(intent);
			}
		});

		return view;
	}

	private void init(View view) {
		sp = getActivity().getSharedPreferences(
				XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(getActivity(), "获取数据...");
		back = (ImageView) view.findViewById(R.id.back);
		back.setVisibility(View.GONE);
		title = (TextView) view.findViewById(R.id.title);
		title.setText("服务");
		gridview = (MyGridView) view.findViewById(R.id.gridview);
		adapter = new MenuGridViewAdapter(getActivity());
		gridview.setAdapter(adapter);
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));// 取消GridView自带item单机效果
		mListView = (PullToRefreshListView) view
				.findViewById(R.id.service_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
	}

	private void initListener() {
		gridview.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
		switch (position) {
		case 0:// 导游
			startActivity(new Intent(getActivity(), GuideListActivity.class));
			break;
		case 1:// 司机
			startActivity(new Intent(getActivity(), DriverListActivity.class));
			break;
		case 2:// 常识
			startActivity(new Intent(getActivity(), KnowledgeActivity.class));
			break;
		case 3:// 救援
			startActivity(new Intent(getActivity(), RescueActivity.class));
			break;
		case 4:// 租车
			startActivity(new Intent(getActivity(), RentCarActivity.class));
			break;
		case 5:// 景区
			startActivity(new Intent(getActivity(), ViewspotListActivity.class));
			break;
		case 6:// 医院
			startActivity(new Intent(getActivity(), HospitalActivity.class));
			break;
		case 7:// 派出所
			startActivity(new Intent(getActivity(), PoliceActivity.class));
			break;
		case 8:// 地主达人
			startActivity(new Intent(getActivity(), MasterListActivity.class));
			break;
		case 9:// 联系我们
			startActivity(new Intent(getActivity(), ContactUsActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			if(!adapterList.isEmpty()){
				adapterList.clear();
				adapter.notifyDataSetChanged();
			}
			page = 1;
			getData(false);
		} else {
			ToastUtil.showToast(getActivity().getApplicationContext(),
					XZContranst.no_net);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isConnect()) {
			getData(false);
		} else {
			ToastUtil.showToast(getActivity().getApplicationContext(),
					XZContranst.no_net);
		}
	}

	/**
	 * isConnect 判断网络连接
	 * 
	 * @return
	 */
	public boolean isConnect() {
		ConnectivityManager conManager = (ConnectivityManager)
		getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错
			return networkInfo.isAvailable();
		}
		return false;
	}// isConnect
	
	private void getData(boolean ifshow) {
		if (ifshow) {
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("catid", "17");
					data.put("page", page);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.Route;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "推荐路线提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "推荐路线返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if(TextUtils.equals(status, "1")){
						JSONArray arry = jsonj.getJSONArray("data");
						adapterListMore = JSON.parseArray(arry.toString(),
								RouteModel.class);
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

}
