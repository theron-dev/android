package org.hailong.service.tasks;

public interface IDownlinkPageTask extends IDownlinkTask {

	public int getPageIndex();

	public int getPageSize();
	
	public int getOffset();
	
}
