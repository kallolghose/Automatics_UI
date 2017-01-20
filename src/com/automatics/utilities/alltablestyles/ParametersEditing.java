package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class ParametersEditing extends EditingSupport
{
	private TableViewer viewer;
	private CellEditor editor;
	private int index;
	
	public ParametersEditing(TableViewer viewer, int index) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(this.viewer.getTable());
		this.index = index;
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
		return ((ArrayList<String>)element).get(index);
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		((ArrayList<String>) element).set(index, value.toString());
		viewer.update(element, null);
	}
	
}
