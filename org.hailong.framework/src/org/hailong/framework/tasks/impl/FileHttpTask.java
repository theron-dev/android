package org.hailong.framework.tasks.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

public abstract class FileHttpTask extends AbstractHttpTask<File> implements  ResponseHandler<File>{

	private File _file;
	
	public File getFile(){
		return _file;
	}
	
	public FileHttpTask(HttpUriRequest httpRequest,File file){
		super(httpRequest);
		_file = file;
	}
	

	public ResponseHandler<File> getResponseHandler() {
		return this;
	}


	public File handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
		InputStream in =  response.getEntity().getContent();
		
		if(in != null){
			FileOutputStream outputStream = new FileOutputStream(_file);
			byte[] buffer = new byte[102400];
			int length = in.read(buffer);
			while(length >0){
				outputStream.write(buffer, 0, length);
				length = in.read(buffer);
			}
			outputStream.close();
			return _file;
		}
		return null;
	}

}
