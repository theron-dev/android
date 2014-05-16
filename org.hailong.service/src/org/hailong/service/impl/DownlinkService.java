package org.hailong.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hailong.db.DBContext;
import org.hailong.db.DBObject;
import org.hailong.db.annotation.DBEntity;
import org.hailong.db.annotation.DBField;
import org.hailong.db.annotation.DBFieldType;
import org.hailong.service.AbstractService;
import org.hailong.service.IServiceContext;
import org.hailong.service.ITask;
import org.hailong.service.tasks.IDownlinkTask;


import android.os.Handler;

public class DownlinkService extends AbstractService {

	private DBContext _dbContext = null;
	private ThreadPoolExecutor _poolExcutor = null;
	
	private DBContext getDBContext(){
		
		IServiceContext ctx = getContext();
		
		if(_dbContext == null && ctx != null){
			
	
			
		}
		
		return _dbContext;
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
				
//				DBContext dbContext = getDataContext();
//				
//				DataFetchRequest<DownlinkService.DataObject> request = new DataFetchRequest<DownlinkService.DataObject>();
//				
//				request.setDataPredicate(new Field(DataObject.DF_DATAKEY).eq(new Value(dataKey)));
//				request.setFetchLimit(1);
//				
//				try {
//					
//					List<DownlinkService.DataObject> dataObjects = dataContext.executeFetchRequest(request, 1);
//					
//					if(dataObjects != null && dataObjects.size() >0){
//						
//						DownlinkService.DataObject dataObject = dataObjects.get(0);
//						
//						final Object resultsData = JSON.decodeString(dataObject.getContent());
//						
//						final long timestamp = dataObject.getTimestamp();
//						
//						if(resultsData != null){
//							handler.post(new Runnable(){
//
//								public void run() {
//									task.onDidLoadedFromCached(type, resultsData, timestamp);
//								}});
//						}
//						
//						
//					}
//					
//				} catch (Exception e) {
//				}
				
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
					
//					DataContext dataContext = getDataContext();
//					
//					DataFetchRequest<DownlinkService.DataObject> request = new DataFetchRequest<DownlinkService.DataObject>();
//					
//					request.setDataPredicate(new Field(DataObject.DF_DATAKEY).eq(new Value(dataKey)));
//					request.setFetchLimit(1);
//					
//					try {
//						
//						DownlinkService.DataObject dataObject = null;
//						
//						List<DownlinkService.DataObject> dataObjects = dataContext.executeFetchRequest(request, 1);
//						
//						if(dataObjects != null && dataObjects.size() >0){
//							dataObject = dataObjects.get(0);
//						}
//						else{
//							dataObject = dataContext.insertDataItem(DownlinkService.DataObject.class);
//						}
//						
//						dataObject.setDataKey(dataKey);
//						dataObject.setContent(JSON.encodeObject(data));
//						dataObject.setTimestamp(new Date().getTime());
//						
//						dataContext.save();
//						
//					} catch (Exception e) {
//					}
					
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

	@DBEntity(value = "DataObject",fields={
				@DBField(value = "dataKey",length = 128,index = true),
				@DBField(value = "content",type = DBFieldType.TEXT),
				@DBField(value = "timestmap",type = DBFieldType.BIGINT)
			})
	private static class DataObject extends DBObject{
		
		public final static DBEntity Entity;
		public final static DBField DF_DATAKEY;
		public final static DBField DF_CONTENT;
		public final static DBField DF_TIMESTAMP;
		
		static {
			
			Entity = DataObject.class.getAnnotation(DBEntity.class);
			
			DBField[] dataFields = Entity.fields();
			
			DF_DATAKEY = dataFields[0];
			DF_CONTENT = dataFields[1];
			DF_TIMESTAMP = dataFields[2];
		}
		
		public void setDataKey(String value){
			setObjectValue(DF_DATAKEY, value);
		}
		
		public String getContent(){
			return (String) getObjectValue(DF_CONTENT);
		}
		
		public void setContent(String value){
			setObjectValue(DF_CONTENT,value);
		}
		
		public long getTimestamp(){
			return (Long) getObjectValue(DF_TIMESTAMP);
		}
		
		public void setTimestamp(long timestamp){
			setObjectValue(DF_TIMESTAMP, timestamp);
		}
	}
}
