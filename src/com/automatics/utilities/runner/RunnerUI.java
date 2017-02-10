package com.automatics.utilities.runner;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Control;

import com.automatics.mongo.packages.AutomaticsDBTestSuiteQueries;
import com.automatics.utilities.helpers.Utilities;

public class RunnerUI {

	protected Shell shlRunner;
	private Table testsuiterunnerTable;
	private TableViewer  testsuitetableViewer;
	private List<String> allTestSuites;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RunnerUI window = new RunnerUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlRunner.open();
		shlRunner.layout();
		while (!shlRunner.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlRunner = new Shell();
		shlRunner.setSize(554, 572);
		shlRunner.setText("Runner");
		shlRunner.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(shlRunner, SWT.NONE);
		parentComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite testSuiteComposite = new Composite(parentComposite, SWT.BORDER);
		testSuiteComposite.setLayout(new GridLayout(1, false));
		
		Composite iconsToolBar = new Composite(testSuiteComposite, SWT.NONE);
		GridData gd_iconsToolBar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_iconsToolBar.widthHint = 441;
		gd_iconsToolBar.heightHint = 24;
		iconsToolBar.setLayoutData(gd_iconsToolBar);
		
		ToolBar iconToolBar = new ToolBar(iconsToolBar, SWT.FLAT | SWT.RIGHT);
		iconToolBar.setBounds(0, 0, 440, 23);
		
		ToolItem runItem = new ToolItem(iconToolBar, SWT.NONE);
		runItem.setToolTipText("Run");
		runItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/run.png"));
		
		Composite allTSContainer = new Composite(testSuiteComposite, SWT.BORDER);
		allTSContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_allTSContainer = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_allTSContainer.heightHint = 219;
		gd_allTSContainer.widthHint = 439;
		allTSContainer.setLayoutData(gd_allTSContainer);
		
		testsuitetableViewer = new TableViewer(allTSContainer, SWT.CHECK | SWT.FULL_SELECTION);
		testsuiterunnerTable = testsuitetableViewer.getTable();
		testsuiterunnerTable.setLinesVisible(true);
		testsuiterunnerTable.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(testsuitetableViewer, SWT.NONE);
		TableColumn tblclmnSelect = tableViewerColumn.getColumn();
		tblclmnSelect.setResizable(false);
		tblclmnSelect.setWidth(25);
		
		TableViewerColumn tsSuiteViewer = new TableViewerColumn(testsuitetableViewer, SWT.NONE);
		TableColumn tsColumn = tsSuiteViewer.getColumn();
		tsColumn.setWidth(236);
		tsColumn.setText("Test Suite ");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(testsuitetableViewer, SWT.NONE);
		TableColumn tblclmnParallelThreadCount = tableViewerColumn_1.getColumn();
		tblclmnParallelThreadCount.setWidth(140);
		tblclmnParallelThreadCount.setText("Parallel Thread Count");
		
		TableViewerColumn statusViewer = new TableViewerColumn(testsuitetableViewer, SWT.NONE);
		TableColumn statusColumn = statusViewer.getColumn();
		statusColumn.setWidth(110);
		statusColumn.setText("Status");
		
		Composite runnerComposite = new Composite(parentComposite, SWT.BORDER);
		runnerComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder testRunConsole = new TabFolder(runnerComposite, SWT.NONE);
		
		TabItem tsConsole1 = new TabItem(testRunConsole, SWT.NONE);
		tsConsole1.setText("Console");
		
		Composite composite = new Composite(testRunConsole, SWT.NONE);
		tsConsole1.setControl(composite);
		shlRunner.setTabList(new Control[]{parentComposite});

	}
	
	public void getAllTestSuites()
	{
		try
		{
			allTestSuites = AutomaticsDBTestSuiteQueries.getAllTS(Utilities.getMongoDB());
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : getAllTestSuites()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void setAllTestSuiteToTable()
	{
		
	}
}
