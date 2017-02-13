package com.automatics.packages.check;

import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.helpers.Utilities;

public class PhirseCheck
{
	public static void main(String[] args) 
	{
		System.out.println(AutomaticsDBTestCaseQueries.getAllTC(Utilities.getMongoDB()));
		TCGson tcGson = Utilities.getGSONFromJSON(AutomaticsDBTestCaseQueries.getTC(Utilities.getMongoDB(), "FirstTC").toString(),TCGson.class);
		System.out.println(tcGson.projectName);
	}
}
