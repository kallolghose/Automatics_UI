package com.automatics.packages.Model;

import java.util.*;

public class TestSuiteTaskService 
{
	private static TestSuiteTaskService taskService = new TestSuiteTaskService();
	private List<TestSuiteTask> tasks = new ArrayList<TestSuiteTask>();
	
	public static TestSuiteTaskService getInstance()
	{
		return taskService;
	}
	
	public void addTasks(TestSuiteTask task)
	{
		tasks.add(task);
	}
	
	public List<TestSuiteTask> getTasks()
	{
		return tasks;
	}
	
	public TestSuiteTask getTaskByTSName(String tsName)
	{
		for(TestSuiteTask todo : tasks)
		{
			if(todo.getTsName().equals(tsName))
				return todo;
		}
		return null;
	}
}
