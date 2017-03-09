package com.automatics.utilities.chrome.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.json.simple.JSONObject;

import com.automatics.packages.Editors.ObjectMapEditor;
import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Views.ObjectList;
import com.automatics.packages.Views.ObjectMap;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class AddOnUtility 
{
	public static boolean cancelOperation = false;
	
	private static AddOnUtility utility = new AddOnUtility();
	private static JettyServer server = new JettyServer();
	private static IEditorInput editorInput;
	private static TCEditor testcaseEditor;
	private static ObjectMapEditor objectmapEditor;
	private static Display display;
	
	private AddOnUtility()
	{}
	
	public static AddOnUtility getInstance()
	{
		return utility;	
	}
	
	public void setEditorInput(IEditorInput eInput)
	{
		editorInput = eInput;
	}
	
	public void setTestCaseEditor(TCEditor editor)
	{
		testcaseEditor = editor;
	}
	
	public void setDisplay(Display d)
	{
		display = d;
	}
	
	public void setObjectMapEditor(ObjectMapEditor editor)
	{
		objectmapEditor = editor;
	}
	
	public void setTestCaseEditorAndDisplay(TCEditor editor, IEditorInput eInput, Display d)
	{
		testcaseEditor = editor;
		editorInput = eInput;
		display = d;
	}
	
	public void setObjectMapEditorAndDisplay(ObjectMapEditor editor, IEditorInput eInput, Display d)
	{
		objectmapEditor = editor;
		editorInput = eInput;
		display = d;
	}
	
	public void openCloseServer(boolean open)
	{
		try
		{
			if(open)
			{
				/*Check if server is stopped*/
				if(server.isStopped())
				{
					server.stop();
					server.start();
					Thread.sleep(2*1000); //Session creation timeout
				}
			}
			else
			{
				/*Check if server is started*/
				if(server.isStarted())
				{
					server.stop();
					Thread.sleep(2*1000); //Session clearing timeout
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : openCloseServer()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void findElement(String xpath)
	{
		try
		{
			System.out.println("Status : " + server.isStarted());
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("value", xpath);
	        jsonObj.put("from","highlightElement");
	        WebSocketHandlerForAddIn.sendMsg(jsonObj.toJSONString()); 
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : findElement()] - Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public void getXPath()
	{
		try
		{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("from","getxpath");
			WebSocketHandlerForAddIn.sendMsg(jsonObj.toJSONString());
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : getXPath()] - Exception :" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void start_stop_Recording(boolean start_rec)
	{
		try
		{
			if(start_rec)
			{
				WebSocketHandlerForAddIn.initializeEntities();
				WebSocketHandlerForAddIn.setRecorder(start_rec);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("❮ from","iamrecording");
				WebSocketHandlerForAddIn.sendMsg(jsonObj.toJSONString());
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : start_stop_Recording()] - Exception() - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public List<VerifyElementsClass> verifyAllElements(List<OMDetails> listOMDetails)
	{
		try
		{
			WebSocketHandlerForAddIn.initializeEntities();
			for(int i=0;i<listOMDetails.size();i++)
			{
				OMDetails details = listOMDetails.get(i);
				if(details.locatorType.equalsIgnoreCase("xpath"))
				{
					JSONObject sendJSON = new JSONObject();
					sendJSON.put("from","verifyAll");
					sendJSON.put("value", details.locatorInfo);
					sendJSON.put("rowNum",i);
					WebSocketHandlerForAddIn.sendMsg(sendJSON.toJSONString());
				}
				else
				{
					AddInProgressBar.updateProgressBar(i+1);
				}
			}
			Thread.sleep(1000);
			List<VerifyElementsClass> list = WebSocketHandlerForAddIn.getVerifyEltList();
			if(editorInput instanceof ObjectMapEditorInput)
			{
				ObjectMapEditor editor = (ObjectMapEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
																	.getActivePage().findEditor(editorInput);
				for(int i=0;i<list.size();i++)
				{
					VerifyElementsClass verifyClass = list.get(i);
					editor.updateTableRow(verifyClass.rowNo, verifyClass.status);
				}
			}
			AddInProgressBar.updateButtonDisplay(true);
			return list;
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : verifyAllElements()] - Exception " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
	 * All Methods to be used by the WebSocketHandlerForAddin
	 * Including data members and methods 
	 */
	
	private static ArrayList<OMDetails> omDetails = new ArrayList<OMDetails>();
	private static ArrayList<TCStepsGSON> steps = new ArrayList<TCStepsGSON>();
	private static ArrayList<VerifyElementsClass> verifyEltsList = new ArrayList<VerifyElementsClass>();
	private static boolean isRecorded = false;
	private static VerifyElementsClass verifyStandAlone = null;
	
	public void initializeEntities()
	{
		omDetails = new ArrayList<OMDetails>();
		steps = new ArrayList<TCStepsGSON>();
		verifyEltsList = new ArrayList<VerifyElementsClass>();
		verifyStandAlone = new VerifyElementsClass();
	}
	
	public void addRecordedContents(final TCStepsGSON step, final OMDetails details)
	{
		try
		{
			if(editorInput instanceof TestCaseEditorInput)
			{
				//TCEditor editor = (TCEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(editorInput);
				
				/*Call Test Case Editor Method to add contents to editor*/
				display.asyncExec(new Runnable() {	
					public void run() {
						testcaseEditor.addContentsToTableGrid(step, details);
					}
				});
			}
			steps.add(step);
			omDetails.add(details);
		}
		catch(Exception e)
		{
			System.out.println("["+getClass().getName() +" : addRecordedContents()] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
}
