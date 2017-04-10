package com.automatics.utilities.alltablestyles;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.automatics.utilities.gsons.configuration.ConfigurationParameter;

public class ConfigurationParamEditingSupport extends EditingSupport 
{
	private TableViewer viewer;
	private TextCellEditor editor;

	public ConfigurationParamEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		editor = new TextCellEditor(this.viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) 
	{
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) 
	{
		return true;
	}

	@Override
	protected Object getValue(Object element) 
	{
		ConfigurationParameter config = (ConfigurationParameter)element;
		return config.parameterName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		((ConfigurationParameter)element).parameterName = updateVal;
		viewer.update(element,null);
	}
	
}
