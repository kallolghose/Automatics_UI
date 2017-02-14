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
		try
		{
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editor!=null)
			{
				
				if(editor.getEditorInput() instanceof TestCaseEditorInput)
				{
					System.out.println("Here ---- Test Case");
					System.out.println("Heerere " + editor.getTitle());
				}
				else if(editor.getEditorInput() instanceof TestSuiteEditorInput)
				{
					System.out.println("Here --- Test Suite");
				}
				else if(editor.getEditorInput() instanceof ObjectMapEditorInput)
				{
					System.out.println("Here ----- Object Map");
				}
				else if(editor.getEditorInput() instanceof FileEditorInput)
				{
					String title = editor.getTitle();
					if(title.contains(".xml")) //If the suite file then open test suite editor
					{
						String filename [] = title.split("\\.");
						TestSuiteEditorInput input = new TestSuiteEditorInput(filename[0]);
						page.openEditor(input, TestSuiteEditor.ID);
					}
					else if(title.contains(".java")) //If testcase or test suite
					{	
						IFile file = ((FileEditorInput) editor.getEditorInput()).getFile();
						String filename [] = title.split("\\.");
						if(file!=null)
						{
							String filePath = file.getFullPath().toString();
							
							if(filePath.contains("objectMap"))
							{
								System.out.println("My ObjectMap");
								ObjectMapEditorInput input = new ObjectMapEditorInput(filename[0]);
								page.openEditor(input, ObjectMapEditor.ID);
							}
							else if(filePath.contains("testScripts"))
							{
								System.out.println("My TestScripts");
								TestCaseEditorInput input = new TestCaseEditorInput(filename[0]);
								page.openEditor(input, TCEditor.ID);
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : addPerspective()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void perspectiveChanged(IWorkbenchPage page,IPerspectiveDescriptor perspective, String changeId) 
	{
		
	}

}
