package com.automatics.packages.Editors;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.mongo.packages.AutomaticsDBTestSuiteQueries;
import com.automatics.packages.Perspective;
import com.automatics.packages.Model.TestSuiteTask;
import com.automatics.packages.Model.TestSuiteTaskService;
import com.automatics.utilities.alltablestyles.TSFifthColumnEditable;
import com.automatics.utilities.alltablestyles.TSFirstColumnEditable;
import com.automatics.utilities.alltablestyles.TSFourthColumnEditable;
import com.automatics.utilities.alltablestyles.TSSecondColumnEditable;
import com.automatics.utilities.alltablestyles.TSTestCaseColumnEditable;
import com.automatics.utilities.alltablestyles.TSThirdColumnEditable;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.gsons.testsuite.TSTCParamGson;
import com.automatics.utilities.helpers.Utilities;
import com.mongodb.DB;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class TestSuiteEditor extends EditorPart {

	public static String ID = "com.automatics.packages.Editors.tsEditor"; 
	private TestSuiteTask tsTask;
	private TestSuiteEditorInput input;
	private Table testsuitetable;
	private TableViewer testsuiteviewer;
	public static ArrayList<String> testCaseList = new ArrayList<String>();
	private DB db;
	private boolean isDirty = false;
	private ToolItem addBtn,delBtn;
	
	public TestSuiteEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		try
		{
			boolean warning = false;
			List<TSTCGson> testscriptDetail = (ArrayList<TSTCGson>)testsuiteviewer.getInput();
			for(TSTCGson tstcGson : testscriptDetail)
			{
				if(tstcGson.tcName.equalsIgnoreCase(""))
				{
					warning = true;
					break;
				}
			}
			if(!warning)
			{
				TSGson tssaveGson = tsTask.getTsGson();
				tssaveGson.tsTCLink = testscriptDetail;
				//Save the file new data in the task
				tsTask.setTsGson(tssaveGson);
				
				JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TSGson.class, tssaveGson));
				System.out.println(jsonObj.toString());
				if(jsonObj!=null)
				{
					AutomaticsDBTestSuiteQueries.updateTS(Utilities.getMongoDB(), tsTask.getTsName(), jsonObj);
					isDirty = false;
					firePropertyChange(PROP_DIRTY);
				}
				else 
				{
					Utilities.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Save Failed",
										 "Some Unexpected Error Occured", "ERR");
					throw new RuntimeException("Error In Test Suite Save");
				}
				
			}
			else
			{
				Utilities.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Cannot Save",
						"One or more Field(s) are not completed. Please provide value(s) for them.", 
						"WARN").open();
			}
		}
		catch(Exception e)
		{
			
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		if(!(input instanceof TestSuiteEditorInput))
		{
			throw new RuntimeException("Wrong input");
		}
		
		this.input = (TestSuiteEditorInput) input;
		setSite(site);
		setInput(input);
		tsTask = TestSuiteTaskService.getInstance().getTaskByTSName(this.input.getId());
		setPartName("TestSuite:" + tsTask.getTsName());
		
		//Load all the test case names in the ArrayList for drop down
		db = Utilities.getMongoDB();
		testCaseList = AutomaticsDBTestCaseQueries.getAllTC(db);
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 26;
		gd_composite.widthHint = 585;
		composite.setLayoutData(gd_composite);
		
		ToolBar iconsToolbar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		iconsToolbar.setBounds(0, 0, 426, 23);
		
		addBtn = new ToolItem(iconsToolbar, SWT.NONE);
		addBtn.setToolTipText("Add a test case detail");
		addBtn.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		addBtn.setSelection(true);
		
		delBtn = new ToolItem(iconsToolbar, SWT.NONE);
		delBtn.setToolTipText("Delete a test suite detail");
		delBtn.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		delBtn.setSelection(true);
		
		testsuiteviewer = new TableViewer(parentComposite, SWT.BORDER | SWT.FULL_SELECTION);
		testsuiteviewer.setContentProvider(new ArrayContentProvider());
		testsuitetable = testsuiteviewer.getTable();
		testsuitetable.setLinesVisible(true);
		testsuitetable.setHeaderVisible(true);
		testsuitetable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn testcaseColViewer = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		testcaseColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TSTCGson tctsGSON = (TSTCGson) element;
				return tctsGSON.tcName;
			}
		});
		
		TableColumn testcaseCol = testcaseColViewer.getColumn();
		testcaseCol.setWidth(130);
		testcaseCol.setText("Test Case Name");
		testcaseColViewer.setEditingSupport(new TSTestCaseColumnEditable(testsuiteviewer, testCaseList));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TSTCGson tctsGSON = (TSTCGson) element;
				return tctsGSON.tcParams.get(0).tcparamValue;
			}
		});
		TableColumn tblclmnColumn = tableViewerColumn.getColumn();
		tblclmnColumn.setWidth(110);
		tblclmnColumn.setText("Column1");
		tableViewerColumn.setEditingSupport(new TSFirstColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TSTCGson tctsGSON = (TSTCGson) element;
				return tctsGSON.tcParams.get(1).tcparamValue;
			}
		});
		TableColumn tblclmnColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnColumn_1.setWidth(110);
		tblclmnColumn_1.setText("Column 2");
		tableViewerColumn_1.setEditingSupport(new TSSecondColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TSTCGson tctsGSON = (TSTCGson) element;
				return tctsGSON.tcParams.get(2).tcparamValue;
			}
		});
		TableColumn tblclmnColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnColumn_2.setWidth(100);
		tblclmnColumn_2.setText("Column3");
		tableViewerColumn_2.setEditingSupport(new TSThirdColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TSTCGson tctsGSON = (TSTCGson) element;
				return tctsGSON.tcParams.get(3).tcparamValue;
			}
		});
		TableColumn tblclmnColumn_3 = tableViewerColumn_3.getColumn();
		tblclmnColumn_3.setWidth(100);
		tblclmnColumn_3.setText("Column4");
		tableViewerColumn_3.setEditingSupport(new TSFourthColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				TSTCGson tctsGSON = (TSTCGson) element;
				return tctsGSON.tcParams.get(4).tcparamValue;
			}
		});
		TableColumn tblclmnColumn_4 = tableViewerColumn_4.getColumn();
		tblclmnColumn_4.setWidth(100);
		tblclmnColumn_4.setText("Column5");
		tableViewerColumn_4.setEditingSupport(new TSFifthColumnEditable(testsuiteviewer));
		//Load the data to test suite table
		loadTestSuiteData();
		setListeners();
	}

	public void setListeners()
	{
		testsuitetable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				isDirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});
		
		addBtn.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event event) 
			{
				try
				{
					int selectedIndex = testsuitetable.getSelectionIndex();
					List<TSTCGson> list = (ArrayList<TSTCGson>) testsuiteviewer.getInput();
					if(selectedIndex!=-1)
					{
						TSTCGson tsdetails = new TSTCGson();
						tsdetails.tcName = "";
						List<TSTCParamGson> newList =  new ArrayList<TSTCParamGson>();
						
					}
					else
					{
						
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : setListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void loadTestSuiteData()
	{
		try
		{
			TSGson tsGson = tsTask.getTsGson();
			testsuiteviewer.setInput(tsGson.tsTCLink);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : loadTestSuiteData()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		try
		{
			//Get which perspective
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IPerspectiveDescriptor perspectiveNow = page.getPerspective();
			String labelID = perspectiveNow.getId();
			
			//Change the perspective if not Automatics Perspective
			if(!labelID.equalsIgnoreCase(Perspective.perspectiveID)) 
			{
				IPerspectiveRegistry perspectiveRegistry = window.getWorkbench()
															.getPerspectiveRegistry();
				IPerspectiveDescriptor openAutomaticsPerspective = perspectiveRegistry
				        .findPerspectiveWithId(Perspective.perspectiveID);
				//page.savePerspectiveAs(openAutomaticsPerspective);
				page.setPerspective(openAutomaticsPerspective);
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : setFocus() - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
