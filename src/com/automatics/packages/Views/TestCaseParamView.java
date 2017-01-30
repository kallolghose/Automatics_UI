package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

import com.automatics.packages.Model.TestCaseTask;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class TestCaseParamView extends ViewPart {
	
	public static String ID = "com.automatics.pacakges.Views.TestCaseParamView";
	
	private String draggedColumnName = "";
	private static Table testcaseParamTable;
	private static TableViewer testcaseParamViewer;
	private ToolItem addBtn, delBtn;
	private Menu addMenu;
	private MenuItem addItem1,addItem2;
	private ToolBar IconsToolBar;
	public static TestCaseTask currentTask;
	
	public TestCaseParamView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
		
		Composite buttonComposite = new Composite(parentComposite, SWT.NONE);
		GridData gd_buttonComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_buttonComposite.widthHint = 585;
		gd_buttonComposite.heightHint = 25;
		buttonComposite.setLayoutData(gd_buttonComposite);
		
		IconsToolBar = new ToolBar(buttonComposite, SWT.FLAT | SWT.RIGHT);
		IconsToolBar.setBounds(0, 0, 205, 25);
		
		addBtn = new ToolItem(IconsToolBar, SWT.DROP_DOWN);
		addBtn.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		addBtn.setSelection(true);
		
		//Add menu items
		addMenu = new Menu(IconsToolBar);
		addItem1 = new MenuItem(addMenu, SWT.NONE);
		addItem1.setText("Add Test Parameter");
		addItem2 = new MenuItem(addMenu, SWT.NONE);
		addItem2.setText("Add Test Values");
		
		delBtn = new ToolItem(IconsToolBar, SWT.NONE);
		delBtn.setSelection(true);
		delBtn.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		
		testcaseParamViewer = new TableViewer(parentComposite, SWT.FULL_SELECTION);
		testcaseParamViewer.setLabelProvider(new TestParamsLabelProvider());
		testcaseParamViewer.setContentProvider(new TestParamsContentProvider());
		testcaseParamTable = testcaseParamViewer.getTable();
		testcaseParamTable.setLinesVisible(true);
		testcaseParamTable.setHeaderVisible(true);
		testcaseParamTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		//addEditorToColumnHeader(testcaseParamViewer);
		
		DragSource dragSource = new DragSource(testcaseParamTable, DND.DROP_COPY );
		setListeners();
		setDragListener(dragSource);
		// TODO Auto-generated method stub

	}
	
	public void setListeners()
	{
		try
		{
			//Add listener to get the column name
			testcaseParamTable.addListener(SWT.MouseDown, new Listener() {
				
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					try
					{
						Point p = new Point(event.x, event.y);
						ViewerCell cell = testcaseParamViewer.getCell(p);
						if(cell!=null)
						{
							draggedColumnName = testcaseParamTable.getColumn(cell.getColumnIndex()).getText();
						}
					}
					catch(Exception e)
					{
						System.out.println("[" + getClass().getName() + " : SetListeners()] - Exception : " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			
			addBtn.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					 // TODO Auto-generated method stub
					 if (event.detail == SWT.ARROW) 
					 {
						  Rectangle rect = addBtn.getBounds();
					      Point pt = new Point(rect.x, rect.y + rect.height);
					      pt = IconsToolBar.toDisplay(pt);
					      addMenu.setLocation(pt.x, pt.y);
					      addMenu.setVisible(true);
					 }
				}
			});
			
			delBtn.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					
				}
			});
			
			addItem1.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					//Create a new column for the table
					InputDialog getNewTestParam = new InputDialog(getSite().getShell(), "New Test Case Param",
														 "Enter New Test Case Parameter Name : ", "", new ParamNameValidator());
					
					if(getNewTestParam.open() == Window.OK)
					{
						ArrayList<ArrayList<String>> ipData = (ArrayList<ArrayList<String>>)testcaseParamViewer.getInput();
						for(ArrayList<String> data : ipData)
						{
							data.add("");
						}
						
						int index = testcaseParamTable.getColumnCount();
						TableViewerColumn columnViewer = new TableViewerColumn(testcaseParamViewer, SWT.NONE);
						columnViewer.setLabelProvider(new TestCaseParamColumnLabelProvider(index));
						TableColumn tableColumn = columnViewer.getColumn();
						tableColumn.setResizable(true);
						tableColumn.setWidth(100);
						tableColumn.setText(getNewTestParam.getValue());
						columnViewer.setEditingSupport(new ParametersEditing(testcaseParamViewer, index));
						testcaseParamViewer.refresh();
					}
				}
			});
			
			addItem2.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					//Add a new row to the table
					ArrayList<ArrayList<String>> data = (ArrayList<ArrayList<String>>)testcaseParamViewer.getInput();
					int colCount = testcaseParamTable.getColumnCount();
					
					ArrayList<String> newData = new ArrayList<String>();
					for(int i=0;i<colCount;i++)
					{
						newData.add("");
					}
					data.add(newData);
					testcaseParamViewer.refresh();
					testcaseParamViewer.editElement(newData, 0);
				}
			});
			
			getSite().getPage().addPartListener(new IPartListener2() {
				
				public void partVisible(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
				
				public void partOpened(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
				
				public void partInputChanged(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
				
				public void partHidden(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
				
				public void partDeactivated(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					if(currentTask!=null)
					{
						testcaseParamTable.forceFocus();
						TCGson tcGSON = currentTask.getTcGson();
						tcGSON.tcParams = getAllTestCaseParameters();
						currentTask.setTcGson(tcGSON);
					}
					
				}
				
				public void partClosed(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
				
				public void partBroughtToTop(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
				
				public void partActivated(IWorkbenchPartReference partRef) {
					// TODO Auto-generated method stub
					
				}
			});
			
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + ":setListeners] - Exception :" + e.getMessage());
			e.printStackTrace();
		}
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

					if(event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
					{
						EventObject source = event.sourceEvent;
						if(source instanceof MouseEvent && ((MouseEvent)source).button ==3)
						{
							return false;
						}
						return true;
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
					//DragSource ds = (DragSource) event.widget;
			        //Table table = (Table) ds.getControl();
			        //TableItem [] selection = table.getSelection();
			        event.data = "PARAMS__TEST{" + draggedColumnName +"}";
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
		try
		{
			if(tcGSON.tcParams!=null)
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
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseParamView - loadTestCaseParameters() ] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
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
	
	public static List<TCParams> getAllTestCaseParameters()
	{
		try
		{
			List<TCParams> params = new ArrayList<TCParams>();
			ArrayList<ArrayList<String>> iParams = (ArrayList<ArrayList<String>>)testcaseParamViewer.getInput();
			if(iParams!=null)
			{
				for(int i=0;i<iParams.size();i++)
				{
					ArrayList<String> iteration = iParams.get(i);
					List<ItrParams> storeItrVals = new ArrayList<ItrParams>();
					for(int k=0;k<iteration.size();k++)
					{
						ItrParams itrparams = new ItrParams();
						itrparams.iparamName = testcaseParamTable.getColumn(k).getText();
						itrparams.iparamValue = iteration.get(k);
						storeItrVals.add(itrparams);
					}
					
					TCParams newParam = new TCParams();
					newParam.iterNum = (i+1);
					newParam.iterParams = storeItrVals;
					params.add(newParam);
				}
				return params;
			}
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseParamView : getAllTestCaseParameters()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}

class ParamNameValidator implements IInputValidator
{

	public String isValid(String newText) {
		// TODO Auto-generated method stub
		String inValidPattern = ".*[!@#\\$%^&\\*\\(\\)-\\+=\\{\\}\\[\\]\\|\\.]+.*";
		if(newText.equalsIgnoreCase(""))
		{
			return "Please enter any value !!";
		}
		
		if(newText.matches(inValidPattern))
		{
			return "Cannot Contain Special Characters !!";
		}
		
		return null;
	}
	
}
