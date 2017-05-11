package com.king.geomodel.utils.uploadutils;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class PalUploadThread implements Runnable {

	private final String TAG = "PalUploadThread";

	//private Context context;
	private Handler handler;
	
//	private String PicPath;
	private Map<String,String> params ;
	private ArrayList<FormFile> formList ; 
	private String requestUrl;
	public PalUploadThread(Context con, Handler handle, Map<String,String> pm, ArrayList<FormFile> fl,String postUrl) {
	//	context = con;
		handler = handle;	
		params = pm;
		formList = fl;
		this.requestUrl = postUrl;
	}

	/**
	 * 0 正常 1 json异常 2 web返回错误 3网络异常 4未知异常
	 * 
	 * @param flag
	 */
	private void sendMsg(int flag, String JsonUrl) {
		Message msg = new Message();
		msg.what = flag;
		//if (flag == 0) {
		Bundle b = new Bundle();
		b.putString("strRet", JsonUrl);
		msg.setData(b);
		//}
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
		try {
			Looper.prepare();
			// 初始化
			postData();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendMsg(-1, "{\"errorcode\":-1,\"errormsg\":\"jsonException\"}");
			// LogWzy.d(TAG, "jsonException_" + e.toString());
//			Toast.makeText(context, "jsonException!", Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			// LogWzy.d(TAG, "4未知异常_" + ex.toString());
			sendMsg(-1, "{\"errorcode\":-1,\"errormsg\":\"41未知异常\"}");
//			Toast.makeText(context, "未知异常!", Toast.LENGTH_SHORT).show();
		} finally {
			Looper.loop();
		}
	}
	
	private void postData() throws JSONException {
		Log.i(TAG, "upload start");
		try {
 
			String strResult = "0";
			if(formList != null){
				strResult = HttpRequester.post(requestUrl, params, formList);

			}else{
				strResult = HttpRequester.postFromHttpClient(requestUrl, params, "UTF-8");
			}
			
			sendMsg(0, strResult);
			
			Log.d(TAG, "上传返回：" + strResult);
		} catch (Exception e) {
			Log.i(TAG, "upload error" + e.toString());
			sendMsg(-1, "{\"errorcode\":-1,\"errormsg\":\"网络异常\"}");
			e.printStackTrace();
		}
		Log.i(TAG, "upload end");
	}
}
