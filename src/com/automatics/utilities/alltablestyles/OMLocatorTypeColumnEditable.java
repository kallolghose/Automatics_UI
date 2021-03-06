package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.automatics.packages.Editors.ObjectMapEditor;
import com.automatics.utilities.gsons.objectmap.OMDetails;

public class OMLocatorTypeColumnEditable extends EditingSupport 
{
	private TableViewer viewer;
	//private CellEditor editor;
	private ComboBoxViewerCellEditor editor = null;
	
	public OMLocatorTypeColumnEditable(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		//this.editor = new TextCellEditor(this.viewer.getTable());
		this.editor = new ComboBoxViewerCellEditor(this.viewer.getTable(), SWT.READ_ONLY);
		this.editor.setContenProvider(new ArrayContentProvider());
		this.editor.setInput(ObjectMapEditor.getLocatorType());
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
		return ((OMDetails)element).locatorType;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updated = "";
		if(value==null)
			updated = "";
		else
			updated = value.toString();
		((OMDetails) element).locatorType = updated;
		viewer.update(element, null);
	}
}
