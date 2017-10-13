package com.power.travel.xixuntravel.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.LoginActivity;
import com.power.travel.xixuntravel.activity.RecommendDetailActivity;
import com.power.travel.xixuntravel.adapter.RecommendListListAdapter;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.impl.PraiseOnItemOnClickListener;
import com.power.travel.xixuntravel.model.RecommentModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.weight.ChildViewPager;
import com.power.travel.xixuntravel.weight.MyGridView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/**
 * 推荐
 * 
 * @author fan
 * 
 */
public class RecommendFragment extends Fragment implements
		OnRefreshListener2<ScrollView>, OnPageChangeListener,
		PraiseOnItemOnClickListener,ChildViewPager.OnSingleTouchListener {

	private ImageView back;
	private TextView title, hot_title;
	List<RecommentModel> adapterList = new ArrayList<RecommentModel>();
	List<RecommentModel> adapterListMore = new ArrayList<RecommentModel>();
//	 List<RecommentHotModel> UrlList = new ArrayList<RecommentHotModel>();
	List<Map<String, String>> UrlList = new ArrayList<Map<String, String>>();
	RecommendListListAdapter mAdapter;
	private PullToRefreshScrollView scroll;
	public static ChildViewPager vPager;// 广告滚动栏
	private LinearLayout del, title_bg;// 图片展示提示
	private ArrayList<View> views; // 保存view的数组，用来显示在页卡
	MyApplication mApplication;
	private Drawable point, pointFocus;
	boolean isPalyAd = false;
	MyGridView gridview;

	private String data, TAG = "ServiceFragment", info, mobile;
	private ProgressDialogUtils pd;
	SharedPreferences sp;
	int page = 1, praisePosi;
	private FrameLayout frame_ad;


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 成功
				if (page == 1) {
					adapterList.addAll(adapterListMore);
					mAdapter = new RecommendListListAdapter(getActivity(),
							adapterList);
					mAdapter.OnItemOnClick(RecommendFragment.this);
					gridview.setAdapter(mAdapter);
//					showAd();// 加载广告
				} else {
					adapterList.addAll(adapterListMore);
					mAdapter.notifyDataSetChanged();
				}
				page = page + 1;
			} else if (msg.what == 6){
				frame_ad.setVisibility(View.VISIBLE);
				showAd();
			}
			else if (msg.what == 0) {// 失败
				ToastUtil
						.showToast(getActivity().getApplicationContext(), info);
			} else if (msg.what == -1) {// 其他情况
				ToastUtil
						.showToast(getActivity().getApplicationContext(), info);
			} else if (msg.what == 2) {// 成功点赞
				ToastUtil
						.showToast(getActivity().getApplicationContext(), info);
				if (TextUtils.equals(adapterList.get(praisePosi).getZanIf(),
						"1")) {// 赞变不赞

				} else {// 不赞变赞

				}
			} else if (msg.what == -2) {// 失败点赞
				ToastUtil
						.showToast(getActivity().getApplicationContext(), info);
			}

			if (pd != null && RecommendFragment.this != null) {
				pd.dismiss();
			}
			if (RecommendFragment.this != null && scroll != null) {
				scroll.onRefreshComplete();
			}
		}
	};

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			palyAD();
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_recomment, container,
				false);
		init(view);
		if (isConnect()) {
			getData(true);
		} else {
			ToastUtil.showToast(getActivity().getApplicationContext(),
					XZContranst.no_net);
		}

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Intent intent = new Intent(getActivity(),
						RecommendDetailActivity.class);
				intent.putExtra("id", adapterList.get(position).getId());
				startActivity(intent);
			}
		});

		return view;
	}

	private void init(View view) {
		sp = getActivity().getSharedPreferences(
				XZContranst.MAIN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(getActivity(), "获取数据...");
		mApplication = (MyApplication) getActivity().getApplicationContext();
		back = (ImageView) view.findViewById(R.id.back);
		back.setVisibility(View.GONE);
		title = (TextView) view.findViewById(R.id.title);
		title.setText("推荐");
		scroll = (PullToRefreshScrollView) view
				.findViewById(R.id.recomment_scroll);
		scroll.setOnRefreshListener(this);
		// scroll.setMode(Mode.DISABLED);//什么也没有
		// scroll.setMode(Mode.PULL_FROM_START);//只有刷新
		scroll.setMode(Mode.BOTH);// 刷新加载更多都有
		vPager = (ChildViewPager) view.findViewById(R.id.vp);
		vPager.setFocusable(true);
		vPager.setFocusableInTouchMode(true);
		vPager.requestFocus();
		del = (LinearLayout) view.findViewById(R.id.del);
		title_bg = (LinearLayout) view.findViewById(R.id.title_bg);
		gridview = (MyGridView) view.findViewById(R.id.recomment_gridview);
		hot_title = (TextView) view.findViewById(R.id.hot_title);
		frame_ad = (FrameLayout) view.findViewById(R.id.frame_ad);
	}

	@Override
	public void OnItemClick(int typeint, int position) {
		switch (typeint) {
		case 1:// 点赞
			if (sp.getBoolean(XZContranst.if_login, false)) {
				praisePosi = position;
				praise(adapterList.get(position).getId());
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		if (!adapterList.isEmpty()) {
			adapterList.clear();
			mAdapter.notifyDataSetChanged();
		}
		page = 1;
		getData(false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		getData(false);
	}

	@SuppressWarnings("deprecation")
	private void showAd() {
		if (mApplication.isRun) {
			try {
				views = new ArrayList<View>();
				// for (int i = 0; i < UrlList.size(); i++) {
				for (int i = 0; i < 1; i++) {
					ImageView imageView = new ImageView(
							RecommendFragment.this.getActivity());
					imageView.setLayoutParams(new Gallery.LayoutParams(
							Gallery.LayoutParams.FILL_PARENT,
							Gallery.LayoutParams.FILL_PARENT));
					imageView.setScaleType(ScaleType.FIT_XY);

					DisplayImageOptions options = new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.imaleloadlogo)
							.cacheInMemory(true).cacheOnDisc(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565).build();

					ImageLoader.getInstance().displayImage(
							UrlList.get(i).get("thumb"), imageView, options);
					final String id = UrlList.get(i).get("id");
					hot_title.setText(UrlList.get(i).get("title"));

					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// ToastUtil.showToast(getActivity(), "单击。。。。。。");
							Intent intent = new Intent(new Intent(
									getActivity(),
									RecommendDetailActivity.class));
							intent.putExtra("id", id);
							getActivity().startActivity(intent);
						}

					});
					views.add(imageView);
					point = getResources()
							.getDrawable(R.drawable.move_unselect);
					pointFocus = getResources().getDrawable(
							R.drawable.move_select);
					initDel();
					vPager.setOnSingleTouchListener(this);
					vPager.setAdapter(new ViewPageAdpter());
					vPager.setOnPageChangeListener(RecommendFragment.this);
					vPager.setOnTouchListener(new View.OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
							case MotionEvent.ACTION_MOVE:

								break;
							case MotionEvent.ACTION_UP:

								break;
							default:

								break;
							}
							return false;
						}
					});
				}
			} catch (Exception e) {
				Log.e("TAG", "showAd问题" + e.toString());
			}

			if (!isPalyAd) {
				isPalyAd = true;
				// myHandler.sendEmptyMessageDelayed(0, 5000);
			}
		}

	}

	private void initDel() {
		try {
			if (views != null && views.size() != 0) {
				del.removeAllViews();
				for (int i = 0; i < views.size(); i++) {
					ImageView im = new ImageView(
							RecommendFragment.this.getActivity());
					if (i == 0) {
						im.setImageDrawable(pointFocus);
					} else {
						im.setImageDrawable(point);
					}
					del.removeView(im);
					del.addView(im);
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "initDel蹦了" + e.toString());
		}

	}

	public void palyAD() {
		try {
			if (mApplication.isRun) {
				int position = vPager.getCurrentItem();
				int index = (position + 1) % views.size();
				vPager.setCurrentItem(index);
				if (isPalyAd) {
					// myHandler.sendEmptyMessageDelayed(0, 3000);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "palyAD问题" + e.toString());
		}

	}

	class ViewPageAdpter extends PagerAdapter {// 装载view的适配器
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) { // 销毁view
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public int getCount() { // view数量
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, final int arg1) { // 返回相应的view
			((ViewPager) arg0).addView(views.get(arg1), 0);
			

			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		mApplication.isRun = false;

	}

	@Override
	public void onResume() {
		super.onResume();
		mApplication.isRun = true;

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		if (mApplication.isRun) {
			try {
				del.removeAllViews();
				// for (int i = 0; i < UrlList.size(); i++) {
				for (int i = 0; i < 1; i++) {
					ImageView im = new ImageView(
							RecommendFragment.this.getActivity());
					if (i == arg0) {
						im.setImageDrawable(pointFocus);
						del.removeView(im);
						del.addView(im);
					} else {
						im.setImageDrawable(point);
						del.removeView(im);
						del.addView(im);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "onPageSelected蹦了" + e.toString());
			}

		}
	}
	
	@Override
	public void onSingleTouch() {//viewpage的单击事件
		 String id = UrlList.get(0).get("id");
		Intent intent = new Intent(new Intent(
				getActivity(),
				RecommendDetailActivity.class));
		intent.putExtra("id", id);
		getActivity().startActivity(intent);
		
	}


	/**
	 * isConnect 判断网络连接
	 * 
	 * @return
	 */
	public boolean isConnect() {
		ConnectivityManager conManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错
			return networkInfo.isAvailable();
		}
		return false;
	}// isConnect

	/**
	 * 获取数据
	 * 
	 * @param ifshow
	 */
	private void getData(boolean ifshow) {
		if (ifshow) {
			pd.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("catid", "2");
					data.put("mid", sp.getString(XZContranst.id, null));
					data.put("page", page);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.Recommend;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "推荐提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;
				String flag = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "推荐返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					String hot = jsonj.getString("hot");
					LogUtil.e(TAG,"-----------"+hot);
					if (!TextUtils.equals(hot, null)){
						flag = "6";
						LogUtil.e(TAG,"----------怎么会执行呢？----------"+flag);
						JSONObject temp = jsonj.getJSONObject("hot");
						// UrlList = JSON.parseArray(hot.toString(),
						// RecommentHotModel.class);
						if (temp != null){
							for (int i = 0; i < 1; i++) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("id", temp.getString("id"));
								map.put("onclick", temp.getString("onclick"));
								map.put("addtime", temp.getString("addtime"));//
								map.put("title", temp.getString("title"));//
								map.put("thumb",
										HttpUrl.Url + temp.getString("thumb"));//
								map.put("description",
										temp.getString("description"));//
								UrlList.add(map);
							}
						}
					}
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
						adapterListMore = JSON.parseArray(arry.toString(),
								RecommentModel.class);
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
				if (TextUtils.equals(flag, "6")){
					handler.sendEmptyMessage(6);
				}

			}
		}).start();
	}

	// 点赞 取消赞
	private void praise(final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject data = new JSONObject();
				try {
					data.put("aid", id);
					data.put("mid", sp.getString(XZContranst.id, null));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String url = HttpUrl.recommendZan;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "推荐赞提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "推荐赞返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");
					if (TextUtils.equals(status, "1")) {
						JSONArray arry = jsonj.getJSONArray("data");
					}

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
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


}
