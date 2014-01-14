package org.hailong.framework.tasks.impl;


import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.hailong.framework.tasks.IHttpTask;

public class BaseHttpRequestTask extends AbstractHttpRequestTask<String> implements IHttpTask<String> {

	
	public BaseHttpRequestTask(HttpUriRequest httpRequest){
		super(httpRequest);
	}
	
	@Override
	public HttpUriRequest getHttpRequest() {
		return httpRequest;
	}

	public ResponseHandler<String> getResponseHandler() {
		return new BasicResponseHandler();
	}

	
	@Override
	public void onFinish(String result){

	}
	
	@Override
	public void onException(Exception ex){
		
	}

}
