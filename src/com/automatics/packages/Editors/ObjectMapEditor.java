package com.automatics.packages.Editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import com.automatics.utilities.git.GitUtilities;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;
import com.google.gson.Gson;

import java.io.File;
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
import org.eclipse.jface.dialogs.MessageDialog;
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
	private ToolItem btnAdd,btnDelete, pasteItem, copyItem, openEditor, saveItem,lockItem;
	private List<OMDetails> list;
	private int index;
	private ToolItem commitItem;
	private ToolItem pullItem;
	private GitUtilities gitUtil;
	private boolean viewAllElements = true;
	private String lock_image = "images/icons/Open_lock.png";
	
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
						//Utilities.createObjectMap(saveGSON);
						isDirty = false;
						firePropertyChange(PROP_DIRTY);
						Utilities.createObjectMap(omTask.getOmGson());
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
		
		//Check for sync status from remote GIT
		gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		gitUtil.initExistingRepository();
		
		//Check if the object map is private or not
		OMGson omGson = omTask.getOmGson();
		if(omGson.omFlag.equalsIgnoreCase("PRIVATE"))
		{
			if(!Utilities.AUTOMATICS_USERNAME.equalsIgnoreCase(omGson.username))
			{
				MessageDialog privateChk = new MessageDialog(site.getShell(), "Error", null, "Cannot Open Private Object Map", 
	    				MessageDialog.ERROR, 
	    				new String[]{"OK"}, 0);
	    		privateChk.open();
	    		return;
			}
		}
		else if(omGson.omFlag.equalsIgnoreCase("EDIT"))
		{
			if(!Utilities.AUTOMATICS_USERNAME.equalsIgnoreCase(omGson.username))
			{
				viewAllElements = false; //Add this flag to disable all operations	
			}
			else
	    	{
	    		lock_image = "images/icons/lock.png";
	    	}
		}
		else
		{
		    String currentFileName = Utilities.OBJECTMAP_FILE_LOCATION + omTask.getOmName() + ".java";
		    boolean syncstaus = gitUtil.getSync(currentFileName);
		    if(syncstaus)
		    {
		    	MessageDialog dialog = new MessageDialog(site.getShell(), "Warning", null, "File not in sync. Please get sync",
		    			MessageDialog.WARNING, 
						new String[]{"OK","Cancel"}, 0);
		    	int selected = dialog.open();
		    	switch(selected)
		    	{
		    	case 0:
		    		
		    		gitUtil.performSpecificPull(currentFileName);
		    		MessageDialog promptMsg = new MessageDialog(site.getShell(), "Information", null,"Synch Completed !!", 
		    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
		    		promptMsg.open();
		    		break;
		    	}
		    }
		}
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
		iconsToolBar.setBounds(0, 0, 287, 22);
		
		btnAdd = new ToolItem(iconsToolBar, SWT.NONE);
		btnAdd.setToolTipText("Add new object details");
		btnAdd.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		btnAdd.setSelection(true);
		btnAdd.setEnabled(viewAllElements);
		
		btnDelete = new ToolItem(iconsToolBar, SWT.NONE);
		btnDelete.setToolTipText("Delete object details");
		btnDelete.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		btnDelete.setSelection(true);
		btnDelete.setEnabled(viewAllElements);
		
		saveItem = new ToolItem(iconsToolBar, SWT.NONE);
		saveItem.setToolTipText("Save");
		saveItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Save.png"));
		saveItem.setSelection(true);
		saveItem.setEnabled(viewAllElements);
		
		copyItem = new ToolItem(iconsToolBar, SWT.NONE);
		copyItem.setToolTipText("Copy");
		copyItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Copy.png"));
		copyItem.setSelection(true);
		copyItem.setEnabled(viewAllElements);
		
		pasteItem = new ToolItem(iconsToolBar, SWT.NONE);
		pasteItem.setToolTipText("Paste");
		pasteItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/1485966418_Paste.png"));
		pasteItem.setSelection(true);
		pasteItem.setEnabled(viewAllElements);
		
		openEditor = new ToolItem(iconsToolBar, SWT.NONE);
		openEditor.setToolTipText("View Editor");
		openEditor.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/1485966863_editor-grid-view-block-glyph.png"));
		openEditor.setSelection(true);
		openEditor.setEnabled(viewAllElements);
		
		commitItem = new ToolItem(iconsToolBar, SWT.NONE);
		commitItem.setWidth(30);
		commitItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/git_commit.png"));
		commitItem.setToolTipText("Commit and Push");
		commitItem.setEnabled(viewAllElements);
		
		pullItem = new ToolItem(iconsToolBar, SWT.NONE);
		pullItem.setToolTipText("Pull");
		pullItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/pull.png"));
		pullItem.setSelection(true);
		pullItem.setEnabled(viewAllElements);
		
		lockItem = new ToolItem(iconsToolBar, SWT.NONE);
		lockItem.setToolTipText("Lock for Editing");
		lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Open_lock.png"));
		lockItem.setSelection(true);
		lockItem.setData("Locked", false);
		lockItem.setEnabled(viewAllElements);
		
		objectMapTableViewer = new TableViewer(parentComposite, SWT.FULL_SELECTION | SWT.MULTI);
		objectMapTableViewer.setContentProvider(new ArrayContentProvider());
		objectMapDataTable = objectMapTableViewer.getTable();
		objectMapDataTable.setLinesVisible(true);
		objectMapDataTable.setHeaderVisible(true);
		objectMapDataTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		objectMapDataTable.setEnabled(viewAllElements);
		
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
			
			saveItem.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					try{
					saveActionPerform();
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + "tltmNewItem - setListener] - Exceptioen : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			copyItem.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
				try
					{
						list=(List<OMDetails>) objectMapTableViewer.getInput();
						index=objectMapDataTable.getSelectionIndex();
						list.get(index);
						isDirty = true;
						firePropertyChange(PROP_DIRTY);
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + "tltmForCopy - setListener] - Exceptioen : " + e.getMessage());
						e.printStackTrace();
					}
					}
				
			});
			pasteItem.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					try
					{	
						final int pasteIndex=objectMapDataTable.getSelectionIndex();
						list.add(pasteIndex, list.get(index));
						objectMapTableViewer.refresh();
						isDirty = true;
						firePropertyChange(PROP_DIRTY);
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + "pasteItem - setListener] - Exceptioen : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			openEditor.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) 
				{
					try
					{
						String fileName = Utilities.createObjectMap(omTask.getOmGson());
						
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
						System.out.println("[" + getClass().getName() + " : openEditor.addListener()] - Exception : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			commitItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					/*Save the object map with flag as PUBLIC*/
					OMGson omGson = omTask.getOmGson();
					omGson.omFlag = "PUBLIC";
					omTask.setOmGson(omGson);
					saveActionPerform();

					lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Open_lock.png"));
					lockItem.setData("Locked",false);
					
					/*Commit the files to the GIT repository*/
					String currentFileName = Utilities.OBJECTMAP_FILE_LOCATION + omTask.getOmName() + ".java";
					gitUtil.performSpecificCommit(currentFileName);
					gitUtil.performPush();
					MessageDialog commitMsg = new MessageDialog(getSite().getShell(), "Information", null,"Commit and Push Performed.", 
		    				MessageDialog.INFORMATION, new String[]{"OK"}, 0);
					commitMsg.open();
				}
			});
			
			pullItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					String currentFileName = Utilities.OBJECTMAP_FILE_LOCATION + omTask.getOmName() + ".java";
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
				}
			});
			
			lockItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					boolean locked = new Boolean(lockItem.getData("Locked").toString());
					if(!locked)
					{
						lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/lock.png"));
						OMGson omGson = omTask.getOmGson();
						omGson.omFlag = "EDIT";
						omGson.username = Utilities.AUTOMATICS_USERNAME;
						omTask.setOmGson(omGson);
						saveActionPerform();
					}
					else
					{
						lockItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Open_Lock.png"));
						OMGson omGson = omTask.getOmGson();
						omGson.omFlag = "PUBLIC";
						omGson.username = Utilities.AUTOMATICS_USERNAME;
						omTask.setOmGson(omGson);
						saveActionPerform();
					}
					lockItem.setData("Locked", !locked);
				}
			});
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - setListener] - Exception : " + e.getMessage());
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
	
	/**
	 * saveActionPerform.
	 */
	private void saveActionPerform() {
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
						isDirty = false;
						firePropertyChange(PROP_DIRTY);
						Utilities.createObjectMap(omTask.getOmGson());
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
	
	public static ArrayList<String> getLocatorType()
	{
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("className");
		arr.add("css");
		arr.add("id");
		arr.add("linkText");
		arr.add("name");
		arr.add("partialLinkText");
		arr.add("tagName");
		arr.add("xpath");
		return arr;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		List<OMDetails> list = (ArrayList<OMDetails>)objectMapTableViewer.getInput();
		omTask = ObjectMapTaskService.getInstance().getTaskByOmName(omTask.getOmName());
		list = omTask.getOmGson().omDetails;
		System.out.println("Here List : " + list);
		objectMapTableViewer.setInput(list);
		objectMapTableViewer.refresh();
	}
}
