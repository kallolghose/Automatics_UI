package com.automatics.utilities.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.testng.TestNG;

import com.automatics.utilities.helpers.Utilities;

public class TestSuiteExecutor 
{
	private List<String> listofTestSuites;
	private static String PROJECT_NAME = Utilities.PROJECT_NAME;
	private ConsoleOutputStream consoleOP = null;
	public TestSuiteExecutor(List<String> listofTestSuites, ConsoleOutputStream consoleOP)
	{
		this.listofTestSuites = listofTestSuites;
		this.consoleOP = consoleOP;
	}
	
	public void executeTestSuite()
	{
		try
		{	
			for(String testNgPath : listofTestSuites)
			{
				String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
				String location  = workspacePath + "\\" + PROJECT_NAME;
				//String libPath = location + "\\com.automatics.package\\com.automatics.package.jars";
				String libPath = workspacePath + "\\ext\\jars";
				String binPath = location + "\\bin";
				String dir = location.charAt(0) + ":";
				
				String cmd_for_testng = "java -cp " + libPath + "\\*;" + binPath + " org.testng.TestNG " + testNgPath;
				System.out.println(cmd_for_testng);
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/k", dir + " && cd \"" + location + "\" && " + cmd_for_testng);
				Process process = builder.start();
				StringWriter writer = new StringWriter();
				StreamBoozer setInfo = new StreamBoozer(process.getInputStream(), new PrintWriter(consoleOP));
				StreamBoozer setError = new StreamBoozer(process.getErrorStream(), new PrintWriter(writer));
				setInfo.start();
				setError.start();
				process.waitFor();
				setInfo.join();
				setError.join();
				//ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd_for_testng);
				
				//process.destroy();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}

class StreamBoozer extends Thread
{
	private InputStream in;
	private PrintWriter pw;
	
	StreamBoozer(InputStream in, PrintWriter pw)
	{
		this.in = in;
		this.pw = pw;
	}
	
	@Override
	public void run(){
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			int count = 0;
			while((line = br.readLine())!=null)
			{
				if(count>10)
					break;
				System.out.println(line);
				pw.println(line);
				count++;
			}
			pw.println("Hello");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : run()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("[" + getClass().getName() + " : run()] - Exception : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
