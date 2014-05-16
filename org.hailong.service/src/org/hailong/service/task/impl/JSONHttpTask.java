package org.hailong.service.task.impl;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import android.util.Log;
import org.hailong.core.JSON;
import org.hailong.service.S;

public abstract class JSONHttpTask extends BaseHttpTask{

	private Object _object;
	
	public JSONHttpTask(HttpUriRequest httpRequest) {
		super(httpRequest);
	}


	public void onBackgroundLoaded(String result){
		try {
			_object = JSON.decodeString(result);
		} catch (JSONException e) {
			Log.d(S.TAG, Log.getStackTraceString(e));
			_object = null;
		}
	}
	
	public void onLoaded(String result){
		onLoadedObject(_object);
	}
	
	abstract public  void onLoadedObject(Object result);

}
