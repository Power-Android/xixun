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
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.fragment.RescueNewsFragment;
import com.power.travel.xixuntravel.fragment.RescuePhoneFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 救援 tab
 * @author fan
 *
 */
public class RescueActivity extends BaseActivity {

	private ImageView back;
	private TextView title;
	private List<Fragment> fragments=new ArrayList<Fragment>();
	private FragmentTabAdapter tabAdapter;
	private RadioGroup radioGroup;
	private RadioButton left,right;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rescue);
		MyApplication.getInstance().addActivity(this);
		
		initView();
		initListener();
	}
	private void initView() {
		
		back=(ImageView)findViewById(R.id.back);
		title=(TextView)findViewById(R.id.title);
		title.setText("救援");
		
		fragments.add(new RescueNewsFragment());
		fragments.add(new RescuePhoneFragment());
		
		radioGroup=(RadioGroup)findViewById(R.id.main_radiogroup);
		left=(RadioButton)findViewById(R.id.tab_rb_news);
		right=(RadioButton)findViewById(R.id.tab_rb_phone);
		tabAdapter=new FragmentTabAdapter(this, fragments, R.id.tab_content, radioGroup);
		tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
			@SuppressLint("NewApi")
			@Override
			public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
					int checkedId, int index) {
				switch (index) {
				case 0:
					left.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg));
					right.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
					break;
				case 1:
					right.setBackground(getResources().getDrawable(R.drawable.tab_btn_selectedbg));
					left.setBackground(getResources().getDrawable(R.drawable.tab_btn_normallbg));
					break;

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
