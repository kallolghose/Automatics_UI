package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.automatics.utilities.gsons.testsuite.TSTCGson;

public class TSFifthColumnEditable extends EditingSupport 
{
	private TableViewer viewer;
	private TextCellEditor editor;
	
	public TSFifthColumnEditable(TableViewer viewer) {
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
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		TSTCGson tstcGson = (TSTCGson)element;
		return tstcGson.tcParams.get(4).tcparamValue;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		((TSTCGson) element).tcParams.get(4).tcparamValue = value.toString(); 
		viewer.update(element, null);
	}
	
}
