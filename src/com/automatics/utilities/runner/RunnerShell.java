package com.automatics.utilities.runner;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

import com.automatics.mongo.packages.AutomaticsDBTestSuiteQueries;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.helpers.Utilities;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

public class RunnerShell extends Shell {
	private Table testsuiteRunnerTable;
	private TableViewer testsuiteTableViewer;
	private List<String> testSuites;
	private List<TestSuiteRunnerAPI> runners;
	private ToolItem runItem;
	private static Image CHECKED_IMG = ResourceManager.getPluginImage("Automatics", "images/icons/checked.png");
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			RunnerShell shell = new RunnerShell(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public RunnerShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(this, SWT.NONE);
		parentComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite suitedetailsComposite = new Composite(parentComposite, SWT.NONE);
		suitedetailsComposite.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(suitedetailsComposite, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.widthHint = 535;
		gd_composite.heightHint = 24;
		composite.setLayoutData(gd_composite);
		
		ToolBar iconToolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		iconToolBar.setBounds(0, 0, 264, 24);
		
		runItem = new ToolItem(iconToolBar, SWT.NONE);
		runItem.setToolTipText("Run Selected Test Case");
		runItem.setSelection(true);
		runItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/run.png"));
		
		testsuiteTableViewer = new TableViewer(suitedetailsComposite, SWT.BORDER | SWT.FULL_SELECTION);
		testsuiteTableViewer.setContentProvider(new ArrayContentProvider());
		testsuiteRunnerTable = testsuiteTableViewer.getTable();
		testsuiteRunnerTable.setLinesVisible(true);
		testsuiteRunnerTable.setHeaderVisible(true);
		testsuiteRunnerTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(testsuiteTableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				TestSuiteRunnerAPI temp = (TestSuiteRunnerAPI)element;
				if(temp.selected)
					return CHECKED_IMG;
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				//return element == null ? "" : element.toString();
				return null;
			}
		});
		TableColumn checkCol = tableViewerColumn.getColumn();
		checkCol.setResizable(false);
		checkCol.setWidth(27);
		tableViewerColumn.setEditingSupport(new CheckCellEditor(testsuiteTableViewer));
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(testsuiteTableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TestSuiteRunnerAPI run = (TestSuiteRunnerAPI)element;
				return run.testsuiteName;
			}
		});
		TableColumn tsColumn = tableViewerColumn_1.getColumn();
		tsColumn.setWidth(200);
		tsColumn.setText("Test Suite");
		
		TableViewerColumn threadcountColViewer = new TableViewerColumn(testsuiteTableViewer, SWT.NONE);
		threadcountColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TestSuiteRunnerAPI run = (TestSuiteRunnerAPI)element;
				return "" + run.threadCount;
			}
		});
		TableColumn threadCol = threadcountColViewer.getColumn();
		threadCol.setWidth(165);
		threadCol.setText("Parallel Count");
		threadcountColViewer.setEditingSupport(new ThreadCountEditor(testsuiteTableViewer));

		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(testsuiteTableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TestSuiteRunnerAPI run = (TestSuiteRunnerAPI)element;
				return run.status;
			}
		});
		TableColumn statusCol = tableViewerColumn_3.getColumn();
		statusCol.setWidth(115);
		statusCol.setText("Status");
		
		Composite consoleComposite = new Composite(parentComposite, SWT.NONE);
		consoleComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(consoleComposite, SWT.NONE);
		
		TabItem tbtmConsole = new TabItem(tabFolder, SWT.NONE);
		tbtmConsole.setText("Console");
		
		Composite console = new Composite(tabFolder, SWT.NONE);
		tbtmConsole.setControl(console);
		createContents();
		getAllTestSuites();
		setListeners();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Runner Application");
		setSize(551, 572);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	//My Methods
	
	public void setListeners()
	{
		try
		{		
			runItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					try
					{
						List<TestSuiteRunnerAPI> list = (ArrayList<TestSuiteRunnerAPI>)testsuiteTableViewer.getInput();
						for(int i=0;i<list.size();i++)
						{
							TestSuiteRunnerAPI runner = list.get(i); 
							if(runner.selected)
								Utilities.createTestng(runner.tsGson, runner);
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " : runItem.addListener()] - Exception :" + e.getMessage());
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : setListeners()] - Exception " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void getAllTestSuites()
	{
		try
		{
			testSuites = AutomaticsDBTestSuiteQueries.getAllTS(Utilities.getMongoDB());
			runners = new ArrayList<TestSuiteRunnerAPI>();
			for(String str : testSuites)
			{
				TestSuiteRunnerAPI runner = new TestSuiteRunnerAPI();
				runner.selected = false;
				runner.testsuiteName = str;
				runner.threadCount = "1";
				runner.status = "";
				runner.tsGson = Utilities.getGSONFromJSON(AutomaticsDBTestSuiteQueries.getTS(Utilities.getMongoDB(), str).toString(), TSGson.class);
				runners.add(runner);
			}
			testsuiteTableViewer.setInput(runners);
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : getAllTestSuites] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
