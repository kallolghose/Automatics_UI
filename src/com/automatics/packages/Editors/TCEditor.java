 package com.automatics.packages.Editors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.Views.ObjectList;
import com.automatics.packages.Views.ObjectMap;
import com.automatics.packages.Views.TestCaseParamView;
import com.automatics.utilities.alltablestyles.TCArgumentsColumnEditable;
import com.automatics.utilities.alltablestyles.TCObjectNameColumnEditable;
import com.automatics.utilities.alltablestyles.TCOperationColumnEditable;
import com.automatics.utilities.alltablestyles.TCPageNameColumnEditable;
import com.automatics.utilities.alltablestyles.TCVariableColumnEditable;
import com.automatics.utilities.chrome.extension.AddOnUtility;
import com.automatics.utilities.git.GitUtilities;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCParams;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowLayout;


public class TCEditor extends EditorPart {
	
	public static String ID = "com.automatics.packages.Editors.tcEditor";
	public static String currentTestCase = null;
	private TestCaseTask tcTask;
	private TestCaseEditorInput input;
	private Table testscriptTable;
	private TableViewer testscriptsViewer; 
	private boolean isDirty = false;
	private GitUtilities gitUtil;
	private ToolItem addBtn, delBtn, saveItem, copyItem, pasteItem, openEditor, commitItem, pullItem, lockItem, start_stop_recording;
	private Label lockLabel;
	
	private boolean isFocus = false;
	private boolean viewAllElements = true;
	private List<TCStepsGSON> listOfTestCaseSteps;
	private TCStepsGSON copiedGson;
	private String lock_image = "images/icons/Open_lock.png";
	private boolean public_view = false, private_view = false;
	private String lock_message = "Lock for editing";
	private String user_lock_message = "";
	
	/*
	 * Variables for recording
	 * */
	private AddOnUtility addOnUtility = AddOnUtility.getInstance();
	private String OBJECTMAP_FOR_RECORDING = "";
	
