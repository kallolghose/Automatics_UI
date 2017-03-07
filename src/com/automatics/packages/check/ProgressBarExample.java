package com.automatics.packages.check;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressBarExample {
  Display d;

  Shell s;

  ProgressBarExample() {
    d = new Display();
    s = new Shell(d);
    s.setSize(250, 250);
    
    s.setText("A ProgressBar Example");

    final ProgressBar pb = new ProgressBar(s, SWT.HORIZONTAL);
    pb.setMinimum(0);
    pb.setMaximum(100);
    pb.setSelection(50);
    pb.setBounds(10, 10, 200, 20);

    s.open();
    while (!s.isDisposed()) {
      if (!d.readAndDispatch())
        d.sleep();
      pb.setSelection(70);
      
    }
    d.dispose();
  }

  public static void main(String [] args) {
    new ProgressBarExample();
  }

}