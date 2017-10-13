package com.power.travel.xixuntravel.net;

import com.power.travel.xixuntravel.utils.LogUtil;
import com.power.travel.xixuntravel.utils.StringUtils;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;



/**
 * HttpClient Post 上传类(也可以做get用)
 *
 */
public class HttpClientPostUpload {


	static String TAG="HttpClientPost";
	// List<NameValuePair> params,
	public static String Upload(String enty, String url){
		HttpClient httpClient=new DefaultHttpClient();
		HttpPost httpPost=new HttpPost(url);
		String result= StringUtils.setBasicJSON();
		
		try {
			//HttpClient 设置超时时间
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 9000);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 9000);
			//Httplient 设置HttpEntity对象
//			HttpEntity httpEntity= new UrlEncodedFormEntity(params, "UTF-8");
			HttpEntity httpEntity2= new StringEntity(enty, "UTF-8");
			httpPost.setEntity(httpEntity2);
			//HttpPost执行ppst表单提交
			HttpResponse httpResponse =httpClient.execute(httpPost);
			
			if(httpResponse.getStatusLine().getStatusCode()==200){
				result=EntityUtils.toString(httpResponse.getEntity());
//				LogUtil.e(TAG, "result: "+result);
			}else{
				LogUtil.e(TAG, "result: "+httpResponse.getStatusLine().getStatusCode());
			}
			
		} catch (Exception e) {
			LogUtil.e(TAG,"请求数据错误原因："+ e.toString());
		}finally{
			//释放资源
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

}
