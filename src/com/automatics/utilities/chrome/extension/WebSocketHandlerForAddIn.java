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


@WebSocket
public class WebSocketHandlerForAddIn {
	
	private static ArrayList<OMDetails> omDetails = new ArrayList<OMDetails>();
	private static ArrayList<TCStepsGSON> steps = new ArrayList<TCStepsGSON>();
	private static ArrayList<VerifyElementsClass> verifyEltsList = new ArrayList<VerifyElementsClass>();
	private static boolean isRecorded = false;
	private static VerifyElementsClass verifyStandAlone = null;
	
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
        
    	System.out.println("Hello");
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
			        	//Create Object Map Details
			        	OMDetails omDetails = new OMDetails();
			        	omDetails.pageName = obj.get("title").toString();
			        	omDetails.objName = ""; //Need to find out
			        	omDetails.locatorInfo = obj.get("trgt").toString();
			        	omDetails.locatorType = "xpath";
			        	
			        	//Create Test Case Step
			        	TCStepsGSON step = new TCStepsGSON();
			        	step.stepNo = -1; 
			        	step.stepOperation = obj.get("cmd").toString();
			        	step.stepPageName = obj.get("title").toString();
			        	step.stepObjName = ""; //Need to find out
			        	step.stepArgument = obj.get("val").toString(); 
			        	step.omName = "";
		        	}
		        	 /*
		        	 Vector<Object> data = new Vector<Object>();
		        	 data.add(obj.get("cmd"));
					 data.add(obj.get("trgt"));
					 data.add(obj.get("val"));
					 if(isRecording==true)
					 {
						 dm.addRow(data);
					 }*/
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
