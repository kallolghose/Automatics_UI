package com.automatics.utilities.runner;

import org.eclipse.swt.widgets.Text;
import org.testng.IReporter;
import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import java.io.PrintStream;
import java.util.*;

public class TSExecutor implements Runnable
{
	private List<String> listofTestSuites;
	private Thread thread; 
	
	public TSExecutor(List<String> list)
	{
		this.listofTestSuites = list;
	}
	public void startThread()
	{
		try
		{
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			//Set the testng runner
			TestNG runner=new TestNG();
			runner.setTestSuites(listofTestSuites);
			runner.run();
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : run()] - Exception : " + e.getMessage());
			System.out.println("Error");
			//e.printStackTrace();
		}
		finally
		{
			System.setOut(System.out);
			System.setErr(System.err);
		}
	}
	
}
