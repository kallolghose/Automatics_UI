package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class TCVariableColumnEditable extends EditingSupport 
{
	private TableViewer viewer;
	private CellEditor editor;
	
	public TCVariableColumnEditable(TableViewer viewer) {
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
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		return ((TCStepsGSON)element).stepVarName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		((TCStepsGSON) element).stepVarName = updateVal; 
		viewer.update(element, null);
	}

}
