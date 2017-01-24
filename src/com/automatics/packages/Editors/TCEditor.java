package com.automatics.packages.Editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.packages.Perspective;
import com.automatics.packages.Model.TaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.Views.ObjectMap;
import com.automatics.packages.Views.TestCaseParamView;
import com.automatics.utilities.alltablestyles.TCArgumentsColumnEditable;
import com.automatics.utilities.alltablestyles.TCObjectNameColumnEditable;
import com.automatics.utilities.alltablestyles.TCOperationColumnEditable;
import com.automatics.utilities.alltablestyles.TCPageNameColumnEditable;
import com.automatics.utilities.alltablestyles.TCVariableColumnEditable;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCParams;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.TableColumnsEditable;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;
import org.eclipse.wb.swt.TableViewerColumnSorter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class TCEditor extends EditorPart {
	
	public static String ID = "com.automatics.packages.Editors.tcEditor";
	private TestCaseTask tcTask;
	private TestCaseEditorInput input;
	private Table testscriptTable;
	private TableViewer testscriptsViewer; 
	private boolean isDirty = false;
	
	private ToolItem addBtn, delBtn;
	private boolean isFocus = false;
	
	public TCEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		try
		{
			boolean warning = false;
			List<TCStepsGSON> steps = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
			testscriptTable.forceFocus();
			if(steps != null)
			{
				for(TCStepsGSON step : steps)
				{
					if(step.stepOperation.equals(""))
					{
						warning = true;
						break;
					}
				}
				if(!warning)
				{
					TCGson tcSaveGson = tcTask.getTcGson();
					//Update task
					//1. Update Test Steps
					tcSaveGson.tcSteps = steps;
					
					//2. Update the object map
					tcSaveGson.tcObjectMapLink = ObjectMap.getAllOjectMapNamesSelected();
					
					//3. Update the parameters
					tcSaveGson.tcParams = TestCaseParamView.getAllTestCaseParameters();
					
					tcTask.setTcGson(tcSaveGson);
					JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TCGson.class, tcSaveGson));
					System.out.println(jsonObj.toString());
					if(jsonObj !=null)
					{
						AutomaticsDBTestCaseQueries.updateTC(Utilities.getMongoDB(), tcSaveGson.tcName, jsonObj);
						isDirty = false;
						firePropertyChange(PROP_DIRTY);
					}
					else 
					{
						Utilities.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Save Failed",
											 "Some Unexpected Error Occured", "ERR");
						throw new RuntimeException("Error In Test Case Save");
					}
				}
				else
				{
					//Display error message
					Utilities.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Cannot Save",
										"One or more Step(s) are not completed. Please provide value(s) for them.", 
										"WARN").open();
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : doSave() ] - Exception  : " + e.getMessage() );
			e.printStackTrace();
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
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		try
		{
			parent.setLayout(new FillLayout(SWT.HORIZONTAL));
			
			TabFolder tcEditorTabFolder = new TabFolder(parent, SWT.BOTTOM);
			
			TabItem tbtmScripts = new TabItem(tcEditorTabFolder, SWT.NONE);
			tbtmScripts.setText("Scripts");
			
			Composite script_composite = new Composite(tcEditorTabFolder, SWT.NONE);
			tbtmScripts.setControl(script_composite);
			script_composite.setLayout(new GridLayout(1, false));
			
			Composite composite = new Composite(script_composite, SWT.NONE);
			GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			gd_composite.heightHint = 24;
			gd_composite.widthHint = 576;
			composite.setLayoutData(gd_composite);
			
			ToolBar iconsToolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
			iconsToolBar.setBounds(0, 0, 123, 23);
			
			addBtn = new ToolItem(iconsToolBar, SWT.NONE);
			addBtn.setWidth(30);
			addBtn.setToolTipText("Add Testcase Step");
			addBtn.setSelection(true);
			addBtn.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
			
			delBtn = new ToolItem(iconsToolBar, SWT.NONE);
			delBtn.setToolTipText("Delete Testcase step");
			delBtn.setSelection(true);
			delBtn.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
			
			//Implementation of table using TableViewer
			testscriptsViewer = new TableViewer(script_composite, SWT.BORDER | SWT.FULL_SELECTION);
			testscriptsViewer.setContentProvider(new ArrayContentProvider());
			testscriptTable = testscriptsViewer.getTable();
			testscriptTable.setLinesVisible(true);
			testscriptTable.setHeaderVisible(true);
			testscriptTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			
			
			TableViewerColumn snoViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			snoViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON) element;
					return "" + step.stepNo;
				}
			});
			
			TableColumn snoCol = snoViewer.getColumn();
			snoCol.setResizable(false);
			snoCol.setWidth(57);
			snoCol.setText("S.No");
			
			TableViewerColumn oprViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			oprViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON) element;
					return step.stepOperation;
				}
			});
			TableColumn operationCol = oprViewer.getColumn();
			operationCol.setWidth(107);
			operationCol.setText("Operation");
			oprViewer.setEditingSupport(new TCOperationColumnEditable(testscriptsViewer));
			
			TableViewerColumn pgViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			pgViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON)element;
					return step.stepPageName;
				}
			});
			TableColumn pageNameCol = pgViewer.getColumn();
			pageNameCol.setWidth(114);
			pageNameCol.setText("Page Name");
			pgViewer.setEditingSupport(new TCPageNameColumnEditable(testscriptsViewer));
			
			
			TableViewerColumn objViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			objViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON)element;
					return step.stepObjName;
				}
			});
			TableColumn objCol = objViewer.getColumn();
			objCol.setWidth(106);
			objCol.setText("Object Name");
			objViewer.setEditingSupport(new TCObjectNameColumnEditable(testscriptsViewer));
			
			TableViewerColumn argColViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			argColViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON)element;
					return step.stepArgument;
				}
			});
			TableColumn argCol = argColViewer.getColumn();
			argCol.setWidth(100);
			argCol.setText("Argument");
			argColViewer.setEditingSupport(new TCArgumentsColumnEditable(testscriptsViewer));
			
			TableViewerColumn varColViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			varColViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON)element;
					return step.stepVarName;
				}
			});
			TableColumn varNameCol = varColViewer.getColumn();
			varNameCol.setWidth(100);
			varNameCol.setText("Variable Name");
			varColViewer.setEditingSupport(new TCVariableColumnEditable(testscriptsViewer));
			// TODO Auto-generated method stub
			
			TableViewerColumn objmapViewer = new TableViewerColumn(testscriptsViewer, SWT.NONE);
			objmapViewer.setLabelProvider(new ColumnLabelProvider() {
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
				public String getText(Object element) {
					// TODO Auto-generated method stub
					TCStepsGSON step = (TCStepsGSON)element;
					return step.omName;
				}
			});
			TableColumn objmapCol = objmapViewer.getColumn();
			objmapCol.setResizable(false);
			objmapCol.setText("ObjMapCol");
	
			//Load Test Steps
			loadTestSteps(testscriptsViewer);
			//Open Object Map View
			IViewPart objectMapView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ObjectMap.ID);
			
			//load object maps
			for(String omName : tcTask.getTcGson().tcObjectMapLink)
			{
				ObjectMap.loadObjectMap(omName);
			}
			
			DropTarget dropTarget = new DropTarget(testscriptTable, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
			setDropListener(dropTarget); //Set Drop Listener
			setListeners();
			addEditorListerner();
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : createcontent - Exception : " + e.getMessage());
			e.getMessage();
		}
	}
	
	public void addEditorListerner()
	{
		getSite().getPage().addPartListener(new IPartListener2() {
			
			public void partVisible(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			public void partOpened(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			public void partHidden(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				//Code for saving the test parameters when focus is lost
				isFocus = false;
				TCGson tcUpdateGson = tcTask.getTcGson();
				List<TCParams> tcsomeParams = TestCaseParamView.getAllTestCaseParameters();
				if(tcsomeParams!=null)
				{
					tcUpdateGson.tcParams = TestCaseParamView.getAllTestCaseParameters();
					tcTask.setTcGson(tcUpdateGson);
				}
			}
			
			public void partClosed(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			public void partActivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setListeners()
	{
		/*
		testscriptTable.addListener(SWT.MouseHover, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				Point p = new Point(event.x, event.y);
				ViewerCell cell = testscriptsViewer.getCell(p);
				
				if(cell==null)
				{
					System.out.println("Cell Null");
				}
			}
		});
		*/
		addBtn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				try
				{
					int selectedIndex = testscriptTable.getSelectionIndex(); 
					List<TCStepsGSON> list = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
					if(selectedIndex!=-1)
					{
						//Add at particular index
						TCStepsGSON step = new TCStepsGSON();
						step.stepNo = selectedIndex + 1;
						step.stepOperation = "";
						step.stepPageName = "";
						step.stepObjName = "";
						step.stepArgument = "";
						step.stepVarName = "";
						list.add(selectedIndex+1, step);
						list = updateCountTestCase(list);
						testscriptsViewer.refresh();
						testscriptsViewer.editElement(step, 0);
						isDirty = true;
						firePropertyChange(PROP_DIRTY);
					}
					else
					{
						//Add at the bottom of the table
						TCStepsGSON step = new TCStepsGSON();
						step.stepNo = list.size()+1;
						step.stepOperation = "";
						step.stepPageName = "";
						step.stepObjName = "";
						step.stepArgument = "";
						step.stepVarName = "";
						list.add(step);
						testscriptsViewer.refresh();
						testscriptsViewer.editElement(step, 0);
						isDirty = true;
						firePropertyChange(PROP_DIRTY);
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : addListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		delBtn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				try
				{
					int selectedIndex = testscriptTable.getSelectionIndex();
					List<TCStepsGSON> list = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
					if(selectedIndex!=-1)
					{
						list.remove(selectedIndex);
						list = updateCountTestCase(list);
						testscriptsViewer.refresh();
						isDirty = true;
						firePropertyChange(PROP_DIRTY);
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : addListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void setDropListener(DropTarget target)
	{
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		final Transfer[] types = new Transfer[] {textTransfer};
		target.setTransfer(types);
		target.addDropListener(new DropTargetListener() {
			
			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void drop(DropTargetEvent event) {
				// TODO Auto-generated method stub
				if(textTransfer.isSupportedType(event.currentDataType))
				{
					String text = (String)event.data;
					String data[] = text.split("__");
					if(data[0].equals("OBJECTDATA"))
					{
						List<TCStepsGSON> tcIP = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
						TCStepsGSON newStep = new TCStepsGSON();
						newStep.stepOperation = "";
						newStep.stepNo = tcIP.size()+1;
						newStep.stepPageName = data[1];
						newStep.stepObjName = data[2];
						newStep.omName = data[3];
						tcIP.add(newStep);
						testscriptsViewer.refresh();
						if(!isDirty)
						{
							isDirty = true;
							firePropertyChange(PROP_DIRTY);
						}
					}
					else if(data[0].equals("PARAMS"))
					{
						Point point = new Point(event.x,event.y);
						TableItem item = (TableItem)event.item;
						if(item!=null) //if item has a value then
						{
							int index = new Integer(item.toString());
							List<TCStepsGSON> tcIPatPos = (ArrayList<TCStepsGSON>) testscriptsViewer.getInput();
							TCStepsGSON tcStep = tcIPatPos.get(index);
							tcStep.stepArgument = data[1];
							testscriptsViewer.refresh();
							if(!isDirty)
							{
								isDirty = true;
								firePropertyChange(PROP_DIRTY);
							}
						}
					}
				}
			}
			
			public void dragOver(DropTargetEvent event) {
				// TODO Auto-generated method stub
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				if(textTransfer.isSupportedType(event.currentDataType))
				{
					Object o = textTransfer.nativeToJava(event.currentDataType);
					String t = (String)o;
					if(t!=null)
					{}
				}
			}
			
			public void dragOperationChanged(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void dragEnter(DropTargetEvent event) {
				// TODO Auto-generated method stub
				if(event.detail == DND.DROP_DEFAULT)
				{
					if((event.operations & DND.DROP_COPY)!=0)
					{
						event.detail = DND.DROP_COPY;
					}
					else
					{
						event.detail = DND.DROP_NONE;
					}
				}
			}
		});
	}
	
	public void loadTestSteps(TableViewer parent)
	{
		try
		{
			TCGson tcGson = tcTask.getTcGson();
			parent.setInput(tcGson.tcSteps);
			
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
		try
		{
			if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(TestCaseParamView.ID)==null)
			{
				IViewPart testcaseParamView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TestCaseParamView.ID);
				
			}
			if(!isFocus)
			{
				TestCaseParamView.loadTestCaseParameters(tcTask.getTcGson());
				isFocus = true;
			}
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
				page.setPerspective(openAutomaticsPerspective);
			}
			
		}
		catch(Exception e){
			System.out.println("["+getClass().getName() + " - SetFocus] : Exception "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	//Update the test case step count
	public List<TCStepsGSON> updateCountTestCase(List<TCStepsGSON> steps)
	{
		for(int i=0;i<steps.size();i++)
		{
			steps.get(i).stepNo = (i+1);
		}
		return steps;
	}
}
