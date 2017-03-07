package com.automatics.packages.check;

import java.io.File;

public class GetAllFiles 
{
	public static void listFilesForFolder(final File folder) 
	{
	    for (final File fileEntry : folder.listFiles()) 
	    {
	        if (fileEntry.isDirectory()) 
	        {
	            listFilesForFolder(fileEntry);
	        } 
	        else 
	        {
	            System.out.println("jar/jetty_jars/" + fileEntry.getName() + ",");
	        }
	    }
	}
	
	public static void main(String[] args) {
		listFilesForFolder(new File("D:/KG00360770/ATT/Automatic_DC/Automatics/jar/jetty_jars"));
	}
	
}
