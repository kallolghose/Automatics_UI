package com.automatics.packages.check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
		
		File file = new File("D:\\KG00360770\\ATT\\runtime-Automatics.application\\Automation_Suite\\com.automatics.data\\com\\automatics\\data"
				+ "\\testScripts\\FirstTC.java");
		InputStream stream = new FileInputStream(file);
		AutomaticsParser parser = new AutomaticsParser();
		parser.setTcStream(stream);
		parser.parseContentofTestCase();
		parser.displayAllDetails();
	}
}
