package com.automatics.packages.Views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.automatics.utilities.gsons.configuration.ConfigurationParameter;
import com.automatics.utilities.alltablestyles.ConfigurationParamEditingSupport;
import com.automatics.utilities.alltablestyles.ConfigurationParamValueEditingSupport;

public class ConfigParameter extends ViewPart {
	private Table configuration_table;
	private ToolItem save;
	private TableViewer configurationTableViewer;
	
	public ConfigParameter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite menu_composite = new Composite(composite, SWT.NONE);
		menu_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_menu_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_menu_composite.heightHint = 23;
		menu_composite.setLayoutData(gd_menu_composite);
		
		ToolBar toolBar = new ToolBar(menu_composite, SWT.FLAT | SWT.RIGHT);
		
		ToolItem add = new ToolItem(toolBar, SWT.NONE);
		add.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		add.setToolTipText("Add New Row");
		add.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) 
			{
				
			}
		});
		
		
		ToolItem del = new ToolItem(toolBar, SWT.NONE);
		del.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		del.setToolTipText("Delete Selected Row");
		del.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) 
			{
				
			}
		});
		
		save = new ToolItem(toolBar, SWT.NONE);
		save.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Save.png"));
		save.setToolTipText("Save Parameters");
		save.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				
			}
		});
		
		Composite grid_composite = new Composite(composite, SWT.NONE);
		grid_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		grid_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		configurationTableViewer  = new TableViewer(grid_composite, SWT.BORDER | SWT.FULL_SELECTION);
		configuration_table = configurationTableViewer.getTable();
		configuration_table.setLinesVisible(true);
		configuration_table.setHeaderVisible(true);
		configurationTableViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn configParamViewer = new TableViewerColumn(configurationTableViewer, SWT.NONE);
		configParamViewer.setEditingSupport(new ConfigurationParamEditingSupport(configurationTableViewer));
		configParamViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				ConfigurationParameter param = (ConfigurationParameter) element;
				return element == null ? "" : param.parameterName;
			}
		});
		TableColumn configParameter = configParamViewer.getColumn();
		configParameter.setWidth(250);
		configParameter.setText("Configuration Parameter Name");
		
		TableViewerColumn configValueViewer = new TableViewerColumn(configurationTableViewer, SWT.NONE);
		configValueViewer.setEditingSupport(new ConfigurationParamValueEditingSupport(configurationTableViewer));
		configValueViewer.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				ConfigurationParameter param = (ConfigurationParameter) element;
				return element == null ? "" : param.parameterValue;
			}
		});
		TableColumn configValue = configValueViewer.getColumn();
		configValue.setWidth(250);
		configValue.setText("Configuration Parameter Value");
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
