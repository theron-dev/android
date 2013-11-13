package org.hailong.framework.data;

import java.util.List;


public interface IDataFetchedResults<T extends DataItem> {

	public T getFetchedDataItem(int index);
	
	public int getFetchedDataItemCount();
	
	public List<T> getFetchedDataItems();
	
	public DataFetchRequest<T> getFetchRequest();
	
	public IDataFetchedResultsDelegate<T> getDelegate();
	
	public void setDelegate(IDataFetchedResultsDelegate<T> delegate);
	
	public void destroy();
}
