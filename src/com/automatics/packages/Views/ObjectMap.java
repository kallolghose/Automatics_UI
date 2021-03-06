package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.packages.Model.TestCaseTask;
import com.automatics.packages.Model.TestCaseTaskService;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.ResourceManager;

public class ObjectMap extends ViewPart {

	public static String ID = "Automatics.ObjectMap";
	private static Tree objectNameTree, pageNameTree;
	private static Tree objectTree;
	private static TabFolder tabFolder;
	private MenuItem removefromTC;
	
	
	/*
	 * Risk involved in case user uses more than 2 object maps and each contains a same page name then only one object map 
	 * will be reflected (which ever would be last one)
	 */
	private static HashMap<String,String> pageName_ObjectMapName = new HashMap<String,String>();
	private static HashMap<String,ArrayList<String>> pageName_objectName = new HashMap<String,ArrayList<String>>();
	
	
	
	public ObjectMap() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void createPartControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new TabFolder(mainComposite, SWT.NONE);
		
		TabItem objectItem = new TabItem(tabFolder, SWT.NONE);
		objectItem.setText("Objects");
		
		Composite object_composite = new Composite(tabFolder, SWT.NONE);
		objectItem.setControl(object_composite);
		object_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		objectTree = new Tree(object_composite, SWT.BORDER);
		
		Menu menu = new Menu(objectTree);
		objectTree.setMenu(menu);
		
		removefromTC = new MenuItem(menu, SWT.NONE);
		removefromTC.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/recycle.png"));
		removefromTC.setText("Remove From Test Case");
		
		TabItem PageName = new TabItem(tabFolder, SWT.NONE);
		PageName.setText("Page Name");
		
		
		Composite page_composite = new Composite(tabFolder, SWT.NONE);
		PageName.setControl(page_composite);
		page_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		pageNameTree = new Tree(page_composite, SWT.BORDER);
		
		TreeItem pageNameRoot = new TreeItem(pageNameTree, SWT.NONE);
		pageNameRoot.setText("ObjectMap1");
		pageNameRoot.setExpanded(true);
		
		TabItem ObjectName = new TabItem(tabFolder, SWT.NONE);
		ObjectName.setText("Object Name");
		
		Composite object_name_composite = new Composite(tabFolder, SWT.NONE);
		ObjectName.setControl(object_name_composite);
		object_name_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		objectNameTree = new Tree(object_name_composite, SWT.BORDER);
		
		TreeItem objectNameRoot = new TreeItem(objectNameTree, SWT.NONE);
		objectNameRoot.setText("PageName1");
		objectNameRoot.setData("eltData", "Page");
		
		objectNameRoot.setExpanded(true);
		
