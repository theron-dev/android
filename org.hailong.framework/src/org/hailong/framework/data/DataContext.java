package org.hailong.framework.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hailong.framework.data.annotation.DataEntity;


public class DataContext {
	
	public final static int BATCH_SIZE_MIN = 0;
	public final static int BATCH_SIZE_NORMAL = 500;
	public final static int BATCH_SIZE_MAX	= 1000;
	
	private Map<DataEntity,DataEntityContext> _entityContexts;
	private IDataStorage _dataStorage;
	private DataModel _dataModel;
	
	public DataContext(IDataStorage dataStorage,DataModel dataModel){
		_dataStorage = dataStorage;
		_dataModel = dataModel;
		_entityContexts = new HashMap<DataEntity,DataEntityContext>();
		for(DataEntity dataEntity : _dataModel.getDataEntitys()){
			_dataStorage.register(dataEntity);
			_entityContexts.put(dataEntity, new DataEntityContext(dataStorage,dataModel));
		}
		
	}
	
	public void destroy(){
		for(DataEntityContext  entityContext : _entityContexts.values()){
			entityContext.destroy();
		}
		_entityContexts = null;
		_dataStorage = null;
		_dataModel= null;
	}
	
	public boolean hasChange(){
		for(DataEntityContext entityContext : _entityContexts.values()){
			if(entityContext.hasChange()){
				return true;
			}
		}
		return false;
	}
	
	public void save() throws DataException{
		
		_dataStorage.beginTransaction();
		
		try{
			for(DataEntityContext entityContext : _entityContexts.values()){
				if(entityContext.hasChange()){
					entityContext.save();
				}
			}
			
			_dataStorage.commitTransaction();
		}
		catch(DataException ex){
			_dataStorage.rerollTransaction();
			throw ex;
		}

	}
	
	public <T extends DataObject> T insertDataItem(Class<T> dataItemClass) throws DataException{
		DataEntity dataEntity = dataItemClass.getAnnotation(DataEntity.class);
		DataEntityContext entityContext = _entityContexts.get(dataEntity);
		
		return entityContext.insertDataItem(dataItemClass);
	}
	
	public <T extends DataObject> List<T> executeFetchRequest(DataFetchRequest<T> fetchRequest,int batchSize) throws DataException{
		
		DataEntity dataEntity = fetchRequest.getDataEntity();
		
		DataEntityContext entityContext = _entityContexts.get(dataEntity);
		return entityContext.executeFetchRequest(fetchRequest, batchSize);
	}
	
	/**
	 * 
	 * @param fetchRequest
	 * @param batchSize  
	 * @return
	 * @throws DataException 
	 */
	public <T extends DataObject> IDataFetchedResults<T> executeFetchRequestResults(DataFetchRequest<T> fetchRequest,int batchSize) throws DataException{
		
		DataEntity dataEntity = fetchRequest.getDataEntity();
		DataEntityContext entityContext = _entityContexts.get(dataEntity);
		return entityContext.executeFetchRequestResults(fetchRequest, batchSize);
	}
	
	public boolean delete(DataObject dataItem){
		DataEntity dataEntity = dataItem.getClass().getAnnotation(DataEntity.class);
		DataEntityContext entityContext = _entityContexts.get(dataEntity);
		return entityContext.delete(dataItem);
	}
	
	
	
}
