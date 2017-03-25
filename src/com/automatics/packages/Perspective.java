package com.automatics.packages;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class Perspective implements IPerspectiveFactory {

	public static String perspectiveID = "Automatics.perspective";
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new PerspectiveListener());
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new EditorListeners());
	}
}
