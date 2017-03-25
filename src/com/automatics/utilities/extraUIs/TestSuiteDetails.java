package com.automatics.utilities.extraUIs;

import java.util.List;

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
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.helpers.Utilities;

public class TestSuiteDetails extends Shell {
	private Text testsuiteName;
	private Button btnCancel, btnOk;
	private Text testsuiteDesc;
	private Label errLabel;
	
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
		super(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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
		
		testsuiteName = new Text(composite, SWT.BORDER);
		testsuiteName.setBounds(139, 81, 295, 21);
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 268, 444, 15);
		
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(359, 289, 75, 25);
		btnCancel.setText("Cancel");
		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.setBounds(278, 289, 75, 25);
		btnOk.setText("Create");
		
		Label lblTestSuiteDescriptions = new Label(composite, SWT.NONE);
		lblTestSuiteDescriptions.setBounds(10, 114, 124, 15);
		lblTestSuiteDescriptions.setText("Test Suite Description :");
		
		testsuiteDesc = new Text(composite, SWT.BORDER | SWT.MULTI);
		testsuiteDesc.setBounds(139, 111, 295, 139);
		
		errLabel = new Label(composite, SWT.NONE);
		errLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		errLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		errLabel.setBounds(10, 294, 263, 15);
		errLabel.setText("Error Label");
		errLabel.setVisible(false);
		setListeners();
		createContents();
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
				try
				{
					
					String tsName = testsuiteName.getText();

					errLabel.setVisible(false);
					
					final List<String> collValidityMessage=Utilities.validateEntityValues(tsName);
					for (String validityMessage : collValidityMessage) {
						errLabel.setText(validityMessage);
						errLabel.setVisible(true);
						return;
					}
					

					String tsDesc = testsuiteDesc.getText();
					final List<String> colldescriptionMessage=Utilities.validateDescriptionValue(tsDesc);
					for (String message : colldescriptionMessage) {
						errLabel.setText(message);
						errLabel.setVisible(true);
						return;
					}
					
					TSGson gson = new TSGson();
					gson.tsName = tsName;
					gson.tsDesc = tsDesc;
					gson.tsIdentifier = tsName;
					gson.lockedBy = Utilities.AUTOMATICS_USERNAME;
					gson.tsCreatedBy = Utilities.AUTOMATICS_USERNAME;
					gson.projectName = Utilities.DB_PROJECT_NAME;
					gson.tsTCLink = null;
					TC_TS_List.addTestSuite(gson);
					dispose();
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : setListeners()]" + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Test Suite");
		setSize(450, 356);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
