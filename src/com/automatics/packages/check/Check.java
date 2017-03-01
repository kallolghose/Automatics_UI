package com.automatics.packages.check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.testng.TestNG;

import com.automatics.mongo.packages.AutomaticsDBOperationQueries;
import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.utilities.helpers.AutomaticsParser;
import com.automatics.utilities.helpers.Utilities;
import com.mongodb.*;

public class Check 
{
	public static void main(String[] args) throws IOException {
//		List<String> some = new ArrayList<String>();
//		some.add("D:/KG00360770/ATT/runtime-Automatics.application/Automation_Suite/FirstTS.xml");
//		
//		TestNG runner=new TestNG();
//		runner.setTestSuites(some);
//		runner.run();
		
//		File fileTC = new File("D:\\KG00360770\\ATT\\runtime-Automatics.application\\Automation_Suite\\com.automatics.data\\com\\automatics\\data"
//				+ "\\testScripts\\FirstTC.java");
//		
//		File fileOM = new File("D:\\KG00360770\\ATT\\runtime-Automatics.application\\Automation_Suite\\com.automatics.data\\com\\automatics\\data"
//				+ "\\objectMap\\Google_Demo.java");
//		
//		InputStream stream = new FileInputStream(fileTC);
//		AutomaticsParser parser = new AutomaticsParser();
//		//parser.setTcStream(stream);
//		//parser.parseContentofTestCase();
//		//parser.displayAllTestCaseSteps();
//		InputStream omStream = new FileInputStream(fileOM);
//		parser.setOmStream(omStream);
//		parser.parseContentsOfObjectMap();
//		parser.displayAllObjectMapDetails();
		
		System.out.println(System.getProperty("user.name"));
		
		 final Display display = new Display();
		    Shell shell = new Shell(display);
		    final ProgressBar bar = new ProgressBar(shell, SWT.SMOOTH);
		    bar.setBounds(10, 10, 200, 32);
		    shell.open();
		    final int maximum = bar.getMaximum();
		    new Thread() {
		      public void run() {
		        for (final int[] i = new int[1]; i[0] <= maximum; i[0]++) {
		          try {
		            Thread.sleep(100);
		          } catch (Throwable th) {
		          }
		          if (display.isDisposed())
		            return;
		          display.asyncExec(new Runnable() {
		            public void run() {
		              if (bar.isDisposed())
		                return;
		              bar.setSelection(i[0]);
		            }
		          });
		        }
		      }
		    }.start();
		    while (!shell.isDisposed()) {
		      if (!display.readAndDispatch())
		        display.sleep();
		    }
		    display.dispose();
		  }
		
	}

