package org.hailong.framework.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hailong.framework.Framework;
import org.hailong.framework.data.annotation.DataEntity;
import org.hailong.framework.data.annotation.DataField;
import org.hailong.framework.data.annotation.DataFieldType;

import android.database.Cursor;
import android.util.Log;

class DataEntityContext {
	
	private final static String RAWID = "rawid";

	private List<DataEntityRawDataImpl> _insertedRawDatas;
	private Map<Long,DataEntityRawDataImpl> _rawDatas;
	private List<DataFetchedResultsImpl<?>> _fetchedResults;
	private IDataStorage _dataStorage;
	private DataModel _dataModel;
	
	public DataEntityContext(IDataStorage dataStorage,DataModel dataModel){
		_dataStorage = dataStorage;
		_dataModel = dataModel;
		_rawDatas = null;
		_fetchedResults = null;
	}
	
	public void destroy(){
		if(_rawDatas !=null){
			for(DataEntityRawDataImpl data : _rawDatas.values().toArray(new DataEntityRawDataImpl[_rawDatas.size()])){
				data.destroy();
			}
		}
		
		if(_fetchedResults !=null){
			for(DataFetchedResultsImpl<?> result : _fetchedResults.toArray(new DataFetchedResultsImpl<?>[_fetchedResults.size()])){
				result.destroy();
			}
		}
		if(_dataStorage !=null){
			_dataStorage.destroy();
		}
		_rawDatas = null;
		_fetchedResults = null;
		_dataStorage = null;
		_dataModel= null;
	}
	
