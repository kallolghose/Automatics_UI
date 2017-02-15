package com.automatics.packages;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.automatics.packages.Editors.ObjectMapEditor;
import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Editors.TestSuiteEditor;
import com.automatics.packages.Editors.TestSuiteEditorInput;

public class PerspectiveListener implements IPerspectiveListener
{

	public void perspectiveActivated(IWorkbenchPage page,IPerspectiveDescriptor perspective) 
	{
		IEditorPart editor = page.getActiveEditor();
		
		if(perspective.getId().equalsIgnoreCase(Perspective.perspectiveID))
		{
			if(editor!=null)
			{
				if(page.getActiveEditor().getTitle().contains(".java"))
				{
					System.out.println("Autom CHange");
				}
			}
		}
		else if(perspective.getId().equalsIgnoreCase("org.eclipse.jdt.ui.JavaPerspective"))
		{
			if(editor!=null)
			{
				if(!page.getActiveEditor().getTitle().contains(".java"))
				{
					System.out.println("Java CHngae");
				}
			}
		}
	}

	public void perspectiveChanged(IWorkbenchPage page,IPerspectiveDescriptor perspective, String changeId) 
	{
		
	}

}
