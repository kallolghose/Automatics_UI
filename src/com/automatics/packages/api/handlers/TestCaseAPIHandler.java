package com.automatics.packages.api.handlers;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import com.automatics.mongo.api.TestcaseAPI;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.helpers.Utilities;
import com.google.gson.Gson;

public class TestCaseAPIHandler 
{
	private static TestCaseAPIHandler instance = new TestCaseAPIHandler();
	private TCGson allTestCases[];
	public static String TESTCASE_RESPONSE_MESSAGE = "";
	public static int TESTCASE_RESPONSE_CODE = -99;
	public static JsonObject TESTCASE_JSON_ERROR_REPSONSE = null;
	
	private TestCaseAPIHandler()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray testcaseJsonArr = TestcaseAPI.getAllTestCases();
			allTestCases = gson.fromJson(testcaseJsonArr.toString(), TCGson[].class);
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - TestCaseAPIHandler] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	
	public static TestCaseAPIHandler getInstance()
	{
		return instance;
	}
	
	public TCGson [] getAllTestCases()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray testcaseJsonArr = TestcaseAPI.getAllTestCases();
			TESTCASE_RESPONSE_CODE = TestcaseAPI.RESPONSE_CODE;
			TESTCASE_RESPONSE_MESSAGE = TestcaseAPI.RESPONSE_MESSAGE; 
	
			if(TESTCASE_RESPONSE_CODE == 200)
			{
				allTestCases = gson.fromJson(testcaseJsonArr.toString(), TCGson[].class);
				return allTestCases;
			}
			/*In case of an error*/
			TESTCASE_JSON_ERROR_REPSONSE = testcaseJsonArr.getJsonObject(0);
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - TestCaseAPIHandler] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public void refreshTestCaseList()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray testcaseJsonArr = TestcaseAPI.getAllTestCases();
			TESTCASE_RESPONSE_CODE = TestcaseAPI.RESPONSE_CODE;
			TESTCASE_RESPONSE_MESSAGE = TestcaseAPI.RESPONSE_MESSAGE; 
	
			if(TESTCASE_RESPONSE_CODE == 200)
			{
				allTestCases = gson.fromJson(testcaseJsonArr.toString(), TCGson[].class);
			}
			/*In case of an error*/
			TESTCASE_JSON_ERROR_REPSONSE = testcaseJsonArr.getJsonObject(0);
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - refreshTestCaseList] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public TCGson getSpecificTestCase(String tcName)
	{
		try
		{
			JsonArray testcaseObjects = TestcaseAPI.getTestCaseByName(tcName);
			TESTCASE_RESPONSE_CODE = TestcaseAPI.RESPONSE_CODE;
			TESTCASE_RESPONSE_MESSAGE = TestcaseAPI.RESPONSE_MESSAGE;
			if(TESTCASE_RESPONSE_CODE==200)
			{
				TCGson[] tcGson = Utilities.getGSONFromJSON(testcaseObjects.toString(), TCGson[].class);
				return tcGson[0];
			}

			/*In case of an error*/
			TESTCASE_JSON_ERROR_REPSONSE = testcaseObjects.getJsonObject(0);
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - getSpecificTestCase] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public TCGson updateTestCase(String tcName, TCGson tcGson)
	{
		try
		{
			String jsonStr = Utilities.getJSONFomGSON(TCGson.class, tcGson);
			JsonObject updateObj = Json.createReader(new StringReader(jsonStr)).readObject();
			JsonObject responseObj = TestcaseAPI.putTestCase(tcName, updateObj);
			TESTCASE_RESPONSE_CODE = TestcaseAPI.RESPONSE_CODE;
			TESTCASE_RESPONSE_MESSAGE = TestcaseAPI.RESPONSE_MESSAGE;
			if(TESTCASE_RESPONSE_CODE==200)
			{
				TCGson responseGson = Utilities.getGSONFromJSON(responseObj.toString(), TCGson.class);
				return responseGson;
			}
			/*In case of an error*/
			TESTCASE_JSON_ERROR_REPSONSE = responseObj;
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - updateTestCase] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public TCGson postTestCase(TCGson tcGson)
	{
		try
		{
			String jsonStr = Utilities.getJSONFomGSON(TCGson.class, tcGson);
			JsonObject updateObj = Json.createReader(new StringReader(jsonStr)).readObject();
			JsonObject responseObj = TestcaseAPI.postTestCase(updateObj);
			TESTCASE_RESPONSE_CODE = TestcaseAPI.RESPONSE_CODE;
			TESTCASE_RESPONSE_MESSAGE = TestcaseAPI.RESPONSE_MESSAGE;
			if(TESTCASE_RESPONSE_CODE==200)
			{
				TCGson responseGson = Utilities.getGSONFromJSON(responseObj.toString(), TCGson.class);
				return responseGson;
			}
			/*In case of an error*/
			TESTCASE_JSON_ERROR_REPSONSE = responseObj;
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - postTestCase] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public boolean deleteTestCase(String tcName)
	{
		try
		{
			JsonObject responseObj = TestcaseAPI.deleteTestCase(tcName);
			TESTCASE_RESPONSE_CODE = TestcaseAPI.RESPONSE_CODE;
			TESTCASE_RESPONSE_MESSAGE = TestcaseAPI.RESPONSE_MESSAGE;
			if(TESTCASE_RESPONSE_CODE==200)
				return true;

			/*In case of an error*/
			TESTCASE_JSON_ERROR_REPSONSE = responseObj;
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseAPIHandler - deleteTestCase] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return false;
	}
}
