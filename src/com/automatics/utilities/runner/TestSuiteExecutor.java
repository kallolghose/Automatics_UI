package com.automatics.utilities.runner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.testng.TestNG;

import com.automatics.utilities.helpers.Utilities;

public class TestSuiteExecutor 
{
	private List<String> listofTestSuites;
	private static String PROJECT_NAME = Utilities.PROJECT_NAME;
	public TestSuiteExecutor(List<String> listofTestSuites)
	{
		this.listofTestSuites = listofTestSuites;
	}
	
	public void executeTestSuite()
	{
		try
		{
			
			for(String testNgPath : listofTestSuites)
			{
				String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
				String location  = workspacePath + "\\" + PROJECT_NAME;
				String libPath = location + "\\com.automatics.package\\com.automatics.package.jars";
				String binPath = location + "\\bin";
				String dir = location.charAt(0) + ":";
				
				String cmd_for_testng = "java -cp " + libPath + "\\*;" + binPath + " org.testng.TestNG " + testNgPath;
				System.out.println(cmd_for_testng);
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/k", dir + " && cd \"" + location + "\" && " + cmd_for_testng);
				//ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd_for_testng);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				BufferedReader br = new BufferedReader (new InputStreamReader(process.getInputStream()));
				String str = "" , op = "";
				while(!(str=br.readLine()).contains("Total tests"))
				{
					System.out.println(str);
				}
				System.out.println(str);
				System.out.println(br.readLine()+"\n");
				//process.destroy();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
