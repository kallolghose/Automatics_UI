package com.automatics.utilities.helpers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class TableColumnsEditable extends EditingSupport
{

	public TableColumnsEditable(ColumnViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO Auto-generated method stub
		return new TextCellEditor((Composite) getViewer().getControl());
	}

	@Override
	protected boolean canEdit(Object element) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		return element.toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		getViewer().update(value, null);
	}

}
