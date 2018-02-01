package com.power.travel.xixuntravel.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.adapter.CarTypeAdapter;
import com.power.travel.xixuntravel.adapter.CarageAdapter;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.model.CarModel;
import com.power.travel.xixuntravel.net.HttpClientPostUpload;
import com.power.travel.xixuntravel.net.HttpUrl;
import com.power.travel.xixuntravel.net.UploadUtil;
import com.power.travel.xixuntravel.pic.Bimp;
import com.power.travel.xixuntravel.pic.FileUtils;
import com.power.travel.xixuntravel.pic.PhotoActivity;
import com.power.travel.xixuntravel.selectpic.SelectPicMainActivity;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ProgressDialogUtils;
import com.power.travel.xixuntravel.utils.SaveBitmapUtil;
import com.power.travel.xixuntravel.utils.StringUtils;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.utils.XZContranst;
import com.power.travel.xixuntravel.weight.MyGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZuFangActivity extends BaseActivity {

    private TextView title;
    private ImageView back;
    private EditText biaoti_tv;
    private TextView shi_tv;
    private TextView ting_tv;
    private EditText jiage_tv;
    private EditText mianji_tv;
    private EditText weizhi_tv;
    private EditText lxfs_tv;
    private EditText content_et;
    private MyGridView noScrollgridview;
    private Button upload;
    private GridAdapter adapter;
    private String TAG = "ZuCheActivity", info, headportrait;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    private String path = "",imageFileName = "";
    private int TAKE_PICTURE = 0x000000;
    File file1,file2,file3,file4,file5,file6,file7,file8,file9;
    private ProgressDialogUtils pd;
    SharedPreferences sp,spLocation;
    int clickType = 1;
    private List<CarModel> adapterList = new ArrayList<CarModel>();
    private List<CarModel> CarTypeList = new ArrayList<CarModel>();
    private PopupWindow popupWindow;
    private String pinpai, pinpaiid, chexing, chexingid;
    private ListView carListView;
    CarTypeAdapter pinpaiAdapter, chexingAdapter;
    CarageAdapter carageAdapter;

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {//
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 2) {// 上传图片成功

            } else if (msg.what == -2) {// 上传图片失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 3) {// 成功

            } else if (msg.what == -3) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            } else if (msg.what == 4) {//发布成功
                ToastUtil.showToast(getApplicationContext(), info);
                onBackPressed();
            } else if (msg.what == -4) {// 发布失败
                ToastUtil.showToast(getApplicationContext(), info);
            }else if (msg.what == 5) {// 成功
                initmPopupWindowView(clickType);
                switch (clickType) {
                    case 1:
                        popupWindow.showAsDropDown(shi_tv, 0, 0);
                        break;
                    case 2:
                        popupWindow.showAsDropDown(ting_tv, 0, 0);
                        break;
                    default:
                        break;
                }
            } else if (msg.what == -5) {// 失败
                ToastUtil.showToast(getApplicationContext(), info);
            }

            if (pd != null && ZuFangActivity.this != null) {
                pd.dismiss();
            }

        }
    };

    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {// 上传图片成功
                sendTrip();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zu_fang);
        MyApplication.getInstance().addActivity(this);
        initView();

        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();
                if (isOpen) {// 如果输入法打开 取消输入法
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(
                            ZuFangActivity.this.getCurrentFocus()
                                    .getWindowToken(),

                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // isOpen若返回true，则表示输入法打开
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(ZuFangActivity.this, noScrollgridview);
                } else {
                    Intent intent = new Intent(ZuFangActivity.this,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        adapter.update();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bimp.mSelectedImage.clear();
        Bimp.bmp.clear();
        Bimp.drr.clear();
        Bimp.max = 0;
        FileUtils.deleteDir();
        finish();
    }

    private void initView() {
        sp = this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        spLocation= this.getSharedPreferences(XZContranst.MAIN_SHARED_PREFERENCES_LO,
                Context.MODE_PRIVATE);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        title.setText("发布租房");
        biaoti_tv = findViewById(R.id.biaoti_tv);
        shi_tv = findViewById(R.id.shi_tv);
        ting_tv = findViewById(R.id.ting_tv);
        jiage_tv = findViewById(R.id.jiage_tv);
        mianji_tv = findViewById(R.id.mianji_tv);
        weizhi_tv = findViewById(R.id.weizhi_tv);
        lxfs_tv = findViewById(R.id.lxfs_tv);
        content_et = findViewById(R.id.content_et);
        noScrollgridview = findViewById(R.id.noScrollgridview);
        upload = findViewById(R.id.upload);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        noScrollgridview.setAdapter(adapter);
        initListener();
        for (int i = 1; i < 6; i++) {
            CarModel carModel = new CarModel();
            carModel.setName(i+"室");
            adapterList.add(carModel);
        }
        for (int i = 0; i < 4; i++) {
            CarModel carModel1 = new CarModel();
            carModel1.setName(i+"厅");
            CarTypeList.add(carModel1);
        }

    }

    private void initListener() {
        back.setOnClickListener(this);
        shi_tv.setOnClickListener(this);
        ting_tv.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == back){
            onBackPressed();
        }else if (v == upload){
            if (validate()) {
                uploadpic();
            }
        }else if (v == shi_tv) {// 室
            clickType = 1;
            initmPopupWindowView(clickType);
            popupWindow.showAsDropDown(shi_tv, 0, 0);

        } else if (v == ting_tv) {// 厅
            clickType = 2;
            initmPopupWindowView(clickType);
            popupWindow.showAsDropDown(ting_tv, 0, 0);
        }
    }

    /**
     * 品牌车型
     *
     * @param type
     */
    private void initmPopupWindowView(final int type) {

        View customView = getLayoutInflater().inflate(
                R.layout.item_cartype_layout, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        // popupWindow.setAnimationStyle(R.style.AnimationFade);
        // popwindow获取焦点
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SignPopupWindow弹出窗体的背景
        // popupWindow.setBackgroundDrawable(dw);

        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 单击内部空白区域，pop消失
                 */
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    return true;
                }
                /**
                 * 单击pop外部区域，pop消失
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }

                return false;
            }

        });
        switch (type) {
            case 1:// 品牌
                carListView = (ListView) customView.findViewById(R.id.list_chexing);
                pinpaiAdapter = new CarTypeAdapter(ZuFangActivity.this,
                        adapterList);
                carListView.setAdapter(pinpaiAdapter);
                break;
            case 2:// 车型
                carListView = (ListView) customView.findViewById(R.id.list_carage);
                chexingAdapter = new CarTypeAdapter(ZuFangActivity.this,
                        CarTypeList);
                carListView.setAdapter(chexingAdapter);
                break;
            default:
                break;
        }

        carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (type) {
                    case 1:
                        pinpai = adapterList.get(position).getName();
                        shi_tv.setText(pinpai);
                        break;
                    case 2://
                        chexing = CarTypeList.get(position).getName();
                        ting_tv.setText(chexing);
                        break;
                    default:
                        break;
                }
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    private boolean validate() {
        if (TextUtils.isEmpty(biaoti_tv.getText().toString())) {
            biaoti_tv.setFocusableInTouchMode(true);
            biaoti_tv.requestFocus();
            biaoti_tv.findFocus();
            ToastUtil.showToast(getApplicationContext(), "请输入标题");
            return false;
        }
        if (TextUtils.equals("室",shi_tv.getText().toString())){
            ToastUtil.showToast(getApplicationContext(), "请输入室");
            return false;
        }
        if (TextUtils.equals("厅",ting_tv.getText().toString())){
            ToastUtil.showToast(getApplicationContext(), "请输入厅");
            return false;
        }
        if (TextUtils.isEmpty(jiage_tv.getText().toString())) {
            jiage_tv.setFocusableInTouchMode(true);
            jiage_tv.requestFocus();
            jiage_tv.findFocus();
            ToastUtil.showToast(getApplicationContext(), "请输入价格");
            return false;
        }
        if (Bimp.drr.size() ==0) {
            ToastUtil.showToast(getApplicationContext(), "请选择图片");
            return false;
        }
        if (TextUtils.isEmpty(mianji_tv.getText().toString())) {
            mianji_tv.setFocusableInTouchMode(true);
            mianji_tv.requestFocus();
            mianji_tv.findFocus();
            ToastUtil.showToast(getApplicationContext(), "请输入面积");
            return false;
        }
        if (TextUtils.isEmpty(weizhi_tv.getText().toString())) {
            weizhi_tv.setFocusableInTouchMode(true);
            weizhi_tv.requestFocus();
            weizhi_tv.findFocus();
            ToastUtil.showToast(getApplicationContext(), "请输入位置");
            return false;
        }
        if (TextUtils.isEmpty(lxfs_tv.getText().toString())) {
            lxfs_tv.setFocusableInTouchMode(true);
            lxfs_tv.requestFocus();
            lxfs_tv.findFocus();
            ToastUtil.showToast(getApplicationContext(), "请输入联系方式");
            return false;
        }
        if (TextUtils.isEmpty(content_et.getText().toString())) {
            content_et.setFocusableInTouchMode(true);
            content_et.requestFocus();
            content_et.findFocus();
            ToastUtil.showToast(getApplicationContext(), "请输入内容");
            return false;
        }
        return true;
    }

    // 选择相册还是拍照
    public class PopupWindows extends PopupWindow {

        public PopupWindows(final Context mContext, View parent) {

            final View view = View.inflate(mContext,
                    R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(ZuFangActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(ZuFangActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    }else {
                        path = null;
                        photo();
                    }
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(ZuFangActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(ZuFangActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_STORAGE_REQUEST_CODE);
                    }else {
                        startActivity(new Intent(ZuFangActivity.this,
                                SelectPicMainActivity.class));
                    }
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//				 Permission Granted
                path = null;
                photo();
            } else {
                // Permission Denied
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                startActivity(new Intent(ZuFangActivity.this,
                        SelectPicMainActivity.class));
            } else {
                // Permission Denied
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            File file=new File(path);
            if (file.exists()) {
                if (Bimp.drr.size() < 9) {
                    Bimp.drr.add(SaveBitmapUtil.SaveBitmap(this, path, imageFileName));
                } else {
                    ToastUtil.showToast(getApplicationContext(), "最多选择9张图片");
                }
            }
        }
    }

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pfile = null;
        try {
            pfile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this,getPackageName()+".FileProvider", pfile);
//			uri = Uri.fromFile(pfile);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } else {
            uri = Uri.fromFile(pfile);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        }

		/*openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pfile));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);*/
    }

    protected File createImageFile() throws IOException {

        imageFileName = String.valueOf(System.currentTimeMillis())
                + ".jpg";
		/* File的构造方法，指定路径以及文件名称来实例化File对象 */
        File image = new File(getAlbumDir(), imageFileName);
        path = image.getCanonicalPath();// getCanonicalPath()获得绝对路径
        return image;
    }//

    public static File getAlbumDir() {
        // Environment.getExternalStorageDirectory() 获得sd卡
        File dir = new File(Environment.getExternalStorageDirectory(),
                getAlbunName());
        if (!dir.exists()) {// 文件夹不存在 创建文件
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取保存图片的文件夹名称
     *
     * @return
     */
    private static String getAlbunName() {
        return "myimage";
    }

    // 上传图片
    private void uploadpic() {
        pd = ProgressDialogUtils.show(this, "提交数据...");
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = HttpUrl.uploadify;
                Map<String, String> word = new HashMap<String, String>();

                Map<String, File> files = new HashMap<String, File>();
                file1 = new File(Bimp.drr.get(0));
                if (file1 != null && file1.exists()) {
                    files.put("file_box1", file1);
                }
                if (Bimp.drr.size() > 1) {
                    file2 = new File(Bimp.drr.get(1));
                    if (file2 != null && file2.exists()) {
                        files.put("file_box2", file2);
                    }
                }
                if (Bimp.drr.size() > 2) {
                    file3 = new File(Bimp.drr.get(2));
                    if (file3 != null && file3.exists()) {
                        files.put("file_box3", file3);
                    }
                }
                if (Bimp.drr.size() > 3) {
                    file4 = new File(Bimp.drr.get(3));
                    if (file4 != null && file4.exists()) {
                        files.put("file_box4", file4);
                    }
                }
                if (Bimp.drr.size() > 4) {
                    file5 = new File(Bimp.drr.get(4));
                    if (file5 != null && file5.exists()) {
                        files.put("file_box5", file5);
                    }
                }
                if (Bimp.drr.size() > 5) {
                    file6 = new File(Bimp.drr.get(5));
                    if (file6 != null && file6.exists()) {
                        files.put("file_box6", file6);
                    }
                }
                if (Bimp.drr.size() > 6) {
                    file7 = new File(Bimp.drr.get(6));
                    if (file7 != null && file7.exists()) {
                        files.put("file_box7", file7);
                    }
                }
                if (Bimp.drr.size() > 7) {
                    file8 = new File(Bimp.drr.get(7));
                    if (file8 != null && file8.exists()) {
                        files.put("file_box8", file8);
                    }
                }
                if (Bimp.drr.size() > 8) {
                    file9 = new File(Bimp.drr.get(8));
                    if (file9 != null && file9.exists()) {
                        files.put("file_box9", file9);
                    }
                }

                String request = UploadUtil.uploadFile(word, files, url);

                JSONObject jsonj = null;
                String code = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "上传图片返回的数据" + jsonj.toString());

                    code = jsonj.getString("status");// 1成功
                    info = jsonj.getString("msg");
                    headportrait =  jsonj.optString("img");// 图片网络地址
                    // 全称

                } catch (Exception e) {
                    LogUtil.e(TAG, "上传图片解析错误" + e.toString());
                }

                if (TextUtils.equals(code, "1")) {
                    handler2.sendEmptyMessage(2);
                } else if (TextUtils.equals(code, "0")) {
                    handler.sendEmptyMessage(-2);
                } else {
                    handler.sendEmptyMessage(-1);
                }

            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {

        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item
         */
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));

            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        public Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        Handler handlerimg = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            // Message message = new Message();
                            // message.what = 1;
                            handlerimg.sendEmptyMessage(1);
                            LogUtil.e(TAG, "刷新获取相册的数据");
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;

                                handlerimg.sendEmptyMessage(1);
                                LogUtil.e(TAG, "刷新照相的数据");
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    private void sendTrip() {//发布租车
        new Thread(new Runnable() {

            @Override
            public void run() {

                JSONObject data = new JSONObject();
                try {
                    data.put("mid", sp.getString(XZContranst.id, null));
                    data.put("x", spLocation.getString(XZContranst.coordinate_x, ""));
                    data.put("y", spLocation.getString(XZContranst.coordinate_y, ""));
                    data.put("title", biaoti_tv.getText().toString());
                    data.put("layoutRoom",shi_tv.getText().toString());
                    data.put("layoutOffice",ting_tv.getText().toString());
                    data.put("measure",mianji_tv.getText().toString());
                    data.put("money",jiage_tv.getText().toString());
                    data.put("position",weizhi_tv.getText().toString());
                    data.put("contacts",lxfs_tv.getText().toString());
                    data.put("content",content_et.getText().toString());
                    data.put("img",headportrait);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                String url = HttpUrl.addRental;
                String json = StringUtils.setJSON(data);

                LogUtil.e(TAG, "发布租车提交的数据" + json);
                String request = HttpClientPostUpload.Upload(json, url);

                JSONObject jsonj = null;
                String status = null;

                try {
                    jsonj = new JSONObject(request);
                    LogUtil.e(TAG, "发布租车返回的数据" + jsonj.toString());

                    status = jsonj.getString("status");
                    info = jsonj.getString("info");

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "解析错误" + e.toString());
                }

                if (TextUtils.equals(status, "1")) {
                    handler.sendEmptyMessage(4);
                } else if (TextUtils.equals(status, "0")) {
                    handler.sendEmptyMessage(-4);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }
}
