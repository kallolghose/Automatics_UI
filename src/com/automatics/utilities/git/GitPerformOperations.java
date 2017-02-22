package com.automatics.utilities.git;

public class GitPerformOperations
{
	public static void main(String[] args) 
	{
		GitUtilities git = new GitUtilities();
		git.loadAndSetProperties("git_config.properties");
		//git.init();
		//git.createLocalRepositary();
		git.cloneRepository();
		//git.performPull();
	}
}
