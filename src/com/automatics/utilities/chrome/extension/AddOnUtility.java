package com.automatics.utilities.chrome.extension;

import java.util.List;

import org.json.simple.JSONObject;

import com.automatics.utilities.gsons.objectmap.OMDetails;

public class AddOnUtility 
{
	
	private static AddOnUtility utility = new AddOnUtility();
	private static JettyServer server = new JettyServer();
	
	private AddOnUtility()
	{}
	
	public static AddOnUtility getInstance()
	{
		return utility;	
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
	
	public void verifyAllElements(List<OMDetails> listOMDetails)
	{
		try
		{
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
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : verifyAllElements()] - Exception " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
}
