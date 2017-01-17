package com.automatics.packages.run;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.*;

public class LaunchConfiguration extends AbstractLaunchConfigurationTabGroup {

	public LaunchConfiguration() {
		// TODO Auto-generated constructor stub
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		// TODO Auto-generated method stub
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JavaArgumentsTab(),
				new JavaJRETab(),
				new JavaClasspathTab(), 
				new CommonTab()
		};
		setTabs(tabs);

	}

}
