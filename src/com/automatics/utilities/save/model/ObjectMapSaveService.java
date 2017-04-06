package com.automatics.utilities.save.model;

import java.util.*;

public class ObjectMapSaveService 
{
	public static ObjectMapSaveService saveService = new ObjectMapSaveService();
	private HashMap<String,ObjectMapSaveTask> saveTasks = new HashMap<String,ObjectMapSaveTask>();
	
	private ObjectMapSaveService()
	{
		
	}
	
	public static ObjectMapSaveService getInstance()
	{
		return saveService;
	}
	
	public void addSaveTask(ObjectMapSaveTask task)
	{
		saveTasks.put(task.getOmName(), task);
	}
	
	public void updateSaveTask(ObjectMapSaveTask task)
	{
		saveTasks.remove(task.getOmName());
		saveTasks.put(task.getOmName(),task);
	}
	
	public ObjectMapSaveTask getSaveTask(String omName)
	{
		return saveTasks.get(omName);
	}
	
	public HashMap<String,ObjectMapSaveTask> getAllMaps()
	{
		return saveTasks;
	}
}
