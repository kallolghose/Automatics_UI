package com.automatics.packages.Editors;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.management.RuntimeErrorException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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

import com.automatics.packages.Perspective;
import com.automatics.packages.Model.TestSuiteTask;
import com.automatics.packages.Model.TestSuiteTaskService;
import com.automatics.packages.api.handlers.TestCaseAPIHandler;
import com.automatics.packages.api.handlers.TestSuiteAPIHandler;
import com.automatics.utilities.alltablestyles.TSFifthColumnEditable;
import com.automatics.utilities.alltablestyles.TSFirstColumnEditable;
import com.automatics.utilities.alltablestyles.TSFourthColumnEditable;
import com.automatics.utilities.alltablestyles.TSSecondColumnEditable;
import com.automatics.utilities.alltablestyles.TSTestCaseColumnEditable;
import com.automatics.utilities.alltablestyles.TSThirdColumnEditable;
import com.automatics.utilities.git.GitUtilities;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.gsons.testsuite.TSTCParamGson;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.runner.TestSuiteRunnerAPI;
import com.mongodb.DB;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.wb.swt.SWTResourceManager;

public class TestSuiteEditor extends EditorPart {

	public static String ID = "com.automatics.packages.Editors.tsEditor"; 
	private TestSuiteTask tsTask;
	private TestSuiteEditorInput input;
	private Table testsuitetable;
	private TableViewer testsuiteviewer;
	public static ArrayList<String> testCaseList = new ArrayList<String>();
	private DB db;
	private boolean isDirty = false;
	private ToolItem addBtn,delBtn, saveItem, copyItem, pasteItem, viewEditor, lockItem;
	private List<TSTCGson> listStepGSON;
	private ArrayList<TSTCGson> copiedCell;
	private GitUtilities gitUtil;
	private Label errLabel;
	private String user_lock_message = "Lock Username";
	private boolean viewAllElements = true;
	private String lock_image = "images/icons/Open_lock.png";
	private String lock_message = "Lock for editing";
	
