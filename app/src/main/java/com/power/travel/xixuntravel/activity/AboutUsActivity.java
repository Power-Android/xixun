package com.power.travel.xixuntravel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.power.travel.xixuntravel.BuildConfig;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;

/**
 * 关于我们
 * @author fan
 *
 */
public class AboutUsActivity extends BaseActivity {

	private ImageView back;
	private TextView title,tv_code;
	private RelativeLayout function_introduction,feedback,contact_us;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_aboutus);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();
	}
	
	private void initView() {
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		tv_code = (TextView) findViewById(R.id.tv_code);
		int versionCode = BuildConfig.VERSION_CODE;
		tv_code.setText("v"+versionCode);
		title.setText("关于我们");
		function_introduction=(RelativeLayout)findViewById(R.id.function_introduction);
		feedback=(RelativeLayout)findViewById(R.id.feedback);
		contact_us=(RelativeLayout)findViewById(R.id.contact_us);
	}

	private void initListener() {
		back.setOnClickListener(this);
		function_introduction.setOnClickListener(this);
		feedback.setOnClickListener(this);
		contact_us.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}else if(v==function_introduction){//公司简介
			startActivity(new Intent(this,CompanyActivity.class));
		}else if(v==feedback){//意见反馈
			startActivity(new Intent(this,FeedbackActivity.class));
		}else if(v==contact_us){//联系我们
			startActivity(new Intent(this,ContactUsActivity.class));
		}
	}
}
