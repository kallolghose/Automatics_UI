package com.automatics.packages.check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.automatics.mongo.packages.AutomaticsDBOperationQueries;
import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.utilities.helpers.Utilities;
import com.mongodb.*;

public class Check 
{
	public static void main(String[] args) throws IOException {
		//DB db = Utilities.getMongoDB();
		//System.out.println(AutomaticsDBTestCaseQueries.getTC(db, "FirstTC").toString());
//		File file = new File("RequiredFiles\\beforecontent.txt");
//		BufferedReader reader = new BufferedReader(new FileReader(file));
//		String str = "", tmp;
//		while((tmp = reader.readLine()) !=null)
//		{
//			str = str + tmp;
//		}
//		reader.close();
//		System.out.println(str);
		
		System.out.println(AutomaticsDBOperationQueries.getOPN(Utilities.getMongoDB(), "Open_URL").toString());
	}
}
