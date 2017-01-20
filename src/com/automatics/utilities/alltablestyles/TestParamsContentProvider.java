package com.automatics.utilities.alltablestyles;

import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TestParamsContentProvider implements IStructuredContentProvider  {

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return ((List) inputElement).toArray();
	}
	
}
