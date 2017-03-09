package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.packages.Editors.ObjectMapEditor;
import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.helpers.MyTitleAreaDialog;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;
import com.mongodb.DB;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ObjectList extends ViewPart {

	public static String ID = "com.automatics.pacakges.Views.OMList";
	
	private static Tree omListTree;
	private ObjectMapTaskService service = ObjectMapTaskService.getInstance();
	private MenuItem addToTestCase,newObjMap,opnObjMap,copyObjMap,pasteObjMap,delObjMap;
	private ObjectMapTask copyTask;
	private MenuItem refreshMap;
	
	
	public ObjectList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		omListTree = new Tree(composite, SWT.BORDER);
		
		
		loadOMList();
		setListerners();
	}

	public void setListerners()
	{
		//Add listener to om list tree
		omListTree.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				try
				{
					//Get All Workbench
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					TreeItem selected[] = omListTree.getSelection();
					if(selected[0].getData("eltType").toString().equalsIgnoreCase("OBJECTMAP"))
					{
						ObjectMapEditorInput input = new ObjectMapEditorInput(selected[0].getText());
				        page.openEditor(input, ObjectMapEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
					}	
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " -  setListeners] : Exception " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		addToTestCase.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if(TCEditor.currentTestCase!=null) 
				{
					TestCaseTask currentTask = TestCaseTaskService.getInstance().getTaskByTcName(TCEditor.currentTestCase);
					TCGson tcGson = currentTask.getTcGson();
					TreeItem [] selected = omListTree.getSelection();
					ArrayList<String> omArr = (ArrayList<String>)tcGson.tcObjectMapLink;
					if(omArr==null)
						omArr = new ArrayList<String>();
					
					if(selected[0].getData("eltType").toString().equalsIgnoreCase("OBJECTMAP"))
					{
						omArr.add(selected[0].getText());
						ObjectMap.addObjectMap(selected[0].getText());
					}
					omArr = Utilities.removeDuplicatesFromArrayList(omArr);
					tcGson.tcObjectMapLink = omArr;
					currentTask.setTcGson(tcGson);
				}
			}
		});
		
		newObjMap.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				try
				{
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					handlerService.executeCommand("com.automatics.packages.new.ObjectMap", event);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		opnObjMap.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		copyObjMap.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try
				{

					TreeItem selected[] = omListTree.getSelection();
					if (selected[0].getData("eltType").toString().equalsIgnoreCase("OBJECTMAP")) 
					{
						ObjectMapTaskService objectMapTaskService = ObjectMapTaskService.getInstance();
						copyTask = objectMapTaskService.getTaskByOmName(selected[0].getText());
					}

				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : copyObjMap ] - Exception  : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		pasteObjMap.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MyTitleAreaDialog dialog = new MyTitleAreaDialog(omListTree
						.getShell());
				dialog.create();
				if (dialog.open() == Window.OK) {
					copyTask.setOmName(dialog.getFirstName());
					OMGson omGson = copyTask.getOmGson();
					omGson.omName = dialog.getFirstName();
					TreeItem objectListItem = new TreeItem(omListTree
							.getItem(0), SWT.NONE);
					objectListItem.setText(omGson.omName);
					objectListItem.setData("eltType", "OBJECTMAP");
					objectListItem.setImage(ResourceManager.getPluginImage(
							"Automatics", "images/icons/om_logo_new.png"));
					JsonObject jsonObj = Utilities
							.getJsonObjectFromString(Utilities.getJSONFomGSON(
									OMGson.class, omGson));
					if (jsonObj != null) {
						AutomaticsDBObjectMapQueries.postOM(
								Utilities.getMongoDB(), jsonObj);
					}
				}
			}
		});
		
		refreshMap.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) 
			{
				loadOMList();
			}
		});
		
	}
	
	public void loadOMList()
	{
		try
		{	
			if(omListTree.getItemCount()>0)
				omListTree.getItem(0).dispose();
			
			TreeItem root = new TreeItem(omListTree, SWT.NONE);
			root.setText("App_Name");
			root.setData("eltType","APPNAME");
			root.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/project.png"));
			
			Menu menu = new Menu(omListTree);
			omListTree.setMenu(menu);
			
			addToTestCase = new MenuItem(menu, SWT.NONE);
			addToTestCase.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/arrow_left.png"));
			addToTestCase.setText("Add To Test Case");
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			newObjMap = new MenuItem(menu, SWT.NONE);
			newObjMap.setText("New");
			
			opnObjMap = new MenuItem(menu, SWT.NONE);
			opnObjMap.setText("Open");
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			copyObjMap = new MenuItem(menu, SWT.NONE);
			copyObjMap.setText("Copy");
			
			pasteObjMap = new MenuItem(menu, SWT.NONE);
			pasteObjMap.setText("Paste");
			
			delObjMap = new MenuItem(menu, SWT.NONE);
			delObjMap.setText("Delete");
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			refreshMap = new MenuItem(menu, SWT.NONE);
			refreshMap.setText("Refresh");
			
			
			DB db = Utilities.getMongoDB();
			ArrayList<String> omList = AutomaticsDBObjectMapQueries.getAllOM(db);
			for(String om : omList)
			{
				TreeItem omTree = new TreeItem(root, SWT.NONE);
				omTree.setText(om);
				omTree.setData("eltType", "OBJECTMAP");
				omTree.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/om_logo_new.png"));
				
				//Get Specific OMs add load the same
				OMGson omGson = Utilities.getGSONFromJSON(AutomaticsDBObjectMapQueries.getOM(db,om).toString(), OMGson.class);
				//Add the same to save task
				ObjectMapSaveTask omTask = new ObjectMapSaveTask(omGson.omName,omGson);
				ObjectMapSaveService.getInstance().addSaveTask(omTask);
				
				//Add | Update Editor Task
				if(service.getTaskByOmName(om) == null)
				{
					ObjectMapTask omEditorTask = new ObjectMapTask(om, omGson.omDesc, omGson.omIdentifier, omGson);
					service.addTasks(omEditorTask);
				}
				else
				{
					ObjectMapTask task = service.getTaskByOmName(om);
					task.setOmGson(omGson);
				}
			}
			root.setExpanded(true);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + ":loadOMList() ] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
		
	public static void createOjectMap(OMGson gson)
	{
		try
		{
			//Create a node
			TreeItem omTree = new TreeItem(omListTree.getItem(0), SWT.NONE);
			omTree.setText(gson.omName);
			omTree.setData("eltType", "OBJECTMAP");
			omTree.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/om_logo_new.png"));
			omTree.setChecked(true);
			
			ObjectMapTask newTask = new ObjectMapTask(gson.omName, gson.omDesc, gson.omIdentifier, gson);
			ObjectMapTaskService service = ObjectMapTaskService.getInstance();
			service.addTasks(newTask);

			ObjectMapSaveTask omTask = new ObjectMapSaveTask(gson.omName,gson);
			ObjectMapSaveService.getInstance().addSaveTask(omTask);
			
			//Open the object map editor
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			ObjectMapEditorInput input = new ObjectMapEditorInput(gson.omName);
			page.openEditor(input, ObjectMapEditor.ID, false, IWorkbenchPage.MATCH_INPUT);
			
			//Save the object map in DB
			JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(OMGson.class, gson));
			if(jsonObj!=null)
			{
				AutomaticsDBObjectMapQueries.postOM(Utilities.getMongoDB(), jsonObj);
			}
		}
		catch(Exception e)
		{
			System.out.println("[ObjectList - createObjectMap()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is to add the Object Map to the list (Particularly for recording)
	 */
	public static void addNewObjectMap(ObjectMapTask omTask) 
	{
		try
		{
			/*Check if the omTask is alreader added to the treelist*/
			TreeItem parent = omListTree.getItem(0);
			TreeItem newItem = new TreeItem(parent, SWT.NONE);
			for(TreeItem item : parent.getItems())
			{
				if(item.getText().equalsIgnoreCase(omTask.getOmName())) //If already added no need to add the same
					return;
			}
			newItem.setText(omTask.getOmName());
			newItem.setData("eltType", "OBJECTMAP");
			newItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/om_logo_new.png"));
		}
		catch(Exception e)
		{
			System.out.println("[ObjectList : addNewObjectMap() ] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void visibilityOfAddToTestCaseItem(boolean enable)
	{
		addToTestCase.setEnabled(enable);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	
}