	public boolean hasChange(){
		if(_insertedRawDatas !=null && _insertedRawDatas.size() >0){
			return true;
		}
		if(_rawDatas != null){
			for(DataEntityRawDataImpl data : _rawDatas.values()){
				if(data.isDeleted() || data.isInserted() || data.hasChange()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void save() throws DataException{
		
	
		try{

			Set<DataEntityRawDataImpl> datas = new HashSet<DataEntityRawDataImpl>();
			
			if(_insertedRawDatas !=null){
				for(DataEntityRawDataImpl data : _insertedRawDatas){
					if(data.isInserted()){
						data.setRawId( _dataStorage.insert(data.getDataEntity(),data._values));
					}
					datas.add(data);
				}
			}
			
			
			if(_rawDatas !=null){
				for(DataEntityRawDataImpl data : _rawDatas.values()){
					if(data.isDeleted() || data.isInserted()  || data.hasChange()){
						if(data.isDeleted()){
							_dataStorage.delete(data.getDataEntity(), data.getRawId());
						}
						else if(data.hasChange()){
							_dataStorage.update(data.getDataEntity(), data.getRawId(), data.getChangeValues());
						}
						datas.add(data);
					}
				}
			}
			
			_dataStorage.commitTransaction();
			
			List<DataFetchedResultsImpl<?>> results = new ArrayList<DataFetchedResultsImpl<?>>();
			
			if(_fetchedResults != null && _fetchedResults.size() >0){
				for(DataFetchedResultsImpl<?> result : _fetchedResults){
					if(result.beginChange(datas)){
						results.add(result);
					}
				}
			}
			
			for(DataEntityRawDataImpl data : datas){
				if(data.isDeleted()){
					_rawDatas.remove(data.getRawId());
				}
				else if(data.isInserted()){
					if(_rawDatas == null){
						_rawDatas = new HashMap<Long,DataEntityRawDataImpl>(4);
					}
					_rawDatas.put(data.getRawId(), data);
					if(_insertedRawDatas!=null){
						_insertedRawDatas.remove(data);
					}
				}
				data.finishChange();
				if(data.retainCount() <=0){
					_rawDatas.remove(data.getRawId());
				}
			}
			
			for(DataFetchedResultsImpl<?> result : results){
				result.endChange();
			}
			
		}
		catch(Exception ex){
			throw new DataException(ex);
		}
	}
	
	public <T extends DataItem> T insertDataItem(Class<T> dataItemClass) throws DataException{
		DataEntity dataEntity = dataItemClass.getAnnotation(DataEntity.class);
		DataEntityRawDataImpl rawData = new DataEntityRawDataImpl(dataEntity,0,null);
		rawData.setInserted(true);
		if(_insertedRawDatas == null){
			_insertedRawDatas = new ArrayList<DataEntityRawDataImpl>();
		}
		_insertedRawDatas.add(rawData);
		return newDataItem(dataItemClass,rawData);
	}
	
	private static Object getDataFieldValue(DataField dataField,Cursor cursor,int column) throws StreamCorruptedException, IOException, ClassNotFoundException{
		DataFieldType fieldType = dataField.type();
		if(fieldType == DataFieldType.BIGINT){
			return cursor.isNull(column) ? 0: cursor.getLong(column);
		}
		else if(fieldType == DataFieldType.INT){
			return cursor.isNull(column) ? 0:cursor.getInt(column);
		}
		else if(fieldType == DataFieldType.DOUBLE){
			return cursor.isNull(column) ? 0:cursor.getDouble(column);
		}
		else if(fieldType == DataFieldType.VARCHAR || fieldType == DataFieldType.TEXT){
			return cursor.isNull(column) ? null:cursor.getString(column);
		}
		else if(fieldType == DataFieldType.BYTES){
			return cursor.isNull(column) ? null:cursor.getBlob(column);
		}
		else if(fieldType == DataFieldType.OBJECT){
			if(!cursor.isNull(column)){
				byte[] bytes = cursor.getBlob(column);
				if(bytes !=null){
					ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
					return objectInputStream.readObject();
				}
			}
		}
		return null;
	}
	
	public <T extends DataItem> List<T> executeFetchRequest(DataFetchRequest<T> fetchRequest,int batchSize) throws DataException{
		
		List<T> items = new ArrayList<T>(4);
		
		Cursor cursor = _dataStorage.query(fetchRequest);
		
		if(cursor!=null){
			
			DataEntity dataEntity = fetchRequest.getDataEntity();
			
			DataField[] fields = dataEntity.fields();
			
			Map<String,DataField> dataFields = new HashMap<String,DataField>(4);
			if(fields !=null){
				for(DataField field : fields){
					dataFields.put(field.value(), field);
				}
			}

			Class<T> itemClass = _dataModel.getDataItemClass(dataEntity);
			int index = 0;
			Map<String,Object> values ;
			long rawId = 0;
			int columnCount,i;
			String name;
			DataEntityRawDataImpl rawData ;
			try{
				while(cursor.moveToNext()){
					if(index < batchSize){
						values = new HashMap<String,Object>();
					}
					else{
						values = null;
					}
					columnCount = cursor.getColumnCount();
					for(i=0;i<columnCount;i++){
						name = cursor.getColumnName(i);
						if(RAWID.equals(name)){
							rawId = cursor.getLong(i);
							if(index >= batchSize){
								break;
							}
						}
						else{
							if( index < batchSize){
								DataField dataField = dataFields.get(name);
								if(dataField !=null){
									try{
										values.put(name, getDataFieldValue(dataField,cursor,i));
									}
									catch(Throwable ex){
										Log.d("DataContext", Log.getStackTraceString(ex));
									}
								}
							}
						}
					}
					if(_rawDatas ==null){
						_rawDatas = new HashMap<Long,DataEntityRawDataImpl>(4);
					}
					rawData = _rawDatas.get(rawId);
					if(_rawDatas.containsKey(rawId)){
						rawData = _rawDatas.get(rawId);
					}
					else{
						rawData =  new DataEntityRawDataImpl(dataEntity,rawId,values);
						_rawDatas.put(rawId, rawData);
					}
					
					if(!rawData.isDeleted()){
						items.add(newDataItem(itemClass, rawData));
						index ++;
					}
				}
				
				if(_insertedRawDatas != null){
					for(DataEntityRawDataImpl data : _insertedRawDatas){
						if(fetchRequest.filter(data)){
							items.add(newDataItem(itemClass, data));
						}
					}
				}
			}
			catch(Exception ex){
				throw new DataException(ex);
			}
			finally{
				cursor.close();
			}
		}
		return items;
	}
	
	/**
	 * 
	 * @param fetchRequest
	 * @param batchSize  
	 * @return
	 * @throws DataException 
	 */
	public <T extends DataItem> IDataFetchedResults<T> executeFetchRequestResults(DataFetchRequest<T> fetchRequest,int batchSize) throws DataException{
		int index = 0;
		DataFetchedResultsImpl<T> fetchedResult = new DataFetchedResultsImpl<T>(fetchRequest);
		if(_fetchedResults == null){
			_fetchedResults = new ArrayList<DataFetchedResultsImpl<?>>();
		}
		
		_fetchedResults.add(fetchedResult);
		
		Cursor cursor = _dataStorage.query(fetchRequest);
		if(cursor!=null){
			
			DataEntity dataEntity = fetchRequest.getDataEntity();
			
			DataField[] fields = dataEntity.fields();
			
			Map<String,DataField> dataFields = new HashMap<String,DataField>(4);
			if(fields !=null){
				for(DataField field : fields){
					dataFields.put(field.value(), field);
				}
			}
			
			
			Map<String,Object> values ;
			long rawId = 0;
			long time = 0;
			int columnCount,i;
			String name;
			DataEntityRawDataImpl rawData ;
			try{
				while(cursor.moveToNext()){
					time = System.currentTimeMillis();
					if(index < batchSize){
						values = new HashMap<String,Object>();
					}
					else{
						values = null;
					}
					columnCount = cursor.getColumnCount();
					for(i=0;i<columnCount;i++){
						name = cursor.getColumnName(i);
						if(RAWID.equals(name)){
							rawId = cursor.getLong(i);
							if(index >= batchSize){
								break;
							}
						}
						else{
							if( index < batchSize){
								DataField dataField = dataFields.get(name);
								if(dataField !=null){
									try{
										values.put(name, getDataFieldValue(dataField,cursor,i));
									}
									catch(Throwable ex){
										Log.d(Framework.TAG, Log.getStackTraceString(ex));
									}
								}
							}
						}
					}
					if(_rawDatas ==null){
						_rawDatas = new HashMap<Long,DataEntityRawDataImpl>(4);
					}

					if(_rawDatas.containsKey(rawId)){
						rawData = _rawDatas.get(rawId);
					}
					else{
						rawData =  new DataEntityRawDataImpl(dataEntity,rawId,values);
						_rawDatas.put(rawId, rawData);
					}
					
					if(!rawData.isDeleted()){
						fetchedResult.addRawData(rawData);
						index ++;
					}
					
					Log.d(Framework.TAG, "executeFetchRequestResults "+index+": "+ String.valueOf(System.currentTimeMillis() - time));
					
				}
				
				if(_insertedRawDatas != null){
					for(DataEntityRawDataImpl data : _insertedRawDatas){
						if(fetchRequest.filter(data)){
							fetchedResult.addRawData(data);
						}
					}
				}
			}
			catch(Exception ex){
				fetchedResult.destroy();
				throw new DataException(ex);
			}
			finally{
				cursor.close();
			}
		}
		return fetchedResult;
	}
	
	public boolean delete(DataItem dataItem){
		IDataEntityRawData rawData = dataItem.getRawData();
		if(rawData instanceof DataEntityRawDataImpl){
			((DataEntityRawDataImpl)rawData).setDeleted(true);
			return true;
		}
		return false;
	}
	
	private <T extends DataItem> T newDataItem(Class<T> dataItemClass, DataEntityRawDataImpl rawData) throws DataException{
		try {
			T data = dataItemClass.newInstance();
			data.setRawData(rawData);
			return data;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
	
	private class DataEntityRawDataImpl implements IDataEntityRawData{

		private DataEntity _dataEntity;
		private long _rawId;
		private Map<String,Object> _values;
		private Map<String,Object> _changeValues;
		private boolean _isDeleted;
		private boolean _isInserted;
		private int _retainCount;
		
		public DataEntityRawDataImpl(DataEntity dataEntity ,long rawId,Map<String,Object> values){
			_dataEntity = dataEntity;
			_rawId = rawId;
			_values = values;
			_retainCount = 0;
		}
		
		public long getRawId() {
			return _rawId;
		}
		
		public void setRawId(long rawId){
			_rawId = rawId;
		}

		public boolean hasChange() {
			return _changeValues != null && _changeValues.size() >0 ;
		}

		public Map<String,Object> getChangeValues(){
			return _changeValues;
		}
		
		public boolean isDeleted() {
			return _isDeleted;
		}
		
		public void setDeleted(boolean deleted){
			_isDeleted = deleted;
		}
		
		public boolean isInserted(){
			return _isInserted;
		}
		
		public void setInserted(boolean isInserted){
			_isInserted = isInserted;
		}

		public Object getValue(DataField dataField) {
			if(dataField != null){
				String name = dataField.value();
				Object value = null;
				
				if(_changeValues !=null){
					value = _changeValues.get(name);
				}
				
				if(value == null){
					if(_values == null && !_isInserted && !_isDeleted){
						_values = _dataStorage.get(_dataEntity, _rawId);
					}
					if(_values != null){
						value = _values.get(name);
					}
				}
				
				return value;
			}

			return null;
		}

		public void setValue(DataField dataField, Object value) {
			if(dataField !=null && !_isDeleted){
				if(_isInserted){
					if(_values == null){
						_values = new HashMap<String,Object>();
					}
					_values.put(dataField.value(), value);
				}
				else{
					if(_changeValues == null){
						_changeValues = new HashMap<String,Object>();
					}
					_changeValues.put(dataField.value(), value);
				}
				
			}
		}
		
		public void destroy(){
			if(_rawDatas !=null){
				_rawDatas.remove(_rawId);
			}
			if(_insertedRawDatas !=null){
				_insertedRawDatas.remove(this);
			}
			_rawId = 0;
			_dataEntity = null;
			_values = null;
			_changeValues = null;
			
		}
		
		
		public DataEntity getDataEntity(){
			return _dataEntity;
		}
		
		public void finishChange(){
			if(_isInserted){
				_isInserted = false;
			}
			
			if(_isDeleted){
				_changeValues = null;
				_values = null;
			}
		
			if(_changeValues!=null){
				if(_values == null){
					_values = new HashMap<String,Object>(4);
				}
				for(String key :_changeValues.keySet()){
					_values.put(key, _changeValues.get(key));
				}
				_changeValues = null;
			}
		}
		
		@Override
		public int hashCode(){
			if(_rawId == 0){
				return super.hashCode();
			}
			return (int)_rawId;
		}
		
		@Override
		public boolean equals(Object obj){
			IDataEntityRawData rawData = (IDataEntityRawData)obj;
			if(_rawId ==0 && rawData.getRawId() ==0){
				return super.equals(obj);
			}
			
			return _rawId == rawData.getRawId();
		}

		public int retainCount() {
			return _retainCount;
		}

		public void retain() {
			_retainCount ++;
		}


		public void release() {
			if(--_retainCount <=0){
				if(!hasChange() && !isInserted() && !isDeleted()){
					destroy();
				}
			}
		}
	}
	
	private class DataFetchedResultsImpl<T extends DataItem> implements IDataFetchedResults<T> {

		private Class<T> _itemClass;
		private DataFetchRequest<T> _fetchRequest;
		private List<DataEntityRawDataImpl> _datas;
		private List<T> _dataItems;
		private List<Integer> _changeIndexs;
		private List<Integer> _changeStates;
		private List<DataEntityRawDataImpl> _insertedDatas;
		
		private IDataFetchedResultsDelegate<T> _delegate;
		
		DataFetchedResultsImpl(DataFetchRequest<T> fetchRequest){
			_fetchRequest = fetchRequest;
			_itemClass = _dataModel.getDataItemClass(fetchRequest.getDataEntity());
			_datas = null;
			_dataItems = null;
		}
		
		public void destroy(){
			if(_fetchedResults !=null){
				_fetchedResults.remove(this);
			}
			_fetchRequest = null;
			_datas = null;
			_dataItems = null;
			_delegate = null;
			
		}
		
		public DataFetchRequest<T> getFetchRequest(){
			return _fetchRequest;
		}
		
		public boolean beginChange(Set<DataEntityRawDataImpl> datas){
			if(_datas !=null && datas !=null){
				for(int i=0;i<_datas.size();i++){
					DataEntityRawDataImpl data = _datas.get(i);
					if(datas.contains(data)){
						if(_changeIndexs == null){
							_changeIndexs = new ArrayList<Integer>();
						}
						if(_changeStates == null){
							_changeStates = new ArrayList<Integer>();
						}
						_changeIndexs.add(i);
						if(data.isDeleted()){
							_changeStates.add(1);
						}
						else{
							_changeStates.add(2);
						}
					}
				}
			}
			for(DataEntityRawDataImpl data : datas){
				if(data.isInserted() && _fetchRequest.filter(data)){
					if(_insertedDatas == null){
						_insertedDatas = new ArrayList<DataEntityRawDataImpl>(4);
					}
					_insertedDatas.add(data);
					data.retain();
				}
			}
			return (_changeIndexs != null && _changeIndexs.size() >0) 
					|| (_insertedDatas !=null && _insertedDatas.size() >0);
		}
		
		public void endChange() throws DataException{
			if(_delegate !=null){
				_delegate.onDataContentChanging();
			}
			if(_changeIndexs !=null){
				List<DataEntityRawDataImpl> deletedRawDatas = new ArrayList<DataEntityRawDataImpl>(4);
				List<T> deletedDataItems = new ArrayList<T>(4);
				for(int i=0;i<_changeIndexs.size();i++){
					int index = _changeIndexs.get(i);
					int state = _changeStates.get(i);
					T dataItem = _dataItems.get(index);
					if(state == 1){
						deletedRawDatas.add(_datas.get(index));
						deletedDataItems.add(dataItem);
					}
					if(_delegate !=null){
						switch(state){
						case 1:
							_delegate.onDataItemDeleted(dataItem);
							break;
						case 2:
							_delegate.onDataItemUpdated(dataItem);
							break;
						}
					}
				}
				for(DataEntityRawDataImpl data :deletedRawDatas){
					_datas.remove(data);
				}
				for(T dataItem : deletedDataItems){
					_dataItems.remove(dataItem);
				}
			}
			if(_insertedDatas != null){
				for(DataEntityRawDataImpl data : _insertedDatas){
					T dataItem = addRawData(data);
					data.release();
					if(_delegate != null){
						_delegate.onDataItemInserted(dataItem);
					}
				}
				_insertedDatas = null;
			}
			@SuppressWarnings("unchecked")
			List<Integer> indexs = _fetchRequest.store((List<IDataEntityRawData>)(Object)_datas);
			List<DataEntityRawDataImpl> newDatas = new ArrayList<DataEntityRawDataImpl>(4);
			List<T> newDataItems = new ArrayList<T>(4);
			for(Integer index :indexs){
				newDatas.add(_datas.get(index));
				newDataItems.add(_dataItems.get(index));
			}
			_datas = newDatas;
			_dataItems = newDataItems;
			_changeIndexs = null;
			_changeStates = null;
			if(_delegate !=null){
				_delegate.onDataContentChanged();
			}
		}
		
		public T addRawData(DataEntityRawDataImpl data) throws DataException{
			if(_datas == null){
				_datas = new ArrayList<DataEntityRawDataImpl>();
			}
			_datas.add(data);
			if(_dataItems == null){
				_dataItems = new ArrayList<T>();
			}
			T dataItem = (T) newDataItem(_itemClass,data);
			_dataItems.add(dataItem);
			return dataItem;
		}


		public List<T> getFetchedDataItems() {
			return _dataItems != null? new ArrayList<T>(_dataItems):new ArrayList<T>(0);
		}
		
		public IDataFetchedResultsDelegate<T> getDelegate(){
			return _delegate;
		}
		
		public void setDelegate(IDataFetchedResultsDelegate<T> delegate){
			_delegate = delegate;
		}

		public T getFetchedDataItem(int index) {
			if(_dataItems != null && index >=0 && index<_dataItems.size()){
				return _dataItems.get(index);
			}
			return null;
		}

		public int getFetchedDataItemCount() {
			return _dataItems != null ? _dataItems.size(): 0;
		}
	}
	
}
