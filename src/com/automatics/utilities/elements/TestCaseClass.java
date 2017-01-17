package com.automatics.utilities.elements;

import java.util.*;

public class TestCaseClass 
{
	private String testcaseName;
	private String testcaseDescription;
	private String applicationType; //WEB, NON-WEB, MOBILE WEB|NON-WEB
	private String createBy;
	private Date dateofCreation;
	private String modifiedBy;
	private Date modificationDate;
	private ArrayList<TestCase_Details> testSteps;
	private ArrayList<String> objectMapDetails;
	
	public String getTestcaseName() {
		return testcaseName;
	}
	public void setTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
	}
	public String getTestcaseDescription() {
		return testcaseDescription;
	}
	public void setTestcaseDescription(String testcaseDescription) {
		this.testcaseDescription = testcaseDescription;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
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
	public ArrayList<TestCase_Details> getTestSteps() {
		return testSteps;
	}
	public void setTestSteps(ArrayList<TestCase_Details> testSteps) {
		this.testSteps = testSteps;
	}
	public ArrayList<String> getObjectMapDetails() {
		return objectMapDetails;
	}
	public void setObjectMapDetails(ArrayList<String> objectMapDetails) {
		this.objectMapDetails = objectMapDetails;
	}
	
	public String createJavaFile()
	{
		String ret = "";
		return ret;
	}
	
	public String toString()
	{
		String json = "{\n";
		json = json + "\t\t\"TestCaseName\" : \""+testcaseName+"\"\n";
		json = json + "\t\t\"TestCaseDescription\" : \""+testcaseDescription+"\"\n";
		json = json + "\t\t\"CreateBy\" : \""+createBy+"\"\n";
		json = json + "\t\t\"DateOfCreation\" : \""+dateofCreation+"\"\n";
		json = json + "\t\t\"ModifiedBy\" : \""+modifiedBy+"\"\n";
		json = json + "\t\t\"DateOfModification\" : \""+modificationDate+"\"\n";
		json = json + "\t\t\"TestSteps\" : [\n";
		Iterator<TestCase_Details> itr = testSteps.iterator();
		while(itr.hasNext())
		{
			TestCase_Details tcSteps = itr.next();
			json = json + "\t\t\t{\n";
			json = json + "\t\t\t\t\"S_No\" : " + tcSteps.getS_No() + "\n";
			json = json + "\t\t\t\t\"Operation\" : " + tcSteps.getOperation_Name()+ "\n";
			json = json + "\t\t\t\t\"PageName\" : " + tcSteps.getPage_Name()+ "\n";
			json = json + "\t\t\t\t\"ObjectName\" : " + tcSteps.getObject_Name()+ "\n";
			json = json + "\t\t\t\t\"Arguments\" : " + tcSteps.getArguments()+ "\n";
			json = json + "\t\t\t}\n";
		}
		json = json + "\t\t]\n";
		json = json + "}";
		return json;
	}
}
