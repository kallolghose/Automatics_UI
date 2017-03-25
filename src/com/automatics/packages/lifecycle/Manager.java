package com.automatics.packages.lifecycle;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.automatics.utilities.extraUIs.*;

public class Manager 
{
	@PostContextCreate
	public void postContextCreate(IApplicationContext appContext, Display display)
	{
		try
		{
			final Shell shell = new Shell(SWT.SHELL_TRIM);
			LoginDialog loginDialog = new LoginDialog(shell);
			appContext.applicationRunning();
			
			setLocation(display, shell);
			if(loginDialog.open() != Window.OK)
			{
				System.exit(-1);
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - postContextCreate()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
 	}
	
	private void setLocation(Display display, Shell shell) 
	{
        Monitor monitor = display.getPrimaryMonitor();
        Rectangle monitorRect = monitor.getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
        int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
        shell.setLocation(x, y);
	}
}
