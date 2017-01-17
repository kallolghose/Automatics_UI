package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.automatics.utilities.extraUIs.ProjectDetails;

public class NewProject extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		// TODO Auto-generated method stub
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ProjectDetails projectPop = new ProjectDetails(window.getShell());
        projectPop.open();
		return null;
	}

}
