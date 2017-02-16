package com.automatics.packages;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Editors.TestSuiteEditorInput;

public class EditorListeners implements IPartListener2
{
	public void partActivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		try
		{
			//System.out.println("Some Val : " + partRef.getId() + "   " + partRef.getTitle());
			if(partRef instanceof IEditorReference)
			{
				IWorkbench workBench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workBench.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IEditorReference editor = (IEditorReference)partRef;
				
				if(editor.getEditorInput() instanceof TestCaseEditorInput)
				{
					IPerspectiveRegistry perspectiveRegistry = window.getWorkbench().getPerspectiveRegistry();
					IPerspectiveDescriptor openAutomaticsPerspective = perspectiveRegistry.findPerspectiveWithId(Perspective.perspectiveID);
					page.setPerspective(openAutomaticsPerspective);
				}
				else if(editor.getEditorInput() instanceof TestSuiteEditorInput)
				{
					IPerspectiveRegistry perspectiveRegistry = window.getWorkbench().getPerspectiveRegistry();
					IPerspectiveDescriptor openAutomaticsPerspective = perspectiveRegistry.findPerspectiveWithId(Perspective.perspectiveID);
					
					page.setPerspective(openAutomaticsPerspective);
				}
				else if(editor.getEditorInput() instanceof ObjectMapEditorInput)
				{		
					IPerspectiveRegistry perspectiveRegistry = window.getWorkbench().getPerspectiveRegistry();
					IPerspectiveDescriptor openAutomaticsPerspective = perspectiveRegistry.findPerspectiveWithId(Perspective.perspectiveID);
					
					page.setPerspective(openAutomaticsPerspective);
				}
				else if(editor.getEditorInput() instanceof FileEditorInput)
				{
					IPerspectiveRegistry perspectiveRegistry = window.getWorkbench().getPerspectiveRegistry();
					IPerspectiveDescriptor openJavaPerspective = perspectiveRegistry.findPerspectiveWithId("org.eclipse.jdt.ui.JavaPerspective");
					
					page.setPerspective(openJavaPerspective);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : partBroughtToTop()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
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
