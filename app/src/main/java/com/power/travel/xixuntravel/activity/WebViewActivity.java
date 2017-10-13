package com.power.travel.xixuntravel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;

public class WebViewActivity extends BaseActivity {

	private String url;
	private WebView webView;
	private TextView title;
	private ImageView back;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	
		setContentView(R.layout.activity_webview);
		MyApplication.getInstance().addActivity(this);
		
		
		webView = (WebView) findViewById(R.id.content);
		// 设置WebView属性，能够执行Javascript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		// 设置Web视图 21.
		webView.setWebViewClient(new HelloWebViewClient());
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		title=(TextView)findViewById(R.id.title);
		Intent intent = getIntent();
		if (intent != null) {
			url = intent.getStringExtra("url");
			if (!TextUtils.isEmpty(url)){
				title.setText(intent.getStringExtra("name"));
			}else {
				title.setText("");
			}
			if(url!=null&&!url.trim().equals("")){
				webView.loadUrl(url);
				
			}
			
		}
	}

	private class HelloWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面

			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			this.finish();
		}
	}

}
