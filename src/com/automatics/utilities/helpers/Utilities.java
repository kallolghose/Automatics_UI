package com.automatics.utilities.helpers;

import java.io.StringReader;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.google.gson.Gson;
import com.mongodb.DB;

public class Utilities 
{
	
	public static DB getMongoDB()
	{
		DB db = AutomaticsDBConnection.getConnection("localhost", 27017, "automatics_db");
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
			System.out.println("[ Utilities - getGSONFromJSON ] - Exception : " + e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> String getJSONFomGSON(Class <T> clazz, Object jsonObj)
	{
		String ret = "";
		try
		{
			Gson gson = new Gson();
			ret = gson.toJson(jsonObj);
			return ret;
		}
		catch(Exception e)
		{
			System.out.println("[ Utilities - getJSONFomGSON ] - Exception : " + e.getMessage() );
			e.printStackTrace();
		}
		return ret;
	}
	
	public static JsonObject getJsonObjectFromString(String jsonStr)
	{
		try
		{
			JsonObject object = null;
			JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
			object = jsonReader.readObject();
			jsonReader.close();
			return object;
		}
		catch(Exception e)
		{
			System.out.println("[ Utilities - getJsonObjectFromString ] - Exception : " + e.getMessage());
			e.printStackTrace();
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
	
	public static MessageDialog openDialog(Shell shell, String title, String msg, String type)
	{
		MessageDialog dialog = null;
		try
		{
			if(type.equalsIgnoreCase("WARN"))
			{
				dialog = new MessageDialog(shell, title, null, msg, MessageDialog.WARNING, 
											new String[]{"OK"}, 0);
				
			}
			else if(type.equalsIgnoreCase("ERR"))
			{
				dialog = new MessageDialog(shell, title, null, msg, MessageDialog.ERROR, 
											 new String[]{"OK"}, 0);
			}
			else if(type.equalsIgnoreCase("INFO"))
			{
				
			}
			else if(type.equalsIgnoreCase("QUESTION"))
			{
				
			}
			else if(type.equalsIgnoreCase("CONFIRM"))
			{
				
			}
		}
		catch(Exception e)
		{
			System.out.println("[ Utilities - openDialog ] - Exception : " + e.getMessage() );
			e.printStackTrace();
		}
		return dialog;
	}
}
