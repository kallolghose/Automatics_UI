package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.automatics.packages.Views.TC_TS_List;
import com.automatics.utilities.extraUIs.ProjectDetails;
import com.automatics.utilities.extraUIs.TestCaseDetails;

public class NewTestCase extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		TestCaseDetails testcasePop = new TestCaseDetails(window.getShell());
        testcasePop.open();
		return null;
	}

}
