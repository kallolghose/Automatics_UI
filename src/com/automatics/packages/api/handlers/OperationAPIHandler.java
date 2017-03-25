package com.automatics.packages.api.handlers;

import javax.json.JsonArray;

import com.automatics.mongo.api.OperationsAPI;
import com.automatics.utilities.gsons.operation.OperationGSON;
import com.google.gson.Gson;

public class OperationAPIHandler 
{
	private static OperationAPIHandler instance = new OperationAPIHandler();
	private OperationGSON allOperations[];
	private OperationAPIHandler()
	{
		Gson gson = new Gson();
		JsonArray operationJsonArr = OperationsAPI.getAllOperations();
		allOperations = gson.fromJson(operationJsonArr.toString(), OperationGSON[].class);
	}
	
	public static OperationAPIHandler getInstance()
	{
		return instance;
	}
	
	public OperationGSON[] getAllOperations()
	{
		return allOperations;
	}
	
	public OperationGSON getSpecificOperation(String opnName)
	{
		for(int i=0;i<allOperations.length;i++)
		{
			if(allOperations[i].opnName.equalsIgnoreCase(opnName))
			{
				return allOperations[i];
			}
		}
		return null;
	}
}
