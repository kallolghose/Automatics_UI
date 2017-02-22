package com.automatics.packages.Views;

import java.awt.Menu;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.automatics.mongo.packages.AutomaticsDBTestCaseQueries;
import com.automatics.mongo.packages.AutomaticsDBTestSuiteQueries;
import com.automatics.packages.EditorListeners;
import com.automatics.packages.PerspectiveListener;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Editors.TestSuiteEditor;
import com.automatics.packages.Editors.TestSuiteEditorInput;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.Model.TestSuiteTask;
import com.automatics.packages.Model.TestSuiteTaskService;
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
	private MenuItem copyItem,pasteItemForTS,deleteItem;	
	
	private MenuItem copyItemforTC, pasteItemforTC;
	private TestSuiteTask copyTask;
	private TestCaseTask copyTaskForTC;
	
	
	public TC_TS_List() {
		// TODO Auto-generated constructor stub
		
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
		
		/*TreeItem application_name_item = new TreeItem(testSuiteList, SWT.NONE);
		application_name_item.setText("App_Name");
		application_name_item.setExpanded(true);
		*/
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
		
		
		MenuItem openItem = new MenuItem(testsuitePopUp, SWT.NONE);
		openItem.setText("Open");
		
		new MenuItem(testsuitePopUp, SWT.SEPARATOR);
		
		copyItem = new MenuItem(testsuitePopUp, SWT.NONE);
		copyItem.setText("Copy");
		
		pasteItemForTS = new MenuItem(testsuitePopUp, SWT.NONE);
		pasteItemForTS.setText("Paste");
		
		deleteItem = new MenuItem(testsuitePopUp, SWT.NONE);
		deleteItem.setText("Delete");
	
		TabItem testCaseTab = new TabItem(tabFolder, SWT.NONE);
		testCaseTab.setText("Test Case");
		
		Composite testcaseListComposite = new Composite(tabFolder, SWT.NONE);
		testCaseTab.setControl(testcaseListComposite);
		testcaseListComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		testCaseList = new Tree(testcaseListComposite, SWT.BORDER);
		
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
			DB db = Utilities.getMongoDB(); //Get Mongo DB
			TreeItem application_name_item = new TreeItem(testSuiteList, SWT.NONE);
			application_name_item.setText("App_Name"); //Set This Later
			application_name_item.setData("eltType", "APPLICATION");
			application_name_item.setExpanded(true);
			application_name_item.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/project.png"));
			ArrayList<String> allTSList = AutomaticsDBTestSuiteQueries.getAllTS(db); //Get all test suites
			
			for(String tsName : allTSList)
			{
				//Add test suite to the application tree
				TreeItem testSuiteItem = new TreeItem(application_name_item, SWT.NONE);
				testSuiteItem.setText(tsName);
				testSuiteItem.setData("eltType", "TESTSUITE");
				testSuiteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/ts_logo.png"));
				
				//Get all the test cases for the test case
				//System.out.println(AutomaticsDBTestSuiteQueries.getTS(db, tsName).toString());
				TSGson tsGson = Utilities.getGSONFromJSON(AutomaticsDBTestSuiteQueries.getTS(db, tsName).toString(), TSGson.class);
				
				//Create task of test suite
				if(tsService.getTaskByTSName(tsName)==null) //Add task only if the task is not already added
				{
					TestSuiteTask tsTask = new TestSuiteTask(tsName, tsGson.tsDesc, tsGson.tsIdentifier, tsGson);
					tsService.addTasks(tsTask);
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
			
			//Load all test cases to the test case list
			TreeItem appName = new TreeItem(testCaseList, SWT.NONE);
			appName.setText("App_Name");
			appName.setData("eltType","APPNAME");
			appName.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/project.png"));
			
			DragSource dragSource = new DragSource(testCaseList, DND.DROP_MOVE);
			setDragListener(dragSource);
			
			org.eclipse.swt.widgets.Menu testcasePopUp = new org.eclipse.swt.widgets.Menu(testCaseList);
			testCaseList.setMenu(testcasePopUp);
			
			copyItemforTC = new MenuItem(testcasePopUp, SWT.NONE);
			copyItemforTC.setText("Copy");
			
			pasteItemforTC = new MenuItem(testcasePopUp, SWT.NONE);
			pasteItemforTC.setText("Paste");
			
			ArrayList<String> allTCList = AutomaticsDBTestCaseQueries.getAllTC(db);
			for(String tcName : allTCList)
			{
				TreeItem testCaseItem = new TreeItem(appName,SWT.NONE);
				testCaseItem.setText(tcName);
				testCaseItem.setData("eltType","TESTCASE");
				testCaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
				//Add test case task
				if(tcService.getTaskByTcName(tcName)==null) //Add task only if the task is not added
				{
					TCGson tcGson = Utilities.getGSONFromJSON(AutomaticsDBTestCaseQueries.getTC(db, tcName).toString(),TCGson.class);
					TestCaseTask tcTask = new TestCaseTask(tcName, tcGson.tcDesc, tcGson.tcType, tcGson.tcIdentifier, tcGson);
					tcService.addTasks(tcTask);
				}
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
			
			
			deleteItem.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
				
					TreeItem item = testSuiteList.getSelection()[0];
					String value = item.getData("eltType").toString();
					if(!value.equals("APPLICATION")&& value.equals("TESTSUITE")){
					item.dispose();
				AutomaticsDBTestSuiteQueries.deleteTS(Utilities.getMongoDB(), item.getText());
					
					}else{
						if(!value.equals("APPLICATION") && value.equals("TESTCASE")){
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
						item.dispose();
						AutomaticsDBTestCaseQueries.deleteTC(Utilities.getMongoDB(), item.getText());
					}
					}
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
						copyTask.setTsName(dialog.getFirstName());
						TSGson tsGson = copyTask.getTsGson();
						tsGson.tsName = dialog.getFirstName();

						TreeItem testSuiteItem = new TreeItem(testSuiteList
								.getItem(0), SWT.NONE);
						testSuiteItem.setText(tsGson.tsName);
						testSuiteItem.setData("eltType", "TESTSUITE");
						testSuiteItem.setImage(ResourceManager.getPluginImage(
								"Automatics", "images/icons/ts_logo.png"));

						String name = tsGson.tsName;
						Iterator<TSTCGson> itr = tsGson.tsTCLink.iterator();

						if (tsService.getTaskByTSName(name) == null) {
							tsService.addTasks(copyTask);
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
						JsonObject jsonObj = Utilities
								.getJsonObjectFromString(Utilities
										.getJSONFomGSON(TSGson.class, tsGson));
						if (jsonObj != null) {
							AutomaticsDBTestSuiteQueries.postTS(
									Utilities.getMongoDB(), jsonObj);
						}

					}
				}
			});
			
			deleteItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) 
				{
					try
					{
						TreeItem item = testSuiteList.getSelection()[0];
						boolean result = MessageDialog.openConfirm(
								testSuiteList.getShell(), "Confirm",
								"Are you sure that you want to permanently  delete the seleted item");
	
						if (result) {
							String value = item.getData("eltType").toString();
							if (!value.equals("APPLICATION")
									&& value.equals("TESTSUITE")) {
	
								AutomaticsDBTestSuiteQueries.deleteTS(
										Utilities.getMongoDB(), item.getText());
								item.dispose();
	
							} else {
								if (!value.equals("APPLICATION")
										&& value.equals("TESTCASE")) {
									TestSuiteTaskService tsService = TestSuiteTaskService
											.getInstance();
									TestSuiteTask tsTask = tsService
											.getTaskByTSName(item.getParentItem()
													.getText());
									TSGson tsGson = tsTask.getTsGson();
									List<TSTCGson> list = tsGson.tsTCLink;
	
									for (int i = 0; i < list.size(); i++) {
										TSTCGson tstcGson = list.get(i);
										String name = tstcGson.tcName;
										if (name.equals(item.getText())) {
											list.remove(i);
											AutomaticsDBTestCaseQueries.deleteTC(
													Utilities.getMongoDB(), name);
											item.dispose();
											break;
										}
									}
								}
							}
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " : deleteItem.addListener()] - Exception : " + e.getMessage());
						e.printStackTrace();
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
							copyTaskForTC.setTcName(dialog.getFirstName());
							TCGson tcGson = copyTaskForTC.getTcGson();
							tcGson.tcName = dialog.getFirstName();
							TreeItem testsuite_testcaseItem = new TreeItem(testCaseList.getItem(0), SWT.NONE);
							testsuite_testcaseItem.setText(tcGson.tcName);
							testsuite_testcaseItem.setData("eltType", "TESTCASE");
							testsuite_testcaseItem.setImage(ResourceManager.getPluginImage("Automatics","images/icons/tc_logo.png"));
							
							// Save the TCGson to DB
							JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TSGson.class, tcGson));
							if (jsonObj != null) 
							{
								AutomaticsDBTestCaseQueries.postTC(Utilities.getMongoDB(), jsonObj);
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
			
		}
		catch(Exception exp)
		{
			System.out.println("[" + getClass().getName() + "-setListeners()] Exception : " + exp.getMessage());
			exp.printStackTrace();
		}
		
		
	}

	public void setFocus() {
		/*IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TESTJDT");
		try 
		{
			project.create(null);
			project.open(null);
			IProjectDescription description = null;
			description = project.getDescription();
			project.setDescription(description, null);
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] buildPath = {
											JavaCore.newSourceEntry(project.getFullPath().append("src")),
											JavaRuntime.getDefaultJREContainerEntry() 
										  };
			javaProject.setRawClasspath(buildPath, project.getFullPath().append("bin"), null);
			IFolder folder = project.getFolder("src");		
			folder.create(true, true, null);
			IPackageFragmentRoot srcFolder = javaProject
						.getPackageFragmentRoot(folder);
		 
			IPackageFragment fragment = null;
			fragment = srcFolder.createPackageFragment("com.programcreek", true, null);
			String str = "package com.programcreek;" + "\n"
			+ "public class Test  {" + "\n" + " private String name;"
			+ "\n" + "}";
		 
				ICompilationUnit cu = null;
				try {
					cu = fragment.createCompilationUnit("Test.java", str,
							false, null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
		 
			IType type = cu.getType("Test");
			type.createField("private String age;", null, true, null);
			type.createField("private int value;", null, true, null);
			
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		*/
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
	
	public static void addTestCase(TCGson gson)
	{
		try
		{
			//Add the treeitem to test suite list
			TreeItem [] selectedNode = testSuiteList.getSelection();
			if(selectedNode[0] != null)
			{
				if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE"))
				{
					TreeItem testcaseItem = new TreeItem(selectedNode[0],SWT.NONE);
					testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
					testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
					testcaseItem.setText(gson.tcName);
					
					//Add the test case to test suite as well
					String tsName = selectedNode[0].getText();
					TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
					TestSuiteTask tsTask = tsService.getTaskByTSName(tsName);
					TSGson  tsGson = tsTask.getTsGson();
					List<TSTCGson> list = tsGson.tsTCLink;
					
					//Add the test suite details
					TSTCGson details = new TSTCGson();
					details.tcName = gson.tcName;
					List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
					TSTCParamGson param1 = new TSTCParamGson();
					param1.tcparamName = "Column1";
					param1.tcparamValue = "";
					TSTCParamGson param2 = new TSTCParamGson();
					param2.tcparamName = "Column2";
					param2.tcparamValue = "";
					TSTCParamGson param3 = new TSTCParamGson();
					param3.tcparamName = "Column3";
					param3.tcparamValue = "";
					TSTCParamGson param4 = new TSTCParamGson();
					param4.tcparamName = "Column4";
					param4.tcparamValue = "";
					TSTCParamGson param5 = new TSTCParamGson();
					param5.tcparamName = "Column5";
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
					String tsName = parent.getText();
					TestSuiteTaskService tsService = TestSuiteTaskService.getInstance();
					TestSuiteTask tsTask = tsService.getTaskByTSName(tsName);
					TSGson  tsGson = tsTask.getTsGson();
					List<TSTCGson> list = tsGson.tsTCLink;
					
					//Add the test suite details
					TSTCGson details = new TSTCGson();
					details.tcName = gson.tcName;
					List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
					TSTCParamGson param1 = new TSTCParamGson();
					param1.tcparamName = "Column1";
					param1.tcparamValue = "";
					TSTCParamGson param2 = new TSTCParamGson();
					param2.tcparamName = "Column2";
					param2.tcparamValue = "";
					TSTCParamGson param3 = new TSTCParamGson();
					param3.tcparamName = "Column3";
					param3.tcparamValue = "";
					TSTCParamGson param4 = new TSTCParamGson();
					param4.tcparamName = "Column4";
					param4.tcparamValue = "";
					TSTCParamGson param5 = new TSTCParamGson();
					param5.tcparamName = "Column5";
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
				}
			}
			
			//Add the same to the test case list
			TreeItem parent = testCaseList.getItem(0);
			TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
			testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
			testcaseItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/tc_logo.png"));
			testcaseItem.setText(gson.tcName);
			
			//Add the new task to the DB
			JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TCGson.class, gson));
			if(jsonObj!=null)
			{
				AutomaticsDBTestCaseQueries.postTC(Utilities.getMongoDB(), jsonObj);
			}
			
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
			e.printStackTrace();
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
			JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(TSGson.class, gson));
			if(jsonObj!=null)
			{
				AutomaticsDBTestSuiteQueries.postTS(Utilities.getMongoDB(), jsonObj);
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
