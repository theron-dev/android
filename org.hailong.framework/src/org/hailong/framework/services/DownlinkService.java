package org.hailong.framework.services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hailong.framework.AbstractService;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.ITask;
import org.hailong.framework.JSON;
import org.hailong.framework.data.DataContext;
import org.hailong.framework.data.DataFetchRequest;
import org.hailong.framework.data.DataModel;
import org.hailong.framework.data.annotation.DataEntity;
import org.hailong.framework.data.annotation.DataField;
import org.hailong.framework.data.annotation.DataFieldType;
import org.hailong.framework.data.impl.SqlitDataStorage;
import org.hailong.framework.data.predicate.Field;
import org.hailong.framework.data.predicate.Value;
import org.hailong.framework.tasks.IDownlinkTask;

import android.os.Handler;

public class DownlinkService extends AbstractService {

	private DataContext _dataContext = null;
	private ThreadPoolExecutor _poolExcutor = null;
	
	private DataContext getDataContext(){
		
		IServiceContext ctx = getContext();
		
		if(_dataContext == null && ctx != null){
			
			SqlitDataStorage db = new SqlitDataStorage(ctx.getDatabasePath(this.getClass().getName() + ".sqlite"));
			
			DataModel dm = new DataModel();
			
			dm.addDataObjectClass(DataObject.class);
			
			_dataContext = new DataContext(db, dm);
			
		}
		
		return _dataContext;
	}
	
	private ThreadPoolExecutor getPoolExcutor(){
		
		if(_poolExcutor == null){
			_poolExcutor = new ThreadPoolExecutor(0, 1, 0 , TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}
		
		return _poolExcutor;
	}
	
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {


		 
		
		return false;
	}

	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {

		return false;
	}
	
	public void didLoadedFormCached(IDownlinkTask downlinkTask,Class<?> taskType){
		
		final Handler handler = new Handler();
		final String dataKey = dataKey(downlinkTask,taskType);
		final IDownlinkTask task = downlinkTask;
		final Class<?> type = taskType;
		
		ThreadPoolExecutor executor = getPoolExcutor();
		
		executor.execute(new Runnable() {
			
			public void run() {
				
				DataContext dataContext = getDataContext();
				
				DataFetchRequest<DownlinkService.DataObject> request = new DataFetchRequest<DownlinkService.DataObject>();
				
				request.setDataPredicate(new Field(DataObject.DF_DATAKEY).eq(new Value(dataKey)));
				request.setFetchLimit(1);
				
				try {
					
					List<DownlinkService.DataObject> dataObjects = dataContext.executeFetchRequest(request, 1);
					
					if(dataObjects != null && dataObjects.size() >0){
						
						DownlinkService.DataObject dataObject = dataObjects.get(0);
						
						final Object resultsData = JSON.decodeString(dataObject.getContent());
						
						final long timestamp = dataObject.getTimestamp();
						
						if(resultsData != null){
							handler.post(new Runnable(){

								public void run() {
									task.onDidLoadedFromCached(type, resultsData, timestamp);
								}});
						}
						
						
					}
					
				} catch (Exception e) {
				}
				
			}
		});
	}
	
	public void didLoaded(IDownlinkTask downlinkTask,Class<?> taskType,Object resultsData,boolean isCached){
		
		final Handler handler = new Handler();
		final String dataKey = dataKey(downlinkTask,taskType);
		final IDownlinkTask task = downlinkTask;
		final Class<?> type = taskType;
		final Object data = resultsData;
		
		if(isCached){
			
			ThreadPoolExecutor executor = getPoolExcutor();
			
			executor.execute(new Runnable() {
				
				public void run() {
					
					DataContext dataContext = getDataContext();
					
					DataFetchRequest<DownlinkService.DataObject> request = new DataFetchRequest<DownlinkService.DataObject>();
					
					request.setDataPredicate(new Field(DataObject.DF_DATAKEY).eq(new Value(dataKey)));
					request.setFetchLimit(1);
					
					try {
						
						DownlinkService.DataObject dataObject = null;
						
						List<DownlinkService.DataObject> dataObjects = dataContext.executeFetchRequest(request, 1);
						
						if(dataObjects != null && dataObjects.size() >0){
							dataObject = dataObjects.get(0);
						}
						else{
							dataObject = dataContext.insertDataItem(DownlinkService.DataObject.class);
						}
						
						dataObject.setDataKey(dataKey);
						dataObject.setContent(JSON.encodeObject(data));
						dataObject.setTimestamp(new Date().getTime());
						
						dataContext.save();
						
					} catch (Exception e) {
					}
					
					handler.post(new Runnable(){

						public void run() {
							task.onDidLoaded(type, data);
						}});
					
				}
			});
			
		}
		else{
			handler.post(new Runnable(){

				public void run() {
					task.onDidLoaded(type, data);
				}});
		}
	}
	
	public void didException(IDownlinkTask downlinkTask,Class<?> taskType,Exception exception){
		final Handler handler = new Handler();
		final IDownlinkTask task = downlinkTask;
		final Class<?> type = taskType;
		final Exception error = exception;
		
		handler.post(new Runnable(){

			public void run() {
				task.onDidException(type, error);
			}});
	}
	
	public String dataKey(IDownlinkTask downlinkTask,Class<?> taskType){
		return taskType.getName();
	}

	@DataEntity(value = "DataObject",fields={
				@DataField(value = "dataKey",length = 128,index = true),
				@DataField(value = "content",type = DataFieldType.TEXT),
				@DataField(value = "timestmap",type = DataFieldType.BIGINT)
			})
	private static class DataObject extends org.hailong.framework.data.DataObject{
		
		public final static DataField DF_DATAKEY;
		public final static DataField DF_CONTENT;
		public final static DataField DF_TIMESTAMP;
		
		static {
			
			DataEntity dataEntity = DataObject.class.getAnnotation(DataEntity.class);
			
			DataField[] dataFields = dataEntity.fields();
			
			DF_DATAKEY = dataFields[0];
			DF_CONTENT = dataFields[1];
			DF_TIMESTAMP = dataFields[2];
		}
		
		public void setDataKey(String value){
			setValue(DF_DATAKEY, value);
		}
		
		public String getContent(){
			return (String) getValue(DF_CONTENT);
		}
		
		public void setContent(String value){
			setValue(DF_CONTENT,value);
		}
		
		public long getTimestamp(){
			return (Long) getValue(DF_TIMESTAMP);
		}
		
		public void setTimestamp(long timestamp){
			setValue(DF_TIMESTAMP, timestamp);
		}
	}
}
