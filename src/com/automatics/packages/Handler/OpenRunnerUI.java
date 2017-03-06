package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.automatics.utilities.extraUIs.NewObjectMapWindow;
import com.automatics.utilities.runner.NewRunnerUI;
import com.automatics.utilities.runner.RunnerShell;

public class OpenRunnerUI extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbench workBench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workBench.getActiveWorkbenchWindow();
		//RunnerShell runnerShell = new RunnerShell(window.getShell().getDisplay());
		//runnerShell.open();
		NewRunnerUI runnerUI = new NewRunnerUI();
		runnerUI.open();
		return null;
	}

}
