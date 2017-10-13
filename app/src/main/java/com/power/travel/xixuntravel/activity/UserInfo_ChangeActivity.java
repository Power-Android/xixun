package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.DialogUtils;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 修改昵称 修改职业
 * 
 * @author fan
 * 
 */
public class UserInfo_ChangeActivity extends BaseActivity implements TextWatcher {
	private ImageView back, changename_delect;
	private EditText name_ed;
	private TextView changename_change, title, name;
	private ProgressDialogUtils pd;
	private String TAG = "UserInfo_ChangeActivity", info;
	private int type;
	SharedPreferences sp;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功
				Editor editor = sp.edit();
				if (type == 11) {// 修改昵称
					editor.putString("nickname", name_ed.getText().toString());// 昵称
				} else {
					editor.putString("work", name_ed.getText().toString());// 职位
				}
				editor.commit();

				ToastUtil.showToast(getApplicationContext(), info);

				Intent intent = new Intent();
				intent.putExtra("type", type);
				intent.putExtra("name", name_ed.getText().toString());
				setResult(101, intent);
				finish();
			} else if (msg.what == 10) {// 失败
				ToastUtil.showToast(getApplicationContext(), info);
			} else if (msg.what == -1) {// 登录失败 网络异常
				ToastUtil.showToast(getApplicationContext(), info);

			}
			if (pd != null && UserInfo_ChangeActivity.this != null) {
				pd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_userinfo_changename);
		MyApplication.getInstance().addActivity(this);
		init();
		initGetIntent();
	}

	private void init() {
		sp = getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		pd = ProgressDialogUtils.show(this, "提交数据...");
		back = (ImageView) findViewById(R.id.back);
		changename_delect = (ImageView) findViewById(R.id.changename_delect);
		name_ed = (EditText) findViewById(R.id.changename_ed);
		changename_change = (TextView) findViewById(R.id.changename_change);
		title = (TextView) findViewById(R.id.title);
		name = (TextView) findViewById(R.id.changename_tv);
//		InputFilter[] emojiFilters = {emojiFilter};
		InputFilter[] filters = {new InputFilter.LengthFilter(6)};
		name_ed.setFilters(filters);
//		name_ed.setFilters(emojiFilters);

		back.setOnClickListener(this);
		changename_delect.setOnClickListener(this);
		changename_change.setOnClickListener(this);
		name_ed.addTextChangedListener(this);
	}

	private void initGetIntent() {
		Intent intent = getIntent();
		type = intent.getExtras().getInt("type");
		if (type == 11) {// 修改昵称
			title.setText("昵称");
			name.setText("昵称：");
		} else {
			title.setText("职业");
			name.setText("职业：");
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == changename_delect) {// 删除昵称
			name_ed.getText().clear();
			changename_delect.setVisibility(View.GONE);
		} else if (v == changename_change) {// 修改
			if (TextUtils.isEmpty(name_ed.getText().toString())) {
				name_ed.setFocusableInTouchMode(true);
				name_ed.requestFocus();
				name_ed.findFocus();
				if (type == 11) {// 修改昵称
					ToastUtil.showToast(getApplicationContext(), "请输入昵称!");
				} else {
					ToastUtil.showToast(getApplicationContext(), "请输入职业!");
				}
			} else {
				if (isConnect()) {
					if (type == 11) {// 修改昵称
						editInfo(2, name_ed.getText().toString());
					} else {
						editInfo(4, name_ed.getText().toString());
					}
				} else {
					ToastUtil.showToast(getApplicationContext(), getResources()
							.getString(R.string.notnetwork));
				}
			}
		}
	}

	private static InputFilter emojiFilter = new InputFilter() {

        Pattern emoji = Pattern.compile(
  
        "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",  

        Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
  
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {

            Matcher emojiMatcher = emoji.matcher(source);

            if (emojiMatcher.find()) {
                return "";
            }  
            return null;
        }  
    };  
    
	private void showErro(String tipMessage) {
		DialogUtils.showEnsure(this, tipMessage,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		compileExChar(s.toString());
		if (s.length() > 0) {// 显示删除文字图标
			changename_delect.setVisibility(View.VISIBLE);
			// name_ed.setText(s.toString());
		} else {
			changename_delect.setVisibility(View.INVISIBLE);
			return;
		}

	}

	@Override
	public void afterTextChanged(Editable editable) {
		
	}

	//要判断是否包含特殊字符的目标字符串
	private void compileExChar(String str){
		String limitEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern pattern = Pattern.compile(limitEx);
		Matcher m = pattern.matcher(str);
		if( m.find()){
			ToastUtil.showToast(UserInfo_ChangeActivity.this, "不允许输入特殊符号!");
		}
	}


	// 修改资料
	private void editInfo(final int i, final String key) {
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject data = StringUtils.setDataJSON(
						sp.getString(XZContranst.id, null), i, key, "", "", "");
				String url = HttpUrl.edit_info;
				String json = StringUtils.setJSON(data);

				LogUtil.e(TAG, "修改资料提交的数据" + json);
				String request = HttpClientPostUpload.Upload(json, url);

				JSONObject jsonj = null;
				String status = null;

				try {
					jsonj = new JSONObject(request);
					LogUtil.e(TAG, "修改资料返回的数据" + jsonj.toString());

					status = jsonj.getString("status");
					info = jsonj.getString("info");

				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "解析错误" + e.toString());
				}

				if (TextUtils.equals(status, "1")) {
					handler.sendEmptyMessage(1);
				} else if (TextUtils.equals(status, "0")) {
					handler.sendEmptyMessage(10);
				} else {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}
	
	

}
