package com.automatics.utilities.helpers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.DataLine;

import org.eclipse.jface.text.hyperlink.HyperlinkManager.DETECTION_STRATEGY;

import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class AutomaticsParser 
{
	private InputStream tcStream =  null;
	private InputStream omStream = null;
	private List<TCStepsGSON> listOftcGson;
	private List<OMDetails> listOfomGson;
	
	public void setTcStream(InputStream tcStream) {
		this.tcStream = tcStream;
	}

	public void setOmStream(InputStream omStream)
	{
		this.omStream = omStream;
	}
	
	public List<TCStepsGSON> getListOftcGson() {
		return listOftcGson;
	}
	
	public void displayAllTestCaseSteps()
	{
		for(TCStepsGSON tcsteps : listOftcGson)
		{
			System.out.println(tcsteps.stepNo + "  " + tcsteps.stepOperation + "  " + tcsteps.stepPageName + "  " + tcsteps.stepObjName + " "
					 + "  " + tcsteps.stepArgument);
		}
	}

	public void displayAllObjectMapDetails()
	{
		for(OMDetails omDetails : listOfomGson)
		{
			System.out.println(omDetails.pageName + "   " + omDetails.objName + "   " + omDetails.locatorInfo + "   " 
								+ omDetails.locatorType);
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
							String stepArgument = strVals[1].trim();
							tcStepGson.stepArgument = stepArgument.substring(1,stepArgument.length()-1);
							tcStepGson.stepPageName = "NA";
							tcStepGson.stepObjName = "NA";
							String stepVarName = strVals[2].trim();
							tcStepGson.stepVarName = stepVarName.substring(1, stepVarName.length()-1);
							tcStepGson.omName = "";
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
								tcStepGson.omName = "";
								if(strVals.length>1)
								{
									String stepArgument = strVals[1].trim();
									tcStepGson.stepArgument = stepArgument.substring(1,stepArgument.length()-1);
								}
								else
									tcStepGson.stepArgument = "";
								tcStepGson.stepVarName = "";
							}
							else
							{
								String pgObj[] = strVals[1].split("\\.")[1].split("__");
								tcStepGson.stepPageName = pgObj[0];
								tcStepGson.stepObjName = pgObj[1];
								String omName = strVals[1].split("\\.")[0];
								tcStepGson.omName = omName.trim();
								
								if(strVals.length>3)
								{
									String stepArgument = strVals[3].trim();
									tcStepGson.stepArgument = stepArgument.substring(1,stepArgument.length()-1);
									tcStepGson.stepVarName = "";
								}
								else
								{
									tcStepGson.stepArgument = "";
									tcStepGson.stepVarName = "";
								}
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
	
	public List<OMDetails> parseContentsOfObjectMap()
	{
		try
		{
			Pattern REGEX_TAG = Pattern.compile("\\((.+?)\\)");
			BufferedReader br = new BufferedReader(new InputStreamReader(omStream));
			String stmt = "";
			listOfomGson = new ArrayList<OMDetails>();
			boolean startParsing = false;
			boolean element_detected_firstLine = false, element_detected_secondLine = false;
			OMDetails temp = null;
			while((stmt = br.readLine())!=null)
			{
				if(element_detected_secondLine)
				{
					if(stmt.contains("WebElement"))
					{
						String dataInLine[] = stmt.trim().split(" ");
						String pgObj[] = dataInLine[3].split("__");
						temp.pageName = pgObj[0];
						temp.objName = pgObj[1];
						temp.objName = temp.objName.substring(0,temp.objName.length()-1);
						listOfomGson.add(temp);
						element_detected_secondLine = false;
						startParsing = false;
					}
				}
				
				if(stmt.contains("@FindBy"))
				{
					startParsing = true;
					element_detected_firstLine = true;
					element_detected_secondLine = true;
				}
				if(startParsing && element_detected_firstLine)
				{
					temp = new OMDetails();
					final List<String> matchValues = new ArrayList<String>();
					final Matcher matcher = REGEX_TAG.matcher(stmt);
					while(matcher.find())
					{
						matchValues.add(matcher.group(1));
					}
					
					String locatorType = matchValues.get(0).split("=")[0].trim();
					
					Pattern locatorInfoPattern = Pattern.compile("\"(.+?)\"");
					final List<String> locatorInfoMatchValues = new ArrayList<String>();
					final Matcher locatorInfoMatcher = locatorInfoPattern.matcher(stmt);
					while(locatorInfoMatcher.find())
					{
						locatorInfoMatchValues.add(locatorInfoMatcher.group(1));
					}
					
					temp.locatorType = locatorType;
					temp.locatorInfo = locatorInfoMatchValues.get(0);
					element_detected_firstLine = false;
				}
			}
			return listOfomGson;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : parseContentsOfObjectMap()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
