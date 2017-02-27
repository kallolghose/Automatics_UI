package com.automatics.utilities.git;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class PushAllCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		GitUtilities gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		gitUtil.initExistingRepository();
		gitUtil.performPush();
		return null;
	}

}
