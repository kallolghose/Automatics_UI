package com.automatics.utilities.helpers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.automatics.mongo.packages.AutomaticsDBOperationQueries;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.operation.OperationGSON;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.gsons.testsuite.TSGson;
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
	
	public static <T> ArrayList<T> removeDuplicatesFromArrayList(ArrayList<T> list)
	{
		try
		{
			if(list!=null)
			{
				HashSet<T> hashset = new HashSet<T>();
				hashset.addAll(list);
				list.clear();
				list.addAll(hashset);
				return list;
			}
		}
		catch(Exception e)
		{
			System.out.println("[Utilities : removeDuplicatesFromArraylist ] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public void createJavaFiles(TCGson tcGson)
	{
		try
		{
			String javaStmt = "";
			
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String folderPath = workspacePath + "\\Testscripts";
			File folderCheck = new File(folderPath);
			if(!folderCheck.exists())
			{
				folderCheck.mkdirs();
			}
			
			String filePath = folderPath + "\\" + tcGson.tcName + ".java";
			File file = new File(filePath);
			PrintWriter writer = new PrintWriter(file);
			
			//Create import statements
			for(String omName : tcGson.tcObjectMapLink)
			{
				javaStmt = javaStmt +  "\nimport " + omName + "\n";
			}
			
			//Create class statement
			javaStmt = javaStmt + "public class " + tcGson.tcName + "\n";
			javaStmt = javaStmt + "{" + "\n";
			String step = "";
			for(TCStepsGSON steps : tcGson.tcSteps)
			{
				OperationGSON opnGson = Utilities.getGSONFromJSON(
						AutomaticsDBOperationQueries.getOPN(Utilities.getMongoDB(), steps.stepOperation).toString(), OperationGSON.class);
				
				step = step + "\t\t" + opnGson.opnStatement + " " + steps.omName + "." + steps.stepPageName + "__" +
							steps.stepObjName + ";\n";
			}
			javaStmt = javaStmt + step;
			javaStmt = javaStmt + "}";
			
			writer.println(javaStmt);
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : createJavaFiles ] - Exception :" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void createObjectMap(OMGson omGson)
	{
		try
		{
			String javaStmt = "";
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String folderPath = workspacePath + "\\ObjectMap";
			File folderCheck = new File(folderPath);
			if(!folderCheck.exists())
			{
				folderCheck.mkdirs();
			}
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : createObjectMap()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void createTestng(TSGson tsGson)
	{
		try
		{
			String suiteFile = "";
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String filepath = workspacePath + "\\" + tsGson.tsName + ".xml";
			PrintWriter writer = new PrintWriter(filepath);
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : createTestng()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
