package com.automatics.utilities.runner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;

public class AutomaticsRunner extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			AutomaticsRunner shell = new AutomaticsRunner(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public AutomaticsRunner(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ExpandBar expandBar = new ExpandBar(composite, SWT.NONE);
		
		ExpandItem xpndtmFirstitem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmFirstitem.setExpanded(true);
		xpndtmFirstitem.setText("FIrstItem");
		
		Composite composite_1 = new Composite(expandBar, SWT.NONE);
		xpndtmFirstitem.setControl(composite_1);
		xpndtmFirstitem.setHeight(xpndtmFirstitem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		Composite composite_2 = new Composite(expandBar, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
