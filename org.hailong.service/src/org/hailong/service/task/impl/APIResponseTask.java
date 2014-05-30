package org.hailong.service.task.impl;

import java.util.Map;

import org.hailong.service.tasks.IAPIResponseTask;
import org.hailong.service.tasks.IAPITask;

public class APIResponseTask extends APITask implements IAPIResponseTask,
		IAPITask {

	private Object _resultsData;
	private Exception _exception;
	private int _statusCode;
	private Map<String,String> _headers;
	
	@Override
	public Object getResultsData() {
		return _resultsData;
	}

	@Override
	public void setResultsData(Object resultsData) {
		_resultsData = resultsData;
	}

	@Override
	public Exception getException() {
		return _exception;
	}

	@Override
	public void setException(Exception exception) {
		_exception = exception;
	}

	@Override
	public int getStatusCode() {
		return _statusCode;
	}

	@Override
	public void setStatusCode(int statusCode) {
		_statusCode = statusCode;
	}

	@Override
	public Map<String, String> getHeaders() {
		return _headers;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		_headers = headers;
	}
}
