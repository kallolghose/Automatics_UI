package com.automatics.packages;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.AutomaticsParser;
import com.automatics.utilities.helpers.Utilities;

public class PerspectiveListener implements IPerspectiveListener
{

	public void perspectiveActivated(IWorkbenchPage page,IPerspectiveDescriptor perspective) 
	{
		try
		{
				IEditorPart editor = page.getActiveEditor();
				if(perspective.getId().equalsIgnoreCase(Perspective.perspectiveID))
				{
					if(editor!=null)
					{
						if(page.getActiveEditor().getTitle().contains(".java"))
						{
							IFile file = ((FileEditorInput) editor.getEditorInput()).getFile();
							String filename [] = editor.getTitle().split("\\.");
							if(file!=null)
							{
								String filePath = file.getFullPath().toString();
								if(filePath.contains("objectMap"))
								{
									AutomaticsParser parse = new AutomaticsParser();
									parse.setOmStream(file.getContents());
									List<OMDetails> parseList = parse.parseContentsOfObjectMap();
									ObjectMapTask omTask = ObjectMapTaskService.getInstance().getTaskByOmName(filename[0]);
									OMGson omGson= omTask.getOmGson();
									omGson.omDetails = parseList;
									omTask.setOmGson(omGson);
									
									ObjectMapEditorInput input = new ObjectMapEditorInput(filename[0]);
									page.openEditor(input, ObjectMapEditor.ID, false,  IWorkbenchPage.MATCH_INPUT);
								}
								else if(filePath.contains("testScripts"))
								{
									AutomaticsParser parse = new AutomaticsParser();
									parse.setTcStream(file.getContents());
									List<TCStepsGSON> parseList = parse.parseContentofTestCase();
									
									TestCaseTask tcTask = TestCaseTaskService.getInstance().getTaskByTcName(filename[0]);
									TCGson tcGson = tcTask.getTcGson();
									tcGson.tcSteps = parseList;
									tcTask.setTcGson(tcGson);
									
									TestCaseEditorInput input = new TestCaseEditorInput(filename[0]);
									page.openEditor(input, TCEditor.ID, false,  IWorkbenchPage.MATCH_INPUT);
								}
							}	
						}
					}
				}
				else if(perspective.getId().equalsIgnoreCase("org.eclipse.jdt.ui.JavaPerspective"))
				{
					if(editor!=null)
					{
						if(!page.getActiveEditor().getTitle().contains(".java"))
						{
							if(editor.getEditorInput() instanceof TestCaseEditorInput)
							{
								TestCaseEditorInput editorIP = (TestCaseEditorInput)editor.getEditorInput();
								TestCaseTaskService taskService = TestCaseTaskService.getInstance();
								TestCaseTask tcTask = taskService.getTaskByTcName(editorIP.getId());
								String fileName = Utilities.createJavaFiles(tcTask.getTcGson());	
								
								//Open the editor
								//Get file path relative the the workspace
								String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
								File file = new File(fileName);
								String filePath = file.getAbsolutePath().substring(workspacePath.length()+1);
								
								//Open the file
								IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
								IPath location = Path.fromOSString(filePath); 
								IFile projectFile = workspace.getRoot().getFile(location);
								Utilities.openEditor(projectFile, null);
							}
							else if(editor.getEditorInput() instanceof ObjectMapEditorInput)
							{
								ObjectMapEditorInput objEditorIP = (ObjectMapEditorInput) editor.getEditorInput();
								ObjectMapTaskService omService = ObjectMapTaskService.getInstance();
								ObjectMapTask omTask = omService.getTaskByOmName(objEditorIP.getId());
								String fileName = Utilities.createObjectMap(omTask.getOmGson());
								
								String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
								File file = new File(fileName);
								String filePath = file.getAbsolutePath().substring(workspacePath.length()+1);
								
								//Open the file
								IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
								IPath location = Path.fromOSString(filePath); 
								IFile projectFile = workspace.getRoot().getFile(location);
								Utilities.openEditor(projectFile, null);
							}
							else if(editor.getEditorInput() instanceof TestSuiteEditorInput)
							{
								//Do Nothing
							}
						}
					}
				}
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : perspectiveActivated()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void perspectiveChanged(IWorkbenchPage page,IPerspectiveDescriptor perspective, String changeId) 
	{
		
	}

}
