package com.automatics.utilities.runner;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import org.eclipse.swt.custom.CCombo;
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
import com.automatics.utilities.git.GitUtilities;
import com.automatics.utilities.gsons.testsuite.TSGson;
import com.automatics.utilities.gsons.testsuite.TSTCGson;
import com.automatics.utilities.gsons.testsuite.TSTCParamGson;
import com.automatics.utilities.helpers.Utilities;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * NewRunnerUI.
 * @author BD00487363
 *
 */
public class NewRunnerUI {
	//private DataBindingContext m_bindingContext;

	protected Shell shlRunner;
	private List<String> allTestSuites;
	private Text text_1;
	private TreeItem parentTestSuite;
	private Tree remoteTable;
	private Tree localHostTable;
	private Text runnerConsole;
	private TestSuiteExecutor execution = null;
	private GitUtilities gitUtil;
	private Text text_2;
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
		
		/*
		 * Load GIT properties*/
		gitUtil = new GitUtilities();
		gitUtil.loadAndSetProperties(GitUtilities.GIT_PROPERTY_PATH);
		gitUtil.initExistingRepository();
		/*Loading completed*/
		
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
			public void widgetSelected(SelectionEvent e) 
			{
				createTestSuiteTable(remoteTable);
				createTestSuiteTable(localHostTable);
			}
		});
		
		ToolItem clearConsole = new ToolItem(iConToolBar, SWT.NONE);
		clearConsole.setToolTipText("Clear Console");
		clearConsole.setImage(ResourceManager.getPluginImage("Automatics", "images/icons/console.png"));
		clearConsole.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				runnerConsole.setText("");
			}
		});
		
		final TabFolder tabFolder = new TabFolder(suitedetailsComposite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Remote");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_1);
		 GridLayout gl_composite_1 = new GridLayout(1, false);
		 gl_composite_1.marginWidth = 0;
		 gl_composite_1.marginHeight = 0;
		 composite_1.setLayout(gl_composite_1);
		
		 remoteTable = new Tree(composite_1, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL);
		 remoteTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		 remoteTable.setLinesVisible(true);
		remoteTable.setHeaderVisible(true);
		
		TreeColumn remote_ts_tc_name = new TreeColumn(remoteTable, SWT.NONE);
		remote_ts_tc_name.setWidth(121);
		remote_ts_tc_name.setText("TestSuite/TestCase");
		
		
		TreeColumn remote_exe_platform = new TreeColumn(remoteTable, SWT.NONE);
		remote_exe_platform.setWidth(77);
		remote_exe_platform.setText("Exe_Platform");
		
		TreeColumn remote_exe_type = new TreeColumn(remoteTable, SWT.NONE);
		remote_exe_type.setWidth(78);
		remote_exe_type.setText("Exe_Type");
		
		TreeColumn remote_run_on = new TreeColumn(remoteTable, SWT.NONE);
		remote_run_on.setWidth(80);
		remote_run_on.setText("Run_On");
		
		TreeColumn remote_threadcount = new TreeColumn(remoteTable, SWT.NONE);
		remote_threadcount.setWidth(81);
		remote_threadcount.setText("Thread-Count");
		
		TreeColumn trclmnColumn_4 = new TreeColumn(remoteTable, SWT.NONE);
		trclmnColumn_4.setWidth(88);
		trclmnColumn_4.setText("Column5");
		
		
		
		
		createTestSuiteTable(remoteTable);
		
		TabItem localhost_ts_tc_name = new TabItem(tabFolder, SWT.NONE);
		localhost_ts_tc_name.setText("Localhost");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		localhost_ts_tc_name.setControl(composite_2);
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
		
		TreeColumn localhost_exe_platfrom = new TreeColumn(localHostTable, SWT.NONE);
		localhost_exe_platfrom.setWidth(73);
		localhost_exe_platfrom.setText("Exe_Platform");
		
		TreeColumn localhost_exe_type = new TreeColumn(localHostTable, SWT.NONE);
		localhost_exe_type.setWidth(77);
		localhost_exe_type.setText("Exe_Type");
		
		TreeColumn localhost_run_on = new TreeColumn(localHostTable, SWT.NONE);
		localhost_run_on.setWidth(85);
		localhost_run_on.setText("Run_On");
		
		TreeColumn localhost_thread_count = new TreeColumn(localHostTable, SWT.NONE);
		localhost_thread_count.setWidth(81);
		localhost_thread_count.setText("Thread-Count");
		
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
		 
		shlRunner.setTabList(new Control[]{parentComposite});
		//m_bindingContext = initDataBindings();
		createTestSuiteTable(localHostTable);

		editTreeTable(remoteTable);
		editTreeTable(localHostTable);
		getChecked(remoteTable);
		getChecked(localHostTable);
		
		TabItem settingTab = new TabItem(tabFolder, SWT.NONE);
		settingTab.setText("Settings");
		
		Composite composite_6 = new Composite(tabFolder, SWT.NONE);
		composite_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		settingTab.setControl(composite_6);
		composite_6.setLayout(new GridLayout(1, false));
		
		Composite composite_7 = new Composite(composite_6, SWT.NONE);
		composite_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite_7 = new GridLayout(1, false);
		gl_composite_7.marginWidth = 2;
		gl_composite_7.marginHeight = 3;
		composite_7.setLayout(gl_composite_7);
		GridData gd_composite_7 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_7.heightHint = 22;
		composite_7.setLayoutData(gd_composite_7);
		
		Label lblNewLabel_2 = new Label(composite_7, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_2.setText("Schedular Settings");
		
		Composite composite_8 = new Composite(composite_6, SWT.NONE);
		composite_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblDateAndTime_1 = new Label(composite_8, SWT.NONE);
		lblDateAndTime_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDateAndTime_1.setBounds(23, 20, 165, 15);
		lblDateAndTime_1.setText("Date and time for execution :");
		
		Label label_4 = new Label(composite_8, SWT.NONE);
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_4.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label_4.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_4.setBounds(10, 20, 10, 15);
		label_4.setText("*");
		
		Label lblRerun = new Label(composite_8, SWT.NONE);
		lblRerun.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRerun.setText("Re-run failed scripts :");
		lblRerun.setBounds(23, 59, 165, 15);
		
		Label label_6 = new Label(composite_8, SWT.NONE);
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_6.setText("*");
		label_6.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label_6.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_6.setBounds(10, 59, 10, 15);
		
		Label lblNoTimesFor = new Label(composite_8, SWT.NONE);
		lblNoTimesFor.setText("No times for Re-Run :");
		lblNoTimesFor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNoTimesFor.setBounds(23, 102, 165, 15);
		
		Label label_7 = new Label(composite_8, SWT.NONE);
		label_7.setText("*");
		label_7.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label_7.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_7.setBounds(10, 102, 10, 15);
		
		text_2 = new Text(composite_8, SWT.BORDER);
		text_2.setBounds(194, 96, 105, 21);
		
		Label lblSaveReportsTo = new Label(composite_8, SWT.NONE);
		lblSaveReportsTo.setText("Save reports to : ");
		lblSaveReportsTo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSaveReportsTo.setBounds(23, 146, 165, 15);
		
		Label label_8 = new Label(composite_8, SWT.NONE);
		label_8.setText("*");
		label_8.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label_8.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_8.setBounds(10, 146, 10, 15);
		
		Button rerun_yes = new Button(composite_8, SWT.RADIO);
		rerun_yes.setText("Yes");
		rerun_yes.setBounds(194, 56, 38, 21);
		rerun_yes.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Button rerun_no = new Button(composite_8, SWT.RADIO);
		rerun_no.setText("No");
		rerun_no.setBounds(239, 56, 38, 21);
		rerun_no.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Button resetAll = new Button(composite_8, SWT.NONE);
		resetAll.setBounds(436, 211, 75, 25);
		resetAll.setText("Reset All");
		
		Button chkLocalSystem = new Button(composite_8, SWT.CHECK);
		chkLocalSystem.setBounds(194, 145, 93, 16);
		chkLocalSystem.setText("Local System");
		chkLocalSystem.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Button chkPDF = new Button(composite_8, SWT.CHECK);
		chkPDF.setText("PDF");
		chkPDF.setBounds(305, 146, 93, 16);
		chkPDF.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Button chkDatabase = new Button(composite_8, SWT.CHECK);
		chkDatabase.setText("Database");
		chkDatabase.setBounds(194, 171, 93, 16);
		chkDatabase.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Button chkHTML = new Button(composite_8, SWT.CHECK);
		chkHTML.setText("HTML");
		chkHTML.setBounds(305, 171, 93, 16);
		chkHTML.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label selectDateLabel = new Label(composite_8, SWT.NONE);
		selectDateLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		selectDateLabel.setForeground(SWTResourceManager.getColor(0, 0, 128));
		selectDateLabel.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		selectDateLabel.setBounds(389, 20, 122, 15);
		selectDateLabel.setText("Click to select date and time");
		
		
		final Label setdateLabel = new Label(composite_8, SWT.NONE);
		setdateLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setdateLabel.setBounds(194, 20, 165, 15);
		
		
		selectDateLabel.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Shell dialog = new Shell (shlRunner, SWT.DIALOG_TRIM);
			      dialog.setLayout (new GridLayout (5, false));

			      final DateTime calendar = new DateTime (dialog, SWT.CALENDAR | SWT.BORDER);
			      final DateTime date = new DateTime (dialog, SWT.DATE | SWT.SHORT);
			      final DateTime time = new DateTime (dialog, SWT.TIME | SWT.SHORT);

			      //new Label (composite_3, SWT.NONE);
			      //new Label (composite_3, SWT.NONE);
			      Button ok = new Button (dialog, SWT.PUSH);
			      ok.setText ("OK");
			      ok.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false));
			      ok.addSelectionListener (new SelectionAdapter () {
			        public void widgetSelected (SelectionEvent e) 
			        {
			          setdateLabel.setText(+ (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ()+" "+" "+"Time:"+ time.getHours () + ":" + time.getMinutes ());
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
		runnerConsole.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		runnerConsole.setEditable(false);

		
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
		
		/*Disable the setting tag*/
		tabFolder.getTabList()[2].setEnabled(false);
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
		    items[i].dispose();
		}
		TSGson tsGsons[] = TestSuiteAPIHandler.getInstance().getAllTestSuites();
		boolean checked=false;
		for (TSGson tsGson : tsGsons) 
		{
			parentTestSuite = new TreeItem(table, SWT.NONE|SWT.MULTI);
		    parentTestSuite.setText(new String[] { "" +tsGson.tsName, "", "","","","" });
		    parentTestSuite.setData("EltType","TESTSUITE");
		    
		    if(tsGson.tsTCLink==null)
		            continue;
		    
		    if(table.equals(remoteTable))
		    {
		    	for(TSTCGson tsTCGson : tsGson.tsTCLink) 
			    {
			    	TreeItem trtmTestcases = new TreeItem(parentTestSuite, SWT.NONE|SWT.MULTI);
			        trtmTestcases.setText(new String[] {
			        		tsTCGson.tcName, 
			        		tsTCGson.tcParams.get(0)!=null ? tsTCGson.tcParams.get(0).tcparamValue:"",
			        		tsTCGson.tcParams.get(1)!=null ? tsTCGson.tcParams.get(1).tcparamValue:"",
			        		tsTCGson.tcParams.get(2)!=null ? tsTCGson.tcParams.get(2).tcparamValue:"",
			        		tsTCGson.tcParams.get(3)!=null ? tsTCGson.tcParams.get(3).tcparamValue:"", 
			        		tsTCGson.tcParams.get(4)!=null ? tsTCGson.tcParams.get(4).tcparamValue:"" });
			        trtmTestcases.setData("EltType","TESTCASE");

			        /*Add drop down to remote data*/
					CCombo run_on_combo = new CCombo(remoteTable, SWT.READ_ONLY);
					run_on_combo.setItems(new String[]{"Workstation1","Workstation2"});
					run_on_combo.setEditable(false);
					TreeEditor treeEditor = new TreeEditor(remoteTable);
					treeEditor.setEditor(run_on_combo, trtmTestcases, 3);
			        
	                table.setSelection(parentTestSuite);
	                if(checked)
	                {
	                      trtmTestcases.setChecked(checked);
			        }
			    }

		    }
		    else
		    {
			    for(TSTCGson tsTCGson : tsGson.tsTCLink) 
			    {
			    	TreeItem trtmTestcases = new TreeItem(parentTestSuite, SWT.NONE|SWT.MULTI);
			        trtmTestcases.setText(new String[] {
			        		tsTCGson.tcName, 
			        		tsTCGson.tcParams.get(0)!=null ? tsTCGson.tcParams.get(0).tcparamValue:"",
			        		tsTCGson.tcParams.get(1)!=null ? tsTCGson.tcParams.get(1).tcparamValue:"",
			        		"localhost", //Run_On
			        		tsTCGson.tcParams.get(3)!=null ? tsTCGson.tcParams.get(3).tcparamValue:"", 
			        		tsTCGson.tcParams.get(4)!=null ? tsTCGson.tcParams.get(4).tcparamValue:"" });
			        trtmTestcases.setData("EltType","TESTCASE");
	
	                table.setSelection(parentTestSuite);
	                if(checked)
	                {
	                      trtmTestcases.setChecked(checked);
			        }
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
			System.out.println("[" + new Date() + "] - [" + getClass().getName() + " : getAllTestSuites()] - Exception : " + e.getMessage());
			e.printStackTrace(System.out);
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
		boolean gitPassed = this.gitUtil.performGITSyncOperation();
		System.out.println("[" + new Date() + "] : Runner Git Operation : " + gitPassed);
		
	  }
	  execution = new TestSuiteExecutor(testNGList, new ConsoleOutputStream(runnerConsole), runnerConsole);
	  execution.executeTestSuite();
  }
}
