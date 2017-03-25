package com.automatics.utilities.extraUIs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import com.automatics.packages.Views.ObjectList;
import com.automatics.utilities.gsons.objectmap.OMDetails;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.helpers.Utilities;

public class NewObjectMapWindow extends Shell {
	private Text objectmapName;
	private Text objectmapDesc;
	private Button btnCancel, btnCreate;
	private Label errLabel;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		/*try {
			Display display = Display.getDefault();
			NewObjectMapWindow shell = new NewObjectMapWindow(display);
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
	public NewObjectMapWindow(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(this, SWT.NONE);
		
		Composite composite = new Composite(parentComposite, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 444, 64);
		
		Label lblCreateNewObject = new Label(composite, SWT.NONE);
		lblCreateNewObject.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblCreateNewObject.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateNewObject.setBounds(10, 10, 150, 21);
		lblCreateNewObject.setText("Create Object Map");
		
		Label lblCreateNewObject_1 = new Label(composite, SWT.NONE);
		lblCreateNewObject_1.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		lblCreateNewObject_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblCreateNewObject_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateNewObject_1.setBounds(10, 37, 150, 15);
		lblCreateNewObject_1.setText("Create New Object Map");
		
		Label lblNewLabel = new Label(parentComposite, SWT.NONE);
		lblNewLabel.setBounds(10, 81, 103, 15);
		lblNewLabel.setText("Object Map Name :");
		
		objectmapName = new Text(parentComposite, SWT.BORDER);
		objectmapName.setBounds(148, 75, 286, 21);
		
		Label lblObjectMapDescription = new Label(parentComposite, SWT.NONE);
		lblObjectMapDescription.setBounds(10, 114, 136, 15);
		lblObjectMapDescription.setText("Object Map Description :");
		
		objectmapDesc = new Text(parentComposite, SWT.BORDER);
		objectmapDesc.setBounds(148, 108, 286, 114);
		
		Label label = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 302, 444, 2);
		
		btnCancel = new Button(parentComposite, SWT.NONE);
		btnCancel.setBounds(359, 325, 75, 25);
		btnCancel.setText("Cancel");
		
		btnCreate = new Button(parentComposite, SWT.NONE);
		btnCreate.setBounds(278, 325, 75, 25);
		btnCreate.setText("Create");
		
		errLabel = new Label(parentComposite, SWT.NONE);
		errLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		errLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		errLabel.setBounds(10, 330, 262, 15);
		errLabel.setText("Display Error Message");
		errLabel.setVisible(false);
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
		
		btnCreate.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				
				String omName = objectmapName.getText();
				String omDesc = objectmapDesc.getText();
				
				final List<String> collValidityMessage=Utilities.validateEntityValues(omName);
				for (String validityMessage : collValidityMessage) {
					errLabel.setText(validityMessage);
					errLabel.setVisible(true);
					return;
				}
				
				errLabel.setVisible(false);
				final List<String> colldescriptionMessage=Utilities.validateDescriptionValue(omDesc);
				for (String message : colldescriptionMessage) {
					errLabel.setText(message);
					errLabel.setVisible(true);
					return;
				}
				errLabel.setVisible(false);
				
				List<OMDetails> omDetailsList = new ArrayList<OMDetails>();
				
				OMDetails omDetail = new OMDetails();
				omDetail.pageName = "";
				omDetail.objName = "";
				omDetail.locatorInfo = "";
				omDetail.locatorType = "";
				
				omDetailsList.add(omDetail);
				
				OMGson omGson = new OMGson();
				omGson.omName = omName;
				omGson.omDesc = omDesc;
				omGson.omIdentifier = omName;
				omGson.lockedBy = Utilities.AUTOMATICS_USERNAME;
				omGson.omCreatedBy = Utilities.AUTOMATICS_USERNAME;
				omGson.projectName = Utilities.DB_PROJECT_NAME;
				omGson.omDetails = omDetailsList;
				
				ObjectList.createOjectMap(omGson);
				dispose();
			}
		});
	}
	
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Object Map");
		setSize(450, 400);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
