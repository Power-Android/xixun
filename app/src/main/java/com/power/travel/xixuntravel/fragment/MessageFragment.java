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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.TravelDetailActivity;
import com.power.travel.xixuntravel.activity.TripDetailActivity;
import com.power.travel.xixuntravel.adapter.MessageAdapter;
import com.power.travel.xixuntravel.model.MessageModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.DialogUtils;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.ListView;

/**
 * pinglun
 * @author fan
 *
 */
public class MessageFragment extends Fragment implements OnClickListener,
OnRefreshListener2<ListView>{

	private String data, TAG = "MessageFragment", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	private PullToRefreshListView mListView;
	List<MessageModel> adapterList = new ArrayList<MessageModel>();
	List<MessageModel> adapterListMore = new ArrayList<MessageModel>();
	MessageAdapter adapter;
	int page = 1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				adapterList.addAll(adapterListMore);
				if (page == 1) {
					adapter = new MessageAdapter(getActivity(), adapterList);
					mListView.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				page=page+1;
			} else if (msg.what == 0) {// 失败
				ToastUtil.showToast(getActivity().getApplicationContext(), info);
			} else if (msg.what == -1) {//
				ToastUtil.showToast(getActivity().getApplicationContext(), info);
			}
			if (pd != null && getActivity() != null) {
				pd.dismiss();
			}
			if (getActivity() != null && mListView != null) {
				mListView.onRefreshComplete();
			}
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view=inflater.inflate(R.layout.fragment_message, container, false);
		initView(view);
		if(isConnect()){
			getData(true);
		}else{
			ToastUtil.showToast(getActivity().getApplicationContext(), XZContranst.no_net);
		}
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				if(TextUtils.equals(adapterList.get(position-1).getType(), "1")){//约伴、行程
					if(TextUtils.equals(adapterList.get(position-1).getState(), "0")){//未读
						setType(adapterList.get(position-1).getId());
					}
					
					Intent intent = new Intent(getActivity(),
							TripDetailActivity.class);
					intent.putExtra("id", adapterList.get(position - 1).getCid());
					startActivity(intent);
				}else if(TextUtils.equals(adapterList.get(position-1).getType(), "2")){//游记
					if(TextUtils.equals(adapterList.get(position-1).getState(), "0")){//未读
						setType(adapterList.get(position-1).getId());
					}
					Intent intent = new Intent(getActivity(),
							TravelDetailActivity.class);
					intent.putExtra("id", adapterList.get(position - 1).getCid());
					startActivity(intent);
				}
			}
		});
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		setType();
	}
	
	private void initView(View view) {
		sp=getActivity().getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(getActivity(), "加载数据...");
		mListView = (PullToRefreshListView) view.findViewById(R.id.rescuenews_listview);
		mListView.setMode(Mode.BOTH);
		// mListView.setMode(Mode.DISABLED);
		mListView.setOnRefreshListener(this);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),false,true));
	}

	protected void showEnsure(String message, final String phone){
		DialogUtils.showEnsure(getActivity(), message, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent callIntent = new Intent(
						Intent.ACTION_DIAL, Uri
								.parse("tel:"+phone));
				startActivity(callIntent);
				dialog.dismiss();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		
	}
	
	public boolean isConnect() {

		ConnectivityManager conManager = (ConnectivityManager)

		getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错

			return networkInfo.isAvailable();

		}

		return false;
	}// isConnect
	

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
			ToastUtil.showToast(getActivity().getApplicationContext(), XZContranst.no_net);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(isConnect()){
			getData(false);
		}else{
			ToastUtil.showToast(getActivity().getApplicationContext(), XZContranst.no_net);
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
						adapterListMore = JSON.parseArray(arry.toString(),
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
	}
	
	
	private void setType(final String id) {
	
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("messageID", id);
					data.put("mid", sp.getString(XZContranst.id, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.message_type;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "改变状态提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "改变状态返回的数据" + jsonj.toString());
					
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}
			}
		}).start();
	}
	
	private void setType() {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
//					data.put("messageID", id);
					data.put("mid", sp.getString(XZContranst.id, ""));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.message_type;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "改变状态提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "改变状态返回的数据" + jsonj.toString());
					
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}
			}
		}).start();
	}


}
