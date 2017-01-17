package com.automatics.utilities.elements;

import java.util.*;

public class Browser_AppType_Info 
{
	private String browserName;
	private String appType;
	
	public Browser_AppType_Info(String browserName,String appType)
	{
		this.browserName = browserName;
		this.appType = appType;
	}
	
	public String getBrowserName() {
		return browserName;
	}
	
	public String getAppType() {
		return appType;
	}
	
	public String toString()
	{
		String ret = browserName + "__NA__" + appType;
		return ret;
	}
	
}
