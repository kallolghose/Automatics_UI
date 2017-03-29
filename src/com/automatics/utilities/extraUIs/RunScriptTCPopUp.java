package com.automatics.utilities.extraUIs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.automatics.packages.api.handlers.TestCaseAPIHandler;
import com.automatics.utilities.gsons.testcase.TCGson;

public class RunScriptTCPopUp extends Dialog {
	private Table runscriptTCTable;
	private TableViewer runScriptTableViewer;
	private String selectedTestCase;
	private Label errLabel;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RunScriptTCPopUp(Shell parentShell) {
		super(parentShell);
	}

	public String getSelectedTestCase()
	{
		return selectedTestCase;
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_1.heightHint = 24;
		composite_1.setLayoutData(gd_composite_1);
		
		Label lblSelectATest = new Label(composite_1, SWT.NONE);
		lblSelectATest.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblSelectATest.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSelectATest.setText("*");
		
		Label lblSelectATest_1 = new Label(composite_1, SWT.NONE);
		lblSelectATest_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblSelectATest_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSelectATest_1.setText("Select A Test Case");
		
		errLabel = new Label(composite_1, SWT.NONE);
		errLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
		errLabel.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		errLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		errLabel.setText("Please select a test case");
		errLabel.setVisible(false);
		
		runScriptTableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		runscriptTCTable = runScriptTableViewer.getTable();
		runscriptTCTable.setLinesVisible(true);
		runscriptTCTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		runScriptTableViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(runScriptTableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TCGson tcGson = (TCGson)element;
				if(tcGson!=null)
					return tcGson.tcName;
				return null;
			}
		});
		TableColumn tcName = tableViewerColumn.getColumn();
		tcName.setWidth(428);
		tcName.setText("New Column");
		loadTestCases();
		return container;
	}

	public void loadTestCases()
	{
		try
		{
			TCGson []tcGsons = TestCaseAPIHandler.getInstance().getAllTestCases();
			runScriptTableViewer.setInput(tcGsons);
			runScriptTableViewer.refresh();
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - loadTestCases()] : Exception - " + e.getMessage());
			e.printStackTrace(System.out);
		} 
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
    protected void okPressed() 
	{
		errLabel.setVisible(false);
		int selectedIndex = runscriptTCTable.getSelectionIndex();
		if(selectedIndex==-1)
		{
			errLabel.setVisible(true);
			return;
		}
		TCGson[] tcGsons = (TCGson[]) runScriptTableViewer.getInput();
		this.selectedTestCase = tcGsons[selectedIndex].tcName;
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 312);
	}
}
