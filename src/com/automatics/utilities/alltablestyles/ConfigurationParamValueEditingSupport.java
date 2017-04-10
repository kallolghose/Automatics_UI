package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.automatics.utilities.gsons.configuration.ConfigurationParameter;

public class ConfigurationParamValueEditingSupport extends EditingSupport
{
	private TableViewer viewer;
	private TextCellEditor editor;
	
	public ConfigurationParamValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		editor = new TextCellEditor(this.viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO Auto-generated method stub
		return this.editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		ConfigurationParameter param = (ConfigurationParameter)element;
		return param.parameterValue;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		((ConfigurationParameter)element).parameterValue = updateVal;
		viewer.update(element, null);
	}
	
}
