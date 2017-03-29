package com.automatics.packages.Views;

import java.awt.Menu;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonObject;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;

import com.automatics.packages.EditorListeners;
import com.automatics.packages.PerspectiveListener;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Editors.TestSuiteEditor;
import com.automatics.packages.Editors.TestSuiteEditorInput;
import com.automatics.packages.Handler.NewTestCase;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.Model.TestSuiteTask;
import com.automatics.packages.Model.TestSuiteTaskService;
import com.automatics.packages.api.handlers.TestCaseAPIHandler;
import com.automatics.packages.api.handlers.TestSuiteAPIHandler;
import com.automatics.utilities.elements.Project;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.gsons.testsuite.TSTCParamGson;
import com.automatics.utilities.helpers.MyTitleAreaDialog;
import com.automatics.utilities.helpers.SaveClass;
import com.automatics.utilities.helpers.Utilities;
import com.mongodb.*;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

public class TC_TS_List extends ViewPart {

	private static Tree testSuiteList;
	private static Tree testCaseList;
	private TestCaseTaskService tcService = TestCaseTaskService.getInstance();
	private TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
	private MenuItem copyItem, pasteItemForTS, deleteItem, refreshItem, refreshForTC, newForTC, openItem,
					delete_from_tc, renameTC;
	
	private MenuItem copyItemforTC, pasteItemforTC;
	private TestSuiteTask copyTask;
	private TestCaseTask copyTaskForTC;
	
	
	public TC_TS_List() {
		
	}

