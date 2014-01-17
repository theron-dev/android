package org.hailong.framework.data;

public interface IDataFetchedResultsDelegate<T extends DataObject> {

	public void onDataItemDeleted(T dataItem);
	public void onDataItemInserted(T dataItem);
	public void onDataItemUpdated(T dataItem);
	public void onDataContentChanging();
	public void onDataContentChanged();
}
