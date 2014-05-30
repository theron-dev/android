package org.hailong.service.task.impl;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.hailong.service.tasks.IAPIRequestTask;
import org.hailong.service.tasks.IAPITask;

public class APIRequestTask extends APITask implements IAPIRequestTask, IAPITask {

	private String _apiKey;
	private String _apiUrl;
	private Map<String,String> _queryValues;
	private Map<String,String> _headers;
	private HttpEntity _httpEntity;
	
	@Override
	public String getApiKey() {
		return _apiKey;
	}

	@Override
	public void setApiKey(String apiKey) {
		_apiKey = apiKey;
	}

	@Override
	public String getApiUrl() {
		return _apiUrl;
	}

	@Override
	public void setApiUrl(String apiUrl) {
		_apiUrl = apiUrl;
	}

	@Override
	public Map<String, String> getQueryValues() {
		return _queryValues;
	}

	@Override
	public void setQueryValues(Map<String, String> queryValues) {
		_queryValues = queryValues;
	}

	@Override
	public Map<String, String> getHeaders() {
		return _headers;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		_headers = headers;
	}

	@Override
	public HttpEntity getEntity() {
		return _httpEntity;
	}

	@Override
	public void setEntity(HttpEntity entity) {
		_httpEntity = entity;
	}


}