	public TestSuiteEditor() {
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
		
		TSGson tsGson = tsTask.getTsGson();
		
		if(!tsGson.lockedBy.equalsIgnoreCase(""))
		{
			if(!Utilities.AUTOMATICS_USERNAME.equalsIgnoreCase(tsGson.lockedBy))
	    	{
	    		viewAllElements = false;
	    		user_lock_message = "Locked By : " + tsGson.lockedBy;
	    	}
			else
	    	{
				viewAllElements = true;
	    		lock_image = "images/icons/lock.png";
	    		lock_message = "Unlock the file";
	    		user_lock_message = "";
	    	}
			
		}
		else
		{
			viewAllElements = false;
			user_lock_message = "";
		}
		
		/*Load all the test case names in the ArrayList for drop down*/
		TCGson [] allTC = TestCaseAPIHandler.getInstance().getAllTestCases();
		testCaseList = new ArrayList<String>();
		for(TCGson tcGson : allTC)
		{
			testCaseList.add(tcGson.tcName);
		}
		
		//Initialize GIT properties
		gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		gitUtil.initExistingRepository();
		
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
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 26;
		gd_composite.widthHint = 585;
		composite.setLayoutData(gd_composite);
		
		ToolBar iconsToolbar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		iconsToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		addBtn = new ToolItem(iconsToolbar, SWT.NONE);
		addBtn.setToolTipText("Add a test case detail");
		addBtn.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		addBtn.setSelection(true);
		addBtn.setEnabled(viewAllElements);
		
		delBtn = new ToolItem(iconsToolbar, SWT.NONE);
		delBtn.setToolTipText("Delete a test suite detail");
		delBtn.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		delBtn.setSelection(true);
		delBtn.setEnabled(viewAllElements);
		
		saveItem = new ToolItem(iconsToolbar, SWT.NONE);
		saveItem.setToolTipText("Save");
		saveItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Save.png"));
		saveItem.setEnabled(viewAllElements);
		
		copyItem = new ToolItem(iconsToolbar, SWT.NONE);
		copyItem.setToolTipText("Copy");
		copyItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Copy.png"));
		copyItem.setEnabled(viewAllElements);	
		
		pasteItem = new ToolItem(iconsToolbar, SWT.NONE);
		pasteItem.setToolTipText("Paste");
		pasteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/1485966418_Paste.png"));
		pasteItem.setEnabled(viewAllElements);	
		
		viewEditor = new ToolItem(iconsToolbar, SWT.NONE);
		viewEditor.setEnabled(false);
		viewEditor.setToolTipText("View Editor");
		viewEditor.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/1485966863_editor-grid-view-block-glyph.png"));
		
		
		lockItem = new ToolItem(iconsToolbar, SWT.NONE);
		lockItem.setToolTipText(lock_message);
		lockItem.setData("Locked", viewAllElements);
		lockItem.setImage(ResourceManager.getPluginImage("Automatics", lock_image));
		
		errLabel = new Label(composite, SWT.NONE);
		errLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		errLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		errLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		errLabel.setText(user_lock_message);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		testsuiteviewer = new TableViewer(parentComposite, SWT.BORDER | SWT.FULL_SELECTION);
		testsuiteviewer.setContentProvider(new ArrayContentProvider());
		testsuitetable = testsuiteviewer.getTable();
		testsuitetable.setLinesVisible(true);
		testsuitetable.setHeaderVisible(true);
		testsuitetable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		testsuitetable.setEnabled(viewAllElements);
		
		/*Biswabir Code - Tabbing Issue*/
		
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(testsuiteviewer,
				new FocusCellHighlighter(testsuiteviewer){});
		ColumnViewerEditorActivationStrategy editorActivationStrategy =
					new ColumnViewerEditorActivationStrategy(testsuiteviewer) 
					{
			            @Override
			            protected boolean isEditorActivationEvent(
			                ColumnViewerEditorActivationEvent event) {
			                    ViewerCell cell = (ViewerCell) event.getSource();
			                   return cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1||cell.getColumnIndex() == 2 || cell.getColumnIndex() == 3||cell.getColumnIndex() == 4 || cell.getColumnIndex() == 5;
		            }};

		TableViewerEditor.create(testsuiteviewer, focusCellManager, editorActivationStrategy,
			    TableViewerEditor.TABBING_HORIZONTAL);
		
		
		TableViewerColumn testcaseColViewer = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		testcaseColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				if(element==null)
					return "";
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
				if(element==null)
					return "";
				TSTCGson tctsGSON = (TSTCGson) element;
				if(tctsGSON.tcParams.size()>0)
					return tctsGSON.tcParams.get(0).tcparamValue;
				return "";
			}
		});
		TableColumn tblclmnColumn = tableViewerColumn.getColumn();
		tblclmnColumn.setWidth(110);
		tblclmnColumn.setText("Exe_Platform");
		tableViewerColumn.setEditingSupport(new TSFirstColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				if(element==null)
					return "";
				TSTCGson tctsGSON = (TSTCGson) element;
				if(tctsGSON.tcParams.size()>0)
					return tctsGSON.tcParams.get(1).tcparamValue;
				return "";
			}
		});
		TableColumn tblclmnColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnColumn_1.setWidth(110);
		tblclmnColumn_1.setText("Exe_Type");
		tableViewerColumn_1.setEditingSupport(new TSSecondColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				if(element==null)
					return "";
				TSTCGson tctsGSON = (TSTCGson) element;
				if(tctsGSON.tcParams.size()>0)
					return tctsGSON.tcParams.get(2).tcparamValue;
				return "";
			}
		});
		TableColumn tblclmnColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnColumn_2.setWidth(100);
		tblclmnColumn_2.setText("Run_On");
		tableViewerColumn_2.setEditingSupport(new TSThirdColumnEditable(testsuiteviewer));
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(testsuiteviewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				if(element==null)
					return "";
				TSTCGson tctsGSON = (TSTCGson) element;
				if(tctsGSON.tcParams.size()>0)
					return tctsGSON.tcParams.get(3).tcparamValue;
				return "";
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
				if(element==null)
					return "";
				TSTCGson tctsGSON = (TSTCGson) element;
				if(tctsGSON.tcParams.size()>0)
					return tctsGSON.tcParams.get(4).tcparamValue;
				return "";
			}
		});
		TableColumn tblclmnColumn_4 = tableViewerColumn_4.getColumn();
		tblclmnColumn_4.setWidth(100);
		tblclmnColumn_4.setText("Column5");
		tableViewerColumn_4.setEditingSupport(new TSFifthColumnEditable(testsuiteviewer));
		
		DropTarget testsuitedropTarget = new DropTarget(testsuitetable, DND.DROP_MOVE);
		//Load the data to test suite table
		loadTestSuiteData();
		setListeners();
		setDropListener(testsuitedropTarget);
	}

	public void setDropListener(DropTarget target)
	{
		try
		{
			final TextTransfer textTransfer = TextTransfer.getInstance();
			final Transfer type[] = new Transfer[]{textTransfer};
			target.setTransfer(type);
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
						if(data[0].equalsIgnoreCase("TESTSUITEDATA"))
						{
							List<TSTCGson> list = (List<TSTCGson>)testsuiteviewer.getInput();
							TSTCGson newtsDetails = new TSTCGson();
							newtsDetails.tcName = data[1];
							List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
							
							//Add five blank parameters
							for(int i=1;i<testsuitetable.getColumnCount();i++)
							{
								TSTCParamGson newParam = new TSTCParamGson();
								newParam.tcparamName = testsuitetable.getColumn(i).getText();
								newParam.tcparamValue = "";
								paramList.add(newParam);
							}
							newtsDetails.tcParams = paramList;
							if(list == null)
							{
								list = new ArrayList<TSTCGson>();
								list.add(newtsDetails);
								testsuiteviewer.setInput(list);
							}
							else
							{
								list.add(newtsDetails);
							}
							testsuiteviewer.refresh();
							isDirty = true;
							firePropertyChange(PROP_DIRTY);
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
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : setDragListener()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setListeners()
	{
		testsuitetable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				//isDirty = true;
				//firePropertyChange(PROP_DIRTY);
			}
		});
		
		addBtn.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event event) 
			{
				try
				{
					//Set Dirty
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
					
					int selectedIndex = testsuitetable.getSelectionIndex();
					List<TSTCGson> list = (ArrayList<TSTCGson>) testsuiteviewer.getInput();
					
					if(list==null) //Initialize the same
					{
						list = new ArrayList<TSTCGson>();
						testsuiteviewer.setInput(list);
					}
					
					//Create a new test details GSON
					List<TSTCParamGson> newList =  new ArrayList<TSTCParamGson>();
					TSTCParamGson param1 = new TSTCParamGson();
					param1.tcparamName = testsuitetable.getColumn(1).getText();
					param1.tcparamValue = "";
					TSTCParamGson param2 = new TSTCParamGson();
					param2.tcparamName = testsuitetable.getColumn(2).getText();
					param2.tcparamValue = "";
					TSTCParamGson param3 = new TSTCParamGson();
					param3.tcparamName = testsuitetable.getColumn(3).getText();
					param3.tcparamValue = "";
					TSTCParamGson param4 = new TSTCParamGson();
					param4.tcparamName = testsuitetable.getColumn(4).getText();
					param4.tcparamValue = "";
					TSTCParamGson param5 = new TSTCParamGson();
					param5.tcparamName = testsuitetable.getColumn(5).getText();
					param5.tcparamValue = "";
					newList.add(param1);newList.add(param2);newList.add(param3);newList.add(param4);newList.add(param5);
					
					TSTCGson tsdetails = new TSTCGson();
					tsdetails.tcName = "";
					tsdetails.tcParams = newList;
					
					if(selectedIndex!=-1)
					{
						list.add(selectedIndex, tsdetails);
						testsuiteviewer.editElement(tsdetails, 0);
						testsuiteviewer.refresh();
						
					}
					else
					{
						list.add(tsdetails);
						testsuiteviewer.editElement(tsdetails, 0);
						testsuiteviewer.refresh();
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : setListeners()] - Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		delBtn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				try
				{
					int selectedIndex = testsuitetable.getSelectionIndex();
					if(selectedIndex!=-1)
					{
						List<TSTCGson> list = (ArrayList<TSTCGson>) testsuiteviewer.getInput();
						if(list!=null)
						{
							list.remove(selectedIndex);
							testsuiteviewer.refresh();
							isDirty = true;
							firePropertyChange(PROP_DIRTY);
						}
						
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : delBtn:addListener()] - Exception  : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		saveItem.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
            try{    		     
				saveActionPerform();
            }
            catch(Exception e)
			{
				System.out.println("[" + getClass().getName() + " : Save:addListener()] - Exception  : " + e.getMessage());
				e.printStackTrace();
			}
			}
		});
		
		
		copyItem.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				try{
					
				 listStepGSON=(List<TSTCGson>) testsuiteviewer.getInput();
				 if(listStepGSON!=null)
					{
						copiedCell = new ArrayList<TSTCGson>();
						int [] listSelection=testsuitetable.getSelectionIndices();
						for (int i : listSelection) 
	                    {
	                           copiedCell.add(listStepGSON.get(i));
	                    }
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : CopyItem:addListener()] - Exception  : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		pasteItem.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				try{
					List<TSTCGson> list = (List<TSTCGson>) testsuiteviewer.getInput();
					int selectedindex [] = testsuitetable.getSelectionIndices();
					int insertAfter = 0;
					if(selectedindex.length>0)
					{
						insertAfter = selectedindex[selectedindex.length-1];
						insertAfter = insertAfter + 1;
					}
					for(TSTCGson copyGson  : copiedCell) 
					{
						TSTCGson newStep = new TSTCGson();
						newStep.tcName = copyGson.tcName;
						newStep.tcParams = copyGson.tcParams;
						
						list.add(insertAfter,newStep);
						insertAfter++;
					}
					testsuiteviewer.setInput(list);
					testsuiteviewer.refresh();
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : PasteItem:addListener()] - Exception  : " + e.getMessage());
					e.printStackTrace();
				}
				
			}
		});
		
		viewEditor.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
				IPath location = Path.fromOSString("Automation_Suite/" + tsTask.getTsName()+ ".xml"); 
				IFile projectFile = workspace.getRoot().getFile(location);
				Utilities.openEditor(projectFile, null);	
			}
		});
		
		lockItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) 
			{
				boolean locked = new Boolean(lockItem.getData("Locked").toString());
				if(!locked)
				{
					lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/lock.png"));
					lockItem.setToolTipText("Unlock the file");
					TSGson tsGson = tsTask.getTsGson();
					tsGson.lockedBy = Utilities.AUTOMATICS_USERNAME;
					tsTask.setTsGson(tsGson);
					viewAllElements = true;
					saveActionPerform();
				}
				else
				{
					lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Open_lock.png"));
					lockItem.setToolTipText("Lock for editing");
					TSGson tsGson = tsTask.getTsGson();
					tsGson.lockedBy = "";
					tsTask.setTsGson(tsGson);
					viewAllElements = false;
					saveActionPerform();
				}
				lockItem.setData("Locked",!locked);
				addBtn.setEnabled(viewAllElements);
				delBtn.setEnabled(viewAllElements);
				saveItem.setEnabled(viewAllElements);
				copyItem.setEnabled(viewAllElements);
				pasteItem.setEnabled(viewAllElements);
				testsuitetable.setEnabled(viewAllElements);
				
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
	
	private void saveActionPerform() {
		try
		{
			testsuitetable.forceFocus();
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
				
				TestSuiteRunnerAPI runnerAPI = new TestSuiteRunnerAPI();
				runnerAPI.selected = true;
				runnerAPI.threadCount = tssaveGson.tsName;
				runnerAPI.testsuiteName = tssaveGson.tsName;
				runnerAPI.status = "Running";
				//Create Testng File
				Utilities.createTestng(tssaveGson, runnerAPI);
				boolean gitPassed = this.gitUtil.performGITSyncOperation();
				if(!gitPassed)
				{
					MessageDialog errDialog = new MessageDialog(getSite().getShell(),"Save Failure", 
							null, "Something went wrong - " + this.gitUtil.getErrMsg() + "\nPlease save again", MessageDialog.ERROR, 
							new String[]{"OK"}, 0);
					errDialog.open();
					return;
				}
				
				/*JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TSGson.class, tssaveGson));
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
				}*/
				
				tssaveGson = TestSuiteAPIHandler.getInstance().updateTestSuite(tssaveGson);
				if(TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE!=200)
				{
					MessageDialog dialog = new MessageDialog(getSite().getShell(), "Save Error", null, 
							"Cannot Save TestCase Error : " + TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE, MessageDialog.ERROR, 
							new String [] {"OK"}, 0);
							dialog.open();
							throw new RuntimeException("Cannot Save TestCase : " + TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE + " : " 
														 + TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE);
				}
				isDirty = false;
				firePropertyChange(PROP_DIRTY);
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
			System.out.println("[" + getClass().getName() + " : saveActionPerform()] - Exception : " + e.getMessage());
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
