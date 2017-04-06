package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;

import com.automatics.utilities.extraUIs.RunScriptTCPopUp;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class TCArgumentsColumnEditable extends EditingSupport
{
	private TableViewer viewer;
	private CellEditor editor;
		
	public TCArgumentsColumnEditable(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(this.viewer.getTable());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO Auto-generated method stub
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		// TODO Auto-generated method stub
		TCStepsGSON gson = (TCStepsGSON)element;
		if(gson.stepArgument.equals("NA"))
			return false;
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		return ((TCStepsGSON)element).stepArgument;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		/*
		 * Check if the operation is RunScript then show pop-up*/
		TCStepsGSON temp = (TCStepsGSON)element;
		if(temp.stepOperation.equalsIgnoreCase("RunScript"))
		{
			RunScriptTCPopUp rspopUp = new RunScriptTCPopUp(viewer.getTable().getShell());
			if(rspopUp.open() == Window.OK)
			{
				updateVal = rspopUp.getSelectedTestCase();
			}
		}
		/*
		 * Check if Update value contains Double quotes(") and replace it with \"*/
		updateVal = updateVal.replace("\"", "\\\"");
		((TCStepsGSON) element).stepArgument = updateVal; 
		viewer.update(element, null);
	}
	
}
