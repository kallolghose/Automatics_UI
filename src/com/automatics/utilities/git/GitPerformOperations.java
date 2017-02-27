package com.automatics.utilities.git;

import com.automatics.utilities.helpers.Utilities;

public class GitPerformOperations
{
	public static void main(String[] args) 
	{
		GitUtilities gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		//gitUtil.init();
		//gitUtil.createLocalRepositary();
		//gitUtil.cloneRepository();

		gitUtil.initExistingRepository();
		//gitUtil.performSpecificPull("com.automaticsV1.3/cucumber_testng.xml");
		//gitUtil.performSpecificCommit("com.automaticsV1.3/cucumber_testng.xml");
		//gitUtil.performPush();
		//gitUtil.performPull();
//		gitUtil.addToRepository(".");
//		gitUtil.performCommit();
//		gitUtil.performPush();
		String currentFileName = Utilities.TESTCASE_FILE_LOCATION + "FirstTC.java";
		//System.out.println(gitUtil.getSync(currentFileName));
		System.out.println(gitUtil.getSync("com.automaticsV1.3/testng.xml"));
		//System.out.println(gitUtil.getStatus().getModified());
	}
}
