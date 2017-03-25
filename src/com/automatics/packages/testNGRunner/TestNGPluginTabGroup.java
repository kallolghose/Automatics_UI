package com.automatics.packages.testNGRunner;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.pde.ui.launcher.ConfigurationTab;
import org.eclipse.pde.ui.launcher.PluginsTab;
import org.eclipse.pde.ui.launcher.TracingTab;

	/**
	 * TestNGPluginTabGroup.
	 * @author BD00487363
	 *
	 */
	public class TestNGPluginTabGroup extends
	AbstractLaunchConfigurationTabGroup  {

		/**
		 * TestNGPluginTabGroup.
		 */
		public TestNGPluginTabGroup() {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
		 */
		public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
			ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
							new TestTab(),
							new PluginTestNGMainTab(),
							new JavaArgumentsTab(), 
							new PluginsTab(false),
							new JavaArgumentsTab(),
							new PluginsTab(), 
							new TracingTab(),
							new ConfigurationTab(true), 
							new TracingTab(), 
			 				new EnvironmentTab(), 
			 				new CommonTab()
			          };
			 		setTabs(tabs);
					  } 
	    

}
