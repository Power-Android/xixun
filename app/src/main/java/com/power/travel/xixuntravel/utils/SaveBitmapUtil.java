package com.power.travel.xixuntravel.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class SaveBitmapUtil {

	private final static String TAG = "SaveBitmapUtil";

	/**
	 * 保存图片规定图片的大小<120否则进行压缩但是做多进行3次压缩 并且返回加水印的Bitmap
	 * 
	 * @param path
	 */
	@SuppressWarnings("unused")
	public static String SaveBitmap(Context context, String path, String imaName) {
		Bitmap localBitmap = null;
		File image = null;
		if (path != null) {

			try {
				/*
				 * 获取相机返回的数据，并转换为Bitmap图片格式
				 */
				// 防止重复选择图片，内存使用过多
				if (localBitmap != null) {
					localBitmap.recycle();
				}
				localBitmap = CameraUtil.getSmallBitmap(path);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();// 字节数组流
				/*
				 * bitmap.compress(format, quality, stream) 第一个参数format为压缩的格式
				 * 可设置JPEG或PNG格式； 第二个参数quality为图像压缩比的值,0-100.0
				 * 意味着图片压缩率,100意味着高质量压缩 压缩率为0 第三个参数stream为输出流
				 */
				localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				/*
				 * 将图片写入CameraUtil.getAlbumDir()文件夹下，并重命名 new
				 * File(CameraUtil.getAlbumDir
				 * (),file.getName())包含了File的构造方法，指定了文件目录以及文件名称的实例化File对象
				 */
				image = new File(CameraUtil.getAlbumDir(), imaName);
				FileOutputStream fos = new FileOutputStream(image);
				int options = 100;
				/*
				 * baos.toByteArray()获得内循环充中的数据 如果大于120kb则再次压缩，做多压缩三次
				 */
				while (baos.toByteArray().length / 1024 > 100 && options != 10) {
					baos.reset();// reset()重新定位
					/*
					 * 压缩图片为 JPEG模式 ；压缩率0；输出流对象baos
					 */
					localBitmap.compress(Bitmap.CompressFormat.JPEG, options,
							baos);
					options -= 30;
				}
				fos.write(baos.toByteArray());
				fos.close();
				fos.flush();
				fos.close();// 关闭文件输出流 先打开的后关闭
				if (!localBitmap.isRecycled()) {
					localBitmap.recycle();
					Log.v("TAG", "保存图片释放biemap");
				}
			} catch (Exception e) {
			}

		} else {
			Log.e(TAG, "path == null");
		}
		return image.getAbsolutePath();
	}//

}
