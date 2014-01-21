package org.hailong.framework.container;

import org.hailong.framework.Rect;
import org.hailong.framework.Size;
import org.hailong.framework.datasource.DataSource;

import android.widget.Adapter;

public interface IContainerAdapter extends Adapter{

	public Rect getItemRect(int position);
	
	public Size layout(Rect rect);
	
	public String getReuseIdentifier(int position);
	
	public DataSource getDataSource();
	
	public void setDataSource(DataSource dataSource);
	
}
