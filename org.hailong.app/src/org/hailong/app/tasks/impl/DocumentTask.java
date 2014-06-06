package org.hailong.app.tasks.impl;

import org.hailong.app.tasks.IDocumentTask;
import org.hailong.app.tasks.IDocumentTaskListener;
import org.hailong.core.URL;
import org.hailong.dom.DOMBundle;
import org.hailong.service.task.impl.Task;

public class DocumentTask extends Task implements IDocumentTask {

	private boolean _allowCached;
	private URL _documentURL;
	private IDocumentTaskListener _listener;
	private DOMBundle _bundle;
	private boolean _disabledRemoteLoad;
	
	@Override
	public boolean isAllowCached() {
		return _allowCached;
	}
	
	public void setAllowCached(boolean allowCached){
		_allowCached = allowCached;
	}

	@Override
	public URL getDocumentURL() {
		return _documentURL;
	}
	
	public void setDocumentURL(URL documentURL){
		_documentURL = documentURL;
	}

	@Override
	public IDocumentTaskListener getListener() {
		return _listener;
	}

	public void setListener(IDocumentTaskListener listener){
		_listener = listener;
	}

	@Override
	public DOMBundle getBundle() {
		return _bundle;
	}
	
	public void setBundle(DOMBundle bundle){
		_bundle = bundle;
	}

	@Override
	public boolean isDisabledRemoteLoad() {
		return _disabledRemoteLoad;
	}
	
	public void setDisabledRemoteLoad(boolean disabledRemoteLoad){
		_disabledRemoteLoad = disabledRemoteLoad;
	}
}
