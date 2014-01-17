package org.hailong.framework.tasks;

import java.util.Map;

public interface IURLDownlinkTask extends IDownlinkPageTask{

	public String getUrl();
	
	public String getUrlKey();
	
	public Map<String,String> getQueryValues();
	
}
