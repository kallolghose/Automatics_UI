package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.automatics.packages.Views.TC_TS_List;
import com.automatics.utilities.extraUIs.ProjectDetails;
import com.automatics.utilities.extraUIs.TestCaseDetails;
import com.automatics.utilities.git.GitUtilities;

public class NewTestCase extends AbstractHandler {

	public static boolean CREATE_TCONLY_FLAG = false;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		GitUtilities gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		gitUtil.initExistingRepository();
		gitUtil.performPull();
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		TestCaseDetails testcasePop = new TestCaseDetails(window.getShell(), CREATE_TCONLY_FLAG);
        testcasePop.open();
		return null;
	}

}
