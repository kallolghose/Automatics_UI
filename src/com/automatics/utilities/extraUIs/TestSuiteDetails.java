package com.automatics.utilities.extraUIs;

import javax.swing.text.html.ListView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import com.automatics.packages.Views.TC_TS_List;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testsuite.TSGson;

public class TestSuiteDetails extends Shell {
	private Text testSuiteName;
	private Text testCaseFilter;
	private Button btnCancel, btnOk;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			TestSuiteDetails shell = new TestSuiteDetails(null);
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
	public TestSuiteDetails(Shell shell) {
		super(shell, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(null);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setBounds(0, 0, 444, 64);
		
		Label lblCreateTestSuite = new Label(composite_1, SWT.NONE);
		lblCreateTestSuite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateTestSuite.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblCreateTestSuite.setBounds(10, 10, 120, 21);
		lblCreateTestSuite.setText("Create Test Suite");
		
		Label lblCreateANew = new Label(composite_1, SWT.NONE);
		lblCreateANew.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		lblCreateANew.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblCreateANew.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateANew.setBounds(10, 37, 162, 15);
		lblCreateANew.setText("Create a new Test Suite");
		
		Label lblTestSuiteName = new Label(composite, SWT.NONE);
		lblTestSuiteName.setBounds(10, 84, 101, 15);
		lblTestSuiteName.setText("Test Suite Name :");
		
		testSuiteName = new Text(composite, SWT.BORDER);
		testSuiteName.setBounds(112, 81, 322, 21);
		
		Label lblTestCases = new Label(composite, SWT.NONE);
		lblTestCases.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		lblTestCases.setBounds(10, 117, 57, 15);
		lblTestCases.setText("Test Cases :");
		
		testCaseFilter = new Text(composite, SWT.BORDER);
		testCaseFilter.setBounds(10, 133, 424, 21);
		
		Composite composite_2 = new Composite(composite, SWT.BORDER);
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_2.setBounds(10, 160, 424, 168);
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 334, 444, 15);
		
		Label lblAddExistingTest = new Label(composite, SWT.NONE);
		lblAddExistingTest.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		lblAddExistingTest.setFont(SWTResourceManager.getFont("Segoe UI", 6, SWT.BOLD));
		lblAddExistingTest.setBounds(73, 119, 191, 13);
		lblAddExistingTest.setText("(Add Existing Test Case to Test Suite)");
		
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(359, 355, 75, 25);
		btnCancel.setText("Cancel");
		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.setBounds(278, 355, 75, 25);
		btnOk.setText("Create");
		addListenerToComponents();
		createContents();
	}
	
	public void addListenerToComponents()
	{
		btnCancel.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		
		btnOk.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				String tsName = testSuiteName.getText();
				TSGson gson = new TSGson();
				gson.tsName = tsName;
				gson.tsDesc = tsName;
				gson.tsIdentifier = tsName;
				gson.tsTCLink = null;
				TC_TS_List.addTestSuite(gson);
				dispose();
			}
		});
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Test Suite");
		setSize(450, 427);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
