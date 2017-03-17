package com.automatics.packages;

import java.io.FileOutputStream;
import java.util.Properties;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.automatics.utilities.git.GitUtilities;
import com.automatics.utilities.helpers.Installables;
import com.automatics.utilities.helpers.Utilities;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() 
	{
		try
		{
			//Create logs location
			Installables.createLogFileAtLocation();
			
			GitUtilities gitUtil = new GitUtilities();
			GitUtilities.GIT_PROPERTY_PATH = "D:/KG00360770/ATT/Automatic_DC/Automatics/git_config.properties"; /*For Desktop use this*/
			//GitUtilities.GIT_PROPERTY_PATH = System.getProperty("user.dir")+ "/git_config.properties"; /*For Exe Use this*/
			Properties prop = gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
			
			prop.setProperty("LOCAL_PATH", ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/" + Utilities.PROJECT_NAME);
			prop.setProperty("REMOTE_PATH", prop.getProperty("REMOTE_PATH"));
			prop.setProperty("MONGO_DB_URL",  prop.getProperty("MONGO_DB_URL"));
			Utilities.MONGO_DB_URL = prop.getProperty("MONGO_DB_URL");
			prop.store(new FileOutputStream(GitUtilities.GIT_PROPERTY_PATH), null);
			gitUtil.setGitProperties(prop);
			if(gitUtil.cloneRepository())
			{
				System.out.println("Cloning Performed.");
			}
			else
			{
				System.out.println("Error Performed.");
			}
			
			IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
			configurer.setInitialSize(new Point(400, 300));
			configurer.setShowPerspectiveBar(true);
			configurer.setShowCoolBar(true);
			configurer.setShowStatusLine(false);
			configurer.setTitle("Automatics v0.1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
