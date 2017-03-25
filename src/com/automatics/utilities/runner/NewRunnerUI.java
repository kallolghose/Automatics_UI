package com.automatics.utilities.runner;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;





/*
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
*/
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.AllServiceListener;

import sun.security.krb5.Realm;

import com.automatics.packages.api.handlers.TestSuiteAPIHandler;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.gsons.testsuite.TSTCParamGson;
import com.automatics.utilities.helpers.Utilities;

/**
 * NewRunnerUI.
 * @author BD00487363
 *
 */
public class NewRunnerUI {
	//private DataBindingContext m_bindingContext;

	protected Shell shlRunner;
	private List<String> allTestSuites;
	private Text text;
	private Text text_1;
	private TreeItem trtmNewTreeitem;
	private Tree remoteTable;
	private Tree localHostTable;
	private Text runnerConsole;
	/**
	 * Launch the application.
	 * @param args
	 */
	
	public static void main(String[] args) {
		Display display = Display.getDefault();
		/*Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
		*/		try {
					NewRunnerUI window = new NewRunnerUI();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			//}
		//});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlRunner.open();
		shlRunner.layout();
		while (!shlRunner.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shlRunner = new Shell();
		shlRunner.setSize(555, 582);
		shlRunner.setText("Runner");
		shlRunner.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite parentComposite = new Composite(shlRunner, SWT.NONE);
		parentComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite suitedetailsComposite = new Composite(parentComposite, SWT.NONE);
		GridLayout gl_suitedetailsComposite = new GridLayout(1, true);
		gl_suitedetailsComposite.marginHeight = 0;
		gl_suitedetailsComposite.marginWidth = 0;
		suitedetailsComposite.setLayout(gl_suitedetailsComposite);
		
		Composite composite = new Composite(suitedetailsComposite, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ToolBar iConToolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		iConToolBar.setBounds(0, 0, 538, 29);
		
		ToolItem run = new ToolItem(iConToolBar, SWT.NONE);
		run.setToolTipText("Run Selected Test Case");
		run.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/run_icon.png"));
		
		ToolItem refresh = new ToolItem(iConToolBar, SWT.NONE);
		refresh.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/Refresh.png"));
		refresh.setToolTipText("Refresh");
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				AutomaticsDBTestSuiteQueries.deleteTS(Utilities.getMongoDB(), tsName)
				createTestSuiteTable(remoteTable);
				createTestSuiteTable(localHostTable);
			}
		});
		
		final TabFolder tabFolder = new TabFolder(suitedetailsComposite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
//		ArrayList<String>collList=	AutomaticsDBTestSuiteQueries.getAllTS(Utilities.getMongoDB());
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Remote");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_1);
		 GridLayout gl_composite_1 = new GridLayout(1, false);
		 gl_composite_1.marginWidth = 0;
		 gl_composite_1.marginHeight = 0;
		 composite_1.setLayout(gl_composite_1);
		 //composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		 remoteTable = new Tree(composite_1, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL);
		 remoteTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		 remoteTable.setLinesVisible(true);
		remoteTable.setHeaderVisible(true);
		
		TreeColumn trclmnNewColumn = new TreeColumn(remoteTable, SWT.NONE);
		trclmnNewColumn.setWidth(121);
		trclmnNewColumn.setText("TestSuite/TestCase");
		
		
		TreeColumn trclmnColumn = new TreeColumn(remoteTable, SWT.NONE);
		trclmnColumn.setWidth(77);
		trclmnColumn.setText("Exe_Platform");
		
		TreeColumn trclmnColumn_1 = new TreeColumn(remoteTable, SWT.NONE);
		trclmnColumn_1.setWidth(78);
		trclmnColumn_1.setText("Exe_Type");
		
		TreeColumn trclmnColumn_2 = new TreeColumn(remoteTable, SWT.NONE);
		trclmnColumn_2.setWidth(80);
		trclmnColumn_2.setText("Run_On");
		
		TreeColumn trclmnColumn_3 = new TreeColumn(remoteTable, SWT.NONE);
		trclmnColumn_3.setWidth(81);
		trclmnColumn_3.setText("Thread-Count");
		
		TreeColumn trclmnColumn_4 = new TreeColumn(remoteTable, SWT.NONE);
		trclmnColumn_4.setWidth(88);
		trclmnColumn_4.setText("Column5");
		
		createTestSuiteTable(remoteTable);
		
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("Localhost");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(composite_2);
		 GridLayout gl_composite_2 = new GridLayout(1, false);
		 gl_composite_2.marginWidth = 0;
		 gl_composite_2.marginHeight = 0;
		 composite_2.setLayout(gl_composite_2);
		
		 localHostTable = new Tree(composite_2,SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL);
		 localHostTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		 localHostTable.setLinesVisible(true);
		localHostTable.setHeaderVisible(true);
		
		TreeColumn trclmnNewColumn_1 = new TreeColumn(localHostTable, SWT.NONE);
		trclmnNewColumn_1.setWidth(113);
		trclmnNewColumn_1.setText("TestSuite/TestCase");
		
		TreeColumn trclmnColumn_5 = new TreeColumn(localHostTable, SWT.NONE);
		trclmnColumn_5.setWidth(73);
		trclmnColumn_5.setText("Exe_Platform");
		
		TreeColumn trclmnColumn_6 = new TreeColumn(localHostTable, SWT.NONE);
		trclmnColumn_6.setWidth(77);
		trclmnColumn_6.setText("Exe_Type");
		
		TreeColumn trclmnColumn_7 = new TreeColumn(localHostTable, SWT.NONE);
		trclmnColumn_7.setWidth(85);
		trclmnColumn_7.setText("Run_On");
		
		TreeColumn trclmnColumn_8 = new TreeColumn(localHostTable, SWT.NONE);
		trclmnColumn_8.setWidth(81);
		trclmnColumn_8.setText("Thread-Count");
		
		final TreeColumn trclmnColumn_9 = new TreeColumn(localHostTable, SWT.NONE);
		trclmnColumn_9.setWidth(97);
		trclmnColumn_9.setText("Column5");
		trclmnColumn_9.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
			    // Clean up any previous editor control
				final TreeEditor editor = new TreeEditor(trclmnColumn_9.getParent());
                Control oldEditor = editor.getEditor();
                if (oldEditor != null) oldEditor.dispose();

                // Identify the selected row
                TreeItem item = (TreeItem)e.item;
                if (item == null) return;

                // The control that will be the editor must be a child of the Tree
                Text newEditor = new Text(trclmnColumn_9.getParent(), SWT.FULL_SELECTION);
                newEditor.setText(item.getText());
                newEditor.addModifyListener(new ModifyListener() {
                        public void modifyText(ModifyEvent e) {
                                Text text = (Text)editor.getEditor();
                                editor.getItem().setText(text.getText());
                        }
			});
                newEditor.selectAll();
                newEditor.setFocus();
                editor.setEditor(newEditor, item);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		
		
		TabItem tbtmNewItem_2 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_2.setText("Settings");
		
		final Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_2.setControl(composite_3);
			    Label lblNewLabel = new Label(composite_3, SWT.NONE);
			    lblNewLabel.setBounds(10, 96, 112, 31);
			    lblNewLabel.setText("Re-run Failed Scripts:");
		final Button btnRadioButton = new Button(composite_3, SWT.RADIO);
		btnRadioButton.setBounds(136, 85, 38, 42);
		btnRadioButton.setText("Yes");
		
		final Button btnRadioButton_1 = new Button(composite_3, SWT.RADIO);
		btnRadioButton_1.setBounds(193, 85, 48, 42);
		btnRadioButton_1.setText("No");
		new Label(composite_3, SWT.NONE);
		Label lblHowManyTimes = new Label(composite_3, SWT.NONE);
		lblHowManyTimes.setBounds(10, 133, 96, 24);
		lblHowManyTimes.setText("How Many Times:");		
		text = new Text(composite_3, SWT.BORDER);
		text.setBounds(136, 133, 48, 16);
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		Label lblSaveReports = new Label(composite_3, SWT.NONE);
		lblSaveReports.setBounds(10, 193, 70, 24);
		lblSaveReports.setText("Save Reports:");
		
		final Button btnCheckButton = new Button(composite_3, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCheckButton.setBounds(141, 179, 48, 42);
		btnCheckButton.setText("Disk");
		
		final Button btnCheckButton_1 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_1.setBounds(223, 191, 38, 24);
		btnCheckButton_1.setText("DB");
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		
		final Button btnCheckButton_2 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCheckButton_2.setBounds(141, 227, 48, 31);
		btnCheckButton_2.setText("PDF");
		
		final Button btnCheckButton_3 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_3.setBounds(223, 236, 54, 16);
		btnCheckButton_3.setText("HTML");
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		
		Button btnNewButton = new Button(composite_3, SWT.NONE);
		btnNewButton.setBounds(141, 287, 79, 31);
		btnNewButton.setText("Reset");
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);
		Button btnNewButton_1 = new Button(composite_3, SWT.NONE);
		btnNewButton_1.setBounds(10, 27, 96, 23);
		btnNewButton_1.setText("Date And Time");
		
		final Label lblNewLabel_1 = new Label(composite_3, SWT.NONE);
		lblNewLabel_1.setBounds(136, 27, 222, 27);
		btnNewButton_1.addSelectionListener (new SelectionAdapter () {
			    public void widgetSelected (SelectionEvent e) {
			      final Shell dialog = new Shell (shlRunner, SWT.DIALOG_TRIM);
			      dialog.setLayout (new GridLayout (5, false));

			      final DateTime calendar = new DateTime (dialog, SWT.CALENDAR | SWT.BORDER);
			      final DateTime date = new DateTime (dialog, SWT.DATE | SWT.SHORT);
			      final DateTime time = new DateTime (dialog, SWT.TIME | SWT.SHORT);

			      new Label (composite_3, SWT.NONE);
			      new Label (composite_3, SWT.NONE);
			      Button ok = new Button (dialog, SWT.PUSH);
			      ok.setText ("OK");
			      ok.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false));
			      ok.addSelectionListener (new SelectionAdapter () {
			        public void widgetSelected (SelectionEvent e) {
			        	lblNewLabel_1.setText(+ (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ()+" "+" "+"Time:"+ time.getHours () + ":" + time.getMinutes ());
			          System.out.println ("Calendar date selected (MM/DD/YYYY) = " + (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ());
			          System.out.println ("Date selected (MM/YYYY) = " + (date.getMonth () + 1) + "/" + date.getYear ());
			          System.out.println ("Time selected (HH:MM) = " + time.getHours () + ":" + time.getMinutes ());
			          dialog.close ();
			          
			        }
			      });
			      dialog.setDefaultButton (ok);
			      dialog.pack ();
			      dialog.open ();
			    }
			  });
		 
		shlRunner.setTabList(new Control[]{parentComposite});
		//m_bindingContext = initDataBindings();
		createTestSuiteTable(localHostTable);

		editTreeTable(remoteTable);
		editTreeTable(localHostTable);
		getChecked(remoteTable);
		getChecked(localHostTable);
		
		Composite composite_4 = new Composite(suitedetailsComposite, SWT.NONE);
		GridLayout gl_composite_4 = new GridLayout(1, false);
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		composite_4.setLayout(gl_composite_4);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabFolder tabFolder_1 = new TabFolder(composite_4, SWT.NONE);
		tabFolder_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmConsole = new TabItem(tabFolder_1, SWT.NONE);
		tbtmConsole.setText("Console");
		
		Composite composite_5 = new Composite(tabFolder_1, SWT.NONE);
		tbtmConsole.setControl(composite_5);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		runnerConsole = new Text(composite_5, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		runnerConsole.setEditable(false);
		
		btnNewButton.addSelectionListener(new SelectionAdapter()
		  {
		    @Override
		    public void widgetSelected(SelectionEvent e)
		    {
		    	btnCheckButton.setSelection(false);
		    	btnCheckButton_1.setSelection(false);
		    	btnCheckButton_2.setSelection(false);
		    	btnCheckButton_3.setSelection(false);
		    	btnRadioButton.setSelection(false);
		    	btnRadioButton_1.setSelection(false);
		    	text.setText("");
		    }
		});

		
		run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				int index = tabFolder.getSelectionIndex();
				if(index==0)
					genarateTestNGfile(remoteTable);
				else
					genarateTestNGfile(localHostTable);
			}
		});
		
	}

	/**
	 * createTestSuiteTable.
	 * @param table {@link Tree}
	 */
	private void createTestSuiteTable( final Tree table) 
    {      
           //Dispose all elements
		TreeItem items[] = table.getItems();
		for(int i=0;i<items.length;i++)
		{
		    items[0].dispose();
		}
		TSGson tsGsons[] = TestSuiteAPIHandler.getInstance().getAllTestSuites();
		boolean checked=false;
		for (TSGson tsGson : tsGsons) 
		{
			trtmNewTreeitem = new TreeItem(table, SWT.NONE|SWT.MULTI);
		    trtmNewTreeitem.setText(new String[] { "" +tsGson.tsName, "", "","","","" });
		    trtmNewTreeitem.setData("EltType","TESTSUITE");
		
		    if(tsGson.tsTCLink==null)
		            continue;
		    for(TSTCGson tsTCGson : tsGson.tsTCLink) 
		    {
		    	TreeItem trtmTestcases = new TreeItem(trtmNewTreeitem, SWT.NONE|SWT.MULTI);
		        trtmTestcases.setText(new String[] {tsTCGson.tcName, tsTCGson.tcParams.get(0)!=null ?tsTCGson.tcParams.get(0).tcparamValue:"", tsTCGson.tcParams.get(1)!=null ?tsTCGson.tcParams.get(1).tcparamValue:"",tsTCGson.tcParams.get(2)!=null ?tsTCGson.tcParams.get(2).tcparamValue:"",tsTCGson.tcParams.get(3)!=null ?tsTCGson.tcParams.get(3).tcparamValue:"",tsTCGson.tcParams.get(4)!=null ?tsTCGson.tcParams.get(4).tcparamValue:"" });
		        trtmTestcases.setData("EltType","TESTCASE");

                table.setSelection(trtmNewTreeitem);
                if(checked)
                {
                      trtmTestcases.setChecked(checked);
		        }
		    }
		          
		}
    }

	
	/**
	 * editTreeTable.
	 * @param table {@link Tree}
	 */
	private void editTreeTable(final Tree table){
		 final TreeEditor editor = new TreeEditor(table);
	        editor.horizontalAlignment = SWT.LEFT;
	        editor.grabHorizontal = true;

	        table.addMouseListener(new MouseAdapter() {

	            @Override
	            public void mouseUp(final MouseEvent e) {
	                final Control oldEditor = editor.getEditor();
	                if (oldEditor != null) {
	                    oldEditor.dispose();
	                }

	                final Point p = new Point(e.x, e.y);
	                final TreeItem item = table.getItem(p);
	                if (item == null) {
	                    return;
	                }
	                for (int i = 1; i < table.getColumnCount(); ++i) {
	                    if (item.getBounds(i).contains(p)) {
	                        final int columnIndex = i;
	                        // The control that will be the editor must be a
	                        final Text newEditor = new Text(table, SWT.NONE);

	                        newEditor.setText(item.getText(columnIndex ));

	                        newEditor.addModifyListener(new ModifyListener() {
	                            public void modifyText(final ModifyEvent e) {
	                                final Text text = (Text) editor.getEditor();
	                                editor.getItem().setText(columnIndex , text.getText());
	                            }
	                        });
	                        newEditor.selectAll();
	                        newEditor.setFocus();
	                        editor.setEditor(newEditor, item, columnIndex );
	                    }
	                }
	            }

	        });

	}
	
	/**
	 * getAllTestSuites.
	 */
	public void getAllTestSuites()
	{
		try
		{
			TSGson []tsGsons = TestSuiteAPIHandler.getInstance().getAllTestSuites();
			allTestSuites = new ArrayList<String>();
			for(TSGson tsGson : tsGsons)
			{
				allTestSuites.add(tsGson.tsName);
			}
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : getAllTestSuites()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * setAllTestSuiteToTable.
	 */
	private void setAllTestSuiteToTable()
	{
		
	}
//	protected DataBindingContext initDataBindings() {
//		DataBindingContext bindingContext = new DataBindingContext();
//		//
//		return bindingContext;
//	}
	/**
	 * getChecked.
	 * @param table {@link Tree}
	 */
	private void getChecked(Tree table){
		  table.addListener(SWT.Selection, new Listener() {
			  	          public void handleEvent(Event event) {
			  	              if (event.detail == SWT.CHECK) {
			                 TreeItem item = (TreeItem) event.item;
			                  boolean checked = item.getChecked();
			                  checkItems(item, checked);
			                  checkPath(
			                      item.getParentItem(),
			                      checked,
			                      false);
			              }
			          }
			      });

	}
	/**
	 * checkPath.
	 * @param item {@link TreeItem}
	 * @param checked {@link Boolean}
	 * @param grayed {@link Boolean}
	 */
	static void checkPath(
			      TreeItem item,
			      boolean checked,
			  	      boolean grayed) {
			      if (item == null) return;
			  	      if (grayed) {
			          checked = true;
			      } else {
			          int index = 0;
			          TreeItem[] items = item.getItems();
			  	          while (index < items.length) {
			              TreeItem child = items[index];
			              if (child.getGrayed()
			  	                  || checked != child.getChecked()) {
			                  checked = grayed = true;
			                  break;
			              }
			              index++;
			          }
			     }
			      item.setChecked(checked);
			      item.setGrayed(grayed);
			      checkPath(item.getParentItem(), checked, grayed);
			  }
			  
			  	  static void checkItems(TreeItem item, boolean checked) {
			      item.setGrayed(false);
			      item.setChecked(checked);
			      TreeItem[] items = item.getItems();
			  	      for (int i = 0; i < items.length; i++) {
			          checkItems(items[i], checked);
			      }
			  }
			  	  
  private void genarateTestNGfile(Tree table)
  {
	  TreeItem [] items = table.getItems();
	  List<String> testNGList=new ArrayList<String>();
	  
	  for(int i=0;i<items.length;i++)
	  {
		  if(!items[i].getChecked())
			  continue;
		  TSGson tsGson = new TSGson();
		  tsGson.tsName = items[i].getText();
		  List<TSTCGson> tstcList = new ArrayList<TSTCGson>();
		  tsGson.tsTCLink = tstcList;

		  TreeItem [] childs = items[i].getItems();
		  for(int j=0;j<childs.length;j++)
  		  {
			  Object eltTypeObj1 = childs[j].getData("EltType");
			  if(eltTypeObj1!=null)
			  {
  				  String eltType = eltTypeObj1.toString();
  				  if(eltType.equalsIgnoreCase("TESTCASE"))
  				  {
  					  if(childs[j].getChecked())
  					  {
	  						TSTCGson tstcGson = new TSTCGson();
	  						tstcGson.tcName = childs[j].getText(0);
	  						
	  						List<TSTCParamGson> paramList = new ArrayList<TSTCParamGson>();
	  						TSTCParamGson param1 = new TSTCParamGson();
	  						param1.tcparamName = table.getColumn(1).getText();
	  						param1.tcparamValue = childs[j].getText(1);
	  						TSTCParamGson param2 = new TSTCParamGson();
	  						param2.tcparamName = table.getColumn(2).getText();
	  						param2.tcparamValue = childs[j].getText(2);
	  						TSTCParamGson param3 = new TSTCParamGson();
	  						param3.tcparamName = table.getColumn(3).getText();
	  						param3.tcparamValue = childs[j].getText(3);
	  						TSTCParamGson param4 = new TSTCParamGson();
	  						param4.tcparamName = table.getColumn(4).getText();
	  						param4.tcparamValue = "";
	  						TSTCParamGson param5 = new TSTCParamGson();
	  						param5.tcparamName = table.getColumn(5).getText();
	  						param5.tcparamValue = "";
	  						paramList.add(param1);
	  						paramList.add(param2);
	  						paramList.add(param3);
	  						paramList.add(param4);
	  						paramList.add(param5);
	  						tstcGson.tcParams = paramList;
	  						//List<TSTCGson> tctsList = newMap.get(tsGson.tsName);
	  						tstcList.add(tstcGson);	  					
  					  }
  				  }
			  }
  		  }
  		
		TestSuiteRunnerAPI runnerAPI = new TestSuiteRunnerAPI();
		runnerAPI.selected = true;
		runnerAPI.threadCount = items[i].getText(4);
		runnerAPI.testsuiteName = tsGson.tsName;
		runnerAPI.status = "Running";
		testNGList.add(Utilities.createTestng(tsGson, runnerAPI));
		
	  }
	  TestSuiteExecutor execution = new TestSuiteExecutor(testNGList, new ConsoleOutputStream(runnerConsole), runnerConsole);
	  execution.executeTestSuite();
  }
}
