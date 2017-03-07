package com.automatics.utilities.alltablestyles;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TestCaseParamColumnLabelProvider extends ColumnLabelProvider
{
	public static int COLUMN_INDEX_DELETED = 999;
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
		if(this.index>COLUMN_INDEX_DELETED)
		{
			this.index = this.index - 1;
		}
		System.out.println("Some Index : " + index);
		return ((ArrayList<String>)element).get(index);
	}
}
