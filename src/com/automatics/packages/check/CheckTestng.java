package com.automatics.packages.check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

public class CheckTestng 
{
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		List<String> listofTestSuites =  new ArrayList<String>();
		listofTestSuites.add("D:/KG00360770/ATT/runtime-Automatics.application/Automation_Suite/FirstTS.xml");
		List<XmlSuite> s =  (List<XmlSuite>) (new Parser("D:/KG00360770/ATT/runtime-Automatics.application/Automation_Suite/FirstTS.xml")).parse();
		System.out.println(s);
		//TestNG runner=new TestNG();
		//runner.setXmlSuites(s);
		//runner.setTestSuites(listofTestSuites);
		//runner.run();
	}
}