		DragSource dragSource = new DragSource(objectNameTree, DND.DROP_MOVE | DND.DROP_COPY);
		setDragListeners(dragSource);
		setListeners();
	}
	
	public void setDragListeners(DragSource source)
	{
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {

			TreeItem [] selected; 
			
			public void dragStart(DragSourceEvent event) {
				// TODO Auto-generated method stub
				selected = objectNameTree.getSelection();
				if(selected[0].getData("eltData").toString().equals("PAGE" ) || selected[0].getText().equals(""))
				{
					event.doit = false;
				}
			}
			
			public void dragSetData(DragSourceEvent event) {
				 // TODO Auto-generated method stub
				 
				 if (TextTransfer.getInstance().isSupportedType(event.dataType))
				 {
					  OMDetails omD = (OMDetails) (selected[0].getData("eltData"));
	                  event.data = "OBJECTDATA__"+omD.pageName + "__" + omD.objName + "__" + pageNameTree.getSelection()[0].getData("ObjectMapName").toString();
	             }
			}
			
			public void dragFinished(DragSourceEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public void setListeners()
	{
		try
		{
			//Set listeners
			objectTree.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					TreeItem [] selected = objectTree.getSelection();
					if(selected.length==0)
						return;
					String omName = selected[0].getText();
					if(omName!=null)
					{
						loadAllPageName(ObjectMapTaskService.getInstance().getTaskByOmName(omName));
					}
				}
			});
			
			objectTree.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					tabFolder.setSelection(1);
				}
			});
			
			pageNameTree.addListener(SWT.MouseDown, new Listener() {
				
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					try
					{
						TreeItem[] selected = pageNameTree.getSelection();
						String pageName = selected[0].getText();
						String omName = selected[0].getData("ObjectMapName").toString();
						if(pageName!=null)
						{
							loadAllObjectName(ObjectMapTaskService.getInstance().getTaskByOmName(omName), pageName);
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + "-pageNameTree.addListener - Exception] : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			pageNameTree.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) 
				{
					tabFolder.setSelection(2);	
				}
			});
			
			removefromTC.addListener(SWT.Selection, new Listener() 
			{
				public void handleEvent(Event event) 
				{
					MessageDialog warnDialog = new MessageDialog(getSite().getShell(), "Warning", null ,
							"Deassociating Object Map will affect Test Case Steps. Do you want to continue ?", 
							MessageDialog.WARNING, new String[]{"Continue","Cancel"}, 0);
					int val = warnDialog.open();
					if(val == 1) //If clicked on cancel then return
						return;
					
					if(TCEditor.currentTestCase!=null)
					{
						TestCaseTask currentTask = TestCaseTaskService.getInstance().getTaskByTcName(TCEditor.currentTestCase);
						TCGson tcGson = currentTask.getTcGson();
						TreeItem treeItem = objectTree.getSelection()[0];
						String omName = treeItem.getText();
						List<String> omList = tcGson.tcObjectMapLink;
						for(int i=0;i<omList.size();i++)
						{
							if(omList.get(i).equalsIgnoreCase(omName))
							{
								omList.remove(i);
								pageName_ObjectMapName.remove(omName);
								break;
							}
						}
						treeItem.dispose();
						tcGson.tcObjectMapLink = omList;
						//Remove all object with the page name and object name
						List<TCStepsGSON> steps = tcGson.tcSteps;
						for(int i=0;i<steps.size();i++)
						{
							TCStepsGSON step = steps.get(i);
							if(step.omName.equals(omName))
							{
								step.stepPageName = "";
								step.stepObjName = "";
								step.omName = "";
							}
							steps.set(i, step);
						}
						tcGson.tcSteps = steps;
						TCEditor tcEditor = (TCEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
						tcEditor.refreshTableContents();
						currentTask.setTcGson(tcGson);
					}
				}
			});
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : setListeners()] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public ArrayList<OMDetails> loadAllPageName(ObjectMapTask omtask)
	{
		try
		{
			disposePageNames();
			
			String omName = omtask.getOmName();
			ArrayList<OMDetails> pageNames = new ArrayList<OMDetails>();
			TreeItem pageRoot = new TreeItem(pageNameTree, SWT.NONE);
			pageRoot.setText(omtask.getOmName());
			

			OMGson omGson = omtask.getOmGson();
			Iterator<OMDetails> itr = omGson.omDetails.iterator();
			ArrayList<String> pageNameList = new ArrayList<String>();
			while(itr.hasNext())
			{
				OMDetails details = itr.next();
				pageNames.add(details);
				pageNameList.add(details.pageName);
			}
			pageNameList = Utilities.removeDuplicatesFromArrayList(pageNameList);
			for(String pageName : pageNameList)
			{
				TreeItem pageItems = new TreeItem(pageRoot, SWT.NONE);
				pageItems.setText(pageName);
				pageItems.setData("ObjectMapName", omName);
			}
			pageRoot.setExpanded(true);
			return pageNames;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : loadAllPageName()] - " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<OMDetails> loadAllObjectName(ObjectMapTask omtask,String pageName)
	{
		try
		{
			disposeObjectNames();
			
			ArrayList<OMDetails> objectNames = new ArrayList<OMDetails>();
			OMGson omGson = omtask.getOmGson();
			Iterator<OMDetails> itr = omGson.omDetails.iterator();
			
			TreeItem objectRoot = new TreeItem(objectNameTree,SWT.NONE);
			objectRoot.setText(pageName);
			objectRoot.setData("eltData", "PAGE");
			
			while(itr.hasNext())
			{
				OMDetails details = itr.next();
				if(details.pageName.equals(pageName))
				{
					TreeItem objectItems = new TreeItem(objectRoot, SWT.NONE);
					objectItems.setText(details.objName);
					objectItems.setData("eltData", details);
				}
			}
			objectRoot.setExpanded(true);
			return objectNames;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : loadAllObjectName()] - " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public void visibilityOfRemovefromTC(boolean enabled)
	{
		removefromTC.setEnabled(enabled);
	}
	
	
	public static void loadObjectMap(String omName)
	{
		//Remove all children
		for(int i=0;i<objectTree.getItemCount();i++)
		{
			objectTree.getItem(0).dispose();
		}
		ObjectMapTask omT = ObjectMapTaskService.getInstance().getTaskByOmName(omName);
		if(omT == null)
		{
			System.out.println("[" + new Date() + "] - Error in " + omName + " : Object map NOT found");
			return;
		}
		//ObjectMapSaveTask omT = ObjectMapSaveService.getInstance().getSaveTask(omName);
		TreeItem trtmObjectmap = new TreeItem(objectTree, SWT.NONE);
		trtmObjectmap.setText(omName);
		trtmObjectmap.setData("eltType","OBJECTMAP");
		
		//Create the hashmap for new loaded object map
		pageName_ObjectMapName = new HashMap<String,String>();
		pageName_objectName = new HashMap<String,ArrayList<String>>();
		OMGson omGson = omT.getOmGson();
		List<OMDetails> omDetails = omGson.omDetails;
		for(OMDetails details : omDetails)
		{
			pageName_ObjectMapName.put(details.pageName, omName);
			ArrayList<String> objects;
			if(pageName_objectName.get(details.pageName)==null)
			{
				objects = new ArrayList<String>();
				objects.add(details.objName);
			}
			else
			{
				objects = pageName_objectName.get(details.pageName);
				objects.add(details.objName);
			}
			pageName_objectName.put(details.pageName, objects);
		}
		
	}
	
	public static void addObjectMap(String omName)
	{
		/*Check if omName already added*/
		TreeItem [] items = objectTree.getItems();
		for(TreeItem item : items)
		{
			if(item.getText().equalsIgnoreCase(omName))
			{
				return;
			}
		}
		
		TreeItem trtmObjectmap = new TreeItem(objectTree, SWT.NONE);
		trtmObjectmap.setText(omName);
		trtmObjectmap.setData("eltType","OBJECTMAP");
		
		//Add the task to the data
		ObjectMapTask omT = ObjectMapTaskService.getInstance().getTaskByOmName(omName);
		if(omT==null)
		{
			System.out.println("[" + new Date() + "] - Object Map : " + omName + " NOT Found in database.");
			return;
		}
		//ObjectMapSaveTask omT = ObjectMapSaveService.getInstance().getSaveTask(omName);
		OMGson omGson = omT.getOmGson();
		List<OMDetails> omDetails = omGson.omDetails;
		for(OMDetails details : omDetails)
		{
			pageName_ObjectMapName.put(details.pageName, omName);
			ArrayList<String> objects;
			if(pageName_objectName.get(details.pageName)==null)
			{
				objects = new ArrayList<String>();
				objects.add(details.objName);
			}
			else
			{
				objects = pageName_objectName.get(details.pageName);
				objects.add(details.objName);
			}
			pageName_objectName.put(details.pageName, objects);
		}
	}
	
	public static HashMap<String,String> getPageNameObjectMapMapping()
	{
		return pageName_ObjectMapName;
	}
	
	public static HashMap<String,ArrayList<String>> getPageNameObjectMapping()
	{
		return pageName_objectName;
	}
	
	public static ArrayList<String> getPageNamesAddedToObjectMap()
	{
		try
		{
			ArrayList<String> pgList = new ArrayList<String>();
			Iterator<String> itr = pageName_ObjectMapName.keySet().iterator();
			while(itr.hasNext())
			{
				pgList.add(itr.next());
			}
			return pgList;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	
	public static void disposeObjMaps()
	{
		try
		{
			while(objectTree.getItemCount()>0)
			{
				if(objectTree.getItem(0)!=null)
					objectTree.getItem(0).dispose();
			}
			while(pageNameTree.getItemCount()>0)
			{
				if(pageNameTree.getItem(0)!=null)
					pageNameTree.getItem(0).dispose();
			}
			while(objectNameTree.getItemCount()>0)
			{
				if(objectNameTree.getItem(0)!=null)
					objectNameTree.getItem(0).dispose();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void disposePageNames()
	{
		try
		{
			while(pageNameTree.getItemCount()>0)
			{
				if(pageNameTree.getItem(0)!=null)
					pageNameTree.getItem(0).dispose();
			}
			while(objectNameTree.getItemCount()>0)
			{
				if(objectNameTree.getItem(0)!=null)
					objectNameTree.getItem(0).dispose();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void disposeObjectNames()
	{
		try
		{
			while(objectNameTree.getItemCount()>0)
			{
				if(objectNameTree.getItem(0)!=null)
					objectNameTree.getItem(0).dispose();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	public static ArrayList<String> getAllOjectMapNamesSelected()
	{
		try
		{
			ArrayList<String> arr = new ArrayList<String>();
			for(TreeItem tItem : objectTree.getItems())
			{
				arr.add(tItem.getText());
			}
			return arr;
		}
		catch(Exception e)
		{
			System.out.println("[ ObjectMap : getAllObjectMapNamesSelected()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static int getObjectMapsCount()
	{
		return objectTree.getItemCount();
	}
	
	
	public static void setPageNameObjectMapMapping(HashMap<String,String> val) 
	{
		pageName_ObjectMapName = val;
	}

	
	public static void setPageNameObjectMapping(HashMap<String,ArrayList<String>> val)
	{
		pageName_objectName = val;
	}
	
	
	public void setFocus() {

	}
}
