package com.automatics.packages.Views;

import java.awt.Menu;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import com.automatics.mongo.packages.AutomaticsDBConnection;
import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.mongo.packages.AutomaticsDBTestSuiteQueries;
import com.automatics.utilities.elements.Project;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.helpers.SaveClass;
import com.automatics.utilities.helpers.Utilities;
import com.mongodb.*;

public class TC_TS_List extends ViewPart {

	private static Tree testSuiteList;
	private static Tree testCaseList;
	
	public TC_TS_List() {
		// TODO Auto-generated constructor stub
	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
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
		
		MenuItem copyItem = new MenuItem(testsuitePopUp, SWT.NONE);
		copyItem.setText("Copy");
		
		MenuItem pasteItem = new MenuItem(testsuitePopUp, SWT.NONE);
		pasteItem.setText("Paste");
		
		MenuItem deleteItem = new MenuItem(testsuitePopUp, SWT.NONE);
		deleteItem.setText("Delete");
	
		TabItem testCaseTab = new TabItem(tabFolder, SWT.NONE);
		testCaseTab.setText("Test Case");
		
		Composite testcaseListComposite = new Composite(tabFolder, SWT.NONE);
		testCaseTab.setControl(testcaseListComposite);
		testcaseListComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		testCaseList = new Tree(testcaseListComposite, SWT.BORDER);
		
		
		// TODO Auto-generated method stub
		setListeners();
		loadTestSuiteTestCaseTreeView();
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
			ArrayList<String> allTSList = AutomaticsDBTestSuiteQueries.getAllTS(db); //Get all test suites
			
			for(String tsName : allTSList)
			{
				//Add test suite to the application tree
				TreeItem testSuiteItem = new TreeItem(application_name_item, SWT.NONE);
				testSuiteItem.setText(tsName);
				testSuiteItem.setData("eltType", "TESTSUITE");
				
				//Get all the test cases for the test case
				System.out.println(AutomaticsDBTestSuiteQueries.getTS(db, tsName).toString());
				TSGson tsGson = Utilities.getGSONFromJSON(AutomaticsDBTestSuiteQueries.getTS(db, tsName).toString(), TSGson.class);
				Iterator<TSTCGson> itr = tsGson.tsTCLink.iterator();
				while(itr.hasNext())
				{
					TSTCGson tstcGson = itr.next();
					TreeItem testsuite_testcaseItem = new TreeItem(testSuiteItem, SWT.NONE);
					testsuite_testcaseItem.setText(tstcGson.tcName);
					testsuite_testcaseItem.setData("eltType","TESTCASE");
				}
			}
			
			//Load all test cases to the test case list
			TreeItem appName = new TreeItem(testCaseList, SWT.NONE);
			appName.setText("App_Name");
			
			ArrayList<String> allTCList = AutomaticsDBTestSuiteQueries.getAllTS(db);
			for(String tcName : allTCList)
			{
				TreeItem testCaseItem = new TreeItem(appName,SWT.NONE);
				testCaseItem.setText(tcName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("[" + getClass().getName() + "-loadTestSuiteTestCaseTreeView()] Exception : " + e.getMessage());
		}
	}
	
	public void setListeners()
	{
		try
		{
			//Add Listener to test suite list
			testSuiteList.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try
					{
						handlerService.executeCommand("com.automatics.packages.Editors.openEditor", null);
					}
					catch(Exception exp)
					{
						System.out.println(exp.getMessage());
						exp.printStackTrace();
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
				System.out.println(selectedNode[0].getText());
				System.out.println(selectedNode[0].getData("eltType"));
				if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTSUITE"))
				{
					TreeItem testcaseItem = new TreeItem(selectedNode[0],SWT.NONE);
					testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
					testcaseItem.setText(gson.tcName);
				}
				else if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("TESTCASE"))
				{
					TreeItem parent = selectedNode[0].getParentItem();
					TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
					testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
					testcaseItem.setText(gson.tcName);
				}
				else if(selectedNode[0].getData("eltType").toString().equalsIgnoreCase("APPLICATION"))
				{
					TreeItem parent = selectedNode[0].getItem(0);
					TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
					testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
					testcaseItem.setText(gson.tcName);
				}
			}
			
			//Add the same to the test case list
			TreeItem parent = testCaseList.getItem(0);
			TreeItem testcaseItem = new TreeItem(parent,SWT.NONE);
			testcaseItem.setData("eltType","TESTCASE"); //Set the type of object (Here TESTCASE)
			testcaseItem.setText(gson.tcName);
			
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
