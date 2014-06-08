package org.hailong.app.adapter;

import java.util.HashMap;
import java.util.Map;

import org.hailong.app.App;
import org.hailong.controller.ViewLayout;
import org.hailong.service.IServiceContext;
import org.hailong.service.tasks.IURLDownlinkTask;

import android.util.Log;

public class URLDataContainerAdapter extends DataContainerPageAdapter implements
		IURLDownlinkTask {

	private String _url;
	private String _urlKey;
	private Map<String,String> _queryValues;
	
	public URLDataContainerAdapter(ViewLayout viewLayout) {
		super(viewLayout);
	}

	@Override
	public String getUrl() {
		return _url;
	}
	
	public void setUrl(String url){
		_url = url;
	}

	@Override
	public String getUrlKey() {
		return _urlKey;
	}
	
	public void setUrlKey(String urlKey){
		_urlKey = urlKey;
	}

	@Override
	public Map<String, String> getQueryValues() {
		if(_queryValues == null){
			_queryValues = new HashMap<String,String>(4);
		}
		return _queryValues;
	}
	
	public void reloadData(IServiceContext context){
		super.reloadData(context);
		
		setLoading(true);
		
		try {
			context.handle(IURLDownlinkTask.class, this, 0);
		} catch (Exception e) {
			Log.e(App.TAG, Log.getStackTraceString(e));
		}
	}
	
	public void cancel(IServiceContext context){
		super.cancel(context);
		
		try {
			context.cancelHandle(IURLDownlinkTask.class, this);
		} catch (Exception e) {
			Log.e(App.TAG, Log.getStackTraceString(e));
		}
		
	}
	
	public void loadMore(IServiceContext context){
		super.loadMore(context);
		
		setLoading(true);
		
		try {
			context.handle(IURLDownlinkTask.class, this, 0);
		} catch (Exception e) {
			Log.e(App.TAG, Log.getStackTraceString(e));
		}
		
	}

}
