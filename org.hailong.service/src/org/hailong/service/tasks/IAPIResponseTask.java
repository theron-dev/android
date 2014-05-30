package org.hailong.service.tasks;

import java.util.Map;

public interface IAPIResponseTask extends IAPITask {

	public Object getResultsData();
	
	public void setResultsData(Object resultsData);
	
	public Exception getException();
	
	public void setException(Exception exception);
	
	public int getStatusCode();
	
	public void setStatusCode(int statusCode);
	
	public Map<String,String> getHeaders();
	
	public void setHeaders(Map<String,String> headers);
}
