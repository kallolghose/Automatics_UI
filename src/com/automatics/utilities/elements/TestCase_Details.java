package com.automatics.utilities.elements;

public class TestCase_Details 
{
	private int S_No;
	private String Operation_Name;
	private String Page_Name;
	private String Object_Name;
	private String Arguments;
	
	public TestCase_Details(int s_No, String operation_Name, String page_Name,
			String object_Name, String arguments) {
		
		S_No = s_No;
		Operation_Name = operation_Name;
		Page_Name = page_Name;
		Object_Name = object_Name;
		Arguments = arguments;
	}

	public int getS_No() {
		return S_No;
	}

	public void setS_No(int s_No) {
		S_No = s_No;
	}

	public String getOperation_Name() {
		return Operation_Name;
	}

	public void setOperation_Name(String operation_Name) {
		Operation_Name = operation_Name;
	}

	public String getPage_Name() {
		return Page_Name;
	}

	public void setPage_Name(String page_Name) {
		Page_Name = page_Name;
	}

	public String getObject_Name() {
		return Object_Name;
	}

	public void setObject_Name(String object_Name) {
		Object_Name = object_Name;
	}

	public String getArguments() {
		return Arguments;
	}

	public void setArguments(String arguments) {
		Arguments = arguments;
	}
	
	
	
}
