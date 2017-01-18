package com.automatics.packages.Editors;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.automatics.packages.Model.TaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.TableColumnsEditable;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.wb.swt.TableViewerColumnSorter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;

public class TCEditor extends EditorPart {
	
	public static String ID = "com.automatics.packages.Editors.tcEditor";
	private TestCaseTask tcTask;
	private TestCaseEditorInput input;
	private Table testscriptTable;
	
	public TCEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		if (!(input instanceof TestCaseEditorInput)) {
            throw new RuntimeException("Wrong input");
	    }
	
	    this.input = (TestCaseEditorInput) input;
	    setSite(site);
	    setInput(input);
	    tcTask = TestCaseTaskService.getInstance().getTaskByTcName(this.input.getId());
	    setPartName("TestCase:" + tcTask.getTcName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tcEditorTabFolder = new TabFolder(parent, SWT.BOTTOM);
		
		TabItem tbtmScripts = new TabItem(tcEditorTabFolder, SWT.NONE);
		tbtmScripts.setText("Scripts");
		
		Composite script_composite = new Composite(tcEditorTabFolder, SWT.NONE);
		tbtmScripts.setControl(script_composite);
		script_composite.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(script_composite, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 17;
		gd_composite.widthHint = 576;
		composite.setLayoutData(gd_composite);
		
		TableViewer testscriptsViewer = new TableViewer(script_composite, SWT.BORDER | SWT.FULL_SELECTION);
		testscriptTable = testscriptsViewer.getTable();
		testscriptTable.setLinesVisible(true);
		testscriptTable.setHeaderVisible(true);
		testscriptTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn snoViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
		snoViewer.setLabelProvider(new ColumnLabelProvider());
		new TableViewerColumnSorter(snoViewer) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				// TODO Remove this method, if your getValue(Object) returns Comparable.
				// Typical Comparable are String, Integer, Double, etc.
				return super.doCompare(viewer, e1, e2);
			}
			@Override
			protected Object getValue(Object o) {
				// TODO remove this method, if your EditingSupport returns value
				return super.getValue(o);
			}
		};
		TableColumn snoCol = snoViewer.getColumn();
		snoCol.setResizable(false);
		snoCol.setWidth(57);
		snoCol.setText("S.No");
		
		TableViewerColumn oprViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
		TableColumn operationCol = oprViewer.getColumn();
		operationCol.setWidth(107);
		operationCol.setText("Operation");
		oprViewer.setEditingSupport(new TableColumnsEditable(testscriptsViewer));
		
		TableViewerColumn pgViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
		TableColumn pageNameCol = pgViewer.getColumn();
		pageNameCol.setWidth(114);
		pageNameCol.setText("Page Name");
		pgViewer.setEditingSupport(new TableColumnsEditable(testscriptsViewer));
		
		
		TableViewerColumn objViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
		TableColumn objCol = objViewer.getColumn();
		objCol.setWidth(106);
		objCol.setText("Object Name");
		objViewer.setEditingSupport(new TableColumnsEditable(testscriptsViewer));
		
		TableViewerColumn argColViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
		TableColumn argCol = argColViewer.getColumn();
		argCol.setWidth(100);
		argCol.setText("Argument");
		argColViewer.setEditingSupport(new TableColumnsEditable(testscriptsViewer));
		
		TableViewerColumn varColViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
		TableColumn varNameCol = varColViewer.getColumn();
		varNameCol.setWidth(100);
		varNameCol.setText("Variable Name");
		varColViewer.setEditingSupport(new TableColumnsEditable(testscriptsViewer));
		// TODO Auto-generated method stub
		loadTestSteps(testscriptsViewer.getTable());
	}
	
	public void loadTestSteps(Table parent)
	{
		try
		{
			TCGson tcGson = tcTask.getTcGson();
			Iterator<TCStepsGSON> itr = tcGson.tcSteps.iterator();
			while(itr.hasNext())
			{
				TCStepsGSON stepGSON = itr.next();
				TableItem row = new TableItem(parent, SWT.NONE);
				row.setText(new String []
						{stepGSON.stepNo,stepGSON.stepOperation,stepGSON.stepPageName,stepGSON.stepObjName,stepGSON.stepArgument,stepGSON.stepVarName});
			
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + ":loadTestSteps] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
