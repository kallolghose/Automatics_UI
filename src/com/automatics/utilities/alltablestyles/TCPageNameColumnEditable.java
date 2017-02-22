package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

import com.automatics.packages.Views.ObjectMap;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class TCPageNameColumnEditable extends EditingSupport {

	private TableViewer viewer;
	private ComboBoxViewerCellEditor editor = null;
	
	public TCPageNameColumnEditable(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new ComboBoxViewerCellEditor(this.viewer.getTable(), SWT.READ_ONLY);
	}

	@Override
	protected CellEditor getCellEditor(Object element) 
	{
		ArrayList<String> allPageName = ObjectMap.getPageNamesAddedToObjectMap();
		if(allPageName.size()>0)
		{
			this.editor.setContenProvider(new ArrayContentProvider());
			this.editor.setInput(allPageName);
			return editor;
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		TCStepsGSON step = (TCStepsGSON)element;
		if(step.stepPageName.equals("NA"))
			return false;
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((TCStepsGSON)element).stepPageName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String updateVal = "";
		if(value!=null)
		{
			updateVal = value.toString();
			((TCStepsGSON) element).stepPageName = updateVal;
			((TCStepsGSON) element).omName = ObjectMap.getPageNameObjectMapMapping().get(updateVal);
			TCObjectNameColumnEditable.setObjectsArrayList(updateVal); //Set the related objects
		}
		else
		{
			((TCStepsGSON) element).stepPageName = updateVal;
			((TCStepsGSON) element).omName = "";
		}
		 
		viewer.update(element, null);
	}

}
