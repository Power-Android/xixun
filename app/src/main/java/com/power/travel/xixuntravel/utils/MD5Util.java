package com.power.travel.xixuntravel.utils;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MD5Util {

	private static final String TAG = MD5Util.class.getSimpleName();
	private static Map<String, MessageDigest> digests = new ConcurrentHashMap<String, MessageDigest>();

	public static boolean isNullorEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String serializeArray(String[] array, char split) {
		return serializeCollection(Arrays.asList(array), ',');
	}

	public static String serializeArray(String[] array, char split,
                                        String prefix, String subfix) {
		return serializeCollection(Arrays.asList(array), ',', prefix, subfix);
	}

	public static String serializeCollection(Collection<String> array,
                                             char split) {
		return serializeCollection(array, split, "", "");
	}

	public static String serializeCollection(Collection<String> array,
                                             char split, String prefix, String subfix) {
		StringBuilder sb = new StringBuilder();
		for (String str : array) {
			sb.append(prefix).append(str).append(subfix).append(split);
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String MD5(String data) {
		try {
			if (!MD5Util.isNullorEmpty(data)) {
				String md5 = hash(data.getBytes("UTF-8"), "MD5");
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		return data;
	}

	public static String hash(byte[] bytes, String algorithm) {
		synchronized (algorithm.intern()) {
			MessageDigest digest = digests.get(algorithm);
			if (digest == null) {
				try {
					digest = MessageDigest.getInstance(algorithm);
					digests.put(algorithm, digest);
				} catch (NoSuchAlgorithmException nsae) {
					return null;
				}
			}
			// Now, compute hash.
			digest.update(bytes);
			return encodeHex(digest.digest());
		}
	}

	public static String encodeHex(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	public static String subString(String str, int size) {
		if (str.length() <= size) {
			return str;
		}
		return str.substring(0, size) + "...";
	}

	//
	public static void copy(String content, Context context) {
		//
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}
}
