package com.automatics.packages.Model;

import java.util.ArrayList;
import java.util.List;

public class ObjectMapTaskService 
{
	public static ObjectMapTaskService taskService = new ObjectMapTaskService();
	private List<ObjectMapTask> tasks = new ArrayList<ObjectMapTask>();
	
	private ObjectMapTaskService()
	{
		
	}
	
	public static ObjectMapTaskService getInstance()
	{
		return taskService;
	}
	
	public void addTasks(ObjectMapTask task)
	{
		tasks.add(task);
	}
	
	public List<ObjectMapTask> getTasks()
	{
		return tasks;
	}
	
	public ObjectMapTask getTaskByOmName(String omName)
	{
		for(ObjectMapTask task : tasks)
		{
			if(task.getOmName().equals(omName))
				return task;
		}
		return null;
	}
	
}
