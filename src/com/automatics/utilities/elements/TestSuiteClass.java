package com.automatics.utilities.elements;

import java.util.*;

public class TestSuiteClass 
{
	private String testSuiteName;
	private String createBy;
	private Date dateofCreation;
	private String modifiedBy;
	private Date modificationDate;
	private ArrayList<String> testcases;
	private HashMap<String, ArrayList<Browser_AppType_Info>> testName;
	private int parallelThreads = 1;
	
	public int getParallelThreads() {
		return parallelThreads;
	}
	public void setParallelThreads(int parallelThreads) {
		this.parallelThreads = parallelThreads;
	}
	public String getTestSuiteName() {
		return testSuiteName;
	}
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getDateofCreation() {
		return dateofCreation;
	}
	public void setDateofCreation(Date dateofCreation) {
		this.dateofCreation = dateofCreation;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public ArrayList<String> getTestcases() {
		return testcases;
	}
	public void setTestcases(ArrayList<String> testcases) {
		this.testcases = testcases;
	}
	
	public HashMap<String, ArrayList<Browser_AppType_Info>> getTestName() {
		return testName;
	}
	public void setTestName(
			HashMap<String, ArrayList<Browser_AppType_Info>> testName) {
		this.testName = testName;
	}
	
	public String createTestNg()
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xml = xml + "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n";
		xml = xml + "<suite name=\"Suite\" verbose=\"1\" parallel=\"tests\" thread-count=\""+parallelThreads+"\">\n";
		xml = xml + "\n";
		Iterator<Map.Entry<String, ArrayList<Browser_AppType_Info>>> itr = testName.entrySet().iterator();
		while(itr.hasNext())
		{
			String testCaseName = itr.next().getKey();
			ArrayList<Browser_AppType_Info> name = itr.next().getValue();
			for(int i=0;i<name.size();i++)
			{
				String tname = testCaseName + "__" + (i+1) + "__" + name.get(i).toString();
				xml = xml + "\t<test name = \"" + tname + "\">\n";
				xml = xml + "\t\t<classes><class name = \""+testCaseName+"\"/></classes>\n";
				xml = xml + "\t</test>";
			}
		}
		xml = xml + "\n</suite>";
		return xml;
	}
	
	public String toString()
	{
		String json = "{\n";
		json = json + "\t\t\"SuiteName\" : \""+testSuiteName+"\"\n";
		json = json + "\t\t\"CreateBy\" : \""+createBy+"\"\n";
		json = json + "\t\t\"DateOfCreation\" : \""+dateofCreation+"\"\n";
		json = json + "\t\t\"ModifiedBy\" : \""+modifiedBy+"\"\n";
		json = json + "\t\t\"DateOfModification\" : \""+modificationDate+"\"\n";
		json = json + "\t\t\"TestCaseDetails\" : [";
		Iterator<Map.Entry<String, ArrayList<Browser_AppType_Info>>> itr = testName.entrySet().iterator();
		while(itr.hasNext())
		{
			String testCaseName = itr.next().getKey();
			ArrayList<Browser_AppType_Info> name = itr.next().getValue();
			for(int i=0;i<name.size();i++)
			{
				json = json + "\t\t\t{\n";
				json = json + "\t\t\t\t\"TestCaseName\" : \"" + testCaseName + "\"";
				json = json + "\t\t\t\t\"BrowserInfo\" : \"" + name.get(i).getBrowserName() + "\"";
				json = json + "\t\t\t\t\"AppType\" : \"" + name.get(i).getAppType() + "\"";
				json = json + "\t\t\t}\n";
			}
		}
		json = json + "\t\t]";
		json = json + "}";
		return json;
	}
}



