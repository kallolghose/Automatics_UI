package com.automatics.packages;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Editors.TestSuiteEditorInput;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.Views.ObjectList;
import com.automatics.packages.Views.ObjectMap;
import com.automatics.packages.Views.TestCaseParamView;

public class EditorListeners implements IPartListener2
{
	public void partActivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		try
		{
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
					
					//Also need to change the view of objectmap
					/*Enable AddToTestCase Menu Item for Test Cases*/
					ObjectList viewPartOL = (ObjectList)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectList.ID);
					viewPartOL.visibilityOfAddToTestCaseItem(true);
					ObjectMap viewPartOM = (ObjectMap)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectMap.ID);
					viewPartOM.visibilityOfRemovefromTC(true);
					
					TCEditor.currentTestCase = ((TestCaseEditorInput)editor.getEditorInput()).getId();
					
					TestCaseEditorInput input = (TestCaseEditorInput) editor.getEditorInput();
					TestCaseTask tcTask = TestCaseTaskService.getInstance().getTaskByTcName(input.getId());
					
					ObjectMap.disposeObjMaps();
					
						if(tcTask.getTcGson().tcObjectMapLink!=null)
						{
							boolean first = true;
							for(String omName : tcTask.getTcGson().tcObjectMapLink)
							{
								if(first) {
									ObjectMap.loadObjectMap(omName);
									first=false;
								}
								else
								{
									ObjectMap.addObjectMap(omName);
								}
							}
						}
					
					/*Set the test case parameter if any*/
					/*Try to use the object to access the value*/
					TestCaseParamView.currentTask = tcTask;
					TestCaseParamView.loadTestCaseParameters(tcTask.getTcGson());
				}
				else if(editor.getEditorInput() instanceof TestSuiteEditorInput)
				{
					/*Disable AddToTestCase Menu Item for Test Suites*/
					ObjectList viewPart = (ObjectList)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectList.ID);
					viewPart.visibilityOfAddToTestCaseItem(false);
					ObjectMap viewPartOM = (ObjectMap)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectMap.ID);
					viewPartOM.visibilityOfRemovefromTC(false);
					
					/*Remove object map if any from the object map view*/
					ObjectMap.disposeObjMaps();
					
					IPerspectiveRegistry perspectiveRegistry = window.getWorkbench().getPerspectiveRegistry();
					IPerspectiveDescriptor openAutomaticsPerspective = perspectiveRegistry.findPerspectiveWithId(Perspective.perspectiveID);
					page.setPerspective(openAutomaticsPerspective);
				}
				else if(editor.getEditorInput() instanceof ObjectMapEditorInput)
				{		
					/*Disable AddToTestCase Menu Item for Object Maps*/
					ObjectList viewPart = (ObjectList)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectList.ID);
					viewPart.visibilityOfAddToTestCaseItem(false);
					ObjectMap viewPartOM = (ObjectMap)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectMap.ID);
					viewPartOM.visibilityOfRemovefromTC(false);
					
					/*Remove object map if any from the object map view*/
					ObjectMap.disposeObjMaps();
					
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
			e.printStackTrace(System.out);
		}
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		try
		{
			if(partRef instanceof IEditorReference)
			{
				IEditorReference editor = (IEditorReference)partRef;
				if(editor.getEditorInput() instanceof TestCaseEditorInput)
				{
					/*Remove Object*/
					ObjectList viewPart = (ObjectList)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectList.ID);
					viewPart.visibilityOfAddToTestCaseItem(false);
					ObjectMap viewPartOM = (ObjectMap)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ObjectMap.ID);
					viewPartOM.visibilityOfRemovefromTC(false);
					ObjectMap.disposeObjMaps();
					
					/*Remove Parameters as well*/
					boolean canDelete = true;
					IEditorReference refs [] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
					for(IEditorReference ref : refs)
					{
						if(editor.getEditorInput() instanceof TestCaseEditorInput)
						{
							canDelete = false;
							break;
						}
					}
					if(canDelete)
						TestCaseParamView.removeAllColumns();
					
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : partClosed()] - Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
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
		try
		{
			if(partRef instanceof IEditorReference)
			{
				IEditorReference editor = (IEditorReference)partRef;
				if(editor.getEditorInput() instanceof TestCaseEditorInput)
				{
				}		
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : partVisible()] - Exception - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}

}
