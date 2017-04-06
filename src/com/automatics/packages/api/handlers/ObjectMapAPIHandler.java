package com.automatics.packages.api.handlers;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import com.automatics.mongo.api.ObjectMapAPI;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.helpers.Utilities;
import com.google.gson.Gson;

public class ObjectMapAPIHandler 
{
	private static ObjectMapAPIHandler instance = new ObjectMapAPIHandler();
	private OMGson[] allObjectMap;
	public static int OBJECTMAP_RESPONSE_CODE = -99;
	public static String OBJECTMAP_RESPONSE_MESSAGE = "";
	public static JsonObject OBJECTMAP_JSON_ERROR_RESPONSE = null;
	
	private ObjectMapAPIHandler()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray omJsonArr = ObjectMapAPI.getAllObjectMap();
			allObjectMap = gson.fromJson(omJsonArr.toString(), OMGson[].class);
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - ObjectMapAPIHandler] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public static ObjectMapAPIHandler getInstance()
	{
		return instance;
	}
	
	public OMGson[] getAllObjectMap()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray omJsonArr = ObjectMapAPI.getAllObjectMap();
			OBJECTMAP_RESPONSE_CODE = ObjectMapAPI.RESPONSE_CODE;
			OBJECTMAP_RESPONSE_MESSAGE = ObjectMapAPI.RESPONSE_MESSAGE; 
	
			if(OBJECTMAP_RESPONSE_CODE == 200)
			{
				allObjectMap = gson.fromJson(omJsonArr.toString(), OMGson[].class);
				return allObjectMap;
			}
			/*In case of an error*/
			OBJECTMAP_JSON_ERROR_RESPONSE = omJsonArr.getJsonObject(0);
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - ObjectMapAPIHandler] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public void refreshObjectMapList()
	{
		try
		{
			Gson gson = new Gson();
			JsonArray omJsonArr = ObjectMapAPI.getAllObjectMap();
			OBJECTMAP_RESPONSE_CODE = ObjectMapAPI.RESPONSE_CODE;
			OBJECTMAP_RESPONSE_MESSAGE = ObjectMapAPI.RESPONSE_MESSAGE; 
	
			if(OBJECTMAP_RESPONSE_CODE == 200)
			{
				allObjectMap = gson.fromJson(omJsonArr.toString(), OMGson[].class);
			}
			/*In case of an error*/
			OBJECTMAP_JSON_ERROR_RESPONSE = omJsonArr.getJsonObject(0);
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - refreshObjectMapList] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public OMGson getSpecificObjectMap(String omName)
	{
		try
		{
			JsonArray omObjects = ObjectMapAPI.getObjectMapByName(omName);
			OBJECTMAP_RESPONSE_CODE = ObjectMapAPI.RESPONSE_CODE;
			OBJECTMAP_RESPONSE_MESSAGE = ObjectMapAPI.RESPONSE_MESSAGE;
			if(OBJECTMAP_RESPONSE_CODE==200)
			{
				OMGson[] omGson = Utilities.getGSONFromJSON(omObjects.toString(), OMGson[].class);
				return omGson[0];
			}
			/*In case of an error*/
			OBJECTMAP_JSON_ERROR_RESPONSE = omObjects.getJsonObject(0);
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - getSpecificObjectMap] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public OMGson updateObjectMap(OMGson omGson)
	{
		try
		{
			String jsonStr = Utilities.getJSONFomGSON(OMGson.class, omGson);
			JsonObject updateObj = Json.createReader(new StringReader(jsonStr)).readObject();
			JsonObject responseObj = ObjectMapAPI.putObjectMap(omGson.omName, updateObj);
			OBJECTMAP_RESPONSE_CODE = ObjectMapAPI.RESPONSE_CODE;
			OBJECTMAP_RESPONSE_MESSAGE = ObjectMapAPI.RESPONSE_MESSAGE;
			if(OBJECTMAP_RESPONSE_CODE==200)
			{
				OMGson responseGson = Utilities.getGSONFromJSON(responseObj.toString(), OMGson.class);
				return responseGson;
			}
			/*In case of an error*/
			OBJECTMAP_JSON_ERROR_RESPONSE = responseObj;
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - updateObjectMap] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public OMGson postObjectMap(OMGson omGson)
	{
		try
		{
			String jsonStr = Utilities.getJSONFomGSON(OMGson.class, omGson);
			JsonObject updateObj = Json.createReader(new StringReader(jsonStr)).readObject();
			JsonObject responseObj = ObjectMapAPI.postObjectMap(updateObj);
			OBJECTMAP_RESPONSE_CODE = ObjectMapAPI.RESPONSE_CODE;
			OBJECTMAP_RESPONSE_MESSAGE = ObjectMapAPI.RESPONSE_MESSAGE;
			if(OBJECTMAP_RESPONSE_CODE==200)
			{
				OMGson responseGson = Utilities.getGSONFromJSON(responseObj.toString(), OMGson.class);
				return responseGson;
			}
			/*In case of an error*/
			OBJECTMAP_JSON_ERROR_RESPONSE = responseObj;
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - postObjectMap] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public boolean deleteObjectMap(String omName)
	{
		try
		{
			JsonObject responseObj = ObjectMapAPI.deleteObjectMap(omName);
			OBJECTMAP_RESPONSE_CODE = ObjectMapAPI.RESPONSE_CODE;
			OBJECTMAP_RESPONSE_MESSAGE = ObjectMapAPI.RESPONSE_MESSAGE;
			if(OBJECTMAP_RESPONSE_CODE==200)
				return true;

			/*In case of an error*/
			OBJECTMAP_JSON_ERROR_RESPONSE = responseObj;
		}
		catch(Exception e)
		{
			System.out.println("[ObjectMapAPIHandler - deleteObjectMap] : Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return false;
	}
	
}
