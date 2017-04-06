package com.automatics.utilities.runner;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.automatics.utilities.helpers.Utilities;

public class TestSuiteExecutor 
{
	private List<String> listofTestSuites;
	private static String PROJECT_NAME = Utilities.PROJECT_NAME;
	private ConsoleOutputStream consoleOP = null;
	private Text text;
	private DefaultExecuteResultHandler resultHandler;
	private ExecuteWatchdog watchdog;
	private Executor executor;
	
	public TestSuiteExecutor(List<String> listofTestSuites, ConsoleOutputStream consoleOP, Text text)
	{
		this.listofTestSuites = listofTestSuites;
		this.consoleOP = consoleOP;
		this.text = text;
	}
	public void executeTestSuite()
	{
		String testng ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\"><suite name=\"MyBatch\" >\n\t <suite-files>" ;
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		String location  = workspacePath + "\\" + PROJECT_NAME;
		String libPath = workspacePath + "\\ext\\jars";
		String binPath = location + "\\com.automatics.packages";
		String dir = location.charAt(0) + ":";
		String cmd_for_testng = "java -cp " + libPath + "\\*;" + binPath + " org.testng.TestNG " + location +"\\testng.xml";
		
		try
		{	
			for(String xmlName : listofTestSuites)
			{
				testng = testng + "\n\t<suite-file path=\"./com.automatics.data/com/automatics/data/temp/" + xmlName + ".xml\" />";
		  	}
			testng = testng + "\n\t</suite-files>\n	</suite>";
			
			PrintWriter writer = new PrintWriter(location + "\\testng.xml");
			writer.println(testng);
			writer.close();
		
			CommandLine cl = CommandLine.parse("cmd.exe /k" + dir +" && cd \"" + location + "\" && " + cmd_for_testng);
			
	    	resultHandler = new DefaultExecuteResultHandler();
	    	watchdog = new ExecuteWatchdog(-1L);
	    	executor = new DefaultExecutor();
	    	executor.setStreamHandler(new PumpStreamHandler(new LogOutputStream() {
	       	
		        @Override
		        protected void processLine(final String line, @SuppressWarnings("unused") int level) {
		           Display.getDefault().asyncExec(new Runnable() {
					
						public void run() {
					
							if (line.toLowerCase().indexOf("error") > -1) {
				            	text.append(line + "\n");
				            } else if (line.toLowerCase().indexOf("warn") > -1) {
				            	text.append(line + "\n");
				            } else {
				            	text.append(line + "\n");
				            }
						}
		           	});
		        	
		        }
	    	}));
	    	executor.setExitValue(1);
	    	executor.setWatchdog(watchdog);
	    	executor.execute(cl, resultHandler);
	    	
		}
		catch(Exception e)
		{
			System.out.println("[" + new Date() + "] - TestSuiteExecutor - executeTestSuite : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	public void stopExecution()
	{
		try
		{
			watchdog.destroyProcess();	
		}
		catch(Exception e)
		{
			System.out.println("[" + new Date() + "] - TestSuiteExecutor - stopExecution : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
