package com.automatics.packages.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.*;
import org.eclipse.ui.handlers.*;

import com.automatics.packages.Editors.*;
import com.automatics.packages.Model.Task;

public class OpenEditor extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// TODO Auto-generated method stub
		/*IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();
        // get the selection
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection != null && selection instanceof IStructuredSelection) {
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                // if we had a selection lets open the editor
                if (obj != null) {
                        Task todo = (Task) obj;
                        TaskEditorInput input = new TaskEditorInput(0);
                        try {
                                page.openEditor(input, TestCaseEditor.ID);
                        } catch (PartInitException e) {
                                throw new RuntimeException(e);
                        }
                }
        }
        else
        {
        	TaskEditorInput input = new TaskEditorInput(123456L);
            try {
                    page.openEditor(input, TestCaseEditor.ID);
            } catch (PartInitException e) {
                    throw new RuntimeException(e);
            }
        }*/
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();
				TaskEditorInput input = new TaskEditorInput(0);
		        try {
		                page.openEditor(input, TestCaseEditor.ID);
		        		//page.openEditor(input, "automatics.editors.XMLEditor");
		        } catch (PartInitException e) {
		                throw new RuntimeException(e);
		        }
		
		return null;
	}

}
