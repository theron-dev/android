package org.hailong.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskQueue {

	private List<Class<?>> taskTypes;
	private List<ITask> tasks;
	private List<Integer> taskPrioritys;
	private Map<Class<?>,ITask> beginTasks;
	
	public TaskQueue(){
		taskTypes = new ArrayList<Class<?>>();
		tasks = new ArrayList<ITask>();
		taskPrioritys = new ArrayList<Integer>();
		beginTasks = new HashMap<Class<?>,ITask>();
	}
	
	public <T extends ITask> void addTask(Class<T> taskType,T task,int priority){
		if(taskType!=null && task !=null){
			int i;
			int c = tasks.size();
			for(i=0;i<c;i++){
				if(taskPrioritys.get(i) < priority){
					break;
				}
			}

			tasks.add(i, task);
			taskPrioritys.add(i,priority);
			taskTypes.add(i,taskType);
		}
	}
	
	public <T extends ITask> void removeTask(Class<T> taskType,T task){
		if(taskType == null && task == null){
			beginTasks.clear();
			taskTypes.clear();
			tasks.clear();
			taskPrioritys.clear();
		}
		if(task == null || beginTasks.get(taskType) == task){
			beginTasks.remove(taskType);
		}
		int c = tasks.size();
		for(int i=0;i<c;i++){
			if(taskType == taskTypes.get(i)){
				if(task == null || tasks.get(i) == task){
					tasks.remove(i);
					taskTypes.remove(i);
					taskPrioritys.remove(i);
					i--;
					c--;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ITask> T beginTaskType(Class<T> taskType){
		if(taskType == null){
			return null;
		}
		ITask task = beginTasks.get(taskType);
		if(task !=null){
			return (T)task;
		}
		int c = tasks.size();
		for(int i=0;i<c;i++){
			if(taskType == taskTypes.get(i)){
				task = tasks.get(i);
				beginTasks.put(taskType, task);
				tasks.remove(i);
				taskTypes.remove(i);
				taskPrioritys.remove(i);
				return (T)task;
			}
		}
		return null;
	}
	
	public int size(){
		return tasks.size();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ITask> List<T> tasksForType(Class<T> taskType){
		List<T> rs = new ArrayList<T>();
		if(taskType !=null){
			int c = tasks.size();
			for(int i=0;i<c;i++){
				if(taskType == taskTypes.get(i)){
					rs.add((T)tasks.get(i));
				}
			}
		}
		return rs;
	}
}
