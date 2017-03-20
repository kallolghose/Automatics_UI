package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.automatics.utilities.extraUIs.TestCaseDetails;
import com.automatics.utilities.extraUIs.TestSuiteDetails;
import com.automatics.utilities.git.GitUtilities;

public class NewTestSuite extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		GitUtilities gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		gitUtil.initExistingRepository();
		gitUtil.performPull();
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		TestSuiteDetails testsuitePop = new TestSuiteDetails(window.getShell());
		testsuitePop.open();
		return null;
	}

}
