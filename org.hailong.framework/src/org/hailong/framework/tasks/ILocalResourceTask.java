package org.hailong.framework.tasks;

import java.io.File;

public interface ILocalResourceTask {
	/**
	 * 获取资源URL
	 * @return
	 */
	public String getResourceUri();
	/**
	 * 对资源localUri操作
	 * @param localUri
	 */
	public Object setResourceLocalFile(File localUri);
	
	/**
	 * 
	 * @param obj 缓存的资源对象
	 */
	public void setResourceObject(Object obj);
}
