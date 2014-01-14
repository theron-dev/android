package org.hailong.framework.tasks;

import java.io.File;

import org.hailong.framework.ITask;

public interface IResourceTask extends ITask {
	/**
	 * 获取资源URL
	 * @return
	 */
	public String getResourceUri();
	/**
	 * 判断是否能够下载
	 * @return
	 */
	public boolean isNeedDownload();
	/**
	 * 是否强制下载
	 * @return
	 */
	public boolean isForceDownload();
	/**
	 * 对资源localUri操作
	 * @param localUri
	 * @return 缓存对象
	 */
	public Object setResourceLocalFile(File localUri);
	
	/**
	 * 
	 * @param obj 缓存的资源对象
	 */
	public void setResourceObject(Object obj);
	
	/**
	 * 异常
	 * @param ex
	 */
	public void onException(Exception ex);
	
}