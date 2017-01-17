package com.automatics.utilities.helpers;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;


public class Installables 
{
	private static String packageLocation = "D:\\Automatics\\";
	public static PrintStream out = null;
	
	public static String getPackageLocation()
	{
		return packageLocation;
	}
	
	public static void addInstallingPath()
    {
    	File file = new File(packageLocation);
    	if(!file.exists())
    	{
    		file.mkdirs();
    		File ipFile = new File(packageLocation + "\\config.properties");
    		try
    		{
    			PrintWriter writer = new PrintWriter(ipFile);
    			writer.println("IP=http://10.13.67.174");
    			writer.println("Automation_Project_Path=D:\\Automation\\Automation_Suite");
    			writer.close();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	//Add tools tools folder
    	/*
    	 	CLICK=Click
			VERIFY= Verify_ElementPresent
			FILL= EnterText
			PERFORM= customAction
			SELECT= Click
    	 */
    	
    	file = new File(packageLocation + "\\Tools\\Required");
    	if(!file.exists())
    	{
    		file.mkdirs();
    		File ipFile = new File(packageLocation  + "\\Tools\\Required" + "\\FunctionMapper.txt");
    		try
    		{
    			PrintWriter writer = new PrintWriter(ipFile);
    			writer.println("CLICK=Click");
    			writer.println("VERIFY= Verify_ElementPresent");
    			writer.println("FILL= EnterText");
    			writer.println("PERFORM= customAction");
    			writer.println("SELECT= Click");
    			writer.close();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void loadProperties()
    {
    	try
    	{
    		File file = new File(packageLocation + "\\config.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
	
    public static boolean createLogFileAtLocation()
    {
    	//Add Mapper
    	
        boolean retVal = false;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String fileName = packageLocation + "\\" + timeStamp + "_log" + ".log";
        try
        {
            File file = new File(packageLocation);
            if(!file.exists())
            {
                retVal = file.mkdirs();
            }
            out = new PrintStream(new FileOutputStream(fileName));
            System.setOut(out);
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File Cannot Be Created");
            System.exit(1);
        }
        return retVal;
    }
}
