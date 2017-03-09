package com.automatics.utilities.chrome.extension;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.HeadlessException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebSocket
public class WebSocketHandlerForAddIn {
	
	private static ArrayList<OMDetails> omDetails = new ArrayList<OMDetails>();
	private static ArrayList<TCStepsGSON> steps = new ArrayList<TCStepsGSON>();
	private static ArrayList<VerifyElementsClass> verifyEltsList = new ArrayList<VerifyElementsClass>();
	private static boolean isRecorded = false;
	private static VerifyElementsClass verifyStandAlone = null;
	
	private static int OBJ_ELT_COUNT = 1;
	
	private AddOnUtility addOnUtility = AddOnUtility.getInstance();
	
	public static void initializeEntities()
	{
		omDetails = new ArrayList<OMDetails>();
		steps = new ArrayList<TCStepsGSON>();
		verifyEltsList = new ArrayList<VerifyElementsClass>();
		verifyStandAlone = new VerifyElementsClass();
	}
	
	public static ArrayList<OMDetails> getOMDetails()
	{
		return omDetails;
	}
	
	public static ArrayList<TCStepsGSON> getSteps()
	{
		return steps;
	}
	
	public static void setVerifyElementsClass()
	{
		verifyStandAlone = null;
	}
	public static VerifyElementsClass getVerifyElementsClass()
	{
		return verifyStandAlone;
	}
	
	public static void setRecorder(boolean recording)
	{
		isRecorded = recording;
	}
	
	public static boolean getRecorder()
	{
		return isRecorded;
	}
	
	public static ArrayList<VerifyElementsClass> getVerifyEltList()
	{
		return verifyEltsList;
	}
	
	
	public static Session sess;
    public static Session getSess() 
    {
		return sess;
	}

	public static void setSess(Session sess) 
	{
		WebSocketHandlerForAddIn.sess = sess;
	}
	
	@OnWebSocketClose
    public void onClose(int statusCode, String reason) 
	{
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);    
    }

    @OnWebSocketError
    public void onError(Throwable t) 
    {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
    	
    	System.out.println("setting session " + session);
    	setSess(session);
    	sess = session;
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try 
        {
        	session.getRemote().sendString("145236");
        } 
        catch(IOException e) 
        {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) throws ParseException, IOException {
        
	        if(!message.equals("undefined"))
	        {
	        	//System.out.println("❮ Message: " + message);
		        JSONObject obj = new JSONObject();
		        JSONParser parser = new JSONParser();
		        obj =(JSONObject) parser.parse(message);
		        
		        byte[] byteText = message.getBytes("UTF-8");
		        //To get original string from byte.
		        String originalString= new String(byteText , "UTF-8");
		        System.out.println(originalString);
		        
		        if((obj.get("from").toString()).equals("find"))
		    	 {
		        	if(getRecorder())
		        	{
		        		String dom = obj.get("dom").toString();
		        		String objName = dom.split("(\\s|>)+")[0];
		        		objName = objName.trim();
		        		objName = objName.substring(1, objName.length());
		        		objName = objName + OBJ_ELT_COUNT; OBJ_ELT_COUNT++;
		        		
		        		String pageName = obj.get("title").toString();
		        		pageName = pageName.replace(" ", "");
		        		pageName = pageName.replace("-", "");
		        		
		        		//Add regex to replace all special characters
		        		/*Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
		        		Matcher match= pt.matcher(pageName);
		        		while(match.find())
		        		{
		        			String s= match.group();
		        			pageName = pageName.replace("\\"+s, "_");
		        		}*/
		        		
			        	//Create Object Map Details
			        	OMDetails omDetails = new OMDetails();
			        	omDetails.pageName = pageName;
			        	omDetails.objName = objName; 
			        	omDetails.locatorInfo = obj.get("trgt").toString();
			        	omDetails.locatorType = "xpath";
			        	
			        	//Create Test Case Step
			        	TCStepsGSON step = new TCStepsGSON();
			        	step.stepNo = -1; 
			        	step.stepOperation = obj.get("cmd").toString();
			        	step.stepPageName = pageName;
			        	step.stepObjName = objName; 
			        	step.stepArgument = obj.get("val").toString(); 
			        	step.omName = "";
			        	
			        	addOnUtility.addRecordedContents(step, omDetails);
		        	}
		    	 }
		        
		        /*NOT IS USE CURRENTLY*/
		        else if((obj.get("from").toString()).equals("xpath")) 
		        {
		        	//xpathTextField.setText(obj.get("xp").toString());
		        }
		        
		        else if(obj.get("from").toString().equals("verifyAllResult"))
		        {
		        	long l = (Long)obj.get("rowNum");
		        	int rowNum = (int) l;
		        	
		        	VerifyElementsClass verifyEltClass = new VerifyElementsClass();
		        	verifyEltClass.rowNo = rowNum;
		        	verifyEltClass.status = obj.get("result").toString().equalsIgnoreCase("found");
		        	verifyEltsList.add(verifyEltClass);
		        	AddInProgressBar.updateProgressBar(rowNum+1);
		        } 
		        
		        /*NOT IN USE CURRENTLY*/
		        else if((obj.get("from").toString()).equals("perform")) 
		        {
		        	if(obj.get("status").toString().equals("found")) 
		        	{
		        		String actualValue = obj.get("val").toString();
		        		//String expectedValue = verifyTFvalue.getText();
		        		String expectedValue = "";
		        		if(actualValue.equals(expectedValue)){
		        			System.out.println("Verify_Text done");
		        		} else {
		        			System.out.println("Verify_Text failed");
		        		}
		        	} else {
		        		System.out.println("Element not found");
		        	}
		        }
		        
		        else if((obj.get("from").toString()).equals("addToRepo"))
		        {
		        	OMDetails details = new OMDetails();
		        	details.pageName = ""; //GET THIS
		        	details.objName = ""; //Parse DOM
		        	details.locatorInfo = obj.get("xpath").toString();
		        	details.locatorType  = "xpath";
		        	
		        	//Send acknowledgement
		        	JSONObject reply = new JSONObject();
		        	reply.put("from","repositoryDesktop");
		        	reply.put("status","success");
		        	WebSocketHandlerForAddIn.sendMsg(reply.toJSONString());
		        	
		        }
		        else if((obj.get("from").toString()).equals("highlightElementResult"))
		        {
		        	//Find element (Found - Not Found)
		        	verifyStandAlone = new VerifyElementsClass();
		        	verifyStandAlone.rowNo = -999;
		        	verifyStandAlone.status = obj.get("result").toString().equals("found");
		        }
		        
	        }
    }
    
    public static void sendMsg(String msg) 
    {
    	try
    	{
    		System.out.println("Sending to AddIn - Session [" + getSess() + "]");
    		getSess().getRemote().sendString(msg);
    	}
    	catch(Exception e)
    	{
    		System.out.println("[WebSocketHandlerForAddin : sendMsg()] - Exception : " + e.getMessage());
    		e.printStackTrace();
    	}
    }
}
