package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

import com.automatics.utilities.alltablestyles.ParametersEditing;
import com.automatics.utilities.alltablestyles.TestCaseParamColumnLabelProvider;
import com.automatics.utilities.alltablestyles.TestParamsContentProvider;
import com.automatics.utilities.alltablestyles.TestParamsLabelProvider;
import com.automatics.utilities.gsons.testcase.ItrParams;
import com.automatics.utilities.gsons.testcase.TCGson;
import com.automatics.utilities.gsons.testcase.TCParams;
import com.automatics.utilities.gsons.testcase.TCStepsGSON;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;

public class TestCaseParamView extends ViewPart {
	
	public static String ID = "com.automatics.pacakges.Views.TestCaseParamView";
	
	private static Table testcaseParamTable;
	private static TableViewer testcaseParamViewer;
	private Button newTCParamBtn;
	
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
		
		newTCParamBtn = new Button(buttonComposite, SWT.NONE);
		newTCParamBtn.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		newTCParamBtn.setBounds(0, 0, 75, 20);
		newTCParamBtn.setText("Add Parameter");
		
		testcaseParamViewer = new TableViewer(parentComposite, SWT.FULL_SELECTION);
		testcaseParamViewer.setLabelProvider(new TestParamsLabelProvider());
		testcaseParamViewer.setContentProvider(new TestParamsContentProvider());
		testcaseParamTable = testcaseParamViewer.getTable();
		testcaseParamTable.setLinesVisible(true);
		testcaseParamTable.setHeaderVisible(true);
		testcaseParamTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		addEditorToColumnHeader(testcaseParamViewer);
		
		DragSource dragSource = new DragSource(testcaseParamTable, DND.DROP_COPY);
		setListeners();
		setDragListener(dragSource);
		// TODO Auto-generated method stub

	}
	
	public void setListeners()
	{
		newTCParamBtn.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/*
	private void addEditorSupport(TableViewer tv) {
		final CellNavigationStrategy cellNavigation = createCellNavigationStrategy(tv);
		final TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tv, new FocusCellOwnerDrawHighlighter(tv), cellNavigation);
		final ColumnViewerEditorActivationStrategy activationStrategy = createEditorActivationStrategy(tv);
		TableViewerEditor.create(tv, focusCellManager, activationStrategy, 
				ColumnViewerEditor.TABBING_HORIZONTAL 
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL 
				| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		tv.getColumnViewerEditor().addEditorActivationListener(createEditorActivationListener(tv));
	}*/
	
	private void addEditorToColumnHeader(TableViewer viewer)
	{
		try
		{
			TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer,  new FocusCellOwnerDrawHighlighter(viewer));
			ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(viewer){
				protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event){
					System.out.println(event.eventType);
					if(event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
					{
						EventObject source = event.sourceEvent;
						if(source instanceof MouseEvent && ((MouseEvent)source).button ==3)
						{
							return false;
						}
						return true;
					}
					else
					{
						System.out.println("hello");
					}
					return true;
				}
			};
			
			TableViewerEditor.create(viewer, focusCellManager, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL | 
				    ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | 
				    ColumnViewerEditor.TABBING_VERTICAL |
				    ColumnViewerEditor.KEYBOARD_ACTIVATION);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : addEditorToColumnHeader()] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setDragListener(DragSource source)
	{
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
			
			public void dragStart(DragSourceEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void dragSetData(DragSourceEvent event) {
				// TODO Auto-generated method stub
				try
				{
					DragSource ds = (DragSource) event.widget;
			        Table table = (Table) ds.getControl();
			        TableItem [] selection = table.getSelection();
			        event.data = "PARAMS__TEST{" + table.getColumn(selection.length-1).getText()+"}";
				}
				catch(Exception e)
				{
					System.out.println("[" + getClass().getName() + " : setDragListener()] - Exception :" + e.getMessage());
					e.printStackTrace();
				}
			}
			
			public void dragFinished(DragSourceEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private static void removeAllColumns()
	{
		while(testcaseParamViewer.getTable().getColumnCount() > 0)
		{
			testcaseParamViewer.getTable().getColumn(0).dispose();
		}
	}
	
	public static void loadTestCaseParameters(TCGson tcGSON)
	{
		removeAllColumns();
		addColumns(tcGSON.tcParams.get(0));
		
		//Create ArrayList 
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
		Iterator<TCParams> itr = tcGSON.tcParams.iterator();
		while(itr.hasNext())
		{
			TCParams params = itr.next();
			ArrayList<String> paramsArr = new ArrayList<String>();
			for(ItrParams p : params.iterParams)
			{
				paramsArr.add(p.iparamValue);
			}
			arrayList.add(paramsArr);
		}
		testcaseParamViewer.setInput(arrayList);
	}
	

	
	public static void addColumns(TCParams allParams)
	{
		Iterator<ItrParams> itrP = allParams.iterParams.iterator();
		int index = 0;
		while(itrP.hasNext())
		{
			ItrParams param = itrP.next();
			TableViewerColumn columnViewer = new TableViewerColumn(testcaseParamViewer, SWT.NONE);
			columnViewer.setLabelProvider(new TestCaseParamColumnLabelProvider(index));
			TableColumn tableColumn = columnViewer.getColumn();
			tableColumn.setResizable(true);
			tableColumn.setWidth(100);
			tableColumn.setText(param.iparamName);
			columnViewer.setEditingSupport(new ParametersEditing(testcaseParamViewer, index));
			index++; //Store index of each column
		}
	}
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
