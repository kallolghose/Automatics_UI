package com.automatics.packages.testNGRunner;

import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.internal.launching.launcher.LauncherUtils;
import org.eclipse.pde.internal.ui.launcher.ProgramBlock;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.pde.ui.launcher.AbstractLauncherTab;


/**
 * TestNGProgramBlock.
 * @author BD00487363
 *
 */
@SuppressWarnings({"restriction", "rawtypes", "unchecked"})
public class TestNGProgramBlock extends ProgramBlock {

	/**
	 * TestNGProgramBlock.
	 * @param tab {@link AbstractLauncherTab}
	 */
	public TestNGProgramBlock(AbstractLauncherTab tab) {
		super(tab);
		}
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		if (!LauncherUtils.requiresUI(config))
		
			config.setAttribute(IPDELauncherConstants.APPLICATION, ITestNGPluginLauncherConstants.CORE_TEST_APPLICATION);
		
		else
			super.setDefaults(config);
	
	}
	
	
@Override
protected String[] getApplicationNames() {
		TreeSet result = new TreeSet();
		result.add(ITestNGPluginLauncherConstants.TestNGProgramBlock_headless);
		String[] appNames = super.getApplicationNames();
		
		for (int i = 0; i < appNames.length; i++) {
			result.add(appNames[i]);
		}
		return appNames;
		}
		
	@Override
	protected void initializeApplicationSection(ILaunchConfiguration config) throws CoreException {
		
		String application = config.getAttribute(IPDELauncherConstants.APPLICATION, (String)null);
		
		if(ITestNGPluginLauncherConstants.CORE_TEST_APPLICATION.equals(application)) 
			fApplicationCombo.setText(ITestNGPluginLauncherConstants.TestNGProgramBlock_headless); 
		else
			super.initializeApplicationSection(config);
	
	}

	@Override
	protected void saveApplicationSection(ILaunchConfigurationWorkingCopy config) {
		if (fApplicationCombo.getText().equals(ITestNGPluginLauncherConstants.TestNGProgramBlock_headless)) {
			
			String appName = fApplicationCombo.isEnabled() ? ITestNGPluginLauncherConstants.CORE_TEST_APPLICATION : null;
			config.setAttribute(IPDELauncherConstants.APPLICATION, appName);
			config.setAttribute(IPDELauncherConstants.APP_TO_TEST, (String)null);
		}
		} 
		}
