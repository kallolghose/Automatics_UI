package com.automatics.utilities.runner;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.widgets.Text;

public class ConsoleOutputStream extends OutputStream
{
	private Text consoleText;
	
	public ConsoleOutputStream(Text consoleText)
	{
		this.consoleText = consoleText;
	}
	
	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		consoleText.append(String.valueOf((char)b));
		
	}
	
}
