package com.automatics.packages.Editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
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

import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.mongo.packages.AutomaticsDBOperationQueries;
import com.automatics.packages.Perspective;
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.utilities.alltablestyles.OMLocatorInfoColumnEditable;
import com.automatics.utilities.alltablestyles.OMLocatorTypeColumnEditable;
import com.automatics.utilities.alltablestyles.OMObjectNameColumnEditable;
import com.automatics.utilities.alltablestyles.OMPageNameColumnEditable;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;
import com.google.gson.Gson;

import java.util.*;

import javax.json.JsonObject;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.custom.CCombo;

public class ObjectMapEditor extends EditorPart {

	public static String ID = "com.automatics.packages.Editors.omEditor";
	private ObjectMapTask omTask;
	private ObjectMapEditorInput input;
	private Table objectMapDataTable;
	private boolean isDirty = false;
	private TableViewer objectMapTableViewer;
	private ToolItem btnAdd,btnDelete;
	
	public ObjectMapEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		try
		{
			boolean warning= false;
			List<OMDetails> list = (List<OMDetails>) objectMapTableViewer.getInput();
			objectMapDataTable.forceFocus();
			if(list !=null)
			{
				for(OMDetails stepDetails : list)
				{
					if(stepDetails.pageName.equals("") || stepDetails.objName.equals(""))
					{
						warning = true;
						break;
					}
				}
				if(!warning)
				{
					//Go and save the object map
					OMGson saveGSON = omTask.getOmGson();
					saveGSON.omDetails = list;
					omTask.setOmGson(saveGSON); //Add the value to task
					JsonObject jsonObj = Utilities.getJsonObjectFromString(Utilities.getJSONFomGSON(OMGson.class, saveGSON));
					if(jsonObj !=null)
					{
						AutomaticsDBObjectMapQueries.updateOM(Utilities.getMongoDB(), saveGSON.omName, jsonObj);
						ObjectMapSaveService.getInstance().updateSaveTask(new ObjectMapSaveTask(saveGSON.omName, saveGSON));
						Utilities.createObjectMap(saveGSON);
						isDirty = false;
						firePropertyChange(PROP_DIRTY);
					}
					else 
					{
						Utilities.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Save Failed",
											 "Some Unexpected Error Occured", "ERR");
						throw new RuntimeException("Error In Object Map Save");
					}
				}
				else
				{
					//Display Warning
					Utilities.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Cannot Save",
										"One or more PageName/ObjectName is not specified. Please provide value(s) for them", 
										"WARN").open();
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - doSave()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		System.out.println("Save AS");
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		if(!(input instanceof ObjectMapEditorInput))
		{
			throw new RuntimeException("Wrong Input");
		}
		
		this.input = (ObjectMapEditorInput) input;
		setSite(site);
		setInput(input);
		omTask = ObjectMapTaskService.getInstance().getTaskByOmName(this.input.getId());
		setPartName("ObjectMap:" + omTask.getOmName());
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
		
		Composite composite = new Composite(parentComposite, SWT.BORDER);
		
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.widthHint = 587;
		gd_composite.heightHint = 22;
		composite.setLayoutData(gd_composite);
		
		ToolBar iconsToolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		iconsToolBar.setBounds(0, 0, 258, 22);
		
		btnAdd = new ToolItem(iconsToolBar, SWT.NONE);
		btnAdd.setToolTipText("Add new object details");
		btnAdd.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		btnAdd.setSelection(true);
		
		btnDelete = new ToolItem(iconsToolBar, SWT.NONE);
		btnDelete.setToolTipText("Delete object details");
		btnDelete.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		btnDelete.setSelection(true);
		
		objectMapTableViewer = new TableViewer(parentComposite, SWT.FULL_SELECTION | SWT.MULTI);
		objectMapTableViewer.setContentProvider(new ArrayContentProvider());
		objectMapDataTable = objectMapTableViewer.getTable();
		objectMapDataTable.setLinesVisible(true);
		objectMapDataTable.setHeaderVisible(true);
		objectMapDataTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn pagaNameColViewer = new TableViewerColumn(objectMapTableViewer, SWT.NONE);
		pagaNameColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				OMDetails details = (OMDetails) element;
				return details.pageName;
			}
		});
		TableColumn pageNameCol = pagaNameColViewer.getColumn();
		pageNameCol.setWidth(110);
		pageNameCol.setText("Page Name");
		pagaNameColViewer.setEditingSupport(new OMPageNameColumnEditable(objectMapTableViewer));
		
		TableViewerColumn objNamColViewer = new TableViewerColumn(objectMapTableViewer, SWT.NONE);
		objNamColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				OMDetails details = (OMDetails)element;
				return details.objName;
			}
		});
		TableColumn objectNameCol = objNamColViewer.getColumn();
		objectNameCol.setWidth(123);
		objectNameCol.setText("Object Name");
		objNamColViewer.setEditingSupport(new OMObjectNameColumnEditable(objectMapTableViewer));
		
		TableViewerColumn locInfoColViewer = new TableViewerColumn(objectMapTableViewer, SWT.NONE);
		locInfoColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				OMDetails details = (OMDetails)element;
				return details.locatorInfo;
			}
		});
		TableColumn locInfoCol = locInfoColViewer.getColumn();
		locInfoCol.setWidth(127);
		locInfoCol.setText("Locator Information");
		locInfoColViewer.setEditingSupport(new OMLocatorInfoColumnEditable(objectMapTableViewer));
		
		TableViewerColumn locatorTypeColViewer = new TableViewerColumn(objectMapTableViewer, SWT.NONE);
		locatorTypeColViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				OMDetails details = (OMDetails)element;
				return details.locatorType;
			}
		});
		TableColumn locatorType = locatorTypeColViewer.getColumn();
		locatorType.setWidth(118);
		locatorType.setText("Locator Type");
		locatorTypeColViewer.setEditingSupport(new OMLocatorTypeColumnEditable(objectMapTableViewer));
		
		
		// TODO Auto-generated method stub
		setListeners();
		loadObjectMapDetails(objectMapTableViewer);
	}
	
	public void setListeners()
	{
		try
		{
			btnAdd.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					OMDetails newDetails = new OMDetails();
					newDetails.pageName = "";
					newDetails.objName = "";
					newDetails.locatorInfo = "";
					newDetails.locatorType = "";
					List<OMDetails> list = (List<OMDetails>)objectMapTableViewer.getInput();
					list.add(newDetails);
					objectMapTableViewer.refresh();
					objectMapTableViewer.editElement(newDetails, 0);
					//Set the dirty field for editor
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
				}
			});
			
			btnDelete.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					List<OMDetails> list = (List<OMDetails>)objectMapTableViewer.getInput();
					int indices [] =objectMapTableViewer.getTable().getSelectionIndices();
					for(int index : indices)
					{
						list.remove(index);
					}
					
					objectMapTableViewer.refresh();
					//Set the dirty variable for editor
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
				}
			});
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - setListener] - Exceptioen : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void loadObjectMapDetails(TableViewer viewer)
	{
		try
		{
			OMGson omGson = omTask.getOmGson();
			viewer.setInput(omGson.omDetails);
			
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
			System.out.println("[" + getClass().getName() + " : loadObjectMapDetails()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
