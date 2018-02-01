package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.utils.XZContranst;

/**
 * 声音跟震动的提醒
 * 
 * @author fan
 * 
 */
public class SetSoundActivity extends BaseActivity implements
        OnCheckedChangeListener {
	private ImageView back;
	private TextView title;
	private CheckBox setsound_sound, setsound_zhendong;
	private String cacheSize, TAG = "SetSoundActivity";
	// private Vibrator myVibrator;// 振动器
	private AudioManager audio;// 声音
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_setsound);
		MyApplication.getInstance().addActivity(this);
		initView();
		initListener();
		audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

		// if (audio.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {// 静音
		// setsound_sound.setChecked(false);
		// } else {// 非静音
		// setsound_sound.setChecked(true);
		// }
		
		if (sp.getBoolean(XZContranst.sound, false)) {
			setsound_sound.setChecked(true);
		} else {
			setsound_sound.setChecked(false);
		}

		if (sp.getBoolean(XZContranst.zhendong, false)) {
			setsound_zhendong.setChecked(true);
		} else {
			setsound_zhendong.setChecked(false);
		}
		
		setsound_sound.setOnCheckedChangeListener(this);
		setsound_zhendong.setOnCheckedChangeListener(this);

	}

	private void initView() {
		sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("声音提醒");
		setsound_sound = (CheckBox) findViewById(R.id.setsound_sound);
		setsound_zhendong = (CheckBox) findViewById(R.id.setsound_zhendong);

		Drawable drawable = this.getResources().getDrawable(R.drawable.checkbox_public);
		drawable.setBounds(0,0,120,60);
		setsound_sound.setCompoundDrawables(drawable,null,null,null);

		Drawable drawable1 = this.getResources().getDrawable(R.drawable.checkbox_public);
		drawable1.setBounds(0,0,120,60);
		setsound_zhendong.setCompoundDrawables(drawable1,null,null,null);
	}

	private void initListener() {
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		if (v == setsound_sound) {
			if (isChecked) {
				// audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				// audio.setStreamVolume(AudioManager.RINGER_MODE_NORMAL, 6, 0);
				if (setsound_zhendong.isChecked()) {// 震动
					zhenandvoice();
				
				} else {
					voice();
				}
				Editor edit = sp.edit();
				edit.putBoolean(XZContranst.sound, true);
				edit.apply();
			} else {
				// audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				if (setsound_zhendong.isChecked()) {// 震动
					zhen();
				
				} else {
					nozhenandvoice();
					
				}
				Editor edit = sp.edit();
				edit.putBoolean(XZContranst.sound, false);
				edit.apply();
			}

		} else if (v == setsound_zhendong) {
			if (isChecked) {
				if (setsound_sound.isChecked()) {// 静音
					zhenandvoice();
				} else {// 非静音
					zhen();
				}
				Editor edit = sp.edit();
				edit.putBoolean(XZContranst.zhendong, true);
				edit.apply();

			} else {
				if (setsound_sound.isChecked()) {// 静音
					voice();
				} else {// 非静音
					nozhenandvoice();
				}
				Editor edit = sp.edit();
				edit.putBoolean(XZContranst.zhendong, false);
				edit.apply();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void zhen() {
		audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_ON);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_ON);
		
//		Editor edit = sp.edit();
//		edit.putBoolean(XZContranst.sound, false);
//		edit.putBoolean(XZContranst.zhendong, true);
//		edit.apply();
//		Toast.makeText(getApplicationContext(), "设置成功！当前为振动 sound false zhendong true", Toast.LENGTH_LONG)
//		.show();
	}

	@SuppressWarnings("deprecation")
	private void nozhenandvoice() {
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_OFF);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_OFF);
	
//		Editor edit = sp.edit();
//		edit.putBoolean(XZContranst.sound, false);
//		edit.putBoolean(XZContranst.zhendong, false);
//		edit.apply();
//		Toast.makeText(getApplicationContext(), "设置成功！当前为无声无振动 sound false zhendong false",
//				Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("deprecation")
	private void zhenandvoice() {
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_ON);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_ON);
		
//		Editor edit = sp.edit();
//		edit.putBoolean(XZContranst.sound, true);
//		edit.putBoolean(XZContranst.zhendong,true);
//		edit.apply();
		
//		Toast.makeText(getApplicationContext(), "设置成功！当前为铃声加振动  sound true zhendong true",
//				Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("deprecation")
	private void voice() {
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_OFF);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_OFF);
	
//		Editor edit = sp.edit();
//		edit.putBoolean(XZContranst.sound, true);
//		edit.putBoolean(XZContranst.zhendong, false);
//		edit.apply();
		
//		Toast.makeText(getApplicationContext(), "设置成功！当前为铃声 sound true zhendong false", Toast.LENGTH_LONG)
//		.show();
	}

	// int vibrate_setting = -1;
	// int ring_mode = -1;
	// switch(profile.vibrate) {
	// case ProfileConstants.VIBRATE_ALWAYS_ON:// 总是振动
	// vibrate_setting = AudioManager.VIBRATE_SETTING_ON;
	// ring_mode = 1;
	// break;
	// case ProfileConstants.VIBRATE_NEVER://重不振动
	// vibrate_setting = AudioManager.VIBRATE_SETTING_OFF;
	// ring_mode = 0;
	// break;
	// case ProfileConstants.VIBRATE_ONLY_IN_SILENT://静音下振动
	// vibrate_setting = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
	// ring_mode = 1;
	// break;
	// case ProfileConstants.VIBRATE_UNLESS_SILENT://非静音下振动
	// vibrate_setting = AudioManager.VIBRATE_SETTING_ON;
	// ring_mode = 0;
	// break;
	// }

}
