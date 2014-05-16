package org.hailong.service.tasks;

import java.util.Map;

public interface IURLDownlinkTask extends IDownlinkPageTask{

	public String getUrl();
	
	public String getUrlKey();
	
	public Map<String,String> getQueryValues();
	
}
