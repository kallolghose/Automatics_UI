package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.automatics.utilities.extraUIs.NewObjectMapWindow;

public class NewObjectMap extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbench workBench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workBench.getActiveWorkbenchWindow();
		NewObjectMapWindow newObjectMap = new NewObjectMapWindow(window.getShell());
		newObjectMap.open();
		return null;
	}

}
