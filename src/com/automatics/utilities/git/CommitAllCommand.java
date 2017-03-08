package com.automatics.utilities.git;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class CommitAllCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		try
		{
			GitUtilities gitUtil = new GitUtilities();
			gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
			gitUtil.initExistingRepository();
			gitUtil.performCommit();
			gitUtil.performPush();
			MessageDialog commitDialog = new MessageDialog(window.getShell(), "Information", null, 
					"Commit and Push Performed", MessageDialog.INFORMATION, new String[]{"OK"}, 0);
			commitDialog.open();
		}
		catch(Exception e)
		{
			MessageDialog errorDialog = new MessageDialog(window.getShell(), "Error", null, e.getMessage(), 
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			errorDialog.open();
		}
		return null;
	}

}
