package org.hailong.service.impl;

import java.lang.reflect.Method;

import org.hailong.service.AbstractService;
import org.hailong.service.IServiceContext;
import org.hailong.service.ITask;
import org.hailong.service.S;
import org.hailong.service.task.impl.APITask;
import org.hailong.service.tasks.IAPICancelTask;
import org.hailong.service.tasks.IAPIResponseTask;
import org.hailong.service.tasks.IUplinkTask;
import org.hailong.service.tasks.IUplinkTaskListener;

import android.util.Log;

public class UplinkService<ST extends IServiceContext> extends AbstractService<ST> {

	public void doDidUplinkTaskLoaded(Class<?> taskType,IUplinkTask uplinkTask,Object resultsData){
		IUplinkTaskListener listener = uplinkTask.getListener();
		if(listener != null){
			listener.onDidUplinkTaskLoaded(taskType, uplinkTask, resultsData);
		}
	}

	public void doDidUnlinkTaskException(Class<?> taskType,IUplinkTask uplinkTask,Exception exception){
		IUplinkTaskListener listener = uplinkTask.getListener();
		if(listener != null){
			listener.onDidUnlinkTaskException(taskType, uplinkTask, exception);
		}
	}

	@Override
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {
		
		if(IAPIResponseTask.class == taskType){
			
			String name = "handle" + taskType.getSimpleName() + "Response";
			
			Method method = null;
			
			try{
				method = this.getClass().getMethod(name, IServiceContext.class,Class.class,ITask.class,IAPIResponseTask.class);
			}
			catch(Exception ex){
				Log.d(S.TAG, Log.getStackTraceString(ex));
			}
			
			if(method != null){
				return (Boolean) method.invoke(getContext(), taskType,task,(IAPIResponseTask) task);
			}
			
		}
		else {
			
			String name = "handle" + taskType.getSimpleName();
			
			Method method = null;
			
			try{
				method = this.getClass().getMethod(name, IServiceContext.class,Class.class,ITask.class,int.class);
			}
			catch(Exception ex){
				Log.d(S.TAG, Log.getStackTraceString(ex));
			}
			
			if(method != null){
				return (Boolean) method.invoke(getContext(), taskType,task,priority);
			}
			
		}
		
		return false;
	}

	@Override
	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {
		
		APITask cancelTask = new APITask();
		
		cancelTask.setTaskType(taskType);
		cancelTask.setTask(task);
		
		getContext().handle(IAPICancelTask.class, cancelTask, 0);
		
		return false;
	}

	@Override
	public boolean cancelHandleForSource(Object source) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	

}
