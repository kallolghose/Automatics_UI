package com.automatics.packages.Editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.automatics.packages.Model.TestSuiteTask;
import com.automatics.packages.Model.TestSuiteTaskService;

public class TestSuiteEditor extends EditorPart {

	public static String ID = "com.automatics.packages.Editors.tsEditor"; 
	private TestSuiteTask tsTask;
	private TestSuiteEditorInput input;
	
	public TestSuiteEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		if(!(input instanceof TestSuiteEditorInput))
		{
			throw new RuntimeException("Wrong input");
		}
		
		this.input = (TestSuiteEditorInput) input;
		setSite(site);
		setInput(input);
		tsTask = TestSuiteTaskService.getInstance().getTaskByTSName(this.input.getId());
		setPartName("TestSuite:" + tsTask.getTsName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
