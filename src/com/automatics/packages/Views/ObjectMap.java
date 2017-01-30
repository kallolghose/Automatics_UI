package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
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

import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;

public class ObjectMap extends ViewPart {

	public static String ID = "Automatics.ObjectMap";
	private static Tree objectNameTree, pageNameTree;
	private static Tree objectTree;
	
	public ObjectMap() {
		// TODO Auto-generated constructor stub
	}

	public void createPartControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(mainComposite, SWT.NONE);
		
		TabItem objectItem = new TabItem(tabFolder, SWT.NONE);
		objectItem.setText("Objects");
		
		Composite object_composite = new Composite(tabFolder, SWT.NONE);
		objectItem.setControl(object_composite);
		object_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		objectTree = new Tree(object_composite, SWT.BORDER);
		
		/*
		TreeItem trtmObjectmap = new TreeItem(objectTree, SWT.NONE);
		trtmObjectmap.setText("ObjectMap1");
		
		TreeItem trtmObjectmap_1 = new TreeItem(objectTree, SWT.NONE);
		trtmObjectmap_1.setText("ObjectMap2");
		*/
		
		TabItem PageName = new TabItem(tabFolder, SWT.NONE);
		PageName.setText("Page Name");
		
		Composite page_composite = new Composite(tabFolder, SWT.NONE);
		PageName.setControl(page_composite);
		page_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		pageNameTree = new Tree(page_composite, SWT.BORDER);
		
		TreeItem pageNameRoot = new TreeItem(pageNameTree, SWT.NONE);
		pageNameRoot.setText("ObjectMap1");
		
		TreeItem trtmPagename = new TreeItem(pageNameRoot, SWT.NONE);
		trtmPagename.setText("PageName1");
		
		TreeItem trtmPagename_1 = new TreeItem(pageNameRoot, SWT.NONE);
		trtmPagename_1.setText("PageName2");
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
		
		TreeItem trtmObject_1 = new TreeItem(objectNameRoot, SWT.NONE);
		trtmObject_1.setText("Object1");
		trtmObject_1.setData("eltData","Object");
		
		TreeItem trtmObject = new TreeItem(objectNameRoot, SWT.NONE);
		trtmObject.setText("Object2");
		trtmObject.setData("eltData","Object");
		
		TreeItem trtmObject_2 = new TreeItem(objectNameRoot, SWT.NONE);
		trtmObject_2.setText("Object3");
		trtmObject_2.setData("eltData","Object");
		
		TreeItem trtmObject_3 = new TreeItem(objectNameRoot, SWT.NONE);
		trtmObject_3.setText("Object4");
		trtmObject_3.setData("eltData","Object");
		
		objectNameRoot.setExpanded(true);
		
		DragSource dragSource = new DragSource(objectNameTree, DND.DROP_MOVE | DND.DROP_COPY);
		setDragListeners(dragSource);
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
		
		//Set listeners
		objectTree.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				TreeItem [] selected = objectTree.getSelection();
				String omName = selected[0].getText();
				if(omName!=null)
				{
					loadAllPageName(ObjectMapSaveService.getInstance().getSaveTask(omName));
				}
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
						loadAllObjectName(ObjectMapSaveService.getInstance().getSaveTask(omName), pageName);
					}
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + "-pageNameTree.addListener - Exception] : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}
	
	public ArrayList<OMDetails> loadAllPageName(ObjectMapSaveTask omtask)
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
			
			while(itr.hasNext())
			{
				OMDetails details = itr.next();
				pageNames.add(details);
				TreeItem pageItems = new TreeItem(pageRoot, SWT.NONE);
				pageItems.setText(details.pageName);
				pageItems.setData("ObjectMapName", omName);
			}
			return pageNames;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : loadAllPageName()] - " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<OMDetails> loadAllObjectName(ObjectMapSaveTask omtask,String pageName)
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
			return objectNames;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : loadAllObjectName()] - " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static void loadObjectMap(String omName)
	{
		//Remove all children
		for(int i=0;i<objectTree.getItemCount();i++)
		{
			objectTree.getItem(0).dispose();
		}
		ObjectMapSaveTask omT = ObjectMapSaveService.getInstance().getSaveTask(omName);
		TreeItem trtmObjectmap = new TreeItem(objectTree, SWT.NONE);
		trtmObjectmap.setText(omT.getOmName());
		trtmObjectmap.setData("eltType","OBJECTMAP");
	}
	
	public static void disposeObjMaps()
	{
		try
		{
			if(objectTree.getItem(0)!=null)
				objectTree.getItem(0).dispose();
			if(pageNameTree.getItem(0)!=null)
				pageNameTree.getItem(0).dispose();
			if(objectNameTree.getItem(0)!=null)
				objectNameTree.getItem(0).dispose();
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
			if(pageNameTree.getItem(0)!=null)
				pageNameTree.getItem(0).dispose();
			if(objectNameTree.getItem(0)!=null)
				objectNameTree.getItem(0).dispose();
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
			if(objectNameTree.getItem(0)!=null)
				objectNameTree.getItem(0).dispose();
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

	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
