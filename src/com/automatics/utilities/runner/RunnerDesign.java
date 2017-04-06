package com.automatics.utilities.runner;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class RunnerDesign {

	protected Shell shlRunner;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RunnerDesign window = new RunnerDesign();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlRunner.open();
		shlRunner.layout();
		while (!shlRunner.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlRunner = new Shell();
		shlRunner.setSize(540, 527);
		shlRunner.setText("Runner");
		shlRunner.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(shlRunner, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
		
		Composite tc_ts_composite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_tc_ts_composite = new GridLayout(1, false);
		gl_tc_ts_composite.marginWidth = 0;
		gl_tc_ts_composite.marginHeight = 0;
		tc_ts_composite.setLayout(gl_tc_ts_composite);
		
		ToolBar toolBar = new ToolBar(tc_ts_composite, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ToolItem runItem = new ToolItem(toolBar, SWT.NONE);
		runItem.setText("Run");
		
		ToolItem refreshItem = new ToolItem(toolBar, SWT.NONE);
		
		refreshItem.setText("Refresh");
		
		ToolItem stopItem = new ToolItem(toolBar, SWT.NONE);
		stopItem.setText("Stop");
		
		ToolItem clearconsoleItem = new ToolItem(toolBar, SWT.NONE);
		clearconsoleItem.setText("Clear Console");
		
		TabFolder alltabs = new TabFolder(tc_ts_composite, SWT.NONE);
		alltabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmRemote = new TabItem(alltabs, SWT.NONE);
		tbtmRemote.setText("Remote");
		
		Composite composite = new Composite(alltabs, SWT.NONE);
		tbtmRemote.setControl(composite);
		
		TabItem tbtmLocalhost = new TabItem(alltabs, SWT.NONE);
		tbtmLocalhost.setText("Localhost");
		
		Composite composite_1 = new Composite(alltabs, SWT.NONE);
		tbtmLocalhost.setControl(composite_1);
		
		TabItem tbtmSettings = new TabItem(alltabs, SWT.NONE);
		tbtmSettings.setText("Settings");
		
		Composite composite_2 = new Composite(alltabs, SWT.NONE);
		tbtmSettings.setControl(composite_2);
		
		Composite console_composite = new Composite(sashForm, SWT.NONE);
		console_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] {334, 143});

	}
}
