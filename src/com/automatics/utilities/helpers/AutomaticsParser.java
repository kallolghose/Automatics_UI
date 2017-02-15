package com.automatics.utilities.helpers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class AutomaticsParser 
{
	private InputStream tcStream =  null;
	private List<TCStepsGSON> listOftcGson;
	
	public void setTcStream(InputStream tcStream) {
		this.tcStream = tcStream;
	}

	public List<TCStepsGSON> getListOftcGson() {
		return listOftcGson;
	}
	
	public void displayAllDetails()
	{
		for(TCStepsGSON tcsteps : listOftcGson)
		{
			System.out.println(tcsteps.stepNo + "  " + tcsteps.stepOperation + "  " + tcsteps.stepPageName + "  " + tcsteps.stepObjName + " "
					 + "  " + tcsteps.stepArgument);
		}
	}

	public List<TCStepsGSON> parseContentofTestCase()
	{
		try
		{
			Pattern REGEX_TAG = Pattern.compile("\\((.+?)\\)");
			BufferedReader br = new BufferedReader(new InputStreamReader(tcStream));
			String stmt = "";
			boolean startParsing = false;
			listOftcGson = new ArrayList<TCStepsGSON>();
			int stepNo = 1;
			
			while((stmt=br.readLine())!=null)
			{
				TCStepsGSON tcStepGson = new TCStepsGSON();
				if(stmt.trim().contains("try"))
				{
					startParsing = true;
				}
				
				if(stmt.trim().contains("@AfterTest"))
				{
					startParsing = false;
				}
				
				if(startParsing)
				{
					if(stmt.contains("Web."))
					{
						//Get the operation names
						String tmpOP[] = stmt.split("\\(");
						String opName = tmpOP[0].split("Web.")[1];
						//Get the parameters
						final List<String> matchValues = new ArrayList<String>();
						final Matcher matcher = REGEX_TAG.matcher(stmt);
						while(matcher.find())
						{
							matchValues.add(matcher.group(1));
						}
						
						if(opName.equals("LaunchBrowser") || opName.equals("Set_BrowserPreference"))
						{
							tcStepGson.stepNo = stepNo;
							tcStepGson.stepOperation = opName;
							String strVals[] = matchValues.get(0).split(",");
							tcStepGson.stepArgument = strVals[1];
							tcStepGson.stepPageName = "NA";
							tcStepGson.stepObjName = "NA";
							tcStepGson.stepVarName = strVals[2];
						}
						else
						{
							tcStepGson.stepNo = stepNo;
							tcStepGson.stepOperation = opName;
							
							//Process the matchValue as per some value
							String strVals[] = matchValues.get(0).split(",");
							if(strVals.length<3)
							{
								tcStepGson.stepPageName = "NA";
								tcStepGson.stepObjName = "NA";
								if(strVals.length>1)
									tcStepGson.stepArgument = strVals[1];
								else
									tcStepGson.stepArgument = "";
								tcStepGson.stepVarName = "";
							}
							else
							{
								String pgObj[] = strVals[1].split("\\.")[1].split("__");
								tcStepGson.stepPageName = pgObj[0];
								tcStepGson.stepObjName = pgObj[1];
								tcStepGson.stepArgument = strVals[3];
								tcStepGson.stepVarName = "";
							}
						}
						stepNo++;
						listOftcGson.add(tcStepGson);
					}
				}
			}
			return listOftcGson;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : parseContentToTestCaseOP()] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
