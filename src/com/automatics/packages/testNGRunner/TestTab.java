package com.automatics.packages.testNGRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.testng.eclipse.launch.TestNGMainTab;

/**
 * TestTab.
 * @author BD00487363
 *
 */
@SuppressWarnings("restriction")
public class TestTab extends AbstractLaunchConfigurationTab {
  
	private ILaunchConfigurationDialog fLaunchConfigurationDialog;

	private final TestNGMainTab testngLaunchTab;
	private Button runInUIThread;

	/**
	 * Constructor to create a new testNG test tab
	 */
	public TestTab() {
		this.testngLaunchTab = new TestNGMainTab();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		testngLaunchTab.createControl(parent);

		Composite composite = (Composite) getControl();
	createSpacer(composite);
		createRunInUIThreadGroup(composite);
	}

	/**
	 * createRunInUIThreadGroup.
	 * @param comp {@link Composite}
	 */
	private void createRunInUIThreadGroup(Composite comp) {
		runInUIThread = new Button(comp, SWT.CHECK);
		runInUIThread.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		runInUIThread.setText(PDEUIMessages.PDEJUnitLaunchConfigurationTab_Run_Tests_In_UI_Thread);
		GridDataFactory.fillDefaults().span(2, 0).grab(true, false).applyTo(runInUIThread);
	}

	/**
	 * createSpacer.
	 * @param comp {@link Composite}
	 */
	private void createSpacer(Composite comp) {
		Label label = new Label(comp, SWT.NONE);
		GridDataFactory.fillDefaults().span(3, 0).applyTo(label);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration config) {
		testngLaunchTab.initializeFrom(config);
		updateRunInUIThreadGroup(config);
	}

	/**
	 * updateRunInUIThreadGroup.
	 * @param config {@link ILaunchConfiguration}
	 */
	private void updateRunInUIThreadGroup(ILaunchConfiguration config) {
		boolean shouldRunInUIThread = true;
		try {
			shouldRunInUIThread = config.getAttribute(IPDELauncherConstants.RUN_IN_UI_THREAD, true);
		} catch (CoreException ce) {
		}
		runInUIThread.setSelection(shouldRunInUIThread);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		testngLaunchTab.performApply(config);
		boolean selection = runInUIThread.getSelection();
		config.setAttribute(IPDELauncherConstants.RUN_IN_UI_THREAD, selection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
	 */
	@Override
	public String getId() {
		return IPDELauncherConstants.TAB_TEST_ID;
	}

	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		testngLaunchTab.activated(workingCopy);
	}

	@Override
	public boolean canSave() {
		return testngLaunchTab.canSave();
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		testngLaunchTab.deactivated(workingCopy);
	}

	@Override
	public void dispose() {
		testngLaunchTab.dispose();
	}

	@Override
	public String getErrorMessage() {
		return testngLaunchTab.getErrorMessage();
	}

	@Override
	public Image getImage() {
		return testngLaunchTab.getImage();
	}

	@Override
	public String getMessage() {
		return testngLaunchTab.getMessage();
	}

	public String getName() {
		return testngLaunchTab.getName();
	}

	@Override
	public boolean isValid(ILaunchConfiguration config) {
		return testngLaunchTab.isValid(config);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		testngLaunchTab.setDefaults(config);
	}

	@Override
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		testngLaunchTab.setLaunchConfigurationDialog(dialog);
		this.fLaunchConfigurationDialog = dialog;
	}

}
