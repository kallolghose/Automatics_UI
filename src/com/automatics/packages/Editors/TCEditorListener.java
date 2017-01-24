package com.automatics.packages.Editors;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

public class TCEditorListener implements IPartListener2
{
	public void partActivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("Editor Focus Lost");
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partHidden(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partVisible(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}
	
}
