package org.hailong.framework.tasks;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.framework.ITask;

public interface IHttpRequestTask<T> extends ITask {
	public HttpUriRequest getHttpRequest();
	public ResponseHandler<T> getResponseHandler();
	public void sendFinishMessage(T result,Object waiter);
	public void sendErrorMessage(Exception ex,Object waiter);
}
