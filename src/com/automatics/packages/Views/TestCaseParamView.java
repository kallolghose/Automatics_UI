package com.automatics.packages.Views;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
	private Menu headerMenu = null;
	private static Listener tableHeaderListener = null;
	
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
		addBtn.setToolTipText("Add Entity");
		addBtn.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/add.png"));
		addBtn.setSelection(true);
		
		//Add menu items
		addMenu = new Menu(IconsToolBar);
		addItem1 = new MenuItem(addMenu, SWT.NONE);
		addItem1.setText("Add Test Parameter");
		addItem2 = new MenuItem(addMenu, SWT.NONE);
		addItem2.setText("Add Test Values");
		
		delBtn = new ToolItem(IconsToolBar, SWT.NONE);
		delBtn.setToolTipText("Delete Selected Row");
		delBtn.setSelection(true);
		delBtn.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/elcl16/delete_config.gif"));
		
		testcaseParamViewer = new TableViewer(parentComposite, SWT.FULL_SELECTION);
		testcaseParamViewer.setLabelProvider(new TestParamsLabelProvider());
		testcaseParamViewer.setContentProvider(new TestParamsContentProvider());
		testcaseParamTable = testcaseParamViewer.getTable();
		testcaseParamTable.setLinesVisible(true);
		testcaseParamTable.setHeaderVisible(true);
		testcaseParamTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		DragSource dragSource = new DragSource(testcaseParamTable, DND.DROP_COPY );
		
		setListeners();
		setDragListener(dragSource);
		
		/*Parameters Pop Up Menu*/
		headerMenu = new Menu(testcaseParamTable);
		testcaseParamTable.setMenu(headerMenu);
		
		/*
		 * Note : was not able to add the listener in setlistener() so adding the listener here;
		 */
		MenuItem editHeader = new MenuItem(headerMenu, SWT.NONE);
		editHeader.setText("Edit Parameter");
		editHeader.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				InputDialog editExitingParam = new InputDialog(getSite().getShell(), "Edit Test Case Param",
						 "Enter New Test Case Parameter Name : ", selectedTableColName, new ParamNameValidator());
				
				if(editExitingParam.open() == Window.OK)
				{
					if(selectedColIndex!=-1)
					{
						TableColumn col = testcaseParamTable.getColumn(selectedColIndex);
						col.setText(editExitingParam.getValue());
					}
				}
			}
		});
		
		MenuItem deleteHeader = new MenuItem(headerMenu, SWT.NONE);
		deleteHeader.setText("Delete Parameter");
		deleteHeader.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MessageDialog confirmDialog = new MessageDialog(getSite().getShell(), "Paramter Deletion", null, 
						"Are you sure you want to delete parameter : (" + selectedTableColName + ")", 
						MessageDialog.CONFIRM, new String[]{"Delete","Cancel"}, 0);
				switch(confirmDialog.open())
				{
				case 0:
					/*Logic to delete the test case*/
					if(selectedColIndex!=-1)
					{
						ArrayList<ArrayList<String>> tcParamTableIP = (ArrayList<ArrayList<String>>)testcaseParamViewer.getInput();
						//for(ArrayList<String> paramData : tcParamTableIP)
						for(int i=0;i<tcParamTableIP.size();i++)
						{
							ArrayList<String> paramData = tcParamTableIP.get(i);
							paramData.remove(selectedColIndex);
							tcParamTableIP.set(i, paramData);
						}
						TestCaseParamColumnLabelProvider.COLUMN_INDEX_DELETED = selectedColIndex;
						testcaseParamTable.getColumn(selectedColIndex).dispose();
						testcaseParamViewer.setInput(tcParamTableIP);
						testcaseParamViewer.refresh();
						TestCaseParamColumnLabelProvider.COLUMN_INDEX_DELETED = 999;
					}
					break;
				}
			}
		});
		
	}
	
	private String selectedTableColName = "";
	private int selectedColIndex = -1;
	public void setListeners()
	{
		try
		{
			/*Add listener to table header*/
			tableHeaderListener = new Listener() {
				public void handleEvent(Event event) {
					TableColumn col = (TableColumn) event.widget;
					selectedTableColName = col.getText();
					for(int i=0;i<testcaseParamTable.getColumnCount();i++)
					{
						if(testcaseParamTable.getColumn(i).getText().equals(selectedTableColName))
						{
							selectedColIndex = i;
							break;
						}
					}
				}
			};
			
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
			
			testcaseParamTable.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) 
				{
					Point pt = getSite().getShell().getDisplay().map(null, testcaseParamTable, new Point(event.x, event.y));
					Rectangle clientArea = testcaseParamTable.getClientArea();
					boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y + testcaseParamTable.getHeaderHeight());
					testcaseParamTable.setMenu(header ? headerMenu : null);
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
					ArrayList<ArrayList<String>> dataIP = (ArrayList<ArrayList<String>>)testcaseParamViewer.getInput();
					int selectedIndex = testcaseParamTable.getSelectionIndex();
					if(selectedIndex!=-1)
					{
						dataIP.remove(selectedIndex);
						testcaseParamViewer.refresh();
					}
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
						if(ipData!=null) //If some value then append the new value
						{
							for(ArrayList<String> data : ipData)
							{
								data.add("");
							}
						}
						else //If the value is not present then create the same
						{
							ArrayList<String> data = new ArrayList<String>();
							data.add("");
							ipData = new ArrayList<ArrayList<String>>();
							ipData.add(data);
							testcaseParamViewer.setInput(ipData);
							testcaseParamViewer.refresh();
						}
						
						int index = testcaseParamTable.getColumnCount();
						TableViewerColumn columnViewer = new TableViewerColumn(testcaseParamViewer, SWT.NONE);
						columnViewer.setLabelProvider(new TestCaseParamColumnLabelProvider(index));
						TableColumn tableColumn = columnViewer.getColumn();
						tableColumn.setResizable(true);
						tableColumn.setWidth(100);
						tableColumn.addListener(SWT.Selection, tableHeaderListener);
						tableColumn.setText(getNewTestParam.getValue());
						tableColumn.pack();
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
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + ":setListeners] - Exception :" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
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
				try
				{
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
	
	public static void removeAllColumns()
	{
		if(testcaseParamViewer!=null)
		{
			while(testcaseParamViewer.getTable().getColumnCount() > 0)
			{
				testcaseParamViewer.getTable().getColumn(0).dispose();
			}
			testcaseParamViewer.setInput(new ArrayList<ArrayList<String>>());
			testcaseParamViewer.refresh();
		}
	}
	
	public static void loadTestCaseParameters(TCGson tcGSON)
	{
		try
		{
			removeAllColumns();
			if(tcGSON.tcParams!=null && tcGSON.tcParams.size()>0)
			{
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
			tableColumn.setWidth(120);
			tableColumn.setText(param.iparamName);
			tableColumn.addListener(SWT.Selection, tableHeaderListener);
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

	public static void disposeTableColumns()
	{
		try
		{
			testcaseParamViewer.setInput(null);
			testcaseParamViewer.refresh();
			while(testcaseParamTable.getColumnCount()>0)
			{
				testcaseParamTable.getColumns()[0].dispose();
			}
		}
		catch(Exception e)
		{
			System.out.println("[TestCaseParamView : disposeTableColumns()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
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
