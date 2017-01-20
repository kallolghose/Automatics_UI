package com.automatics.packages.Editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.utilities.alltablestyles.OMLocatorInfoColumnEditable;
import com.automatics.utilities.alltablestyles.OMLocatorTypeColumnEditable;
import com.automatics.utilities.alltablestyles.OMObjectNameColumnEditable;
import com.automatics.utilities.alltablestyles.OMPageNameColumnEditable;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;

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

public class ObjectMapEditor extends EditorPart {

	public static String ID = "com.automatics.packages.Editors.omEditor";
	private ObjectMapTask omTask;
	private ObjectMapEditorInput input;
	private Table objectMapDataTable;
	
	public ObjectMapEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

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
		return false;
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
		gd_composite.heightHint = 20;
		composite.setLayoutData(gd_composite);
		
		TableViewer objectMapTableViewer = new TableViewer(parentComposite, SWT.BORDER | SWT.FULL_SELECTION);
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
		loadObjectMapDetails(objectMapTableViewer);
	}
	
	public void loadObjectMapDetails(TableViewer viewer)
	{
		try
		{
			OMGson omGson = omTask.getOmGson();
			viewer.setInput(omGson.omDetails);
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
