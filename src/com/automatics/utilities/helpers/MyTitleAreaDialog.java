package com.automatics.utilities.helpers;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MyTitleAreaDialog extends TitleAreaDialog {

        private Text txtFirstName;
        private String firstName;

        public MyTitleAreaDialog(Shell parentShell) {
                super(parentShell);
        }

        @Override
        public void create() {
                super.create();
                setTitle("This is my custom dialog");
                setMessage("This is a TitleAreaDialog", IMessageProvider.INFORMATION);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
                Composite area = (Composite) super.createDialogArea(parent);
                Composite container = new Composite(area, SWT.NONE);
                container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                GridLayout layout = new GridLayout(2, false);
                container.setLayout(layout);
                createFirstName(container);
                return area;
        }

        private void createFirstName(Composite container) {
                Label lbtFirstName = new Label(container, SWT.NONE);
                lbtFirstName.setText("File Name");

                GridData dataFirstName = new GridData();
                dataFirstName.grabExcessHorizontalSpace = true;
                dataFirstName.horizontalAlignment = GridData.FILL;

                txtFirstName = new Text(container, SWT.BORDER);
                txtFirstName.setLayoutData(dataFirstName);
        }


        @Override
        protected boolean isResizable() {
                return true;
        }

        // save content of the Text fields because they get disposed
        // as soon as the Dialog closes
        private void saveInput() {
                firstName = txtFirstName.getText();

        }

        @Override
        protected void okPressed() {
                saveInput();
                super.okPressed();
        }

        public String getFirstName() {
                return firstName;
        }

}
