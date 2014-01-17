package org.hailong.framework.datasource;

import java.util.HashMap;
import java.util.Map;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.tasks.IURLDownlinkTask;

import android.util.Log;

public class URLDataSource extends PageDataSource implements IURLDownlinkTask {

	private String _url;
	private String _urlKey;
	private Map<String,String> _queryValues;
	
	public URLDataSource(IServiceContext context) {
		super(context);
		
	}

	public String getUrl() {
		return _url;
	}
	
	public void setUrl(String url){
		_url = url;
	}

	public String getUrlKey() {
		return _urlKey;
	}
	
	public void setUrlKey(String urlKey){
		_urlKey = urlKey;
	}

	public Map<String, String> getQueryValues() {
		if(_queryValues == null){
			_queryValues = new HashMap<String,String>(4);
		}
		return _queryValues;
	}
	
	public void reloadData(){
		super.reloadData();
		
		try {
			getContext().handle(IURLDownlinkTask.class, this, 0);
		} catch (Exception e) {
			Log.d(Framework.TAG, Log.getStackTraceString(e));
		}
	}
	
	public void loadMoreData(){
		super.loadMoreData();
		
		try {
			getContext().handle(IURLDownlinkTask.class, this, 0);
		} catch (Exception e) {
			Log.d(Framework.TAG, Log.getStackTraceString(e));
		}
	}

}
