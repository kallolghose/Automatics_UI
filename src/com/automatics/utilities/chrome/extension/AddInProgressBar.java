package com.automatics.utilities.chrome.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class AddInProgressBar extends Shell {

	private static ProgressBar progressBar = null;
	private static Button okBtn = null;
	private static Display displayAync;
	
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			AddInProgressBar shell = new AddInProgressBar(display);
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
	public AddInProgressBar(Display display) {
		super(display, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		displayAync = display;
		
		setText("Validating Object Map");
		setSize(450, 164);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		
		Label lblValidating = new Label(composite, SWT.NONE);
		lblValidating.setBounds(10, 27, 104, 15);
		lblValidating.setText("Validating ....");
		
		progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setBounds(10, 57, 424, 15);
		
		
		okBtn = new Button(composite, SWT.NONE);
		okBtn.setEnabled(false);
		okBtn.setBounds(359, 100, 75, 25);
		okBtn.setText("OK");
		
		okBtn.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				dispose();
			}
		});
		createContents();
	}

	
	protected void createContents() {

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void initializeProgressBar(int maxVal)
	{
		progressBar.setMinimum(0);
		progressBar.setMaximum(maxVal);
	}
	
	public static void updateProgressBar(final int updateVal)
	{
		 displayAync.asyncExec(new Runnable(){
	         public void run() 
	         {
	     		 progressBar.setSelection(updateVal);
	         }
	     });
	}
	
	public static void updateButtonDisplay(final boolean show)
	{
		displayAync.asyncExec(new Runnable() {
			public void run() {
				okBtn.setEnabled(show);
			}
		});
	}
}
