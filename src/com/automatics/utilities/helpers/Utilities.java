package com.automatics.utilities.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.automatics.packages.api.handlers.OperationAPIHandler;
import com.automatics.packages.api.handlers.TestCaseAPIHandler;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.operation.OperationGSON;
import com.automatics.utilities.gsons.testcase.ItrParams;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCParams;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.gsons.testsuite.TSTCParamGson;
import com.automatics.utilities.runner.TestSuiteRunnerAPI;
import com.google.gson.Gson;

public class Utilities 
{
	//private static DB db = AutomaticsDBConnection.getConnection(Utilities.MONGO_DB_URL, 27017, "automatics_db");
	
	public static String DB_PROJECT_NAME = "DEMO_TEST";
	public static String API_URL = "http://localhost:3000";
	public static String PROJECT_NAME = "automatics1.4";
	public final static String TESTCASE_FILE_LOCATION = "com.automatics.packages/com/automatics/packages/testScripts/";
	public final static String OBJECTMAP_FILE_LOCATION = "com.automatics.packages/com/automatics/packages/objectMap/";
	public final static String TESTNG_FILE_LOCATION = "";
	public static String MONGO_DB_URL = "localhost";
	
	
	public static boolean COMPILATION_ERROR = false;
	public static String COMPILATION_ERROR_MESSAGE = "";
	public static String AUTOMATICS_USERNAME = System.getProperty("user.name"); /*Will get populated from LoginDialog*/
	public static String AUTOMATICS_PASSWORD = "default";
	
	/*public static DB getMongoDB()
	{
		db = AutomaticsDBConnection.getConnection(Utilities.MONGO_DB_URL, 27017, "automatics_db");
		return db;
	}*/
	
	/*public static void closeMongoDB()
	{
		try
		{
			db.getMongo().close();
		}
		catch(Exception e)
		{
			System.out.println("[Utilites : closeMongoDB()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}*/
	
	
	
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
	
