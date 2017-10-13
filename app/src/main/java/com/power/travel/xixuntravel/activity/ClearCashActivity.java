package com.power.travel.xixuntravel.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.app.MyApplication;
import com.power.travel.xixuntravel.utils.DataCleanManager;
import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.ToastUtil;
import com.power.travel.xixuntravel.views.RoundProgressBar;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 清理缓存
 * 
 * @author fan
 * 
 */
public class ClearCashActivity extends BaseActivity {

	private ImageView back;
	private TextView title;
	private RoundProgressBar roundprogress;
	private Button clearcash;
	private String cacheSize,TAG="ClearCashActivity";
	private int progress = 0,MaxPro;
	long blockSize,totalBlocks,availableBlocks,totalBlocks2,availableBlocks2;
	long alltotalblock,allavailableBlock;
    String alltot,allavail;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_clearcash);
		MyApplication.getInstance().addActivity(this);

		initView();
		initListener();

		if (ExistSDCard()) {
//			String extSdcardPath = System.getenv("SECONDARY_STORAGE");
//			File path = Environment.getExternalStorageDirectory();
			try {
				File path=new File(getStoragePath(this,true));
				StatFs stat = new StatFs(path.getPath());
				 blockSize = stat.getBlockSize();
				 totalBlocks = stat.getBlockCount();
				 availableBlocks = stat.getAvailableBlocks();

				long totalSize = totalBlocks * blockSize;
				long availSize = availableBlocks * blockSize;

				String totalStr = Formatter.formatFileSize(this, totalSize);
				String availStr = Formatter.formatFileSize(this, availSize);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
//			LogUtil.e("sd卡的数据",path.toString()+"\n"+ totalStr + "\n" + availStr);
			
		}

//		File path2 = Environment.getDataDirectory();
		File path2=new File(getStoragePath(this,false));
		StatFs stat2 = new StatFs(path2.getPath());
		long blockSize2 = stat2.getBlockSize();
		 totalBlocks2 = stat2.getBlockCount();
		 availableBlocks2 = stat2.getAvailableBlocks();

		long totalSize2 = totalBlocks2 * blockSize2;
		long availSize2 = availableBlocks2 * blockSize2;

		String totalStr2 = Formatter.formatFileSize(this, totalSize2);
		String availStr2 = Formatter.formatFileSize(this, availSize2);

//		LogUtil.e("手机的数据",path2.toString()+"\n"+ totalStr2 + "\n" + availStr2);
		
        alltotalblock=totalBlocks*blockSize+totalBlocks2*blockSize2;
        allavailableBlock=availableBlocks*blockSize+availableBlocks2*blockSize2;
        alltot= Formatter.formatFileSize(this, alltotalblock);
        allavail= Formatter.formatFileSize(this, allavailableBlock);
        
//        LogUtil.e("总的数据：",  alltot + "\r\n"+allavail);  

        roundprogress.setStr( allavail+ "/" + alltot);//进度条下面的文字
		roundprogress.setStr_textSize(getResources().getDimension(R.dimen.round_textSize2));//进度条下面的文字 字体大小
		MaxPro=(int)(((float)allavailableBlock/alltotalblock)*100);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(progress < MaxPro){
					progress += 1;

					roundprogress.setProgress(progress);
				
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	
	}


	private void initView() {
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText("清理缓存");
		// + getCacheSize()
		clearcash = (Button) findViewById(R.id.clearcash);
		roundprogress=(RoundProgressBar)findViewById(R.id.roundprogress);
		roundprogress.setCricleColor(getResources().getColor(R.color.gray_cirle));//圆环的颜色
		roundprogress.setCricleProgressColor(getResources().getColor(R.color.huang_cirle));//圆环进度的颜色
		roundprogress.setTextColor(getResources().getColor(R.color.white));//进度百分比 颜色
		roundprogress.setTextSize(getResources().getDimension(R.dimen.round_textSize));///进度百分比字体
		roundprogress.setRoundWidth(5);//圆环宽度  圆环宽度至少为3
		
	}

	private void initListener() {
		back.setOnClickListener(this);
		clearcash.setOnClickListener(this);
	}

	private String getCacheSize() {
		String size = null;
		try {
			size = DataCleanManager.getTotalCacheSize(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	

	//方法一 api》=23不可以，在API 23版本中 SECONDARY_STORAGE 被移除
//	//内置sd卡路径
//	String sdcardPath = System.getenv("EXTERNAL_STORAGE"); 
//	//内置sd卡路径
//	String sdcardPath2 = Environment.getExternalStorageDirectory().getAbsolutePath();
//	//外置置sd卡路径
//	String extSdcardPath = System.getenv("SECONDARY_STORAGE");
//  LogUtil.e("外置SD卡路径：",  sdcardPath + "\r\n"+sdcardPath2+ "\r\n"+extSdcardPath); 
	/**
	 * 方法二
	 * 参数 is_removable为false时得到的是内置sd卡路径，为true则为外置sd卡路径。
	 */
	private static String getStoragePath(Context mContext, boolean is_removale) {

	      StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
	        Class<?> storageVolumeClazz = null;
	        try {
	            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
	            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
	            Method getPath = storageVolumeClazz.getMethod("getPath");
	            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
	            Object result = getVolumeList.invoke(mStorageManager);
	            final int length = Array.getLength(result);
	            for (int i = 0; i < length; i++) {
	                Object storageVolumeElement = Array.get(result, i);
	                String path = (String) getPath.invoke(storageVolumeElement);
	                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
	                if (is_removale == removable) {
	                    return path;
	                }
	            }
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (InvocationTargetException e) {
	            e.printStackTrace();
	        } catch (NoSuchMethodException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	        return null;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == back) {
			finish();
		} else if (v == clearcash) {// 清理缓存
			if (TextUtils.equals(getCacheSize(), "0K")) {
				ToastUtil.showToast(getApplicationContext(), "已经非常干净了！");
			} else {
				progress=0;
				try {
					DataCleanManager.clearAllCache(this);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							while(progress < MaxPro){
								progress += 1;

								roundprogress.setProgress(progress);
							
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							
						}
					}).start();
				
//					title.setText("清理缓存" + getCacheSize());
				} catch (Exception e) {
					LogUtil.e(TAG, e.toString());
				}
				
			}

		}
	}

	private boolean ExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

}
