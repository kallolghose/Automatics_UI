package com.automatics.packages.Model;

import java.util.ArrayList;
import java.util.List;

public class TestCaseTaskService 
{
	private static TestCaseTaskService taskService = new TestCaseTaskService();
    private List<TestCaseTask> tasks = new ArrayList<TestCaseTask>();
    
    public static TestCaseTaskService getInstance() 
    {
        return taskService;
    }
    
    public void addTasks(TestCaseTask task)
    {
    	tasks.add(task);
    }
    
    public List<TestCaseTask> getTasks() 
    {
        return tasks;
    }
    
    public TestCaseTask getTaskByTcName(String tcName) {
        for (TestCaseTask todo : tasks) {
                if (todo.getTcName().equals(tcName)) {
                        return todo;
                }
        }
        return null;
    }
}
