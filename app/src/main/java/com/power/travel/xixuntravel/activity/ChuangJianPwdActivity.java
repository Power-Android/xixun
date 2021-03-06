package com.power.travel.xixuntravel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChuangJianPwdActivity extends BaseActivity {

    private ImageView back;
    private TextView title;
    private Button regist_upload;
    private EditText regist_setpwd, regist_setpwd2;
    private String data, TAG = "ChuangJianPwdActivity", info;
    SharedPreferences sp;
    private String openid;
    private String type;
    private String mobile;
    private String nickname;
    private String face;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {// 成功
                ToastUtil.showToast(getApplicationContext(), info);
                Intent intent = new Intent();
                setResult(122,intent);
                finish();
            } else if (msg.what == 0) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == -1) {// 其他情况
                ToastUtil.showToast(getApplicationContext(), info);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuang_jian_pwd);
        MyApplication.getInstance().addActivity(this);

        initView();
        initListener();
    }

    private void initView() {

        openid = getIntent().getStringExtra("openid");
        type = getIntent().getStringExtra("type");
        mobile = getIntent().getStringExtra("mobile");
        nickname = getIntent().getStringExtra("nickname");
        face = getIntent().getStringExtra("face");

        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        title.setText("创建密码");
        regist_setpwd= (EditText) findViewById(R.id.regist_setpwd);
        regist_setpwd2 = (EditText) findViewById(R.id.regist_setpwd2);
        regist_upload = (Button) findViewById(R.id.regist_upload);

    }

    private void initListener() {
        back.setOnClickListener(this);
        regist_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == back) {
            finish();
        } else if (v == regist_upload) {
            if (validate()) {
                upload();
            }
        }
    }

    /**
     * 是否是纯数字或者纯英文
     * @param psd
     * @return
     */
    public static boolean ispsd(String psd) {
        Pattern p = Pattern
                .compile("^[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]");
        Matcher m = p.matcher(psd);

        return m.matches();
    }

    private boolean validate() {
        String pwd = regist_setpwd.getText().toString();

        if (ispsd(pwd) == false){
            regist_setpwd.setFocusableInTouchMode(true);
            regist_setpwd.requestFocus();
            regist_setpwd.findFocus();
            showEnsure("密码格式不正确！");
            return false;
        }

        if (TextUtils.isEmpty(regist_setpwd.getText().toString())) {
            regist_setpwd.setFocusableInTouchMode(true);
            regist_setpwd.requestFocus();
            regist_setpwd.findFocus();
            showEnsure("请输入新密码!");
            return false;
        }
        if (TextUtils.isEmpty(regist_setpwd2.getText().toString())) {
            regist_setpwd2.setFocusableInTouchMode(true);
            regist_setpwd2.requestFocus();
            regist_setpwd2.findFocus();
            showEnsure("请确认新密码!");
            return false;
        }
        if (!TextUtils.equals(regist_setpwd.getText().toString(),
                regist_setpwd2.getText().toString())) {
            regist_setpwd2.setFocusableInTouchMode(true);
            regist_setpwd2.requestFocus();
            regist_setpwd2.findFocus();
            regist_setpwd2.getText().clear();
            showEnsure("两次密码不相等!");
            return false;
        }
        if (regist_setpwd.getText().toString().trim().length() < 6 && regist_setpwd.getText().toString().trim().length() > 16){
            regist_setpwd2.setFocusableInTouchMode(true);
            regist_setpwd2.requestFocus();
            regist_setpwd2.findFocus();
            showEnsure("请输入6-16位密码");
            return false;
        }
        if (!isConnect()) {
            ToastUtil.showToast(getApplication(),
                    getResources().getString(R.string.notnetwork));
            return false;
        }
        return true;
    }

    private void upload() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                JSONObject data = new JSONObject();
                try {
                    data.put("mobile",mobile);
                    data.put("face",face);
                    data.put("nickname",nickname);
                    data.put("openid",openid);
                    data.put("type",type);
                    data.put("password", regist_setpwd.getText()
                            .toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.openidReg;
                String json= StringUtils.setJSON(data);

                LogUtil.e(TAG, "找回密码提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "找回密码返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");
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