	public static String createJavaFiles(TCGson tcGson)
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
				importObjStmt = importObjStmt +  "\nimport com.automatics.packages.objectMap." + omName + ";\n";
				orInstantiate = orInstantiate + omName +" " + omName + " = new " + omName + "(driver);\n\t\t";
				ordetails = ordetails + "PageFactory.initElements(driver," + omName + ");\n\t\t";
			}
			
			
			//Create class statement
			String step = "";
			for(TCStepsGSON steps : tcGson.tcSteps)
			{
				OperationGSON opnGson = OperationAPIHandler.getInstance().getSpecificOperation(steps.stepOperation);
				step = step + "\t\t\t" + opnGson.opnStatement + "\n";
				String tmpObjStr = steps.omName + "." + steps.stepPageName + "__" + steps.stepObjName;
				//Replace ARG1 if any
				step = step.replace("ARG1", "\"" + steps.stepArgument + "\"");
				step = step.replace("ARG2", "\"" + steps.stepVarName + "\"");
				//Replace  OBJLOC & OBJSTR
				step = step.replace("OBJLOC", tmpObjStr);
				step = step.replace("OBJSTR", "\"" + tmpObjStr + "\"");
				
			}
			javaStmt = javaStmt + step;
			javaStmt = javaStmt + readAfterContent();
			
			
			javaStmt = javaStmt.replace("<PackageName>", "com.automatics.packages.testScripts");
			javaStmt = javaStmt.replace("<ORImport>", importObjStmt);
			javaStmt = javaStmt.replace("<ClassName>", tcGson.tcName);
			javaStmt = javaStmt.replace("<ORINSTANTIATE>", orInstantiate);
			javaStmt = javaStmt.replace("<ORDETAILS>", ordetails);
			//javaStmt = javaStmt.replace("ARG2", "\"\"");
			
			//Write java to the file
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String folderPath = workspacePath + "\\" + PROJECT_NAME + "\\com.automatics.packages\\com\\automatics\\packages\\testScripts"; //com.automatics.data.testScripts
			File folderCheck = new File(folderPath);
			if(!folderCheck.exists())
			{
				folderCheck.mkdirs();
			}
			String javaFilePath = folderPath + "\\" + tcGson.tcName + ".java";
			writeContentstoFile(javaFilePath, javaStmt);
			checkCompilationError(javaFilePath);
			return javaFilePath;
		}
		catch(Exception e)
		{
			System.out.println("[utilities : createJavaFiles ] - Exception :" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createObjectMap(OMGson omGson)
	{
		try
		{
			String javaStmt = "";
			String appName = "com.automatics.packages.objectMap";
			
			javaStmt = javaStmt + "package " + appName + ";\n\n";
			javaStmt = javaStmt + "import org.openqa.selenium.*;\n";
			javaStmt = javaStmt + "import org.openqa.selenium.support.FindBy;\n";
			javaStmt = javaStmt + "import com.automatics.packages.library.common.Utils;\n\n";
			
			javaStmt = javaStmt + "public class " + omGson.omName + " {\n\n";
			for(OMDetails details : omGson.omDetails)
			{
				javaStmt = javaStmt + "\t@FindBy(" + details.locatorType + " = \"" + details.locatorInfo + "\")\n";
				javaStmt = javaStmt + "\tpublic WebElement " + details.pageName + "__" + details.objName + ";\n\n";
			}
			
			javaStmt = javaStmt + "\tWebDriver driver;\n";
			javaStmt = javaStmt + "\tpublic " + omGson.omName + "(WebDriver driver){\n";
			javaStmt = javaStmt + "\t\tUtils.hEnvParams.put(\"App\",this.getClass().getPackage().getName());\n";
			javaStmt = javaStmt + "\t\tthis.driver=driver;\n" ;
			javaStmt = javaStmt + "\t}\n}";
			
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String folderPath = workspacePath + "\\" + PROJECT_NAME + "\\com.automatics.packages\\com\\automatics\\packages\\objectMap";
			//Create folder if does not exists
			File folderCheck = new File(folderPath);
			if(!folderCheck.exists())
			{
				folderCheck.mkdirs();
			}
			String javaFilePath = folderPath + "\\" + omGson.omName + ".java";
			writeContentstoFile(javaFilePath, javaStmt);
			checkCompilationError(javaFilePath);
			return javaFilePath;
		}
		catch(Exception e)
		{
			System.out.println("[utilities : createObjectMap()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createTestng(TSGson tsGson, TestSuiteRunnerAPI runner)
	{
		try
		{
			String suiteFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n";
			
			suiteFile = suiteFile + "<suite name=\"" + tsGson.tsName + "\" verbose=\"1\" parallel=\"tests\" thread-count=\""+
									runner.threadCount+"\">\n";
			for(TSTCGson tstcGson : tsGson.tsTCLink)
			{
				suiteFile = suiteFile + getTestWithParameters(tstcGson);
			}
			
			suiteFile = suiteFile + "</suite>";
			
			//Write Content to file
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			String filepath = workspacePath + "\\" + PROJECT_NAME + "\\com.automatics.data\\com\\automatics\\data\\temp\\" + tsGson.tsName + ".xml";
			PrintWriter writer = new PrintWriter(filepath);
			writer.println(suiteFile);
			writer.close();
			return tsGson.tsName;
		}
		catch(Exception e)
		{
			System.out.println("[utilities : createTestng()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	private static String getTestWithParameters(TSTCGson tstcGson)
	{
		try
		{
			String parameter = "";
			List<TSTCParamGson> params = tstcGson.tcParams;
			
			/*
			 * Get the test case*/
			String tcName = tstcGson.tcName;
			TCGson tcGson = TestCaseAPIHandler.getInstance().getSpecificTestCase(tcName);
			if(TestCaseAPIHandler.TESTCASE_RESPONSE_CODE!=200)
				throw new RuntimeException("Error in Fetching TestCase : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE);
			
			int itrCntr = 1, index =0, loop;
			//for(TCParams tcParams : tcGson.tcParams)
			do
			{
				parameter = parameter + "\n<test name=\""+tcName+"_TP_ITR"+itrCntr+"\">\n";
				parameter = parameter + "\t<parameter name=\"Test_Name\" value=\""+ tstcGson.tcName +"\" />\n";
				parameter = parameter + "\t<parameter name=\"Exe_Platform\" value=\""+ params.get(0).tcparamValue +"\" />\n";
				parameter = parameter + "\t<parameter name=\"Exec_Type\" value=\""+ params.get(1).tcparamValue +"\" />\n";
				parameter = parameter + "\t<parameter name=\"Run_on\" value=\""+ params.get(2).tcparamValue +"\" />\n";
				
				TCParams tcParams = null;
				loop = 0;
				if(tcGson.tcParams!=null)
				{
					tcParams = tcGson.tcParams.size()>0 ? tcGson.tcParams.get(index) : null;
					loop = tcGson.tcParams.size();
				}
					
				//if (tcParams.toString().contains("iterParams")) {
				if (tcParams!=null)
				{
					for(ItrParams tcParam : tcParams.iterParams)
					{
						parameter = parameter + "\t<parameter name=\""+ tcParam.iparamName +"\" value=\""+ tcParam.iparamValue +"\" />\n";
					}
				}
				
				parameter = parameter + "\n\t<classes>\n";
				parameter = parameter + "\t\t<class name=\"com.automatics.packages.testScripts." + tcName + "\"/>\n";
				parameter = parameter + "\t</classes>\n";
				parameter = parameter + "</test>\n";
				itrCntr ++;
				index ++;
			}while(index<loop);
			
			
			return parameter;
		}
		catch(Exception e)
		{
			System.out.println("[Utilities : getParameters()] - Exception  : " +e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static String beforeafterContentPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "\\data";
	
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
			/*
			IProject [] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for(int i=0;i<projects.length;i++)
			{
				//ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].refreshLocal(IResource.DEPTH_INFINITE, null);
				projects[i].refreshLocal(IResource.DEPTH_INFINITE, null);
				projects[i].build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
			}*/
		}
		catch(Exception e)
		{
			System.out.println("[Utilitites : writeContentstoFile()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static boolean checkCompilationError(String filename)
	{
		try
		{
			COMPILATION_ERROR = false;
			COMPILATION_ERROR_MESSAGE = "";
			boolean isError = false;
			String [] filepaths = filename.split("\\\\");
			int len = filepaths.length;
			String tmpPath = "com.automatics.packages\\com\\automatics\\packages\\" + filepaths[len-2] + "\\" + filepaths[len-1];
			
			//Refresh all projects
			IProject [] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for(int i=0;i<projects.length;i++)
			{
				projects[i].refreshLocal(IResource.DEPTH_INFINITE, null);
				projects[i].build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
				
				//Check for compilation errors
				IFile compiledFile = projects[i].getFile(tmpPath);
				if(compiledFile.exists())
				{
					ICompilationUnit unit = JavaCore.createCompilationUnitFrom(compiledFile);
					final ASTParser parser = ASTParser.newParser(AST.JLS3); 
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
					parser.setSource(unit);
					parser.setResolveBindings(true); // we need bindings later on
					CompilationUnit unit2 = (CompilationUnit) parser.createAST(null);
					for(IProblem problem : unit2.getProblems())
					{
						if(problem.isError())
						{
							isError = true;
							COMPILATION_ERROR = true;
							COMPILATION_ERROR_MESSAGE = COMPILATION_ERROR_MESSAGE + problem.getMessage() + "\n";
						}
					}
				}
			}
			return isError;
		}
		catch(Exception e)
		{
			System.out.println("[Utilities : checkCompilationError()] - Exception  : " + e.getMessage());
			e.printStackTrace(System.out);
		}
		return false;
	}
	
	public static void openEditor(IFile file, String editorID)
	{
		try
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
		    IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		    if (editorID == null || editorRegistry.findEditor(editorID) == null)
		    {
		    	editorID = workbench.getEditorRegistry().getDefaultEditor(file.getFullPath().toString()).getId();	
		    }
	
		    IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		    page.openEditor(new FileEditorInput(file), editorID, false, IWorkbenchPage.MATCH_INPUT);
		}
		catch(Exception e)
		{
			System.out.println("[Utilites - openEditor()] : Exception - " + e.getMessage());
			e.printStackTrace();
		}
	} 
	
	
	public static TCGson convertToTestCaseGsonFromContent(InputStream stream)
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String temp = "", parseContent = "";
			boolean startParsing = false;
			while((temp=br.readLine())!=null)
			{
				if(temp.trim().equals("@Test"))
				{
					startParsing = true; 
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[Utilities : convertToTestCaseGsonFromContent()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * validateEntityValues.
	 * @param str {@link String}
	 * @return String
	 */
	public static List<String> validateEntityValues(String str)
	{
		final List<String> collvalidityMessage=new ArrayList<String>();
		Pattern blankCheck = Pattern.compile("^\\s*$");
		Pattern blankCheck1 = Pattern.compile("^\\d");
		//Pattern blankCheck2 = Pattern.compile("(?=.*[~!@#$%^&*-[[:blank:]]])");
		Pattern blankCheck2 = Pattern.compile("[^A-Za-z0-9_]+");
		/*Biswabir Code*/
		Pattern keywordCheck= Pattern.compile("(final|abstract|assert|booean|break|byte|switch|case|try|catch|finally|char|class|continue|"
				+ "default|do|double|if|else|enum|extends|float|for|implements|import|instanceOf|int|interface|long|native|new|package|"
				+ "private|protected|public|return|short|static|strictfp|super|synchronized|this|throw|throws|transient|void|volatile|"
				+ "while|goto|const)");
		
		final Matcher  blankCheckForTsName = blankCheck.matcher(str); 
	    final Matcher  blankCheckForTsNameForNumberCheck = blankCheck1.matcher(str);
	    final Matcher  blankCheckForTsNameForSpecialCherecterCheck = blankCheck2.matcher(str);
	    final Matcher  blankCheckForTsNameForJavaKeywordCheck = keywordCheck.matcher(str);
	    
		if(blankCheckForTsName.find())
		{ 
			collvalidityMessage.add("Please enter name");
		}
		if(blankCheckForTsNameForNumberCheck.find())
		{ 
			collvalidityMessage.add("Name should not start with digit");
		}
		if(blankCheckForTsNameForSpecialCherecterCheck.find())
		{ 
			collvalidityMessage.add("Special Characters/Space not allowed in name");
		}
		if(blankCheckForTsNameForJavaKeywordCheck.find())
		{
			collvalidityMessage.add("Java Keyword not allowed in name");
		}
		return collvalidityMessage;
    }

	/**
	 * validateDescriptionValue.
	 * @param str {@link String}
	 * @return String
	 */
	public static List<String> validateDescriptionValue(String str)
	{
		try
		{
			final List<String> collMessage=new ArrayList<String>();
			Pattern blankCheck = Pattern.compile("^\\s*$");
			final Matcher blankCheckForTsName = blankCheck.matcher(str); 
			if(blankCheckForTsName.find())
			{ 
				collMessage.add("Please enter Description, it cannot be blank");
			}
			return collMessage;
		}
		catch(Exception e)
		{
			System.out.println("[Utitlites - validationDescriptionValue()] : Exception - " + e.getMessage());
			e.printStackTrace();
		}
		return null;
		
	}
}
