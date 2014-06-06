package org.hailong.app.tasks;

import org.hailong.core.URL;
import org.hailong.dom.DOMBundle;
import org.hailong.service.ITask;

public interface IDocumentTask extends ITask {

	/**
	 * 允许从缓存加载
	 * @return
	 */
	public boolean isAllowCached();
	
	/**
	 * 禁止从网络加载
	 * @return
	 */
	public boolean isDisabledRemoteLoad();
	
	public URL getDocumentURL();
	
	public DOMBundle getBundle();
	
	public IDocumentTaskListener getListener();
	
}
