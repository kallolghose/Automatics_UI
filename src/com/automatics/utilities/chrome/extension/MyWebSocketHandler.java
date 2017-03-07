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

//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;


@WebSocket
public class MyWebSocketHandler extends Test   {
	
	public static Session sess;
	

    public static Session getSess() {
		return sess;
	}

	public static void setSess(Session sess) {
		MyWebSocketHandler.sess = sess;
	}
	
	

	@OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        //listenButton.setText("Listennn");
        //label.setText("Not Listening");
        //isClosed =true;
        
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
    	
    	System.out.println("setting session " + session);
    	setSess(session);
    	sess = session;
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {

        	session.getRemote().sendString("145236");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) throws ParseException, IOException {
        
	        if(!message.equals("undefined"))
	        {
	        	System.out.println("❮ Message: " + message);
		        //label.setText(message);
		        JSONObject obj = new JSONObject();
		        JSONParser parser = new JSONParser();
		        obj =(JSONObject) parser.parse(message);
		        
		        byte[] byteText = message.getBytes("UTF-8");
		      //To get original string from byte.
		        String originalString= new String(byteText , "UTF-8");
		        System.out.println(originalString);
		        
		        
		        //System.out.println(obj.get("dom"));
		        Vector<Object> data = new Vector<Object>();
		        //String ss =obj.get("from").toString();
		        if((obj.get("from").toString()).equals("find"))
		    	 {
		        	 data.add(obj.get("cmd"));
					 data.add(obj.get("trgt"));
					 data.add(obj.get("val"));
					 if(isRecording==true)
					 {
						 //recordTable.getModel().getValueAt(0, 0);
						 dm.addRow(data);
					 }
		    	 }
		        else if((obj.get("from").toString()).equals("xpath"))
		        {
		        	xpathTextField.setText(obj.get("xp").toString());
		        }
		        else if(obj.get("from").toString().equals("verifyAllResult"))
		        {
		        	//JOptionPane.showMessageDialog(null, "herer");
		        	//for(int rowCount=1;rowCount<recordTable.getRowCount();rowCount++)
					//{
		        		//if(rowCount == (long)obj.get("rowNum"));
		        		//{
			        	long l = (Long)obj.get("rowNum");
			        	int rowNum = (int) l;
		        			//JOptionPane.showMessageDialog(null, "setting for "+(long)obj.get("rowNum")+" AS "+obj.get("result"));
		        		recordTable.getModel().setValueAt(obj.get("result"), rowNum, 3);
		        		//}
					//}
		        } else if((obj.get("from").toString()).equals("perform"))
		        {
		        	if(obj.get("status").toString().equals("found")) {
		        		String actualValue = obj.get("val").toString();
		        		String expectedValue = verifyTFvalue.getText();
		        		
		        		if(actualValue.equals(expectedValue)){
		        			System.out.println("Verify_Text done");
		        		} else {
		        			System.out.println("Verify_Text failed");
		        		}
		        	} else {
		        		System.out.println("Element not found");
		        	}
		        }else if((obj.get("from").toString()).equals("addToRepo"))
		        {
		        	//send acknowledgement
		        	JSONObject reply = new JSONObject();
		        	reply.put("from","repositoryDesktop");
		        	reply.put("status","success");
//		        	sendMsg(reply.toJSONString());
//		        	sess.getRemote().sendString(reply.toJSONString());
		        	
		        	MyWebSocketHandler.sendMsg(reply.toJSONString());
		        	
		        	
		        	/*
		        	Process to add object to repository
		        	*/
		        }
		        else if((obj.get("from").toString()).equals("highlightElementResult"))
		        {
		        		JOptionPane.showMessageDialog(null, obj.get("result").toString());
		        }
		        
		//        public static boolean Verify_Text(String sTestName, WebElement oEle, String sObjStr, String sExpText) throws HeadlessException, IOException, AWTException, InterruptedException, ClassNotFoundException  {
		//    		
		//    		String sDesc, sActVal = null, sExpVal; boolean bStatus = false;
		//    				
		//    		sDesc = Reporter.log(sTestName) + " Object : " + sObjStr + " -> ( " + UI.getBy(sObjStr) + " )" ;
		//    	
		//    		try { 
		//    			
		//    			sExpText = UI.validateUserInput(sTestName, sExpText);
		//    			sExpVal = Reporter.filterUserInput(sExpText);
		//    			UI.checkReady(sTestName, oEle);
		//    			
		//    			sActVal = oEle.getText();
		//    			bStatus = sExpVal.equalsIgnoreCase(sActVal);
		//    			
		//    			Reporter.print(sTestName, sExpText, sDesc, sExpVal, sActVal, bStatus);
		//    			
		//    		}  catch(Exception e) {
		//    			
		//    	    	if (Utils.handleIntermediateIssue()) { Verify_Text(sTestName, oEle, sObjStr, sExpText); }
		//    			Reporter.exceptionHandler(sTestName, e, sDesc);
		//    		}
		//    		return bStatus;
		//    		
		//    	}
		        
		        
			
		        }
		 
        
        
        
    }
    
    public static void sendMsg(String msg) 
    {
    	try{
    		//System.out.println("sedning");
    		//JOptionPane.showMessageDialog(null, "sending to findd2");
    	System.out.println("Sending to session : " + getSess());
    		getSess().getRemote().sendString(msg);
    	
//    		sess.getRemote().sendString(msg);
    	}
    	catch(Exception e)
    	{
    		
    		JOptionPane.showMessageDialog(null, "Session Closed. hit 'Listen' and check if addon is listening");
    		recordButton.setBackground(Color.WHITE);
    		System.out.println("setting closed session");
    		isRecording = !isRecording;
    		System.out.println(isRecording);
    		
    	}
    }
}
