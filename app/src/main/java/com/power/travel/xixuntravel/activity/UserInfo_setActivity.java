package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.utils.DialogUtils;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

/**
 * 
 * @author fan
 * 
 */
public class UserInfo_setActivity extends BaseActivity implements
        TextWatcher {
	private ImageView back, changename_delect;
	private EditText name_ed;
	private TextView changename_change, title, name;
	private ProgressDialogUtils pd;
	private String TAG = "UserInfo_ChangeActivity", info,str;
	SharedPreferences sp;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 成功


			} 
			
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_userinfo_set);
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
		InputFilter[] filters = {new InputFilter.LengthFilter(4)};
		name_ed.setFilters(filters);
		changename_change = (TextView) findViewById(R.id.changename_change);
		title = (TextView) findViewById(R.id.title);
		name = (TextView) findViewById(R.id.changename_tv);

		back.setOnClickListener(this);
		changename_delect.setOnClickListener(this);
		changename_change.setOnClickListener(this);
		name_ed.addTextChangedListener(this);
	}

	private void initGetIntent() {
		Intent intent = getIntent();
		str = intent.getStringExtra("name");
		title.setText(str);
		name.setText(str+"：");

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
				ToastUtil.showToast(getApplicationContext(), "请输入"+str);
			} else{
				Intent intent = new Intent();
				intent.putExtra("name", name_ed.getText().toString());
				setResult(101, intent);
				finish();
			}
		}
	}

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
		if (s.length() > 0) {// 显示删除文字图标
			changename_delect.setVisibility(View.VISIBLE);
			// name_ed.setText(s.toString());
		} else {
			changename_delect.setVisibility(View.INVISIBLE);
			return;
		}

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

}
