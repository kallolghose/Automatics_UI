package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.automatics.utilities.gsons.objectmap.OMDetails;

public class OMObjectNameColumnEditable extends EditingSupport 
{

	private TableViewer viewer;
	private CellEditor editor;
	
	public OMObjectNameColumnEditable(TableViewer viewer) {
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
		return ((OMDetails)element).objName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		((OMDetails) element).objName = value.toString();
		viewer.update(element, null);
	}
	
}
