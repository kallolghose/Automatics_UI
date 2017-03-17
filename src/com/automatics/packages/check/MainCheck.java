package com.automatics.packages.check;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.mongodb.DB;
public class MainCheck 
{
	public static void main(String[] args) 
	{
		DB db = AutomaticsDBConnection.getConnection("10.13.4.87" , 27017, "automatics_db");
		AutomaticsDBTestCaseQueries.getAllTC(db);
	}
}
