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

public class TCObjectNameColumnEditable extends EditingSupport{

	private TableViewer viewer;
	private ComboBoxViewerCellEditor editor = null;
	private static ArrayList<String> allObjectMaps = null;
	
	public TCObjectNameColumnEditable(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		editor = new ComboBoxViewerCellEditor(this.viewer.getTable(), SWT.READ_ONLY);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if(allObjectMaps!=null)
		{
			if(allObjectMaps.size()>0)
			{
				this.editor.setContenProvider(new ArrayContentProvider());
				this.editor.setInput(allObjectMaps);
				return editor;
			}
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		TCStepsGSON step = (TCStepsGSON)element;
		if(step.stepObjName.equals("NA"))
			return false;
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((TCStepsGSON)element).stepObjName;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String updateVal = "";
		if(value!=null)
			updateVal = value.toString();
		((TCStepsGSON) element).stepObjName = updateVal; 
		viewer.update(element, null);
	}
	
	public static void setObjectsArrayList(String pageName)
	{
		//Get from ObjectMap.java file
		allObjectMaps = ObjectMap.getPageNameObjectMapping().get(pageName);
	}
}
