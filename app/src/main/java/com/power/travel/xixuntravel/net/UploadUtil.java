package com.power.travel.xixuntravel.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import android.util.Log;

/**
 * 
 * 表单 上传工具�?
 */
public class UploadUtil {
	private static final String TAG = "uploadUtil";
	private static final int TIME_OUT = 10*1000;   //超时时间
	private static final String CHARSET = "utf-8"; //设置编码
	public static final String BASE_URL = "http://www.pinhuolang.com";
	/**
	 * android上传文件到服务器
	 * @param file  �?��上传的文�?
	 * @param RequestURL  请求的rul
	 * @return  返回响应的内�?
	 */
	public static String uploadFile(Map<String, String> params, Map<String, File> files, String RequestURL)
	{
		String result = null;
		String BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
		String PREFIX = "--" , LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";   //内容类型
		
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true);  //允许输入�?
			conn.setDoOutput(true); //允许输出�?
			conn.setUseCaches(false);  //不允许使用缓�?
			conn.setRequestMethod("POST");  //请求方式
			conn.setRequestProperty("Charset", CHARSET);  //设置编码
			conn.setRequestProperty("connection", "keep-alive");   
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY); 
			
			if(files!=null)
			{
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
				  for (Map.Entry<String, File> file : files.entrySet())
		            {  
		                StringBuilder sb1 = new StringBuilder();
		                sb1.append(PREFIX);  
		                sb1.append(BOUNDARY);  
		                sb1.append(LINE_END);  
		                // name是post中传参的�?filename是文件的名称  
		                sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\"" + file.getValue() + "\"" + LINE_END);  
		                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);  
		                sb1.append(LINE_END);  
		                dos.write(sb1.toString().getBytes());  
		                InputStream is = new FileInputStream(file.getValue());
		                
		                int bytesAvailable;  
		                while ((bytesAvailable = is.available()) > 0) {  
		                    int bufferSize = Math.min(bytesAvailable, 4096);
		                    byte[] buffer = new byte[bufferSize];  
		                    int bytesRead = is.read(buffer, 0, bufferSize);  
		                    dos.write(buffer, 0, bytesRead);  
		                }  
		                  
		                is.close();  
		                dos.write(LINE_END.getBytes());  
//		                fos.write(LINEND.getBytes());  
		            }  
				
				////请求文字
				  StringBuilder sb2 = new StringBuilder();
			        for (Map.Entry<String, String> entry : params.entrySet())
			        {  
			            sb2.append(PREFIX);  
			            sb2.append(BOUNDARY);  
			            sb2.append(LINE_END);  
			            sb2.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);  
			            sb2.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);  
			            sb2.append("Content-Transfer-Encoding: 8bit" + LINE_END);  
			            sb2.append(LINE_END);  
			            sb2.append(entry.getValue());  
			            sb2.append(LINE_END);  
			        }  
			        dos.write(sb2.toString().getBytes());  
				byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应�? 200=成功
				 * 当响应成功，获取响应的流  
				 */
				int res = conn.getResponseCode();  
				Log.e(TAG, "response code:"+res);
//				if(res==200)
//				{
//					Log.e(TAG, "request success");
					InputStream input =  conn.getInputStream();
					StringBuffer sb1= new StringBuffer();
					int ss ;
					while((ss=input.read())!=-1)
					{
						sb1.append((char)ss);
					}
					result = sb1.toString();
//					Log.e(TAG, "result : "+ result);
//				}
//				else{
//					Log.e(TAG, "request error");
//				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e(TAG, "MalformedURLException"+e.toString());
			return "error";
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "IOException"+e.toString());
			return "error";
		}
		return result;
	}
}




