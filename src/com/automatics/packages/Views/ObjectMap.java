package com.automatics.packages.Views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;

public class ObjectMap extends ViewPart {

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
		
		TabItem PageName = new TabItem(tabFolder, SWT.NONE);
		PageName.setText("Page Name");
		
		Composite page_composite = new Composite(tabFolder, SWT.NONE);
		PageName.setControl(page_composite);
		
		TabItem ObjectName = new TabItem(tabFolder, SWT.NONE);
		ObjectName.setText("Object Name");
		
		Composite object_name_composite = new Composite(tabFolder, SWT.NONE);
		ObjectName.setControl(object_name_composite);
		// TODO Auto-generated method stub

	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