	public TCEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		saveActionPerform();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException 
	{
	    if (!(input instanceof TestCaseEditorInput)) 
	    {
            throw new RuntimeException("Wrong input");
	    }
	
	    this.input = (TestCaseEditorInput) input;
	    tcTask = TestCaseTaskService.getInstance().getTaskByTcName(this.input.getId());
	    
	    if(tcTask==null)
	    {
	    	throw new RuntimeException("Test Case does not exists.");
	    }
		
	    //Initialize the Existing GIT Repository
	    this.gitUtil = new GitUtilities();
	    this.gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
	    this.gitUtil.initExistingRepository();
	    
	    //Check if the test case is private or not
	    TCGson tcGson = tcTask.getTcGson();
	    if(tcGson.tcFlag.equalsIgnoreCase("PRIVATE"))
	    {
	    	private_view = true;
	    	public_view = private_view;
	    	viewAllElements = true;
	    	user_lock_message = "";
	    	if(!Utilities.AUTOMATICS_USERNAME.equalsIgnoreCase(tcGson.username))
	    	{
	    		MessageDialog privateChk = new MessageDialog(site.getShell(), "Error", null, "Cannot Open Private Test Case", 
	    				MessageDialog.ERROR, 
	    				new String[]{"OK"}, 0);
	    		privateChk.open();
	    		throw new RuntimeException("Cannot open private test case");	
	    	}
	    }
	    else if(tcGson.tcFlag.equalsIgnoreCase("EDIT"))
	    {
	    	if(!Utilities.AUTOMATICS_USERNAME.equalsIgnoreCase(tcGson.username))
	    	{
	    		viewAllElements = false;
	    		user_lock_message = "Locked By : " + tcGson.username;
	    	}
	    	else
	    	{
	    		lock_image = "images/icons/lock.png";
	    		lock_message = "Unlock the file";
	    		public_view = true;
	    		user_lock_message = "";
	    	}
	    }
	    else
	    {
	    	public_view = false;
	    	user_lock_message = "";
		    String currentFileName = Utilities.TESTCASE_FILE_LOCATION + tcTask.getTcName() + ".java";
		    boolean syncStatus = this.gitUtil.getSync(currentFileName);
		    if(syncStatus)
		    {
		    	MessageDialog dialog = new MessageDialog(site.getShell(), "Warning", null, "File not in sync. Please get sync",
		    			MessageDialog.WARNING, 
						new String[]{"OK","Cancel"}, 0);
		    	int selected = dialog.open();
		    	switch(selected)
		    	{
		    	case 0:
		    		this.gitUtil.performSpecificPull(currentFileName);
		    		MessageDialog promptMsg = new MessageDialog(site.getShell(), "Information", null,"Synch Completed !!", 
		    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
		    		promptMsg.open();
		    		break;
		    	}
		    }
	    }
	    OBJECTMAP_FOR_RECORDING = tcTask.getTcName() + "_Recorded_OM";
	    //When all operation done call all methods for Editor Display
	    setSite(site);
	    setInput(input);
	    setPartName("TestCase:" + tcTask.getTcName());   
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
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
			GridLayout gl_composite = new GridLayout(2, false);
			gl_composite.marginHeight = 0;
			gl_composite.marginWidth = 0;
			composite.setLayout(gl_composite);
			GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd_composite.heightHint = 24;
			gd_composite.widthHint = 576;
			composite.setLayoutData(gd_composite);
			
			ToolBar iconsToolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
			iconsToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			addBtn = new ToolItem(iconsToolBar, SWT.NONE);
			addBtn.setWidth(30);
			addBtn.setToolTipText("Add Testcase Step");
			addBtn.setSelection(true);
			addBtn.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
			addBtn.setEnabled(viewAllElements && public_view);
			
			delBtn = new ToolItem(iconsToolBar, SWT.NONE);
			delBtn.setToolTipText("Delete Testcase step");
			delBtn.setSelection(true);
			delBtn.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
			delBtn.setEnabled(viewAllElements && public_view);
			
			saveItem = new ToolItem(iconsToolBar, SWT.NONE);
			saveItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Save.png"));
			saveItem.setToolTipText("Save");
			saveItem.setSelection(true);
			saveItem.setEnabled(viewAllElements && public_view);
			
			copyItem = new ToolItem(iconsToolBar, SWT.NONE);
			copyItem.setToolTipText("Copy");
			copyItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Copy.png"));
			copyItem.setSelection(true);
			copyItem.setEnabled(viewAllElements && public_view);
			
			pasteItem = new ToolItem(iconsToolBar, SWT.NONE);
			pasteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/1485966418_Paste.png"));
			pasteItem.setToolTipText("Paste");
			pasteItem.setSelection(true);
			pasteItem.setEnabled(viewAllElements && public_view);
			
			openEditor = new ToolItem(iconsToolBar, SWT.NONE);
			openEditor.setToolTipText("View In Editor");
			openEditor.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/1485966863_editor-grid-view-block-glyph.png"));
			openEditor.setSelection(true);
			openEditor.setEnabled(viewAllElements && public_view);
			
			commitItem = new ToolItem(iconsToolBar, SWT.NONE);
			commitItem.setToolTipText("Commit And Push");
			commitItem.setWidth(26);
			commitItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/git_commit.png"));
			commitItem.setSelection(true);
			commitItem.setEnabled(viewAllElements && public_view);
			
			pullItem = new ToolItem(iconsToolBar, SWT.NONE);
			pullItem.setToolTipText("Pull");
			pullItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/pull.png"));
			pullItem.setSelection(true);
			pullItem.setEnabled(viewAllElements && public_view);
			
			start_stop_recording = new ToolItem(iconsToolBar, SWT.NONE);
			start_stop_recording.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/start_rec_2.png"));
			start_stop_recording.setToolTipText("Start Recording");
			start_stop_recording.setData("isRecorded", false);
			start_stop_recording.setEnabled(viewAllElements && public_view);
			
			lockItem = new ToolItem(iconsToolBar, SWT.NONE);
			lockItem.setSelection(true);
			lockItem.setToolTipText(lock_message);
			lockItem.setImage(ResourceManager.getPluginImage("Automatics", lock_image));
			lockItem.setData("Locked", false);
			lockItem.setEnabled(viewAllElements && !private_view); //If private view then do not show lock
			
			lockLabel = new Label(composite, SWT.HORIZONTAL | SWT.RIGHT);
			lockLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lockLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
			lockLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
			lockLabel.setText("Lock Message");
			if(user_lock_message.equals(""))
			{
				lockLabel.setVisible(false);
			}
			else
			{
				lockLabel.setText(user_lock_message);
				lockLabel.setVisible(true);
			}
			
			//Implementation of table using TableViewer
			testscriptsViewer = new TableViewer(script_composite, SWT.BORDER | SWT.FULL_SELECTION);
			testscriptsViewer.setContentProvider(new ArrayContentProvider());
			testscriptTable = testscriptsViewer.getTable();
			testscriptTable.setLinesVisible(true);
			testscriptTable.setHeaderVisible(true);
			testscriptTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			testscriptTable.setEnabled(viewAllElements && public_view);
			
			/*Biswabir Code - Tabbing*/
			TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(testscriptsViewer,new FocusCellHighlighter(testscriptsViewer) {});
			ColumnViewerEditorActivationStrategy editorActivationStrategy =
						new ColumnViewerEditorActivationStrategy(testscriptsViewer) 
						{
				            @Override
				            protected boolean isEditorActivationEvent(
				                ColumnViewerEditorActivationEvent event) {
				                    ViewerCell cell = (ViewerCell) event.getSource();
				                   return cell.getColumnIndex() == 1 || cell.getColumnIndex() == 2;
			            }};

			TableViewerEditor.create(testscriptsViewer, focusCellManager, editorActivationStrategy,
				    TableViewerEditor.TABBING_HORIZONTAL);
			
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
			snoCol.setWidth(40);
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
			if(tcTask.getTcGson().tcObjectMapLink!=null)
			{
				boolean first = true;
				for(String omName : tcTask.getTcGson().tcObjectMapLink)
				{
					if(first)
					{
						//First time initialize object maps
						ObjectMap.loadObjectMap(omName);
						first=false;
					}
					else
					{
						ObjectMap.addObjectMap(omName);
					}
				}
			}
			
			/*Open test case parameter view*/
			TestCaseParamView.loadTestCaseParameters(tcTask.getTcGson());
			
			DropTarget dropTarget = new DropTarget(testscriptTable, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
			setDropListener(dropTarget); //Set Drop Listener
			setListeners();
		}
		catch(Exception e) 
		{
			System.out.println("[" + getClass().getName() + " : createcontent - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	
	public void setListeners()
	{
		
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
						step.omName = "";
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
						step.omName = "";
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
		
		
		saveItem.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				try
				{
					/*Biswabir fix*/
					List<String>collList=new ArrayList<String>(); 
					List<TCStepsGSON> steps = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
					for (TCStepsGSON tcStepsGSON : steps) {
						if(tcStepsGSON.stepOperation.equals("")){
							collList.add(tcStepsGSON.stepOperation);
							break;
						}
						
						else if(tcStepsGSON.stepPageName.equals("")){
							collList.add(tcStepsGSON.stepPageName);
							break;
									
						}
						else if(tcStepsGSON.stepObjName.equals("")){
							collList.add(tcStepsGSON.stepObjName);
							break;
						}
					}
					if(!collList.contains(""))
					{
						saveActionPerform();
					}
					else
					{
						MessageDialog dialog = new MessageDialog(getSite().getShell(), "Error", null,
							    "Please fill all the fields", MessageDialog.ERROR, new String[] { "ok"
							         }, 0);
						dialog.open();
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " Save: addListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		copyItem.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				try
				{
					listOfTestCaseSteps = (List<TCStepsGSON>)testscriptsViewer.getInput();
					copiedGson = listOfTestCaseSteps.get(testscriptTable.getSelectionIndex());
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
					
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " Copy: addListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		pasteItem.addListener(SWT.Selection, new Listener() {	
			public void handleEvent(Event event) {
				try
				{
					listOfTestCaseSteps = (List<TCStepsGSON>)testscriptsViewer.getInput();
					TCStepsGSON newStep = new TCStepsGSON();
					newStep.stepNo = testscriptTable.getSelectionIndex()+1;
					newStep.stepOperation = copiedGson.stepOperation;
					newStep.stepPageName = copiedGson.stepPageName;
					newStep.stepObjName = copiedGson.stepObjName;
					newStep.stepArgument = copiedGson.stepArgument;
					newStep.stepVarName = copiedGson.stepVarName;
					
					listOfTestCaseSteps.add(testscriptTable.getSelectionIndex(),newStep);
					
					//Update the count of the steps
					List<TCStepsGSON> updatedList = new ArrayList<TCStepsGSON>();
					int cntr = 1;
					for(TCStepsGSON step : listOfTestCaseSteps)
					{
						step.stepNo = cntr;
						updatedList.add(step);
						cntr++;
					}
					testscriptsViewer.setInput(updatedList);
					testscriptsViewer.refresh();
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + "Paste : addListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
				
		});
		openEditor.addListener(SWT.Selection, new Listener(){
			
			public void handleEvent(Event event) 
			{
				try
				{
					String fileName = Utilities.createJavaFiles(tcTask.getTcGson());	
					
					//Open the editor
					//Get file path relative the the workspace
					String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
					File file = new File(fileName);
					String filePath = file.getAbsolutePath().substring(workspacePath.length()+1);
					
					//Open the file
					IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
					IPath location = Path.fromOSString(filePath); 
					IFile projectFile = workspace.getRoot().getFile(location);
					Utilities.openEditor(projectFile, null);
					
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : openEditor.addListener()] - Exception :" + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		commitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				
				/*Save the test case with Flag as Public*/
				TCGson tcGson = tcTask.getTcGson();
				tcGson.tcFlag = "PUBLIC";
				tcTask.setTcGson(tcGson);
				saveActionPerform();
				lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Open_lock.png"));
				lockItem.setToolTipText("Lock for editing");
				lockItem.setData("Locked",false);
				lockItem.setEnabled(true);
				
				/*Add lock to make components un-editable*/
				public_view = false;
				addBtn.setEnabled(viewAllElements && public_view);
				delBtn.setEnabled(viewAllElements && public_view);
				saveItem.setEnabled(viewAllElements && public_view);
				copyItem.setEnabled(viewAllElements && public_view);
				pasteItem.setEnabled(viewAllElements && public_view);
				openEditor.setEnabled(viewAllElements && public_view);
				commitItem.setEnabled(viewAllElements && public_view);
				pullItem.setEnabled(viewAllElements && public_view);
				testscriptTable.setEnabled(viewAllElements && public_view);
				start_stop_recording.setEnabled(viewAllElements && public_view);
				
				/*Commit the changes*/
				String currentFileName = Utilities.TESTCASE_FILE_LOCATION + tcTask.getTcName() + ".java";
				gitUtil.performPull();
				gitUtil.performSpecificCommit(currentFileName);
				gitUtil.performPush();
				MessageDialog commitMsg = new MessageDialog(getSite().getShell(), "Information", null,"Commit And Push Performed.", 
	    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
	    		commitMsg.open();
			}
		});
		
		pullItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String currentFileName = Utilities.TESTCASE_FILE_LOCATION + tcTask.getTcName() + ".java";
				if(gitUtil.getDiff(currentFileName)) //If changes are made to the file then ask to commit or contents shall be replaced
				{
					MessageDialog dialog = new MessageDialog(getSite().getShell(), "Warning", null,
							"Changes made in file are not commited.Please commit them them or changes will be overwritten.",
			    			MessageDialog.WARNING, 
							new String[]{"Commit","Overwrite/Pull"}, 0);
			    	int selected = dialog.open();
			    	switch(selected)
			    	{
			    	case 0:
			    		gitUtil.performSpecificCommit(currentFileName);
			    		MessageDialog commitMsg = new MessageDialog(getSite().getShell(), "Information", null,"Commit Completed !!", 
			    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
			    		commitMsg.open();
			    		break;
			    	case 1:
			    		gitUtil.performSpecificPull(currentFileName);
			    		MessageDialog pullMsg = new MessageDialog(getSite().getShell(), "Information", null,"Pull Performed !!", 
			    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
			    		pullMsg.open();
			    		break;
			    	}
				}
				else
				{
					gitUtil.performSpecificPull(currentFileName);
		    		MessageDialog pullMsg = new MessageDialog(getSite().getShell(), "Information", null,"Pull Performed !!", 
		    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
		    		pullMsg.open();
				}
				
			}
		});
		
		lockItem.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event event) 
			{
				boolean locked = new Boolean(lockItem.getData("Locked").toString());
				if(!locked)
				{
					lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/lock.png"));
					lockItem.setToolTipText("Unlock the file");
					TCGson tcGson = tcTask.getTcGson();
					tcGson.tcFlag = "EDIT";
					tcGson.username = Utilities.AUTOMATICS_USERNAME;
					tcTask.setTcGson(tcGson);
					public_view = true;
					
					saveActionPerform();
				}
				else
				{
					lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Open_lock.png"));
					lockItem.setToolTipText("Lock for editing");
					TCGson tcGson = tcTask.getTcGson();
					tcGson.tcFlag = "PUBLIC";
					tcGson.username = Utilities.AUTOMATICS_USERNAME;
					tcTask.setTcGson(tcGson);
					saveActionPerform();
					
					public_view = false;
					
					/*Commit the changes*/
					/*
					String currentFileName = Utilities.TESTCASE_FILE_LOCATION + tcTask.getTcName() + ".java";
					gitUtil.performPull();
					gitUtil.performSpecificCommit(currentFileName);
					gitUtil.performPush();
					*/
				}
				addBtn.setEnabled(viewAllElements && public_view);
				delBtn.setEnabled(viewAllElements && public_view);
				saveItem.setEnabled(viewAllElements && public_view);
				copyItem.setEnabled(viewAllElements && public_view);
				pasteItem.setEnabled(viewAllElements && public_view);
				openEditor.setEnabled(viewAllElements && public_view);
				commitItem.setEnabled(viewAllElements && public_view);
				pullItem.setEnabled(viewAllElements && public_view);
				testscriptTable.setEnabled(viewAllElements && public_view);
				start_stop_recording.setEnabled(viewAllElements && public_view);
				lockItem.setData("Locked", !locked);
			}
		});
		
		start_stop_recording.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TCEditor editor = (TCEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				boolean isRecording = (Boolean)start_stop_recording.getData("isRecorded");
				/*addOnUtility.setEditorInput(getEditorInput());
				addOnUtility.setTestCaseEditor(editor);
				addOnUtility.setDisplay(getSite().getShell().getDisplay());*/
				addOnUtility.setTestCaseEditorAndDisplay(editor, getEditorInput(), getSite().getShell().getDisplay());

				if(!isRecording)
				{
					/*
					 * Create Object Map task and add to the task service*/
					
					//1.ObjectMapTaskService (To Be Used By Editor)
					//2.ObjectMapSaveService (To Be Used by save service)
					
					ObjectMapTask omTask = ObjectMapTaskService.getInstance().getTaskByOmName(OBJECTMAP_FOR_RECORDING);
					ObjectMapSaveTask omSaveTask = ObjectMapSaveService.getInstance().getSaveTask(OBJECTMAP_FOR_RECORDING);
					if(omTask == null) //If first time recording
					{
						OMGson omGson = new OMGson();
						omGson.omName = OBJECTMAP_FOR_RECORDING;
						omGson.omDesc = "Recorded Object For TestCase : " + tcTask.getTcName();
						omGson.omFlag = "PRIVATE";
						omGson.omIdentifier = OBJECTMAP_FOR_RECORDING;
						omGson.username = System.getProperty("user.name");
						
						omGson.omDetails = new ArrayList<OMDetails>();
						
						omTask = new ObjectMapTask(OBJECTMAP_FOR_RECORDING, omGson.omDesc, omGson.omIdentifier, omGson);
						omSaveTask = new ObjectMapSaveTask(OBJECTMAP_FOR_RECORDING, omGson);
						ObjectMapTaskService.getInstance().addTasks(omTask);
						ObjectMapSaveService.getInstance().addSaveTask(omSaveTask);
						JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(OMGson.class, omGson));
						if(jsonObj!=null)
						{
							AutomaticsDBObjectMapQueries.postOM(Utilities.getMongoDB(), jsonObj);
						}
					}
					
					//Add ObjectMap
					TCGson tcGson = tcTask.getTcGson();
					List<String> omLink = tcGson.tcObjectMapLink;
					if(omLink==null || omLink.size()==0)
						omLink = new ArrayList<String>();
					omLink.add(OBJECTMAP_FOR_RECORDING);
					Set<String> hs = new HashSet();
					hs.addAll(omLink);
					omLink.clear(); omLink.addAll(hs);
					tcGson.tcObjectMapLink = omLink;
					tcTask.setTcGson(tcGson);
					ObjectMap.addObjectMap(OBJECTMAP_FOR_RECORDING);
					ObjectList.addNewObjectMap(omTask);
					
					start_stop_recording.setToolTipText("Stop Recording");
					start_stop_recording.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/stop_rec.png"));
					start_stop_recording.setData("isRecorded", !isRecording);
					addOnUtility.openCloseServer(true); /*Open the server*/
					addOnUtility.start_stop_Recording(true);
					
					MessageDialog popup = new MessageDialog(getSite().getShell(), "Start Recording", null, 
							"Recording started. Please use Google chrome to perform recording.", 
							MessageDialog.INFORMATION, new String[]{"OK"}, 0);
					popup.open();
				}
				else
				{
					start_stop_recording.setToolTipText("Start Recording");
					start_stop_recording.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/start_rec_2.png"));
					start_stop_recording.setData("isRecorded", !isRecording);
					addOnUtility.openCloseServer(false); /*Close the server*/
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
						TableItem item = (TableItem)event.item;
						if(item!=null)
						{
							String findIndex[] = item.toString().split("\\{|\\}");
							System.out.println(findIndex[1]);
							int index = new Integer(findIndex[1]);
							List<TCStepsGSON> tcIP = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
							TCStepsGSON tcStep = tcIP.get(index-1);
							tcStep.stepPageName = data[1];
							tcStep.stepObjName = data[2];
							tcStep.omName = data[3];
							testscriptsViewer.refresh();
						}
						else
						{	
							List<TCStepsGSON> tcIP = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
							TCStepsGSON newStep = new TCStepsGSON();
							newStep.stepNo = tcIP.size()+1;
							newStep.stepOperation = "";
							newStep.stepPageName = data[1];
							newStep.stepObjName = data[2];
							newStep.stepArgument = "";
							newStep.stepVarName = "";
							newStep.omName = data[3];
							tcIP.add(newStep);
							testscriptsViewer.refresh();
						}
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
							String findIndex[] = item.toString().split("\\{|\\}");
							int index = new Integer(findIndex[1]);
							List<TCStepsGSON> tcIPatPos = (ArrayList<TCStepsGSON>) testscriptsViewer.getInput();
							TCStepsGSON tcStep = tcIPatPos.get(index-1);
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
	
	/**
	 * saveActionPerform;
	 */
	private void saveActionPerform() {
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
					List<TCParams> listOfTCParams = TestCaseParamView.getAllTestCaseParameters();
					if(listOfTCParams == null)
						listOfTCParams = new ArrayList<TCParams>();
					
					tcSaveGson.tcParams = listOfTCParams;
					
					tcTask.setTcGson(tcSaveGson);
					
					//Perform git operations
					Utilities.createJavaFiles(tcTask.getTcGson());
					boolean gitPassed = this.gitUtil.performGITSyncOperation();
					if(!gitPassed)
					{
						MessageDialog errDialog = new MessageDialog(getSite().getShell(),"Save Failure", 
								null, "Something went wrong - " + this.gitUtil.getErrMsg() + "\nPlease save again", MessageDialog.ERROR, 
								new String[]{"OK"}, 0);
						errDialog.open();
						return;
					}
					
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
											 "Some Unexpected Error Occured", "ERR").open();
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
	public void setFocus() {
		// TODO Auto-generated method stub
		try
		{
			currentTestCase = tcTask.getTcName();
			
			if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(TestCaseParamView.ID)==null)
			{
				IViewPart testcaseParamView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TestCaseParamView.ID);
				
			}
			
			/*
			TestCaseParamView.currentTask = tcTask;
			TestCaseParamView.loadTestCaseParameters(tcTask.getTcGson());
			isFocus = true;
			*/
			
			List<TCStepsGSON> list = (ArrayList<TCStepsGSON>)testscriptsViewer.getInput();
			tcTask = TestCaseTaskService.getInstance().getTaskByTcName(tcTask.getTcName());
			list = tcTask.getTcGson().tcSteps;
			testscriptsViewer.setInput(list);
			testscriptsViewer.refresh();
			
			if(tcTask.getTcGson().tcObjectMapLink!=null)
			{
				if(tcTask.getTcGson().tcObjectMapLink.size()>0) //Load the object map if the data is in the list
				{
					if(ObjectMap.getObjectMapsCount()==0)
					{
						boolean first = true;
						for(String omName : tcTask.getTcGson().tcObjectMapLink)
						{
							if(first) {
								ObjectMap.loadObjectMap(omName);
								first=false;
							}
							else
							{
								ObjectMap.addObjectMap(omName);
							}
						}
					}
				}
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
	
	public void refreshTableContents()
	{
		try
		{
			TCGson tcGson = tcTask.getTcGson();
			testscriptsViewer.setInput(tcGson.tcSteps);
			testscriptsViewer.refresh();
		}
		catch(Exception e)
		{
			System.out.println("[TCEditor - refreshTableContents()] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * Add Contents to table (Particularly for recording)
	 * */
	public void addContentsToTableGrid(TCStepsGSON step, OMDetails details)
	{
		try
		{
			/*
			 * Add Object Map Values
			 * 1. Update the page name and object map name values
			 * 2. Update the task
			 * */
			HashMap<String,String> pgName_ObjMapName = ObjectMap.getPageNameObjectMapMapping();
			HashMap<String, ArrayList<String>> pgName_ObjName = ObjectMap.getPageNameObjectMapping();
			
			//Add object map array to the page name
			pgName_ObjMapName.put(step.stepPageName, OBJECTMAP_FOR_RECORDING);
			ArrayList<String> objmap_name = pgName_ObjName.get(step.stepPageName);
			
			if(objmap_name == null)
				objmap_name = new ArrayList<String>();
			objmap_name.add(step.stepObjName);
			pgName_ObjName.put(step.stepPageName, objmap_name);
			
			/*
			 * Update task
			 * 1. Editor Task
			 * 2. Object Map Save task
			 */
			
			//#1
			ObjectMapTask omTask = ObjectMapTaskService.getInstance().getTaskByOmName(OBJECTMAP_FOR_RECORDING);
			OMGson omGson = omTask.getOmGson();
			ArrayList<OMDetails> omDetails = (ArrayList<OMDetails>) omGson.omDetails;
			if(omDetails == null)
			{
				omDetails = new ArrayList<OMDetails>(); 
			}
			omDetails.add(details);
			omGson.omDetails = omDetails;
			omTask.setOmGson(omGson);
			
			//#2
			ObjectMapSaveTask omSaveTask = ObjectMapSaveService.getInstance().getSaveTask(OBJECTMAP_FOR_RECORDING);
			omSaveTask.setOmGson(omGson);
			
			/*Update the test case steps*/
			List<TCStepsGSON> list_of_steps = (List<TCStepsGSON>)testscriptsViewer.getInput();
			if(list_of_steps == null)
			{
				list_of_steps = new ArrayList<TCStepsGSON>();
			}
			step.stepNo = list_of_steps.size()+1;
			step.omName = OBJECTMAP_FOR_RECORDING;
			list_of_steps.add(step);
			testscriptsViewer.setInput(list_of_steps);
			testscriptsViewer.refresh();
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : addContentsToTableGrid()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
