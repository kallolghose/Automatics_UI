package com.automatics.utilities.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import sun.security.util.ResourcesMgr;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.automatics.mongo.packages.AutomaticsDBOperationQueries;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.operation.OperationGSON;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.google.gson.Gson;
import com.mongodb.DB;

public class Utilities 
{
	public static String PROJECT_NAME = "Automation_Suite";
	
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
	
	public static void createJavaFiles(TCGson tcGson)
	{
		try
		{
			String javaStmt = "";
			String importObjStmt = "", appName = "App_Name", orInstantiate = "", ordetails = "";
			
			//Start creation of file
			javaStmt = readBeforeContent();
			
			//Create import statements
			for(String omName : tcGson.tcObjectMapLink)
			{
				importObjStmt = importObjStmt +  "\nimport " + appName + "." + omName + ";\n";
				orInstantiate = orInstantiate + omName +" " + omName + " = new " + omName + "(lDriver);\n\t\t";
				ordetails = ordetails + "PageFactory.initElements(lDriver," + omName + ");\n\t\t";
			}
			
			//Replace <PackageName> <ORImport> <ClassName> <ORINSTANTIATE>	<ORDETAILS>
			javaStmt = javaStmt.replace("<PackageName>", appName);
			javaStmt = javaStmt.replace("<ORImport>", importObjStmt);
			javaStmt = javaStmt.replace("<ClassName>", tcGson.tcName);
			javaStmt = javaStmt.replace("<ORINSTANTIATE>", orInstantiate);
			javaStmt = javaStmt.replace("<ORDETAILS>", ordetails);
			
			//Create class statement
			String step = "";
			for(TCStepsGSON steps : tcGson.tcSteps)
			{
				OperationGSON opnGson = Utilities.getGSONFromJSON(
						AutomaticsDBOperationQueries.getOPN(Utilities.getMongoDB(), steps.stepOperation).toString(), OperationGSON.class);
				
				step = step + "\t\t\t" + opnGson.opnStatement + "\n";
				String tmpObjStr = steps.omName + "." + steps.stepPageName + "__" + steps.stepObjName;
				//Replace ARG1 if any
				step = step.replace("ARG1", "\"" + steps.stepArgument + "\"");
				//Replace  OBJLOC & OBJSTR
				step = step.replace("OBJLOC", tmpObjStr);
				step = step.replace("OBJSTR", "\"" + tmpObjStr + "\"");
				
			}
			javaStmt = javaStmt + step;
			javaStmt = javaStmt + readAfterContent();
			
			//Write java to the file
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String folderPath = workspacePath + "\\" + PROJECT_NAME + "\\TestScripts\\" + appName;
			File folderCheck = new File(folderPath);
			if(!folderCheck.exists())
			{
				folderCheck.mkdirs();
			}
			String javaFilePath = folderPath + "\\" + tcGson.tcName + ".java";
			writeContentstoFile(javaFilePath, javaStmt);
		}
		catch(Exception e)
		{
			System.out.println("[utilities : createJavaFiles ] - Exception :" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void createObjectMap(OMGson omGson)
	{
		try
		{
			String javaStmt = "";
			String appName = "App_Name";
			
			javaStmt = javaStmt + "package " + appName + ";\n";
			javaStmt = javaStmt + "import io.appium.java_client.AppiumDriver;\n";
			javaStmt = javaStmt + "import io.appium.java_client.MobileDriver;\n";
			javaStmt = javaStmt + "import org.openqa.selenium.WebElement;\n";
			javaStmt = javaStmt + "import org.openqa.selenium.support.FindBy;\n";
			javaStmt = javaStmt + "import AutoMaTics.Utils;\n";
			
			javaStmt = javaStmt + "public class " + omGson.omName + " {\n";
			for(OMDetails details : omGson.omDetails)
			{
				javaStmt = javaStmt + "\t@FindBy(" + details.locatorType + " = \"" + details.locatorInfo + "\");\n";
				javaStmt = javaStmt + "\tpublic static WebElement " + "\n\n";
			}
			
			javaStmt = javaStmt + "\tAppiumDriver driver;\n";
			javaStmt = javaStmt + "\tpublic " + omGson.omName + "(AppiumDriver driver){\n";
			javaStmt = javaStmt + "\t\tthis.driver=driver;\n" ;
			javaStmt = javaStmt + "}";
			
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String folderPath = workspacePath + "\\" + PROJECT_NAME + "\\ObjectMap\\" + appName;
			//Create folder if does not exists
			File folderCheck = new File(folderPath);
			if(!folderCheck.exists())
			{
				folderCheck.mkdirs();
			}
			String javaFilePath = folderPath + "\\" + omGson.omName + ".java";
			writeContentstoFile(javaFilePath, javaStmt);
		}
		catch(Exception e)
		{
			System.out.println("[utilities : createObjectMap()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void createTestng(TSGson tsGson)
	{
		try
		{
			String suiteFile = "";
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String filepath = workspacePath + "\\" + PROJECT_NAME + "\\" + tsGson.tsName + ".xml";
			PrintWriter writer = new PrintWriter(filepath);
			
		}
		catch(Exception e)
		{
			System.out.println("[utilities : createTestng()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String validateEntityValues(String str)
	{
		return "";
	}
	
	private static String beforeafterContentPath = "D:\\KG00360770\\ATT\\Automatic_DC\\Automatics\\RequiredFiles";
	
	private static String readBeforeContent()
	{
		try
		{
			File file = new File(beforeafterContentPath+"\\beforecontent.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String str = "", tmp;
			while((tmp = reader.readLine()) !=null)
			{
				str = str + tmp + "\n";
			}
			reader.close();
			return str;
		}
		catch(Exception e)
		{
			System.out.println("[Utitlites : readBeforeContent()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;		
	}
	
	private static String readAfterContent()
	{
		try
		{
			File file = new File(beforeafterContentPath+"\\aftercontent.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String str = "", tmp;
			while((tmp = reader.readLine()) !=null)
			{
				str = str + tmp + "\n";
			}
			reader.close();
			return str;
		}
		catch(Exception e)
		{
			System.out.println("[Utitlites : readBeforeContent()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private static void writeContentstoFile(String filename,String data)
	{
		try
		{
			File file = new File(filename);
			PrintWriter writer = new PrintWriter(file);
			writer.println(data);
			writer.close();
			
			//Refresh all projects
			IProject [] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for(int i=0;i<projects.length;i++)
			{
				//ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].refreshLocal(IResource.DEPTH_INFINITE, null);
				projects[0].refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			
			//Get file path relative the the workspace
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String filePath = file.getAbsolutePath().substring(workspacePath.length()+1);
			
			//Open the file
			IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
			IPath location = Path.fromOSString(filePath); 
			IFile projectFile = workspace.getRoot().getFile(location);
			openEditor(projectFile, null);
		}
		catch(Exception e)
		{
			System.out.println("[Utilitites : writeContentstoFile()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void openEditor(IFile file, String editorID)
	{
		try
		{
			System.out.println("Again Path : " + file.getFullPath());
			IWorkbench workbench = PlatformUI.getWorkbench();
		    IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		    if (editorID == null || editorRegistry.findEditor(editorID) == null)
		    {
		    	editorID = workbench.getEditorRegistry().getDefaultEditor(file.getFullPath().toString()).getId();
		    }
	
		    IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		    page.openEditor(new FileEditorInput(file), editorID, true, IWorkbenchPage.MATCH_ID);
		}
		catch(Exception e)
		{
			System.out.println("[Utilites - openEditor()] : Exception - " + e.getMessage());
			e.printStackTrace();
		}
	} 
}
