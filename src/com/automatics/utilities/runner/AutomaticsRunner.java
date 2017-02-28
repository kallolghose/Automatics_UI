package com.automatics.utilities.runner;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.CellEditor;

public class AutomaticsRunner extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			AutomaticsRunner shell = new AutomaticsRunner(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public AutomaticsRunner(Display display) {
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		final Tree tree = new Tree(this, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		
		TreeColumn trclmnNewColumn = new TreeColumn(tree, SWT.NONE);
		trclmnNewColumn.setWidth(100);
		trclmnNewColumn.setText("Col1");
		
		TreeColumn trclmnNewColumn_1 = new TreeColumn(tree, SWT.FULL_SELECTION);
		trclmnNewColumn_1.setWidth(100);
		trclmnNewColumn_1.setText("Col2");

		TreeItem root = new TreeItem(tree, SWT.FULL_SELECTION);
		root.setText(new String[]{"S1","S2"});
		
		
		for(int i=0;i<5;i++)
		{
			TreeItem item = new TreeItem(root,SWT.FULL_SELECTION);
			item.setText(new String[]{"S1" + i,"S2" + i});
		}
		
		tree.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				System.out.println(tree.getSelection()[0].getText(1));
			}
		});
		
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
