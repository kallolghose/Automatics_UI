package com.automatics.packages.Editors;

import org.eclipse.ui.editors.text.TextEditor;

import com.automatics.packages.Editors.Config.TaskSourceViewerConfiguration;

public class MyTextEditor extends TextEditor {

	public static final String ID = "com.automatics.packages.Editors.texteditor";

    public MyTextEditor() {
            System.out.println("Task Text Editor opened");
            setSourceViewerConfiguration(new TaskSourceViewerConfiguration());
    }


}
