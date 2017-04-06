package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

import com.automatics.packages.Editors.TestSuiteEditor;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.helpers.Utilities;

public class TSTestCaseColumnEditable extends EditingSupport 
{
	private TableViewer viewer = null;
	private ComboBoxViewerCellEditor editor = null;
	
	public TSTestCaseColumnEditable(TableViewer viewer, ArrayList<String> dropdownVals) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new ComboBoxViewerCellEditor(this.viewer.getTable(), SWT.READ_ONLY);
		this.editor.setContenProvider(new ArrayContentProvider());
		if(dropdownVals.size()!=0)
			this.editor.setInput(dropdownVals);
	}

	@Override
	protected CellEditor getCellEditor(Object element) 
	{
		this.editor.setInput(TestSuiteEditor.testCaseList);
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
		TSTCGson tstcGSON = (TSTCGson) element;
		return tstcGSON.tcName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updateValue = "";
		if(value!=null)
			updateValue = value.toString();
		((TSTCGson) element).tcName = updateValue; 
		this.viewer.update(element, null);
	}

}
