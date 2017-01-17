package com.automatics.packages.check;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import jsyntaxpane.DefaultSyntaxKit;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.*;
import org.eclipse.swt.layout.FillLayout;

public class SomeFIle {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SomeFIle window = new SomeFIle();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(shell, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(composite);
		
		DefaultSyntaxKit.initKit();
		final JEditorPane codeEditor = new JEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        frame.add(scrPane, BorderLayout.CENTER);
        frame.doLayout();
        codeEditor.setContentType("text/java");
        codeEditor.setText("public static void main(String[] args) {\n}");
        
	}
}
