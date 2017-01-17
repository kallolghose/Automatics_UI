package com.automatics.packages.Views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import jsyntaxpane.DefaultSyntaxKit;

public class SyntaxExample {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
            	SyntaxExample st = new SyntaxExample();
            }
        });
    }

    public SyntaxExample() {
        Frame f = new Frame(SyntaxExample.class.getName());
        //final Container c = f.getContentPane();
        //c.setLayout(new BorderLayout());

        DefaultSyntaxKit.initKit();

        final JEditorPane codeEditor = new JEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        f.add(scrPane, BorderLayout.CENTER);
        f.doLayout();
        codeEditor.setContentType("text/java");
        codeEditor.setText("public static void main(String[] args) {\n}");
        
        f.setSize(800, 600);
        f.setVisible(true);
        //f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}