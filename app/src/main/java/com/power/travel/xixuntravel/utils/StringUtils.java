package com.power.travel.xixuntravel.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;

import com.power.travel.xixuntravel.net.HttpUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StringUtils {

	//把时间戳转换为毫秒
	public static String dateTimeMs(String str){
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
		long msTime = -1;
		try {
			msTime=simpleDateFormat.parse(str).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return String.valueOf(msTime);

	}

	public static Date getData(String cc_time){
		long data_time = Long.valueOf(cc_time);
		Date date = new Date(data_time * 1000L);
		return date;
	}

	/**
	 * 获取两个日期之间的间隔天数
	 * @return
	 */
	public static int getGapCount(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}



	/**
	 * 创建一个返回的基本json字符串
	 */
	public static String setBasicJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("status", "-999");
			json.put("info", "请检查网络并重新操作");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return String.valueOf(json);
	}

	/**
	 * 将接口请求所需参数组合成json字符串
	 */
	public static String setJSON(JSONObject clientKey) {
		JSONObject json = new JSONObject();
		try {
			json.put("data", clientKey);
			json.put("checkcode", StringUtils.md5(clientKey + HttpUrl.keystr));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return String.valueOf(json);
	}

	/**
	 * 修改用户信息组合成json
	 */
	public static JSONObject setDataJSON(String id, int i, String key,
                                         String pro, String city, String country) {
		JSONObject json = new JSONObject();
		try {
			json.put("mid", id);
			switch (i) {
			case 1:
				json.put("face", key);
				break;
			case 2:
				json.put("nickname", key);
				break;
			case 3:
				json.put("sex", key);
				break;
			case 4:
				json.put("work", key);
				break;
			case 5:
				json.put("age", key);
				break;
			case 6:
				json.put("province", pro);
				json.put("city", city);
				json.put("area", country);
				json.put("address", key);
				break;
			case 7:
				json.put("signature", key);
				break;
			default:
				break;
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return json;
	}

	/**
	 * MD5加密
	 */
	public static String md5(String inputStr) {
		String md5Str = inputStr;
		if (inputStr != null) {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			md.update(inputStr.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			md5Str = hash.toString(16);
			if ((md5Str.length() % 2) != 0) {
				md5Str = "0" + md5Str;
			}
		}
		return md5Str;
	}

	/**
	 * 时间戳转换为几天前
	 * 
	 * @param timeStr
	 */
	public static String getStandardDate(String timeStr) {

		StringBuffer sb = new StringBuffer();

		long t = Long.parseLong(timeStr);
		long time = System.currentTimeMillis() - (t * 1000);
		long mill = (long) Math.ceil(time / 1000);// 秒前

		long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

		long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

		long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

		if (day - 1 > 0) {
			sb.append(day + "天");
		} else if (hour - 1 > 0) {
			if (hour >= 24) {
				sb.append("1天");
			} else {
				sb.append(hour + "小时");
			}
		} else if (minute - 1 > 0) {
			if (minute == 60) {
				sb.append("1小时");
			} else {
				sb.append(minute + "分钟");
			}
		} else if (mill - 1 > 0) {
			if (mill == 60) {
				sb.append("1分钟");
			} else {
				sb.append(mill + "秒");
			}
		} else {
			sb.append("刚刚");
		}
		if (!sb.toString().equals("刚刚")) {
			sb.append("前");
		}
		return sb.toString();
	}

	/**
	 * 时间戳转换为时间
	 * 
	 * @param cc_time
	 * @return
	 */
	public static String getStrTimeYMD(String cc_time) {
		String re_StrTime = null;
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTimeYMD2(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTimeMonth(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM月");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTimeDay(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTimeMonthDay(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTimeMonthDaySF(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTimeMonthDay2(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;
	}

	/**
	 * 获取当前时间
	 */
	public static String getNowTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	public static Date getNowDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return curDate;
	}

	/**
	 * 计算跟当前时间的时间差
	 */
	public static String CountTime(String end) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long days = 0;

		try {
			Date d1 = df.parse(getNowTime());
			Date d2 = df.parse(getStrTimeYMD(end));
			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

			days = diff / (1000 * 60 * 60 * 24);
			// long hours = (diff - days * (1000 * 60 * 60 * 24))
			// / (1000 * 60 * 60);
			// long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
			// * (1000 * 60 * 60))
			// / (1000 * 60);
		} catch (Exception e) {
		}
		return String.valueOf(days);
	}

	/**
	 * date转Sting
	 */
	public static String ConverToString(Date date) {
		DateFormat df = new SimpleDateFormat("d");// 日 01 变成 1
		return df.format(date);
	}

	public static String ConverToString2(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");//
		return df.format(date);
	}

	/**
	 * 把字符串转为日期
	 */
	public static Date ConverToDate(String strDate) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.parse(strDate);
	}

	/**
	 * 获取网络视频第一帧图片
	 */

	public static Bitmap createVideoThumbnail(final String url,
                                              final int width, final int height) {
		Bitmap bitmap2 = null;
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 12) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} else {
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		if(!bitmap.isRecycled()){
			bitmap.recycle();   //回收图片所占的内存
		}
		try {
			bitmap2 = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		} catch (Exception e) {
			LogUtil.e("MyTravelAdapter", "内存溢出"+e.toString());
		}
		

		return bitmap2;
	}

}
