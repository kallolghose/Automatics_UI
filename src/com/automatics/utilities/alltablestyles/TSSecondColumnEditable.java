package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.automatics.utilities.gsons.testsuite.TSTCGson;

public class TSSecondColumnEditable extends EditingSupport 
{
	private TableViewer viewer = null;
	private ComboBoxViewerCellEditor editor = null;
	
	public TSSecondColumnEditable(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		
		ArrayList<String> arrList = new ArrayList<String>(Arrays.asList("WINDOWS_WEB","WINDOWS_NONWEB","ANDROID_NATIVE","ANDROID_WEB"));
		editor = new ComboBoxViewerCellEditor(this.viewer.getTable(), SWT.READ_ONLY);
		editor.setContentProvider(new ArrayContentProvider());
		editor.setInput(arrList);
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
		return tstcGson.tcParams.get(1).tcparamValue;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		((TSTCGson) element).tcParams.get(1).tcparamValue = updateVal; 
		viewer.update(element, null);
	}
	
}
