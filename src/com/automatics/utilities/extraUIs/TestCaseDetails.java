package com.automatics.utilities.extraUIs;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

import com.automatics.packages.Views.TC_TS_List;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.helpers.Utilities;

public class TestCaseDetails extends Shell {
	private Text testcaseName;
	private Text testcaseDesc;
	private Button btnCancel, btnOk;
	private Combo applicationType;
	private HashMap<String, String> tcTypeMapping = Utilities.getTCApplicationTypeMapping();
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		/*try {
			Display display = Display.getDefault();
			TestCaseDetails shell = new TestCaseDetails(null);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public TestCaseDetails(Shell shell) {
		super(shell, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(null);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setBounds(0, 0, 444, 64);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 10, 127, 21);
		lblNewLabel.setText("Createa Test Case");
		
		Label lblCreateNewTest = new Label(composite_1, SWT.NONE);
		lblCreateNewTest.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblCreateNewTest.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		lblCreateNewTest.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateNewTest.setBounds(10, 37, 127, 15);
		lblCreateNewTest.setText("Create New Test Case");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(10, 84, 93, 15);
		lblNewLabel_1.setText("Test Case Name : ");
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setBounds(10, 120, 118, 15);
		lblNewLabel_2.setText("Test Case Description :");
		
		testcaseName = new Text(composite, SWT.BORDER);
		testcaseName.setBounds(136, 81, 298, 21);
		
		testcaseDesc = new Text(composite, SWT.BORDER | SWT.MULTI);
		testcaseDesc.setBounds(136, 120, 298, 114);
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setBounds(10, 255, 93, 15);
		lblNewLabel_3.setText("Application : ");
		
		applicationType = new Combo(composite, SWT.NONE);
		applicationType.setItems(new String[] {"Web Application", "Non Web Application"});
		applicationType.setBounds(136, 250, 298, 23);
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 311, 444, 2);
		
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(359, 332, 75, 25);
		btnCancel.setText("Cancel");
		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.setBounds(278, 332, 75, 25);
		btnOk.setText("Create");
		createContents();
		setListeners();
	}

	public void setListeners()
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
				String tcName = testcaseName.getText();
				String tcDesc = testcaseDesc.getText();
				String appType = applicationType.getText();
				appType = tcTypeMapping.get(appType);
				//Add data to GSON class and send it over
				TCGson tcData = new TCGson();
				tcData.tcName = tcName;
				tcData.tcDesc = tcDesc;
				tcData.tcType = appType;
				tcData.tcIdentifier = tcName;
				tcData.tcObjectMapLink = null;
				tcData.tcSteps = null;
				TC_TS_List.addTestCase(tcData);
				dispose();
			}
		});
	}
	
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Test Case");
		setSize(450, 411);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
