package com.automatics.utilities.extraUIs;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import com.automatics.packages.Views.TC_TS_List;
import com.automatics.utilities.elements.Project;

public class ProjectDetails extends Shell {
	private Text projectName;
	private Text projectDescription;
	private Button btnCancel,btnOk;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		/*try {
			Display display = Display.getDefault();
			ProjectDetails shell = new ProjectDetails(display);
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
	public ProjectDetails(Shell parent) {
		super(parent, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(null);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setBounds(0, 0, 445, 64);
		
		Label lblCreateProject = new Label(composite_1, SWT.NONE);
		lblCreateProject.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblCreateProject.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateProject.setBounds(10, 10, 106, 21);
		lblCreateProject.setText("Create Project");
		
		Label lblCreateAAutomatics = new Label(composite_1, SWT.NONE);
		lblCreateAAutomatics.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblCreateAAutomatics.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		lblCreateAAutomatics.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateAAutomatics.setBounds(10, 37, 168, 15);
		lblCreateAAutomatics.setText("Create a Automatics Project");
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 84, 87, 15);
		lblNewLabel.setText("Project Name :");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(10, 120, 106, 15);
		lblNewLabel_1.setText("Project Description :");
		
		projectName = new Text(composite, SWT.BORDER);
		projectName.setBounds(126, 81, 309, 21);
		
		projectDescription = new Text(composite, SWT.BORDER | SWT.MULTI);
		projectDescription.setBounds(126, 120, 309, 86);
		
		Label Seperator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		Seperator.setBounds(0, 274, 445, 2);
		
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(360, 293, 75, 25);
		btnCancel.setText("Cancel");
		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.setBounds(279, 293, 75, 25);
		btnOk.setText("Create");
		addListeners();
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Project");
		setSize(451, 370);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void addListeners()
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
				Project project =  new Project();
				project.setProjectName(projectName.getText());
				project.setProjectDescription(projectDescription.getText());
				project.setCreateBy(System.getProperty("user.name"));
				project.setDateofCreation(new Date());
				project.setModifiedBy(System.getProperty("user.name"));
				project.setModificationDate(new Date());
				TC_TS_List.addProject(project);
				dispose();
			}
		});
	}
}
