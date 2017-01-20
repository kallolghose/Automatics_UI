package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TestCaseParamColumnLabelProvider extends ColumnLabelProvider
{
	private int index;
	public TestCaseParamColumnLabelProvider(int index)
	{	
		this.index = index;
	}
	
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getText(Object element) {
		// TODO Auto-generated method stub
		return ((ArrayList<String>)element).get(index);
	}
}
