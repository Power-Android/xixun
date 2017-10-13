package com.power.travel.xixuntravel.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.FragmentTabAdapter;
import com.power.travel.xixuntravel.adapter.FragmentTabAdapter.OnRgsExtraCheckedChangedListener;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.fragment.ChatFragment;
import com.power.travel.xixuntravel.fragment.MessageFragment;
import com.power.travel.xixuntravel.fragment.SystemMessageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息 tab
 * @author fan
 *
 */
public class MessageActivity extends BaseActivity {

	private ImageView back;
	private TextView title;
	private List<Fragment> fragments=new ArrayList<Fragment>();
	private FragmentTabAdapter tabAdapter;
	private RadioGroup radioGroup;
	private RadioButton left,right;
	private RadioButton rb_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		MyApplication.getInstance().addActivity(this);
		
		initView();
		initListener();
		
	}
	private void initView() {
		
		back=(ImageView)findViewById(R.id.back);
		title=(TextView)findViewById(R.id.title);
		title.setText("消息");

		fragments.add(new ChatFragment());
		fragments.add(new MessageFragment());
		fragments.add(new SystemMessageFragment());
		
		radioGroup=(RadioGroup)findViewById(R.id.main_radiogroup);
		left=(RadioButton)findViewById(R.id.tab_rb_news);
		right=(RadioButton)findViewById(R.id.tab_rb_phone);
		rb_msg = (RadioButton)findViewById(R.id.tab_rb_msg);
		tabAdapter=new FragmentTabAdapter(this, fragments, R.id.tab_content, radioGroup);
		tabAdapter.setOnRgsExtraCheckedChangedListener(new OnRgsExtraCheckedChangedListener(){
			@SuppressLint("NewApi")
			@Override
			public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
					int checkedId, int index) {
				switch (index) {
					case 0:
						rb_msg.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg));
						left.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
						right.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
						break;
					case 1:
						rb_msg.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
						right.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
						left.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg));
						break;
					case 2:
						rb_msg.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
						right.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg));
						left.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));

				default:
					break;
				}
			}
		});
	}
	
	private void initListener() {
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v==back){
			finish();
		}
	}
	
}
