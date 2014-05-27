package org.hailong.service.impl;


import java.util.Date;
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
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

public class DownlinkService extends AbstractService {

	private DBContext _dbContext = null;
	private ThreadPoolExecutor _poolExcutor = null;
	
	private DBContext dbContext(){
		
		IServiceContext ctx = getContext();
		
		if(_dbContext == null && ctx != null){
			
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(ctx.getDatabasePath(getClass().getSimpleName().concat(".sqlite")), null);
			
			_dbContext = new DBContext(database);
			
			_dbContext.registerObjectClass(DataObject.class);
			
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
				
				DBContext dbContext = dbContext();
	
				final DataObject dataObject = (DataObject) dbContext.dataObject(DataObject.Entity, dataKey);

				if(dataObject != null){
					handler.post(new Runnable(){

						public void run() {
							task.onDidLoadedFromCached(type, dataObject.getContent(), dataObject.getTimestamp());
						}});
				}
				
			}
		});
	}
	
	public void didLoaded(IDownlinkTask downlinkTask,Class<?> taskType,final Object resultsData,boolean isCached){
		
		final Handler handler = new Handler();
		final String dataKey = dataKey(downlinkTask,taskType);
		final IDownlinkTask task = downlinkTask;
		final Class<?> type = taskType;
		final Object data = resultsData;
		
		if(isCached){
			
			ThreadPoolExecutor executor = getPoolExcutor();
			
			executor.execute(new Runnable() {
				
				public void run() {
					
					DBContext dbContext = dbContext();
					
					DataObject dataObject = (DataObject) dbContext.dataObject(DataObject.Entity, dataKey);
					
					if(dataObject == null){
						dataObject = new DataObject();
						dataObject.setDataKey(dataKey);
					}
					
					dataObject.setContent(resultsData);
					dataObject.setTimestamp(new Date().getTime());
					
					if(dataObject.rowid() == 0){
						dbContext.insertObject(dataObject);
					}
					else {
						dbContext.updateObject(dataObject);
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

	@DBEntity(value = "DataObject",dataKey="dataKey", fields={
				@DBField(value = "dataKey",length = 128,index = true),
				@DBField(value = "content",type = DBFieldType.OBJECT),
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
			setValue(DF_DATAKEY, value);
		}
		
		public Object getContent(){
			return getValue(DF_CONTENT);
		}
		
		public void setContent(Object value){
			setValue(DF_CONTENT,value);
		}
		
		public long getTimestamp(){
			return longValue(DF_TIMESTAMP,0);
		}
		
		public void setTimestamp(long timestamp){
			setValue(DF_TIMESTAMP, timestamp);
		}
	}
}
