package com.automatics.utilities.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextArea;

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
import org.eclipse.ui.PlatformUI;
import org.testng.TestNG;

import com.automatics.utilities.helpers.Utilities;

public class TestSuiteExecutor 
{
	private List<String> listofTestSuites;
	private static String PROJECT_NAME = Utilities.PROJECT_NAME;
	private ConsoleOutputStream consoleOP = null;
	private Text text;
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
		String binPath = location + "\\bin";
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
			System.out.println(cl.toString());
	    	DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
	    	ExecuteWatchdog watchdog = new ExecuteWatchdog(-1L);
	    	Executor executor = new DefaultExecutor();
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
			e.printStackTrace(System.out);
		}
	}
}
