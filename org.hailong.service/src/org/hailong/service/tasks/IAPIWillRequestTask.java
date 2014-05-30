package org.hailong.service.tasks;

import java.util.Map;

import org.apache.http.HttpEntity;

public interface IAPIWillRequestTask extends IAPITask{
	
	public String getApiKey();
	
	public void setApiKey(String apiKey);
	
	public String getApiUrl();
	
	public void setApiUrl(String apiUrl);
	
	public Map<String,String> getQueryValues();
	
	public void setQueryValues(Map<String,String> queryValues);
	
	public Map<String,String> getHeaders();
	
	public void setHeaders(Map<String,String> headers);
	
	public HttpEntity getEntity();
	
	public void setEntity(HttpEntity entity);
	
}
