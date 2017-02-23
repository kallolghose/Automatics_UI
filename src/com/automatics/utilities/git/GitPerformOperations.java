package com.automatics.utilities.git;

public class GitPerformOperations
{
	public static void main(String[] args) 
	{
		GitUtilities gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties("git_config.properties");
		//gitUtil.init();
		//gitUtil.createLocalRepositary();
		//gitUtil.cloneRepository();

		gitUtil.initExistingRepository();
		//gitUtil.performPull();
//		gitUtil.addToRepository(".");
//		gitUtil.performCommit();
//		gitUtil.performPush();
		gitUtil.getSync();
	}
}
