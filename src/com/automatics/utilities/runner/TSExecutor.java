package com.automatics.utilities.runner;

import org.testng.IReporter;
import org.testng.TestNG;

import java.util.*;

public class TSExecutor implements Runnable
{
	List<String> listofTestSuites;
	public TSExecutor(List<String> list)
	{
		this.listofTestSuites = list;
		Thread t = new Thread();
		t.start();
	}
	public void run() {
		// TODO Auto-generated method stub
		TestNG runner=new TestNG();
		runner.setTestSuites(listofTestSuites);
		runner.run();
		
		Iterator<IReporter> itr = runner.getReporters().iterator();
		while(itr.hasNext())
		{
			IReporter report = itr.next();
		}
	}
	
}
