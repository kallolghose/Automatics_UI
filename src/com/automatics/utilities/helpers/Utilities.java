package com.automatics.utilities.helpers;

import java.util.HashMap;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.google.gson.Gson;
import com.mongodb.DB;

public class Utilities 
{
	public static DB getMongoDB()
	{
		DB db = AutomaticsDBConnection.getConnection("localhost", 27017, "checkDB");
		return db;
	}
	
	public static String getJSONStrFromURL(String URL)
	{
		String str = "";
		return str;
	}
	public static <T> T getJSONFromAPI(String URL,Class<T> clazz)
    {
        Gson gson = new Gson();
        try
        {
        	String jsonStr = getJSONStrFromURL(URL);
            if(clazz != null)
                return gson.fromJson(jsonStr, clazz);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
	
	public static <T> T getGSONFromJSON(String jsonStr,Class<T> clazz)
	{
		try
		{
			Gson gson = new Gson();
			if(clazz != null)
			{
				return gson.fromJson(jsonStr, clazz);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception  : " + e.getMessage());
		}
		return null;
	}
	
	public static HashMap<String,String> getTCApplicationTypeMapping()
	{
		HashMap<String, String> mapData = new HashMap<String,String>();
		mapData.put("Web Application", "WEB");
		mapData.put("Non Web Application", "NON-WEB");
		return mapData;
	}
}
