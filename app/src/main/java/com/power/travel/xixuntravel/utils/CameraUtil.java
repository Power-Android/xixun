package com.power.travel.xixuntravel.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;

public class CameraUtil {

	/**
	 * 计算图片的缩放值ֵ
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;// 图片的长宽不变

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;

	}

	/**
	 * ���·�����ͼƬ��ѹ������bitmap
	 * 
	 * @param filePath
	 * @return BitmapFactory���Դ�һ��ָ���ļ��У�����decodeFile()���Bitmap��Ҳ���Դ��Զ����ͼƬ��Դ�У�
	 *         ����decodeResource()���Bitmap Options�����¼������ԣ�����ָ��decode��ѡ��
	 *         .inSampleSize ����decodeʱ�����ű���
	 *         .injustDecodeBounds�������Ϊtrue���������ͼ��������ȫ���룬�༴decodeXyz()����ֵΪnull��
	 *         ����Options��outAbc�н����ͼ��Ļ���Ϣ .inPreferredConfig
	 *         ָ��decode���ڴ��У��ֻ�������õı��룬��ѡֵ������Bitmap.Config�С� ȱʡֵ��ARGB_8888
	 */
	public static Bitmap getSmallBitmap(String filePath) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// ֻ���ߣ���������
		BitmapFactory.decodeFile(filePath, options);// ��ͼƬת��Ϊbitmap
		
		options.inSampleSize = calculateInSampleSize(options, 350, 350);//���ò�����
		options.inJustDecodeBounds = false;
		options.inInputShareable = true;//����ϵͳ�ڴ治��ʱ��ͼƬ�Զ������� 
		return BitmapFactory.decodeFile(filePath, options);

		/*
		 * ������inJustDecodeBounds= true������decodeFile()�õ�ͼ��Ļ���Ϣ��
		 * ����ͼ��Ŀ�ȣ����߸߶ȣ����ۺϣ��Լ�Ŀ��Ŀ�ȣ��õ�inSampleSizeֵ�� ������inJustDecodeBounds=
		 * false������decodeFile()�õ������ͼ����ݡ�
		 * �Ȼ�ȡ�����ٶ�����ݣ���������������С��ͼ��������Ľ�Լ������Դ����ʱ�򻹻�������������ͼ����Ч��͸������ˡ�
		 */
	}
	
	/**
	 * ��ȡ����ͼƬ��Ŀ¼
	 * 
	 * @return
	 */

	public static File getAlbumDir() {
		// Environment.getExternalStorageDirectory() ���sd��·��
		File dir = new File(Environment.getExternalStorageDirectory(),
				getAlbunName());
		if (!dir.exists()) {//�ļ��в����� ɾ���ļ���
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * ��ȡ����ͼƬ���ļ������
	 * 
	 * @return
	 */
	private static String getAlbunName() {

		return "xizang";
	}

}
