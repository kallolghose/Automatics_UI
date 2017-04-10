package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.json.JsonObject;

import org.eclipse.jface.dialogs.MessageDialog;
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

import com.automatics.packages.Editors.ObjectMapEditor;
import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.packages.api.handlers.ObjectMapAPIHandler;
import com.automatics.packages.api.handlers.TestCaseAPIHandler;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.helpers.MyTitleAreaDialog;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;

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
					System.out.println("[" + new Date() + "] - [" + getClass().getName() + " -  setListeners] : Exception " + e.getMessage());
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
				try
				{
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
					System.out.println("[" + new Date() +"] - [" + getClass().getName() + " - setListeners(opnObjMap)] - Exception  : " + e.getMessage());
					e.printStackTrace(System.out);
				}
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
					System.out.println("[" + new Date() + "] - [" + getClass().getName() + " : copyObjMap ] - Exception  : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		pasteObjMap.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MyTitleAreaDialog dialog = new MyTitleAreaDialog(omListTree
						.getShell());
				dialog.create();
				if (dialog.open() == Window.OK) 
				{
					String pasteOMName = dialog.getFirstName();
					OMGson omGsonOrig = copyTask.getOmGson();
					OMGson omGson = new OMGson();
					omGson.omName = pasteOMName;
					omGson.omDesc = omGsonOrig.omDesc;
					omGson.omIdentifier = pasteOMName;
					omGson.omCreatedBy = Utilities.AUTOMATICS_USERNAME;
					omGson.omDetails = omGsonOrig.omDetails;
					omGson.projectName = Utilities.DB_PROJECT_NAME;
					omGson.lockedBy = Utilities.AUTOMATICS_USERNAME;
					
					TreeItem objectListItem = new TreeItem(omListTree
							.getItem(0), SWT.NONE);
					objectListItem.setText(omGson.omName);
					objectListItem.setData("eltType", "OBJECTMAP");
					objectListItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/om_logo_new.png"));
					
					//Create new task
					ObjectMapTask newPasteTask = new ObjectMapTask(pasteOMName, copyTask.getOmDesc(), pasteOMName,omGson);
					if(service.getTaskByOmName(pasteOMName)==null)
					{
						service.addTasks(newPasteTask);
					}
					
					ObjectMapSaveTask saveTask = new ObjectMapSaveTask(pasteOMName, omGson);
					if(ObjectMapSaveService.getInstance().getSaveTask(pasteOMName)==null)
					{
						ObjectMapSaveService.getInstance().addSaveTask(saveTask);
					}
					
					omGson = ObjectMapAPIHandler.getInstance().postObjectMap(omGson);
					if(ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE!=200)
					{
						MessageDialog edialog = new MessageDialog(getSite().getShell(), "Paste Error", null, "Copy Failed", 
																MessageDialog.ERROR, new String[]{"OK"}, 0);
						edialog.open();
						throw new RuntimeException("Copy Failed In Object Map : "
													+ ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE + "  "
													+ ObjectMapAPIHandler.OBJECTMAP_RESPONSE_MESSAGE);
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
		
		delObjMap.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) 
            {
                  List<String> testCaseValue=new ArrayList<String>();
                  boolean value=false;
                  TreeItem item = omListTree.getSelection()[0];
                  TCGson [] allTCList = TestCaseAPIHandler.getInstance().getAllTestCases();
                  for (TCGson tcGson : allTCList) 
                  {
                	  if(tcGson.tcObjectMapLink.contains(item.getText()))
                	  {
                		  value=true;
                		  testCaseValue.add(tcGson.tcName);
                	  }
                  }
                  if(!value)
                  {
                          MessageDialog deleteDialog = new MessageDialog(getSite().getShell(), "Delete Object Map", null,
                                       "Are you sure you want to delete - " + item.getText() + " ?", 
                                       MessageDialog.CONFIRM, new String[]{"Delete", "Cancel"}, 0);
                         int optionSelected = deleteDialog.open();
                         if(optionSelected == 0)
                         {
	                            ObjectMapAPIHandler.getInstance().deleteObjectMap(item.getText());
	                            System.out.println("[" + new Date() + "] : [Object List Delete Response] - " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE 
	                                             + "  " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_MESSAGE);
	                            if(ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE==200)
	                            {
	                            	ObjectMapTaskService.getInstance().deleteTaskByOMName(item.getText());
	                            	item.dispose();
	                            }	
                         }                                 
                  }
                  else
                  {
                         MessageDialog deleteDialog = new MessageDialog(getSite().getShell(), "Delete Object Map", null,
                                       "This object map is associated with Test Case(s)" +" "+ testCaseValue +". Removal may cause an error in testcase."
                                       + "\nSure want to delete the same ?", 
                                       MessageDialog.WARNING, new String[]{"OK","Cancel"}, 0);
                         int temp = deleteDialog.open();
                         if(temp==0)
                         {
                        	 ObjectMapAPIHandler.getInstance().deleteObjectMap(item.getText());
                             System.out.println("[" + new Date() + "] : [Object List Delete Response] - " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE 
                                              + "  " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_MESSAGE);
                             if(ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE==200)
                             {
                             	ObjectMapTaskService.getInstance().deleteTaskByOMName(item.getText());
                             	item.dispose();
                             }
                         }
                  }
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
			root.setText(Utilities.DB_PROJECT_NAME);
			root.setData("eltType","APPNAME");
			root.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/project.png"));
			
			OMGson [] omGsons = ObjectMapAPIHandler.getInstance().getAllObjectMap();
			System.out.println("[" + new Date() + "] - Load Object Map Response : " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE + "  " 
								   + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_MESSAGE);
			if(omGsons!=null && omGsons.length>0)
			{
				for(OMGson omGson : omGsons)
				{
					if(omGson.omName == null) /*As this is an error*/
						continue;
					TreeItem omTree = new TreeItem(root, SWT.NONE);
					omTree.setText(omGson.omName);
					omTree.setData("eltType", "OBJECTMAP");
					omTree.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/om_logo_new.png"));
					
					//Get Specific OMs add load the same
					//OMGson omGson = ObjectMapAPIHandler.getInstance().getSpecificObjectMap(om.omName);
	
					//Add | Update Editor Task
					if(service.getTaskByOmName(omGson.omName) == null)
					{
						//Add Editor task
						ObjectMapTask omEditorTask = new ObjectMapTask(omGson.omName, omGson.omDesc, omGson.omIdentifier, omGson);
						service.addTasks(omEditorTask);
						
						//Add Save Task
						ObjectMapSaveTask omTask = new ObjectMapSaveTask(omGson.omName,omGson);
						ObjectMapSaveService.getInstance().addSaveTask(omTask);
					}
					else
					{
						//Update Editor Task
						ObjectMapTask task = service.getTaskByOmName(omGson.omName);
						task.setOmGson(omGson);
						
						//Update Save Task
						ObjectMapSaveTask omsaveTask = ObjectMapSaveService.getInstance().getSaveTask(omGson.omName);
						omsaveTask.setOmGson(omGson);
					}
				}
			}
			root.setExpanded(true);
		}
		catch(Exception e)
		{
			System.out.println("[" + new Date() + "] - [" + getClass().getName() + ":loadOMList() ] - Exception : " + e.getMessage());
			e.printStackTrace(System.out);
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
			/*JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(OMGson.class, gson));
			if(jsonObj!=null)
			{
				AutomaticsDBObjectMapQueries.postOM(Utilities.getMongoDB(), jsonObj);
			}*/
			gson = ObjectMapAPIHandler.getInstance().postObjectMap(gson);
			System.out.println("[" + new Date() + "] Object Map : Save object map Response : " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE 
								   + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_MESSAGE);
			if(ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE!=200)
			{
				throw new RuntimeException("Error while creating : " + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_CODE + " : " 
																	 + ObjectMapAPIHandler.OBJECTMAP_RESPONSE_MESSAGE);	
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + new Date() + "] - [ObjectList - createObjectMap()] - Exception : " + e.getMessage());
			e.printStackTrace(System.out);
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
			System.out.println("[" + new Date() + "] - [ObjectList : addNewObjectMap() ] - Exception : " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	public void visibilityOfAddToTestCaseItem(boolean enable)
	{
		addToTestCase.setEnabled(enable);
	}
	
	@Override
	public void setFocus() 
	{
		
	}
	
	
}
