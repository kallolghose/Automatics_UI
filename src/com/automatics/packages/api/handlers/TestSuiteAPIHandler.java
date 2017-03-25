package com.automatics.packages.api.handlers;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import com.automatics.mongo.api.TestSuiteAPI;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.helpers.Utilities;
import com.google.gson.Gson;


public class TestSuiteAPIHandler
{
	private static TestSuiteAPIHandler instance = new TestSuiteAPIHandler();
	private TSGson[] allTestSuites;
	
	public static String TESTSUITE_RESPONSE_MESSAGE = "";
	public static int TESTSUITE_RESPONSE_CODE = -99;
	
	private TestSuiteAPIHandler()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray testcaseJsonArr = TestSuiteAPI.getAllTestSuites();
			allTestSuites = gson.fromJson(testcaseJsonArr.toString(), TSGson[].class);
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - TestSuiteAPIHandler] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public static TestSuiteAPIHandler getInstance()
	{
		return instance;
	}
	
	public TSGson[]  getAllTestSuites()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray testcaseJsonArr = TestSuiteAPI.getAllTestSuites();
			allTestSuites = gson.fromJson(testcaseJsonArr.toString(), TSGson[].class);
			return allTestSuites;
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - TestSuiteAPIHandler] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public void refreshTestSuiteList()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray omJsonArr = TestSuiteAPI.getAllTestSuites();
			TESTSUITE_RESPONSE_CODE = TestSuiteAPI.RESPONSE_CODE;
			TESTSUITE_RESPONSE_MESSAGE = TestSuiteAPI.RESPONSE_MESSAGE; 
	
			if(TESTSUITE_RESPONSE_CODE == 200)
			{
				allTestSuites = gson.fromJson(omJsonArr.toString(), TSGson[].class);
			}
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - refreshTestSuiteList] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	
	public TSGson getSpecificTestSuite(String tsName)
	{
		try
		{
			JsonArray omObjects = TestSuiteAPI.getTestSuiteByName(tsName);
			TESTSUITE_RESPONSE_CODE = TestSuiteAPI.RESPONSE_CODE;
			TESTSUITE_RESPONSE_MESSAGE = TestSuiteAPI.RESPONSE_MESSAGE;
			if(TESTSUITE_RESPONSE_CODE==200)
			{
				TSGson[] tsGson = Utilities.getGSONFromJSON(omObjects.toString(), TSGson[].class);
				return tsGson[0];
			}
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - getSpecificTestSuite] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public TSGson updateTestSuite(TSGson tsGson)
	{
		try
		{
			String jsonStr = Utilities.getJSONFomGSON(OMGson.class, tsGson);
			JsonObject updateObj = Json.createReader(new StringReader(jsonStr)).readObject();
			JsonObject responseObj = TestSuiteAPI.putTestSuite(tsGson.tsName, updateObj);
			TESTSUITE_RESPONSE_CODE = TestSuiteAPI.RESPONSE_CODE;
			TESTSUITE_RESPONSE_MESSAGE = TestSuiteAPI.RESPONSE_MESSAGE;
			if(TESTSUITE_RESPONSE_CODE ==200)
			{
				TSGson responseGson = Utilities.getGSONFromJSON(responseObj.toString(), TSGson.class);
				return responseGson;
			}
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - updateTestSuite] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public TSGson postTestSuite(TSGson tsGson)
	{
		try
		{
			String jsonStr = Utilities.getJSONFomGSON(TSGson.class, tsGson);
			System.out.println(jsonStr);
			JsonObject updateObj = Json.createReader(new StringReader(jsonStr)).readObject();
			JsonObject responseObj = TestSuiteAPI.postTestSuite(updateObj);
			TESTSUITE_RESPONSE_CODE = TestSuiteAPI.RESPONSE_CODE;
			TESTSUITE_RESPONSE_MESSAGE = TestSuiteAPI.RESPONSE_MESSAGE;
			if(TESTSUITE_RESPONSE_CODE==200)
			{
				TSGson responseGson = Utilities.getGSONFromJSON(responseObj.toString(), TSGson.class);
				return responseGson;
			}
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - postTestSuite] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	
	public boolean deleteTestSuite(String tsName)
	{
		try
		{
			JsonObject responseObj = TestSuiteAPI.deleteTestSuite(tsName);
			TESTSUITE_RESPONSE_CODE = TestSuiteAPI.RESPONSE_CODE;
			TESTSUITE_RESPONSE_MESSAGE = TestSuiteAPI.RESPONSE_MESSAGE;
			if(TESTSUITE_RESPONSE_CODE==200)
				return true;
		}
		catch(Exception e)
		{
			System.out.println("[TestSuiteAPIHandler - deleteTestSuite] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return false;
	}
	
}
