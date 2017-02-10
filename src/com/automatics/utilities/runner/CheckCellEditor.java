package com.automatics.utilities.runner;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

public class CheckCellEditor extends EditingSupport {

	private TableViewer viewer;
	private CellEditor editor;
	
	public CheckCellEditor(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new CheckboxCellEditor(null, SWT.CHECK);
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
		TestSuiteRunnerAPI runner = (TestSuiteRunnerAPI)element;
		return runner.selected;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		TestSuiteRunnerAPI runner = (TestSuiteRunnerAPI)element;
		runner.selected = (Boolean)value;
		viewer.update(element, null);
	}

}
