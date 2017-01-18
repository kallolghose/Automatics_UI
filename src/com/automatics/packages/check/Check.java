package com.automatics.packages.check;

import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.utilities.helpers.Utilities;
import com.mongodb.*;

public class Check 
{
	public static void main(String[] args) {
		DB db = Utilities.getMongoDB();
		System.out.println(AutomaticsDBTestCaseQueries.getTC(db, "FirstTC").toString());
	}
}
