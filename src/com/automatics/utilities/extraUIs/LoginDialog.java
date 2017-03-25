package com.automatics.utilities.extraUIs;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import com.automatics.mongo.api.AutomaticsAPI;
import com.automatics.utilities.helpers.Utilities;

public class LoginDialog extends Dialog {
	private Composite headerComposite;
	private Label lblNewLabel;
	private Label lblNewLabel_1;
	private Composite bodyComposite;
	private Label label;
	private Label lblUsername;
	private Label label_1;
	private Label lblPassword;
	private Text usernameTB;
	private Text passwordTB;
	private Label errLabel;

	private static String USER_LOGIN_ADDRESS = "";
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public LoginDialog(Shell parentShell) {
		super(parentShell);
		
		try
		{
			/*Load properties file*/
			String configFile = "D:/KG00360770/ATT/Automatic_DC/Automatics/conf.ini"; //For Desktop use this
			//String configFile = System.getProperty("user.dir")+ "/../conf.ini"; //For Exe Use this
			File file = new File(configFile);
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			USER_LOGIN_ADDRESS = properties.getProperty("API_URL") + "/auth/" + properties.getProperty("PROJECT_NAME");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : LoginDialog()]" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 4;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		
		headerComposite = new Composite(container, SWT.NONE);
		headerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_headerComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_headerComposite.heightHint = 68;
		gd_headerComposite.widthHint = 427;
		headerComposite.setLayoutData(gd_headerComposite);
		
		lblNewLabel = new Label(headerComposite, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblNewLabel.setBounds(10, 10, 166, 21);
		lblNewLabel.setText("Login to AutoMaTics");
		
		lblNewLabel_1 = new Label(headerComposite, SWT.NONE);
		lblNewLabel_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		lblNewLabel_1.setBounds(10, 37, 135, 15);
		lblNewLabel_1.setText("Enter Username and Password");
		
		bodyComposite = new Composite(container, SWT.NONE);
		bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		label = new Label(bodyComposite, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label.setBounds(10, 23, 10, 15);
		label.setText("*");
		
		lblUsername = new Label(bodyComposite, SWT.NONE);
		lblUsername.setBounds(20, 23, 64, 15);
		lblUsername.setText("Username :");
		
		label_1 = new Label(bodyComposite, SWT.NONE);
		label_1.setText("*");
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_1.setBounds(10, 58, 10, 15);
		
		lblPassword = new Label(bodyComposite, SWT.NONE);
		lblPassword.setText("Password :");
		lblPassword.setBounds(20, 58, 64, 15);
		
		usernameTB = new Text(bodyComposite, SWT.BORDER);
		usernameTB.setBounds(90, 20, 344, 21);
		usernameTB.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event) {
				errLabel.setText("");
				errLabel.setVisible(false);
			}
		});
		
		passwordTB = new Text(bodyComposite, SWT.BORDER | SWT.PASSWORD);
		passwordTB.setBounds(90, 55, 344, 21);
		
		errLabel = new Label(bodyComposite, SWT.NONE);
		errLabel.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		errLabel.setForeground(SWTResourceManager.getColor(204, 0, 0));
		errLabel.setBounds(90, 82, 342, 15);
		errLabel.setText("Error Message Display");
		errLabel.setVisible(false);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Login",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 263);
	}

	 @Override
     protected void okPressed() 
	 {
		 boolean validated = false;
		 String username = usernameTB.getText();
		 String password = passwordTB.getText();
		 if(username.equals(""))
		 {
			 errLabel.setText("Please enter credentials");
			 errLabel.setVisible(true);
			 validated = false;
		 }
		 else
		 {
			 System.out.println(USER_LOGIN_ADDRESS);
			 validated = AutomaticsAPI.userAuthentication(USER_LOGIN_ADDRESS, username, password);
			 System.out.println(validated);
			 if(AutomaticsAPI.RESPONSE_CODE!=200)
			 {
				 errLabel.setText("Invalid username/password");
				 errLabel.setVisible(true);
			 }
			 	
		 }
		 //Call the user authentication
		 if(validated)
		 {
			 /*
			  * Set the username and password in the Utilities java file*/
			 Utilities.AUTOMATICS_USERNAME = username;
			 Utilities.AUTOMATICS_PASSWORD = password;
			 super.okPressed();
		 }
     }
}
