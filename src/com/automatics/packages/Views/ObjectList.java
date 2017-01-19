package com.automatics.packages.Views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.automatics.mongo.packages.AutomaticsDBObjectMapQueries;
import com.automatics.utilities.gsons.objectmap.OMGson;
import com.automatics.utilities.helpers.Utilities;
import com.automatics.utilities.save.model.ObjectMapSaveService;
import com.automatics.utilities.save.model.ObjectMapSaveTask;
import com.mongodb.DB;

public class ObjectList extends ViewPart {

	private Tree omListTree;
	
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
		loadOMList();
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
				//Get Specific OMs add load the same
				OMGson omGson = Utilities.getGSONFromJSON(AutomaticsDBObjectMapQueries.getOM(db,om).toString(), OMGson.class);
				//Add the same to save task
				ObjectMapSaveTask omTask = new ObjectMapSaveTask(omGson.omName,omGson);
				System.out.println("TasK : "  + omTask.getOmName());
				ObjectMapSaveService.getInstance().addSaveTask(omTask);
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
