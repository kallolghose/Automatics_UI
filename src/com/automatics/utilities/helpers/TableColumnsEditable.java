package com.automatics.utilities.helpers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class TableColumnsEditable extends EditingSupport
{

	private TableViewer viewer = null;
	private CellEditor editor = null;
	
	public TableColumnsEditable(TableViewer viewer) {
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
		return element.toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		getViewer().update(value, null);
	}

}
