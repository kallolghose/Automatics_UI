package com.automatics.utilities.alltablestyles;

import org.eclipse.debug.internal.ui.sourcelookup.UpAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class TCObjectNameColumnEditable extends EditingSupport{

	private TableViewer viewer;
	private CellEditor editor;
	public TCObjectNameColumnEditable(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		editor = new TextCellEditor(this.viewer.getTable());
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
		
		TCStepsGSON step = (TCStepsGSON)element;
		if(step.stepObjName.equals("NA"))
			return false;
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		return ((TCStepsGSON)element).stepObjName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		((TCStepsGSON) element).stepObjName = updateVal; 
		viewer.update(element, null);
	}

}
