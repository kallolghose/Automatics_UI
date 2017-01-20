package com.automatics.packages.Views;

import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

import com.automatics.utilities.alltablestyles.ParametersEditing;
import com.automatics.utilities.gsons.testcase.ItrParams;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCParams;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

public class TestCaseParamView extends ViewPart {
	private static Table testcaseParamTable;
	private static TableViewer testcaseParamViewer;
	
	public TestCaseParamView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
		
		Composite buttonComposite = new Composite(parentComposite, SWT.BORDER);
		GridData gd_buttonComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_buttonComposite.widthHint = 585;
		gd_buttonComposite.heightHint = 21;
		buttonComposite.setLayoutData(gd_buttonComposite);
		
		Button btnNewButton = new Button(buttonComposite, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		btnNewButton.setBounds(0, 0, 75, 20);
		btnNewButton.setText("Add Parameter");
		
		testcaseParamViewer = new TableViewer(parentComposite, SWT.BORDER | SWT.FULL_SELECTION);
		testcaseParamTable = testcaseParamViewer.getTable();
		testcaseParamTable.setLinesVisible(true);
		testcaseParamTable.setHeaderVisible(true);
		testcaseParamTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// TODO Auto-generated method stub

	}
	
	public static void loadTestCaseParameters(TCGson tcGSON)
	{
		addColumns(tcGSON.tcParams.get(0));
		
		//Create ArrayList 
		
	}
	
	public static void addColumns(TCParams allParams)
	{
		Iterator<ItrParams> itrP = allParams.iterParams.iterator();
		while(itrP.hasNext())
		{
			ItrParams param = itrP.next();
			TableViewerColumn columnViewer = new TableViewerColumn(testcaseParamViewer, SWT.NONE);
			columnViewer.setLabelProvider(new ColumnLabelProvider());
			TableColumn tableColumn = columnViewer.getColumn();
			tableColumn.setResizable(true);
			tableColumn.setWidth(100);
			tableColumn.setText(param.iparamName);
			columnViewer.setEditingSupport(new ParametersEditing(testcaseParamViewer));
		}
	}
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
