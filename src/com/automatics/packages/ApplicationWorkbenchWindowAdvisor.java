package com.automatics.packages;

import java.util.Properties;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.automatics.utilities.git.GitUtilities;
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
		GitUtilities gitUtil = new GitUtilities();
		GitUtilities.GIT_PROPERTY_PATH = "D:/KG00360770/ATT/Automatic_DC/Automatics/git_config.properties";
		Properties prop = gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		//Set this to something more concrete System.getProperty("user.dir") for now keep fixed
		prop.setProperty("LOCAL_PATH", ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		gitUtil.setGitProperties(prop);
		//if(gitUtil.cloneRepository()
		if(true)
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
		System.out.println("User Dir : " + ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());		
	}
}
