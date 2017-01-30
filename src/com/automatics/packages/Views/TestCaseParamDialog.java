package com.automatics.packages.Views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class TestCaseParamDialog extends Dialog {
	private Text testParamName;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TestCaseParamDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE);
	
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblNewLabel.setText("*");
		
		Label lblEnterNewTest = new Label(container, SWT.NONE);
		lblEnterNewTest.setText("Enter new test Parameter Name :");
		new Label(container, SWT.NONE);
		
		testParamName = new Text(container, SWT.BORDER);
		testParamName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(353, 132);
	}

}