	public void createPartControl(Composite parent) {
		try
		{
		
			
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		//Show object list view
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ObjectList.ID);
				
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(mainComposite, SWT.NONE);
		
		TabItem testSuiteTab = new TabItem(tabFolder, SWT.NONE);
		testSuiteTab.setText("Test Suite");
		
		Composite testsuiteListComposite = new Composite(tabFolder, SWT.NONE);
		testSuiteTab.setControl(testsuiteListComposite);
		testsuiteListComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		testSuiteList = new Tree(testsuiteListComposite, SWT.BORDER);
		
		
		org.eclipse.swt.widgets.Menu testsuitePopUp = new org.eclipse.swt.widgets.Menu(testSuiteList);
		testSuiteList.setMenu(testsuitePopUp);
		
		MenuItem newCascadeMenu = new MenuItem(testsuitePopUp, SWT.CASCADE);
		newCascadeMenu.setText("New");
		
		org.eclipse.swt.widgets.Menu cascadePopUp = new org.eclipse.swt.widgets.Menu(newCascadeMenu);
		newCascadeMenu.setMenu(cascadePopUp);
		
		MenuItem newProjectMenu = new MenuItem(cascadePopUp, SWT.NONE);
		newProjectMenu.setEnabled(false);
		newProjectMenu.setText("Project");
		newProjectMenu.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try
				{
					handlerService.executeCommand("com.automatics.packages.new.Project", event);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		MenuItem newTestCase = new MenuItem(cascadePopUp, SWT.NONE);
		newTestCase.setText("Test Case");
		newTestCase.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try
				{
					NewTestCase.CREATE_TCONLY_FLAG = false;
					handlerService.executeCommand("com.automatics.packages.new.TestCase", event);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		MenuItem newTestSuite = new MenuItem(cascadePopUp, SWT.NONE);
		newTestSuite.setText("Test Suite");
		newTestSuite.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try
				{
					handlerService.executeCommand("com.automatics.packages.new.TestSuite", event);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		
		openItem = new MenuItem(testsuitePopUp, SWT.NONE);
		openItem.setText("Open");
		
		new MenuItem(testsuitePopUp, SWT.SEPARATOR);
		
		copyItem = new MenuItem(testsuitePopUp, SWT.NONE);
		copyItem.setText("Copy");
		
		pasteItemForTS = new MenuItem(testsuitePopUp, SWT.NONE);
		pasteItemForTS.setText("Paste");
		
		deleteItem = new MenuItem(testsuitePopUp, SWT.NONE);
		deleteItem.setText("Delete");
		
		new MenuItem(testsuitePopUp, SWT.SEPARATOR);
		
		refreshItem = new MenuItem(testsuitePopUp, SWT.NONE);
		refreshItem.setText("Refresh");
	
		TabItem testCaseTab = new TabItem(tabFolder, SWT.NONE);
		testCaseTab.setText("Test Case");
		
		Composite testcaseListComposite = new Composite(tabFolder, SWT.NONE);
		testCaseTab.setControl(testcaseListComposite);
		testcaseListComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		testCaseList = new Tree(testcaseListComposite, SWT.BORDER);
		
		org.eclipse.swt.widgets.Menu testcasePopUp = new org.eclipse.swt.widgets.Menu(testCaseList);
		testCaseList.setMenu(testcasePopUp);
		
		newForTC = new MenuItem(testcasePopUp, SWT.NONE);
		newForTC.setText("New");
		
		copyItemforTC = new MenuItem(testcasePopUp, SWT.NONE);
		copyItemforTC.setText("Copy");
		
		pasteItemforTC = new MenuItem(testcasePopUp, SWT.NONE);
		pasteItemforTC.setText("Paste");
		
		delete_from_tc = new MenuItem(testcasePopUp, SWT.NONE);
		delete_from_tc.setText("Delete");
		
		renameTC = new MenuItem(testcasePopUp, SWT.NONE);
		renameTC.setText("Rename");
		
		new MenuItem(testcasePopUp, SWT.SEPARATOR);
		
		refreshForTC = new MenuItem(testcasePopUp, SWT.NONE);
		refreshForTC.setText("Refresh");

		DragSource dragSource = new DragSource(testCaseList, DND.DROP_MOVE);
		setDragListener(dragSource);
		
		loadTestSuiteTestCaseTreeView();
		addPerspectiveListerner();
		setListeners();
		
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : CreatePartLayout] - Exception " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void addPerspectiveListerner()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new PerspectiveListener());
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new EditorListeners());
	}
	
	
	
	public void loadTestSuiteTestCaseTreeView()
	{
		try
		{
		
			if(testSuiteList.getItemCount()>0)
				testSuiteList.getItem(0).dispose(); //Remove the root if any
			
			TreeItem application_name_item = new TreeItem(testSuiteList, SWT.NONE);
			application_name_item.setText(Utilities.DB_PROJECT_NAME); //Set This Later
			application_name_item.setData("eltType", "APPLICATION");
			application_name_item.setExpanded(true);
			application_name_item.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/project.png"));
			
			//ArrayList<String> allTSList = AutomaticsDBTestSuiteQueries.getAllTS(db); //Get all test suites
			//Collections.sort(allTSList);
			TSGson allTSList [] = TestSuiteAPIHandler.getInstance().getAllTestSuites();
			for(TSGson tsGson : allTSList)
			{
				//Add test suite to the application tree
				TreeItem testSuiteItem = new TreeItem(application_name_item, SWT.NONE);
				testSuiteItem.setText(tsGson.tsName);
				testSuiteItem.setData("eltType", "TESTSUITE");
				testSuiteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/ts_logo.png"));
			
				//Create or Update task of test suite
				if(tsService.getTaskByTSName(tsGson.tsName)==null) //Add task only if the task is not already added
				{
					TestSuiteTask tsTask = new TestSuiteTask(tsGson.tsName, tsGson.tsDesc, tsGson.tsIdentifier, tsGson);
					tsService.addTasks(tsTask);
				}
				else
				{
					TestSuiteTask tsTask = tsService.getTaskByTSName(tsGson.tsName);
					tsTask.setTsGson(tsGson);
				}
				
				List<TSTCGson> allTestCasesInTestSuite = tsGson.tsTCLink; 
				if(allTestCasesInTestSuite !=null)
				{
					Iterator<TSTCGson> itr = allTestCasesInTestSuite.iterator();
					while(itr.hasNext())
					{
						TSTCGson tstcGson = itr.next();
						TreeItem testsuite_testcaseItem = new TreeItem(testSuiteItem, SWT.NONE);
						testsuite_testcaseItem.setText(tstcGson.tcName);
						testsuite_testcaseItem.setData("eltType","TESTCASE");
						testsuite_testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
					}
				}
			}
			application_name_item.setExpanded(true);
			
			//Load all test cases to the test case list
			if(testCaseList.getItemCount()>0)
				testCaseList.getItem(0).dispose();
			
			TreeItem appName = new TreeItem(testCaseList, SWT.NONE);
			appName.setText(Utilities.DB_PROJECT_NAME);
			appName.setData("eltType","APPNAME");
			appName.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/project.png"));
			
			
			TCGson [] allTCList = TestCaseAPIHandler.getInstance().getAllTestCases();
			
			if(allTCList!=null && allTCList.length>0)
			{
				for(TCGson tcGson : allTCList)
				{
					TreeItem testCaseItem = new TreeItem(appName,SWT.NONE);
					testCaseItem.setText(tcGson.tcName);
					testCaseItem.setData("eltType","TESTCASE");
					testCaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
					//Create or Update test case task
					if(tcService.getTaskByTcName(tcGson.tcName)==null) //Add task only if the task is not added
					{
						TestCaseTask tcTask = new TestCaseTask(tcGson.tcName, tcGson.tcDesc, tcGson.tcType, tcGson.tcIdentifier, tcGson);
						tcService.addTasks(tcTask);
					}
					else
					{
						TestCaseTask tcTask = tcService.getTaskByTcName(tcGson.tcName);
						tcTask.setTcGson(tcGson);
					}
				}
				appName.setExpanded(true);
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + "-loadTestSuiteTestCaseTreeView()] Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setDragListener(DragSource source)
	{
		try
		{
			Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
			source.setTransfer(types);
			source.addDragListener(new DragSourceListener() {
				
				TreeItem []selected;
				
				public void dragStart(DragSourceEvent event) {
					// TODO Auto-generated method stub
					selected = testCaseList.getSelection();
					if(selected==null)
					{
						event.doit=false;
					}
					if(selected[0].getData("eltType").toString().equalsIgnoreCase("APPNAME") || selected[0].getText().equals(""))
					{
						event.doit=false;
					}
				}
				
				public void dragSetData(DragSourceEvent event) {
					 // TODO Auto-generated method stub}
					 if (TextTransfer.getInstance().isSupportedType(event.dataType))
					 {
						 event.data= "TESTSUITEDATA__" + selected[0].getText();
					 }
				}
				
				public void dragFinished(DragSourceEvent event) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : setDragListner()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setListeners()
	{
		try
		{
			//Add Listener to test suite list
			testSuiteList.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) 
				{
					try
					{
						//Get All Workbench
						IWorkbench workbench = PlatformUI.getWorkbench();
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();
						
						TreeItem selected[] = testSuiteList.getSelection();
						if(selected[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE"))
						{
					        TestCaseEditorInput input = new TestCaseEditorInput(selected[0].getText());
					        page.openEditor(input, TCEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
						}
						if(selected[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE"))
						{
							TestSuiteEditorInput input = new TestSuiteEditorInput(selected[0].getText());
							page.openEditor(input, TestSuiteEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
						}
					}
					catch(Exception e){e.printStackTrace();System.out.println("[TC_TS_List-setlisteners()] : Exception" + e.getMessage());}
				}
			});
			
			copyItem.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {
					try
					{
						TreeItem selected[] = testSuiteList.getSelection();
						if (selected[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE")) 
						{
							copyTask = tsService.getTaskByTSName(selected[0]
									.getText());
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " : copyItem.addListener()] - Exception : " + e.getMessage());
						e.printStackTrace();
					} 

				}
			});
			
			pasteItemForTS.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MyTitleAreaDialog dialog = new MyTitleAreaDialog(
							testSuiteList.getShell());

					dialog.create();
					if (dialog.open() == Window.OK) {
						String pasteTSName = dialog.getFirstName();
						
						TSGson tsGson = copyTask.getTsGson();
						tsGson.tsName = pasteTSName;
						tsGson.lockedBy = Utilities.AUTOMATICS_PASSWORD;

						TreeItem testSuiteItem = new TreeItem(testSuiteList
								.getItem(0), SWT.NONE);
						testSuiteItem.setText(tsGson.tsName);
						testSuiteItem.setData("eltType", "TESTSUITE");
						testSuiteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/ts_logo.png"));

						String name = tsGson.tsName;
						Iterator<TSTCGson> itr = tsGson.tsTCLink.iterator();

						
						TestSuiteTask pasteTSTask = new TestSuiteTask(pasteTSName, tsGson.tsDesc, pasteTSName, tsGson);
						if (tsService.getTaskByTSName(name) == null) {
							tsService.addTasks(pasteTSTask);
						}

						while (itr.hasNext()) {
							TSTCGson tstcGson = itr.next();
							TreeItem testsuite_testcaseItem = new TreeItem(
									testSuiteItem, SWT.NONE);
							testsuite_testcaseItem.setText(tstcGson.tcName);
							testsuite_testcaseItem.setData("eltType",
									"TESTCASE");
							testsuite_testcaseItem.setImage(ResourceManager
									.getPluginImage("Automatics",
											"images/icons/tc_logo.png"));

						}

						// Save the TCGson to DB
						/*
						JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TSGson.class, tsGson));
						if (jsonObj != null) 
						{
							AutomaticsDBTestSuiteQueries.postTS(Utilities.getMongoDB(), jsonObj);
						}*/
						tsGson = TestSuiteAPIHandler.getInstance().postTestSuite(tsGson);
						if(TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE!=200)
							throw new RuntimeException("Cannot Copy Test Suite : "
														+ TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE + " : "
														+ TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE);
						
					}
				}
			});
			
			
			
			copyItemforTC.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					try
					{
						TreeItem selected[] = testCaseList.getSelection();
						if (selected[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE")) 
						{
							copyTaskForTC = tcService.getTaskByTcName(selected[0].getText());
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " : copyItemforTC.addListener()] - Exception" + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			pasteItemforTC.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					try
					{
						MyTitleAreaDialog dialog = new MyTitleAreaDialog(testCaseList.getShell());
						dialog.create();
						
						if (dialog.open() == Window.OK) 
						{
							String pasteTCName = dialog.getFirstName();
							
							TCGson tcGson = copyTaskForTC.getTcGson();
							tcGson.tcName = pasteTCName;
							tcGson.lockedBy = Utilities.AUTOMATICS_USERNAME;
							
							TreeItem testsuite_testcaseItem = new TreeItem(testCaseList.getItem(0), SWT.NONE);
							testsuite_testcaseItem.setText(tcGson.tcName);
							testsuite_testcaseItem.setData("eltType", "TESTCASE");
							testsuite_testcaseItem.setImage(ResourceManager.getPluginImage("Automatics","images/icons/tc_logo.png"));
							
							TestCaseTask pasteTCTask = new TestCaseTask(pasteTCName, tcGson.tcDesc, tcGson.tcType,tcGson.tcIdentifier, tcGson);
							if(tcService.getTaskByTcName(pasteTCName)==null)
							{
								tcService.addTasks(pasteTCTask);
							}
							
							
							tcGson = TestCaseAPIHandler.getInstance().postTestCase(tcGson);
							if(TestCaseAPIHandler.TESTCASE_RESPONSE_CODE!=200)
							{
								new MessageDialog(getSite().getShell(), "Copy/Paste Error", null, 
															"Cannot Copy/Paste Test Suite : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE,
															MessageDialog.ERROR, new String[]{"OK"}, 0);
								throw new RuntimeException("Copy Error TestCase : " + TestCaseAPIHandler.TESTCASE_RESPONSE_CODE
															+" : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE);
							}
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " : pasteItemForTC.addListener()] : Exception : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			testCaseList.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					try
					{
						IWorkbench workbench = PlatformUI.getWorkbench();
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();
						
						TreeItem selected[] = testCaseList.getSelection();
						if(selected[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE"))
						{
					        TestCaseEditorInput input = new TestCaseEditorInput(selected[0].getText());
					        page.openEditor(input, TCEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.out.println("[TC_TS_List-setlisteners()] : Exception" + e.getMessage());
					}
				}
			});
			
			refreshItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) 
				{
					loadTestSuiteTestCaseTreeView();
				}
			});
			
			refreshForTC.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) 
				{
					loadTestSuiteTestCaseTreeView();
				}
			});
			
			newForTC.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try
					{
						NewTestCase.CREATE_TCONLY_FLAG = true; /*Flag to create only test case without adding to testsuite*/
						handlerService.executeCommand("com.automatics.packages.new.TestCase", event);
					}
					catch(Exception e)
					{
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			openItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					try
					{
						//Get All Workbench
						IWorkbench workbench = PlatformUI.getWorkbench();
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();
						
						TreeItem selected[] = testSuiteList.getSelection();
						if(selected[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE"))
						{
					        TestCaseEditorInput input = new TestCaseEditorInput(selected[0].getText());
					        page.openEditor(input, TCEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
						}
						if(selected[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE"))
						{
							TestSuiteEditorInput input = new TestSuiteEditorInput(selected[0].getText());
							page.openEditor(input, TestSuiteEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " - openItem.addListener()] : Exception : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			deleteItem.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
				
					TreeItem item = testSuiteList.getSelection()[0];
					String value = item.getData("eltType").toString();
					MessageDialog deleteDialog = new MessageDialog(getSite().getShell(), "Delete Test Entity", null,
							"Are you sure you want to delete - " + item.getText() + " ?", 
							MessageDialog.CONFIRM, new String[]{"Delete", "Cancel"}, 0);
					int optionSelected = deleteDialog.open();
					if(optionSelected == 0)
					{
						if(!value.equals("APPLICATION")&& value.equals("TESTSUITE"))
						{
							TestSuiteAPIHandler.getInstance().deleteTestSuite(item.getText());
							if(TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE==200)
							{
								tsService.removeTaskByTSName(item.getText());
								item.dispose();
							}
							else
							{
								MessageDialog edialog = new MessageDialog(getSite().getShell(), "Deletion Error", null, 
														"Cannot Delete Test Suite : " + TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE,
														MessageDialog.ERROR, new String[] {"OK"}, 0);
								edialog.open();
								throw new RuntimeException("Error while deleting testsuite :" + TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE);
							}
						}
						else
						{
							if(!value.equals("APPLICATION") && value.equals("TESTCASE"))
							{
								TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
								TestSuiteTask tsTask = tsService.getTaskByTSName(item.getParentItem().getText());
								TSGson  tsGson = tsTask.getTsGson();
								List<TSTCGson> list = tsGson.tsTCLink;
								
								for (int i=0;i<list.size();i++) 
								{
									TSTCGson tstcGson = list.get(i); 
									if(tstcGson.tcName.equals(item.getText())){
										list.remove(i);
									}
								}
								//AutomaticsDBTestCaseQueries.deleteTC(Utilities.getMongoDB(), item.getText());
								TestCaseAPIHandler.getInstance().deleteTestCase(item.getText());
								if(TestCaseAPIHandler.TESTCASE_RESPONSE_CODE==200)
								{
									tcService.removeTaskByTCName(item.getText());
									item.dispose();
								}
								else
								{
									MessageDialog edialog = new MessageDialog(getSite().getShell(), "Deletion Error", null, 
											"Cannot Delete Test Case : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE,
											MessageDialog.ERROR, new String[] {"OK"}, 0);
									edialog.open();
									throw new RuntimeException("Error while deleting case :" + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE);
								}
							}
						}
					}
				}
			});
			
			delete_from_tc.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) 
				{
					TreeItem item = testCaseList.getSelection()[0];
					MessageDialog deleteDialog = new MessageDialog(getSite().getShell(), "Delete Test Case", null,
							"Are you sure you want to delete - " + item.getText() + " ?", 
							MessageDialog.CONFIRM, new String[]{"Delete", "Cancel"}, 0);
					int optionSelected = deleteDialog.open();
					if(optionSelected==0)
					{
						TestCaseAPIHandler.getInstance().deleteTestCase(item.getText());
						if(TestCaseAPIHandler.TESTCASE_RESPONSE_CODE==200)
						{
							tcService.removeTaskByTCName(item.getText());
							item.dispose();
						}
						else
						{
							MessageDialog edialog = new MessageDialog(getSite().getShell(), "Deletion Error", null, 
									"Cannot Delete Test Case : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE,
									MessageDialog.ERROR, new String[] {"OK"}, 0);
							edialog.open();
							throw new RuntimeException("Error while deleting case :" + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE);
						}
					}
				}
			});
			
			renameTC.addListener(SWT.Selection, new Listener() 
			{
				@Override
				public void handleEvent(Event event) 
				{
					TreeItem selected[] = testCaseList.getSelection();
					String oldName = selected[0].getText();
					TestCaseTask renameTask = tcService.getTaskByTcName(oldName);
					TCGson renameGSON = renameTask.getTcGson();
					boolean canRename = true;
					if(!renameGSON.lockedBy.equals(""))
					{
						if(!renameGSON.lockedBy.equals(Utilities.AUTOMATICS_USERNAME))
							canRename = false;
					}
					if(canRename)
					{
						MyTitleAreaDialog dialog = new MyTitleAreaDialog(testCaseList.getShell());
                        dialog.create();
                        if(dialog.open() == Window.OK)
                        {
                        	selected[0].setText(dialog.getFirstName());
                        	renameGSON.tcName = dialog.getFirstName();
                        	renameTask.setTcName(dialog.getFirstName());
                        	renameGSON = TestCaseAPIHandler.getInstance().updateTestCase(oldName, renameGSON);
                        	if(TestCaseAPIHandler.TESTCASE_RESPONSE_CODE!=200)
                            {
                                   new MessageDialog(getSite().getShell(), "Rename Error", null, 
                                                                                   "Cannot Rename Test Case : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE,
                                                                                   MessageDialog.ERROR, new String[]{"OK"}, 0);
                                   throw new RuntimeException("Rename Error TestCase : " + TestCaseAPIHandler.TESTCASE_RESPONSE_CODE
                                                                                   +" : " + TestCaseAPIHandler.TESTCASE_RESPONSE_MESSAGE);
                            }
                        }
					}
					else
					{
						MessageDialog popup = new MessageDialog(getSite().getShell(), "Information", null,
								"Cannot rename file. File used by " + renameGSON.lockedBy, MessageDialog.INFORMATION, 
								new String[]{"OK"}, 0);
						popup.open();
					}
				}
			});
			
		}
		catch(Exception exp)
		{
			System.out.println("[" + getClass().getName() + "-setListeners()] Exception : " + exp.getMessage());
			exp.printStackTrace();
		}
		
		
	}

	public void setFocus() {
		/*
		try
		{
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject("TESTJDT");
			if(!project.exists())
			{
				project.create(null);
				project.open(null);
					 
				IProjectDescription description = null;
				description = project.getDescription();
				description.setNatureIds(new String[] { JavaCore.NATURE_ID });
				project.setDescription(description, null);
				IJavaProject javaProject = JavaCore.create(project);
				IClasspathEntry[] buildPath = {JavaCore.newSourceEntry(project.getFullPath().append("src")),
														JavaRuntime.getDefaultJREContainerEntry() };
				javaProject.setRawClasspath(buildPath, project.getFullPath().append(
									"bin"), null);
				IFolder folder = project.getFolder("src");
				
				folder.create(true, true, null); 
				IPackageFragmentRoot srcFolder = javaProject
								.getPackageFragmentRoot(folder);
				 
				IPackageFragment fragment = null;
				
				fragment = srcFolder.createPackageFragment(
							"com.programcreek", true, null);
				String str = "package com.programcreek;" + "\n"
					+ "public class Test  {" + "\n" + " private String name;"
					+ "\n" + "}";
				 
				ICompilationUnit cu = null;
				cu = fragment.createCompilationUnit("Test.java", str,false, null);
			}
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + "-setFocus()] Exception : " + e.getMessage());
			e.printStackTrace();
		}
		*/
	}
	
	public Menu menuForTC()
	{
		org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu(testCaseList);
		MenuItem newItem = new MenuItem(menu, SWT.PUSH);
		newItem.setText("New");
		
		return null;
	}
	
	
	
	public static void addProject(Project project)
	{
		//Create a new project with some basic details
		try
		{
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
			if(workspacePath != null)
			{
				File file = new File(workspacePath + "\\" + project.getProjectName());
				if(!file.exists())
				{
					file.mkdirs();
					//Create directories
					//1.Test Suite Directory
					String testsuiteFolder = workspacePath + "\\" +  project.getProjectName() + "\\testsuite" ;
					file = new File(testsuiteFolder);
					if(!file.exists())
						file.mkdirs();
					//2.Test Case Directory
					String testcaseFolder = workspacePath + "\\" +  project.getProjectName() + "\\testcase" ;
					file = new File(testcaseFolder);
					if(!file.exists())
						file.mkdirs();
					//3.Object Map Directory
					String objMapFolder = workspacePath + "\\" +  project.getProjectName() + "\\objectmap" ;
					file = new File(objMapFolder);
					if(!file.exists())
						file.mkdirs();
					
					//create a .project file
					String projectFilePath = workspacePath + "\\" +  project.getProjectName() + "\\.project" ;
					file = new File(projectFilePath);
					PrintWriter pw = new PrintWriter(new FileWriter(file));
					pw.println(project.toString());
					pw.close();
					
					//Add the project to the list
					TreeItem tsProject = new TreeItem(testSuiteList, SWT.NONE);
					tsProject.setText(project.getProjectName());
					
					TreeItem tcProject = new TreeItem(testCaseList, SWT.NONE);
					tcProject.setText(project.getProjectName());
				}
				else
				{
					//throw Exception
					System.out.println("Project Already Exists");
				}
			}
			else
			{
				//Add some exception
			}
		}
		catch(Exception e)
		{
			System.out.println("[TC_TS_List-addProject()] Exception : " + e.getMessage());
			e.printStackTrace();
		}
 	}
	
	public static void addTestCase(TCGson gson, boolean createTCOnly)
	{
		try
		{
			//Add the treeitem to test suite list
			TreeItem [] selectedNode = testSuiteList.getSelection();
			if(selectedNode.length==0)
			{
				selectedNode = testSuiteList.getItems();
				createTCOnly = true;
			}
			if(!createTCOnly)
			{
				if(selectedNode[0] != null)
				{
					String tsName = "";
					TestSuiteTask tsTask = null;
					TSGson tsGson = null;
					if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE"))
					{
						TreeItem testcaseItem = new TreeItem(selectedNode[0],SWT.NONE);
						testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
						testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
						testcaseItem.setText(gson.tcName);
						
						//Add the test case to test suite as well
						tsName = selectedNode[0].getText();
						TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
						tsTask = tsService.getTaskByTSName(tsName);
						tsGson = tsTask.getTsGson();
						List<TSTCGson> list = tsGson.tsTCLink;
						if(list==null)
						{
							list = new ArrayList<TSTCGson>();
						}
						
						/*
						 * Add the test suite details
						 * Using TestSuiteEditor.all_col_name(ArrayList<String>()) to get all the column names*/
						
						TSTCGson details = new TSTCGson();
						details.tcName = gson.tcName;
						List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
						TSTCParamGson param1 = new TSTCParamGson();
						param1.tcparamName = TestSuiteEditor.all_col_name.get(0); 
						param1.tcparamValue = "";
						TSTCParamGson param2 = new TSTCParamGson();
						param2.tcparamName = TestSuiteEditor.all_col_name.get(1);
						param2.tcparamValue = "";
						TSTCParamGson param3 = new TSTCParamGson();
						param3.tcparamName = TestSuiteEditor.all_col_name.get(2);
						param3.tcparamValue = "";
						TSTCParamGson param4 = new TSTCParamGson();
						param4.tcparamName =TestSuiteEditor.all_col_name.get(3);
						param4.tcparamValue = "";
						TSTCParamGson param5 = new TSTCParamGson();
						param5.tcparamName = TestSuiteEditor.all_col_name.get(4);
						param5.tcparamValue = "";
						paramList.add(param1);
						paramList.add(param2);
						paramList.add(param3);
						paramList.add(param4);
						paramList.add(param5);
						details.tcParams = paramList;
						//Added
						list.add(details);
						tsGson.tsTCLink = list;
						tsTask.setTsGson(tsGson);
						
					}
					else if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE"))
					{
						TreeItem parent = selectedNode[0].getParentItem();
						TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
						testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
						testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
						testcaseItem.setText(gson.tcName);
						
						//Add the test case to test suite as well
						tsName = parent.getText();
						TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
						tsTask = tsService.getTaskByTSName(tsName);
						tsGson = tsTask.getTsGson();
						List<TSTCGson> list = tsGson.tsTCLink;
						if(list==null)
						{
							list = new ArrayList<TSTCGson>();
						}
						
						//Add the test suite details
						TSTCGson details = new TSTCGson();
						details.tcName = gson.tcName;
						List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
						TSTCParamGson param1 = new TSTCParamGson();
						param1.tcparamName = TestSuiteEditor.all_col_name.get(0);
						param1.tcparamValue = "";
						TSTCParamGson param2 = new TSTCParamGson();
						param2.tcparamName = TestSuiteEditor.all_col_name.get(1);
						param2.tcparamValue = "";
						TSTCParamGson param3 = new TSTCParamGson();
						param3.tcparamName = TestSuiteEditor.all_col_name.get(2);
						param3.tcparamValue = "";
						TSTCParamGson param4 = new TSTCParamGson();
						param4.tcparamName = TestSuiteEditor.all_col_name.get(3);
						param4.tcparamValue = "";
						TSTCParamGson param5 = new TSTCParamGson();
						param5.tcparamName = TestSuiteEditor.all_col_name.get(4);
						param5.tcparamValue = "";
						paramList.add(param1);
						paramList.add(param2);
						paramList.add(param3);
						paramList.add(param4);
						paramList.add(param5);
						details.tcParams = paramList;
						//Added 
						list.add(details);
						tsGson.tsTCLink = list;
						tsTask.setTsGson(tsGson);
						
					}
					else if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("APPLICATION"))
					{
						TreeItem parent = selectedNode[0].getItem(0);
						TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
						testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
						testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
						testcaseItem.setText(gson.tcName);
						
						//Add the test case to test suite as well
						tsName = parent.getText();
						TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
						tsTask = tsService.getTaskByTSName(tsName);
						tsGson = tsTask.getTsGson();
						List<TSTCGson> list = tsGson.tsTCLink;
						if(list==null)
						{
							list = new ArrayList<TSTCGson>();
						}
						
						//Add the test suite details
						TSTCGson details = new TSTCGson();
						details.tcName = gson.tcName;
						List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
						TSTCParamGson param1 = new TSTCParamGson();
						param1.tcparamName = TestSuiteEditor.all_col_name.get(0);
						param1.tcparamValue = "";
						TSTCParamGson param2 = new TSTCParamGson();
						param2.tcparamName = TestSuiteEditor.all_col_name.get(1);
						param2.tcparamValue = "";
						TSTCParamGson param3 = new TSTCParamGson();
						param3.tcparamName = TestSuiteEditor.all_col_name.get(2);
						param3.tcparamValue = "";
						TSTCParamGson param4 = new TSTCParamGson();
						param4.tcparamName = TestSuiteEditor.all_col_name.get(3);
						param4.tcparamValue = "";
						TSTCParamGson param5 = new TSTCParamGson();
						param5.tcparamName = TestSuiteEditor.all_col_name.get(4);
						param5.tcparamValue = "";
						paramList.add(param1);
						paramList.add(param2);
						paramList.add(param3);
						paramList.add(param4);
						paramList.add(param5);
						details.tcParams = paramList;
						//Added 
						list.add(details);
						tsGson.tsTCLink = list;
						tsTask.setTsGson(tsGson);
					}
					
					//Save the test suite as well
					/*JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TSGson.class, tsGson));
					System.out.println("Save When TC Added : \n" + jsonObj.toString());
					if(jsonObj!=null)
					{
						AutomaticsDBTestSuiteQueries.updateTS(Utilities.getMongoDB(), tsTask.getTsName(), jsonObj);
					}*/
					tsGson = TestSuiteAPIHandler.getInstance().updateTestSuite(tsGson);
					if(TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE!=200)
					{
						throw new RuntimeException("Exception while save testsuite : " + TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE);
					}
				}
			}
			
			//Add the same to the test case list
			TreeItem parent = testCaseList.getItem(0);
			TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
			testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
			testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
			testcaseItem.setText(gson.tcName);
			
			//Add the new task to the DB
			gson = TestCaseAPIHandler.getInstance().postTestCase(gson);
			if(TestCaseAPIHandler.TESTCASE_RESPONSE_CODE!=200)
			{
				throw new RuntimeException("Cannot Create new test case : " + TestCaseAPIHandler.TESTCASE_RESPONSE_CODE);
			}
			
			//Update the test case list used in test suite dropdown
			TCGson [] allTCGsons = TestCaseAPIHandler.getInstance().getAllTestCases();
			ArrayList<String> tempTCList = new ArrayList<String>();
			for(TCGson tc : allTCGsons)
				tempTCList.add(tc.tcName);
			TestSuiteEditor.testCaseList = tempTCList; 
			
			//Create the task for the newly created test suite
			TestCaseTask newTask = new TestCaseTask(gson.tcName, gson.tcDesc, gson.tcType, gson.tcIdentifier, gson);
			TestCaseTaskService tcService = TestCaseTaskService.getInstance();
			tcService.addTasks(newTask);
			//Open the editor
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			TestCaseEditorInput input = new TestCaseEditorInput(gson.tcName);
	        page.openEditor(input, TCEditor.ID);
		
			//Add the gson to usavedBuffer of test case
			SaveClass.unsaveBufferTestCase.put(gson.tcName, gson);
		}
		catch(Exception e)
		{
			System.out.println("TC_TS_List : addTestCase()-Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public static void addTestSuite(TSGson gson)
	{
		try
		{
			TreeItem [] selectedNode = testSuiteList.getSelection();
			TreeItem parent = null;
			if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE"))
			{
				parent = selectedNode[0].getParentItem().getParentItem();
			}
			else if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE"))
			{
				parent = selectedNode[0].getParentItem();
			}
			else if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("APPLICATION"))
			{
				parent = selectedNode[0];
			}
			else 
			{
				parent = testSuiteList.getItem(0);
			}
			
			TreeItem testsuiteItem = new TreeItem(parent, SWT.NONE);
			testsuiteItem.setText(gson.tsName);
			testsuiteItem.setData("eltType","TESTSUITE");
			testsuiteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/ts_logo.png"));

			//Add the new task to the DB
			
			gson = TestSuiteAPIHandler.getInstance().postTestSuite(gson);
			if(TestSuiteAPIHandler.TESTSUITE_RESPONSE_CODE!=200)
			{
				throw new RuntimeException("Cannot Save Test Suite : " + TestSuiteAPIHandler.TESTSUITE_RESPONSE_MESSAGE);
			}
			
			//Add task to test suite task service
			TestSuiteTask newTask = new TestSuiteTask(gson.tsName, gson.tsDesc, gson.tsIdentifier, gson);
			TestSuiteTaskService service = TestSuiteTaskService.getInstance();
			service.addTasks(newTask);
			
			//Open the test suite editor
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			TestSuiteEditorInput input = new TestSuiteEditorInput(gson.tsName);
			page.openEditor(input, TestSuiteEditor.ID);
			
			//Add the test suite to test suite buffer
			SaveClass.unsaveBufferTestSuite.put(gson.tsName, gson);
		}
		catch(Exception e)
		{
			System.out.println("[TC_TS_List : addTestSuite] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
