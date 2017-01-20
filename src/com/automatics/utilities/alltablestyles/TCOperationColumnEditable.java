package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.automatics.mongo.packages.AutomaticsDBOperationQueries;
import com.automatics.utilities.gsons.operation.AllOperationGSON;
import com.automatics.utilities.gsons.operation.OperationGSON;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.Utilities;

public class TCOperationColumnEditable extends EditingSupport
{
	private TableViewer viewer = null;
	//private CellEditor editor = null;
	private ComboBoxViewerCellEditor editor = null;
	
	public TCOperationColumnEditable(TableViewer viewer) {

		super(viewer);
		try
		{
			this.viewer = viewer;
			//this.editor = new TextCellEditor(this.viewer.getTable());
			
			AllOperationGSON allOPN = Utilities.getGSONFromJSON(AutomaticsDBOperationQueries.getAllOPN(Utilities.getMongoDB()).toString(),
																AllOperationGSON.class);
			
			ArrayList<String> opnName = new ArrayList<String>();
	
			for(OperationGSON opn : allOPN.Records)
			{
				opnName.add(opn.opnName);
			}
			this.editor = new ComboBoxViewerCellEditor(this.viewer.getTable(), SWT.READ_ONLY);
			this.editor.setContenProvider(new ArrayContentProvider());
			this.editor.setInput(opnName);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : TCOperationColumnEditable()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
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
		TCStepsGSON step = (TCStepsGSON) element;
		return step.stepOperation;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		((TCStepsGSON) element).stepOperation = value.toString(); 
		viewer.update(element, null);
	}

}
