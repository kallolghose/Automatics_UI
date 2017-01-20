package com.automatics.packages.Views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.packages.Editors.ObjectMapEditor;
import com.automatics.packages.Editors.ObjectMapEditorInput;
import com.automatics.packages.Editors.TCEditor;
import com.automatics.packages.Editors.TestCaseEditorInput;
import com.automatics.packages.Model.ObjectMapTask;
import com.automatics.packages.Model.ObjectMapTaskService;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;
import com.mongodb.DB;

public class ObjectList extends ViewPart {

	private Tree omListTree;
	private ObjectMapTaskService service = ObjectMapTaskService.getInstance();
	
	public ObjectList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		omListTree = new Tree(composite, SWT.BORDER);
		// TODO Auto-generated method stub
		setListerners();
		loadOMList();
	}

	public void setListerners()
	{
		//Add listener to om list tree
		omListTree.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				try
				{
					//Get All Workbench
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					TreeItem selected[] = omListTree.getSelection();
					if(selected[0].getData("eltType").toString().equalsIgnoreCase("OBJECTMAP"))
					{
						ObjectMapEditorInput input = new ObjectMapEditorInput(selected[0].getText());
				        page.openEditor(input, ObjectMapEditor.ID);
					}
							
							
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " -  setListeners] : Exception " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}
	
	public void loadOMList()
	{
		try
		{
			TreeItem root = new TreeItem(omListTree, SWT.NONE);
			root.setText("App_Name");
			DB db = Utilities.getMongoDB();
			ArrayList<String> omList = AutomaticsDBObjectMapQueries.getAllOM(db);
			for(String om : omList)
			{
				TreeItem omTree = new TreeItem(root, SWT.NONE);
				omTree.setText(om);
				omTree.setData("eltType", "OBJECTMAP");
				
				//Get Specific OMs add load the same
				OMGson omGson = Utilities.getGSONFromJSON(AutomaticsDBObjectMapQueries.getOM(db,om).toString(), OMGson.class);
				//Add the same to save task
				ObjectMapSaveTask omTask = new ObjectMapSaveTask(omGson.omName,omGson);
				ObjectMapSaveService.getInstance().addSaveTask(omTask);
				
				//Add Editor Task
				if(service.getTaskByOmName(om) == null)
				{
					ObjectMapTask omEditorTask = new ObjectMapTask(om, omGson.omDesc, omGson.omIdentifier, omGson);
					service.addTasks(omEditorTask);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + ":loadOMList() ] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
		
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
