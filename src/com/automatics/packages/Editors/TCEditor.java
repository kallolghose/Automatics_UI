 package com.automatics.packages.Editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.Views.ObjectMap;
import com.automatics.packages.Views.TestCaseParamView;
import com.automatics.utilities.alltablestyles.TCArgumentsColumnEditable;
import com.automatics.utilities.alltablestyles.TCObjectNameColumnEditable;
import com.automatics.utilities.alltablestyles.TCOperationColumnEditable;
import com.automatics.utilities.alltablestyles.TCPageNameColumnEditable;
import com.automatics.utilities.alltablestyles.TCVariableColumnEditable;
import com.automatics.utilities.git.GitUtilities;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.Utilities;

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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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


public class TCEditor extends EditorPart {
	
	public static String ID = "com.automatics.packages.Editors.tcEditor";
	public static String currentTestCase = null;
	private TestCaseTask tcTask;
	private TestCaseEditorInput input;
	private Table testscriptTable;
	private TableViewer testscriptsViewer; 
	private boolean isDirty = false;
	private GitUtilities gitUtil;
	private ToolItem addBtn, delBtn, saveItem, copyItem, pasteItem, openEditor, commitItem, pullItem, lockItem;
	private boolean isFocus = false;
	private boolean viewAllElements = true;
	private List<TCStepsGSON> listOfTestCaseSteps;
	private TCStepsGSON copiedGson;
	private String lock_image = "images/icons/Open_lock.png";
	private boolean public_view = false, private_view = false;
	private String lock_message = "Lock for editing";
	
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
	    	}
	    	else
	    	{
	    		lock_image = "images/icons/lock.png";
	    		lock_message = "Unlock the file";
	    		public_view = true;
	    	}
	    }
	    else
	    {
	    	public_view = false;
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
			GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			gd_composite.heightHint = 24;
			gd_composite.widthHint = 576;
			composite.setLayoutData(gd_composite);
			
			ToolBar iconsToolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
			iconsToolBar.setBounds(0, 0, 420, 23);
			
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
			
			lockItem = new ToolItem(iconsToolBar, SWT.NONE);
			lockItem.setSelection(true);
			lockItem.setToolTipText(lock_message);
			lockItem.setImage(ResourceManager.getPluginImage("Automatics", lock_image));
			lockItem.setData("Locked", false);
			lockItem.setEnabled(viewAllElements && !private_view); //If private view then do not show lock
			
			
			//Implementation of table using TableViewer
			testscriptsViewer = new TableViewer(script_composite, SWT.BORDER | SWT.FULL_SELECTION);
			testscriptsViewer.setContentProvider(new ArrayContentProvider());
			testscriptTable = testscriptsViewer.getTable();
			testscriptTable.setLinesVisible(true);
			testscriptTable.setHeaderVisible(true);
			testscriptTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			testscriptTable.setEnabled(viewAllElements && public_view);
			
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
			
			//Open test case parameter view
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
				saveActionPerform();
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
					System.out.println(testscriptTable.getSelectionIndex());
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
				lockItem.setData("Locked", !locked);
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
					tcSaveGson.tcParams = TestCaseParamView.getAllTestCaseParameters();
					
					tcTask.setTcGson(tcSaveGson);
					
					JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TCGson.class, tcSaveGson));
					System.out.println(jsonObj.toString());
					if(jsonObj !=null)
					{
						AutomaticsDBTestCaseQueries.updateTC(Utilities.getMongoDB(), tcSaveGson.tcName, jsonObj);
						isDirty = false;
						firePropertyChange(PROP_DIRTY);
						Utilities.createJavaFiles(tcTask.getTcGson());
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
}
