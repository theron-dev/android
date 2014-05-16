package org.hailong.service.task.impl;


import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.hailong.service.tasks.IHttpTask;

public abstract class BaseHttpTask extends AbstractHttpTask<String> implements IHttpTask<String> ,ResponseHandler<String>{

	
	public BaseHttpTask(HttpUriRequest httpRequest){
		super(httpRequest);
	}
	
	@Override
	public HttpUriRequest getHttpRequest() {
		return httpRequest;
	}

	public ResponseHandler<String> getResponseHandler() {
		return this;
	}

	public String handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
		HttpEntity entity = response.getEntity();
	
		boolean isGzip = false;

		String charset = EntityUtils.getContentCharSet(entity);
		
		if(charset == null){
			charset = "utf-8";
		}
		
		Header h = entity.getContentEncoding();
		
		if(h != null){
			isGzip = h.getValue().indexOf("gzip") >=0;
		}
	
		InputStream in = entity.getContent();
		
		if(in != null){
			
			if(isGzip){
				in = new GZIPInputStream(in);
			}
			
			ByteArrayBuffer bt= new ByteArrayBuffer(4096);
			
			byte[] buf = new byte[4096];
			
			int len;
			
			while((len = in.read(buf)) >0 ){
				
				bt.append(buf, 0, len);
				
			}
			
			in.close();
			
			return new String(bt.toByteArray(),charset);
		}
		
		return null;
	}


}
