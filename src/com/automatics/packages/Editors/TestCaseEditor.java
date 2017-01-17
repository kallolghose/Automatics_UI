package com.automatics.packages.Editors;

import java.awt.*;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;

import com.automatics.packages.Model.Task;
import com.automatics.packages.Model.TaskService;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import jsyntaxpane.DefaultSyntaxKit;

public class TestCaseEditor extends EditorPart {

	public static final String ID = "com.automatics.packages.Editors.editor";
	private Task todo;
    private TaskEditorInput input;
    private Table testCaseTable;
	
	public TestCaseEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		if (!(input instanceof TaskEditorInput)) {
            throw new RuntimeException("Wrong input");
	    }
	
	    this.input = (TaskEditorInput) input;
	    setSite(site);
	    setInput(input);
	    todo = TaskService.getInstance().getTaskById(this.input.getId());
	    setPartName("Todo ID: " + todo.getId());

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		Composite editor_composite = new Composite(parent, SWT.NONE);
		editor_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder editorFolder = new TabFolder(editor_composite, SWT.BOTTOM);
		//editorFolder.setLayout(new FillLayout());
		
		TabItem tbtmDesign = new TabItem(editorFolder, SWT.NONE);
		tbtmDesign.setText("Design");
		
		Composite design_composite = new Composite(editorFolder, SWT.NONE);
		tbtmDesign.setControl(design_composite);
		design_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		testCaseTable = new Table(design_composite, SWT.BORDER | SWT.FULL_SELECTION);
		testCaseTable.setHeaderVisible(true);
		testCaseTable.setLinesVisible(true);
		
		TableColumn snoCol = new TableColumn(testCaseTable, SWT.NONE);
		snoCol.setWidth(41);
		snoCol.setText("S.No");
		
		TableColumn operationCol = new TableColumn(testCaseTable, SWT.NONE);
		operationCol.setWidth(100);
		operationCol.setText("Operation");
		
		TableColumn pagenameCol = new TableColumn(testCaseTable, SWT.NONE);
		pagenameCol.setWidth(100);
		pagenameCol.setText("Page Name");
		
		TableColumn objectNameCol = new TableColumn(testCaseTable, SWT.NONE);
		objectNameCol.setWidth(100);
		objectNameCol.setText("Object Name");
		
		TableColumn argsCol = new TableColumn(testCaseTable, SWT.NONE);
		argsCol.setWidth(100);
		argsCol.setText("Arguments");
		
		TabItem sourceCodeTab = new TabItem(editorFolder, SWT.NONE);
		sourceCodeTab.setText("Source");
		
		Composite code_composite = new Composite(editorFolder, SWT.EMBEDDED | SWT.NONE);
		sourceCodeTab.setControl(code_composite);
		Frame frame = SWT_AWT.new_Frame(code_composite);
		
		//DefaultSyntaxKit.initKit();
		final JEditorPane codeEditor = new JEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        frame.add(scrPane, BorderLayout.CENTER);
        frame.doLayout();
        codeEditor.setContentType("text/java");
        codeEditor.setText("public static void main(String[] args) {\n}");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
