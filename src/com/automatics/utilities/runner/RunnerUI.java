package com.automatics.utilities.runner;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;

public class RunnerUI {

	protected Shell shlRunner;
	private Table table;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RunnerUI window = new RunnerUI();
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
		shlRunner.setSize(470, 572);
		shlRunner.setText("Runner");
		shlRunner.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(shlRunner, SWT.NONE);
		parentComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite testSuiteComposite = new Composite(parentComposite, SWT.BORDER);
		testSuiteComposite.setLayout(new GridLayout(1, false));
		
		Composite iconsToolBar = new Composite(testSuiteComposite, SWT.NONE);
		GridData gd_iconsToolBar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_iconsToolBar.widthHint = 441;
		gd_iconsToolBar.heightHint = 24;
		iconsToolBar.setLayoutData(gd_iconsToolBar);
		
		ToolBar iconToolBar = new ToolBar(iconsToolBar, SWT.FLAT | SWT.RIGHT);
		iconToolBar.setBounds(0, 0, 440, 23);
		
		ToolItem runItem = new ToolItem(iconToolBar, SWT.NONE);
		runItem.setToolTipText("Run");
		runItem.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/run.png"));
		
		Composite allTSContainer = new Composite(testSuiteComposite, SWT.BORDER);
		allTSContainer.setLayout(new GridLayout(1, false));
		GridData gd_allTSContainer = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_allTSContainer.heightHint = 222;
		gd_allTSContainer.widthHint = 440;
		allTSContainer.setLayoutData(gd_allTSContainer);
		
		
		//====
		
		Composite testsuiteCompo = new Composite(allTSContainer, SWT.NONE);
		testsuiteCompo.setLayout(new GridLayout(1, false));
		GridData gd_testsuiteCompo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_testsuiteCompo.heightHint = 125;
		gd_testsuiteCompo.widthHint = 432;
		testsuiteCompo.setLayoutData(gd_testsuiteCompo);
		
		Button btnTestSuiteName = new Button(testsuiteCompo, SWT.CHECK);
		btnTestSuiteName.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		btnTestSuiteName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		btnTestSuiteName.setText("Test Suite Name");
		
		table = new Table(testsuiteCompo, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		
		Label label = new Label(allTSContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 422;
		label.setLayoutData(gd_label);
		
		
		
		//========== UPTO THIS
		
		Composite runnerComposite = new Composite(parentComposite, SWT.BORDER);
		runnerComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder testRunConsole = new TabFolder(runnerComposite, SWT.NONE);
		
		TabItem tsConsole1 = new TabItem(testRunConsole, SWT.NONE);
		tsConsole1.setText("Console");
		
		Composite composite = new Composite(testRunConsole, SWT.NONE);
		tsConsole1.setControl(composite);

	}
}
