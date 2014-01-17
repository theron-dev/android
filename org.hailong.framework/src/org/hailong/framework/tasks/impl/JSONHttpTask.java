package org.hailong.framework.tasks.impl;

import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.framework.Framework;
import org.hailong.framework.JSON;
import org.json.JSONException;

import android.util.Log;



public abstract class JSONHttpTask extends BaseHttpTask{

	public JSONHttpTask(HttpUriRequest httpRequest) {
		super(httpRequest);
	}


	public void onFinish(String result){
		try {
			onFinishObject(JSON.decodeString(result));
		} catch (JSONException e) {
			Log.d(Framework.TAG, Log.getStackTraceString(e));
			onFinishObject(null);
		}
	}
	
	abstract public  void onFinishObject(Object result);

}
