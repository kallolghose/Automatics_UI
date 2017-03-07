package com.automatics.utilities.chrome.extension;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONObject;

public class Test extends JFrame implements ActionListener
{	
	JettyServer server = new JettyServer();
	static boolean isRecording = false;
	//static boolean isClosed = false;
		
	static JFrame mainFrame ;
	static JButton findButton,getxpathButton,listenButton,recordButton,sendAllButton, performButton;
	static JTextField idTextField,xpathTextField, verifyTFxpath, verifyTFvalue;
	static JTable recordTable,tb;
	static JScrollPane scrollPane;
	static JLabel label ;
	static DefaultTableModel dm;
	static JComboBox commandsComboBox = new JComboBox();
	
	

	public void actionPerformed(ActionEvent e) {
		
		JButton btnListen =  (JButton) e.getSource();
//		JOptionPane.showMessageDialog(null, btnListen.getActionCommand());
		if(e.getActionCommand().equals("lstn"))
		{
			if(server.isStarted())
			{
				try {
					System.out.println("start setting ");
					server.stop();
					label.setText("Not Listening");
					btnListen.setText("Listen");
					//recordButton.setBackground(Color.RED);
					//isRecording = true;
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			 }
			else if(server.isStopped())
			{
				 try {
					 System.out.println("stop setting ");
					server.stop();
					server.start();
					
					label.setText("Listening...");
					btnListen.setText("Stop");
					 //recordButton.setBackground(Color.RED);
					 //isRecording = false;
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				 
				
			 }
		}
		else if(e.getActionCommand().equals("find"))
		{
			String tf_txt = idTextField.getText();
			if(tf_txt.isEmpty())
			{
				idTextField.requestFocus();
				idTextField.setBackground(Color.RED);
			}
			else
			{
				System.out.println(tf_txt);
			JSONObject jsonObj = new JSONObject();
	       // String txt_msg = "{\"value\":\""+tf_txt+"\",\"from\":\"javaRecord\"}";
	        jsonObj.put("value", tf_txt);
	        jsonObj.put("from","highlightElement");
	        //JOptionPane.showMessageDialog(null, "sending to findd2");
			MyWebSocketHandler.sendMsg(jsonObj.toJSONString());
			
			}
		}
		else if(e.getActionCommand().equals("getxpath"))
		{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("from","getxpath");
			//JOptionPane.showMessageDialog(null, "sending to findd2");
			MyWebSocketHandler.sendMsg(jsonObj.toJSONString());
		}
		else if(e.getActionCommand().equals("record"))
		{
			isRecording = !isRecording;
			
			if(isRecording){
				recordButton.setBackground(Color.RED);
				JSONObject jsonObj = new JSONObject();
			    jsonObj.put("❮ from","iamrecording");
				MyWebSocketHandler.sendMsg(jsonObj.toJSONString());
//				recordButton.setOpaque(true);
				isRecording = true;
				System.out.println("setting recording");
				System.out.println(isRecording);
			}
			else
			{
				recordButton.setBackground(Color.WHITE);
				isRecording = false;
				System.out.println("setting not recording");
				System.out.println(isRecording);
			}
				
			//System.out.println(isRecording);
			
			
		}
		else if(e.getActionCommand().equals("verifyall"))
		{
			//recordTable.getModel().setValueAt("found", 1, 3);
			
			 
			
			System.out.println("****");
			for(int rowCount=1;rowCount<recordTable.getRowCount();rowCount++)
			{
				String valuee = (String) recordTable.getModel().getValueAt(rowCount, 1);
				
				JSONObject sendJSON = new JSONObject();
				sendJSON.put("from","verifyAll");
				sendJSON.put("value", valuee);
				sendJSON.put("rowNum",rowCount);
		        //JOptionPane.showMessageDialog(null, "sending to findd2");
				MyWebSocketHandler.sendMsg(sendJSON.toJSONString());
				System.out.println(valuee);
			}
			System.out.println("****");
	}
	else if(e.getActionCommand().equals("perform"))
	{
		System.out.println("performingg");
		JSONObject sendJSON = new JSONObject();
		sendJSON.put("from","perform");
		sendJSON.put("xpath", verifyTFxpath.getText());
		MyWebSocketHandler.sendMsg(sendJSON.toJSONString());
	}
		
		
	}

	public void setTextMy(String message){
		
		//label.setText(message);
	}
	
	public static void main(String[] args) throws Exception {
//		new Test();
		Test t = new Test();
		int flag=1;
		
		label= new JLabel();
		scrollPane = new JScrollPane();
		//String column[]={"COMMAND","TARGET","VALUE","STATUS"};
//		String data[][]={{"COMMAND","TARGET","VALUE"},{"COMMAND","TARGET","VALUE"}};  
		mainFrame = new JFrame();
//		findButton = new JButton();
		listenButton= new JButton();
		recordButton= new JButton();
		sendAllButton = new JButton();
		findButton = new JButton();
		getxpathButton = new JButton(); 
		xpathTextField = new JTextField();
		idTextField = new JTextField();
		recordTable = new JTable(1,3);
		tb = new JTable();
		
		verifyTFxpath = new JTextField();
		verifyTFvalue = new JTextField();
		performButton = new JButton("Perform");
		commandsComboBox = new JComboBox();
		
		listenButton.setActionCommand("lstn");
		findButton.setActionCommand("find"); 
		getxpathButton.setActionCommand("getxpath");
		recordButton.setActionCommand("record");
		sendAllButton.setActionCommand("verifyall");
		performButton.setActionCommand("perform");
		commandsComboBox.addItem("Verify_Text");

		
		 dm = new DefaultTableModel(0, 0);
		 String header[] = new String[] {"COMMAND","TARGET","VALUE","STATUS"};
		 dm.setColumnIdentifiers(header);
		 recordTable.setModel(dm);
//		 Vector<Object> data = new Vector<Object>();
//		 data.add("COMMAND");
//		 data.add("TARGET");
//		 data.add("VALUE");
		dm.addRow(header);
		
		listenButton.setBounds(10,10,100,20);
		recordButton.setBounds(130,10,100,20);
		sendAllButton.setBounds(250,10,100,20);
		idTextField.setBounds(10,280,150,20);
		xpathTextField.setBounds(10,360,150,20);
		findButton.setBounds(10,320,100,20);
		getxpathButton.setBounds(10,390,100,20);
		performButton.setBounds(370,720,90,20);
		commandsComboBox.setBounds(10,720,100,20);
		verifyTFxpath.setBounds(120, 720, 220, 20);
		verifyTFvalue.setBounds(120, 750, 220, 20);
		
		findButton.setText("Point");
		getxpathButton.setText("GetXPath");
		listenButton.setText("LISTEN");
		recordButton.setText("RECORD");
		sendAllButton.setText("Verify all");
		
		listenButton.addActionListener(t);
		findButton.addActionListener(t);
		getxpathButton.addActionListener(t);
		recordButton.addActionListener(t);
		sendAllButton.addActionListener(t);
		performButton.addActionListener(t);
		
		recordButton.setBackground(Color.WHITE);
		
		
		label.setBounds(10,40,100,20);
		
		recordTable.setBounds(10,60,400,650);
		label.setText("Not Listening");
		mainFrame.add(idTextField);
		mainFrame.add(xpathTextField);
		mainFrame.add(findButton);
		mainFrame.add(getxpathButton);
		mainFrame.add(label);
		mainFrame.add(listenButton);
		mainFrame.add(recordButton);
		mainFrame.add(sendAllButton);
		mainFrame.add(recordTable);
		mainFrame.add(performButton);
		
		mainFrame.add(commandsComboBox);
		mainFrame.add(verifyTFxpath);
		mainFrame.add(verifyTFvalue);
		
		
		mainFrame.setLayout(null);
		mainFrame.setSize(500,850);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		idTextField.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent e) {
				
				idTextField.setBackground(Color.WHITE);
				
			}
		});
		
				JettyServer server = new JettyServer();
				
//				WebDriver driver = new ChromeDriver();
//				driver.get("http://www.jquery.com");
//
//			     //Load the External js file into DOM
//
//			     ((JavascriptExecutor) driver)
//			      .executeScript("var addscript=window.document.createElement('script');addscript.type='text/javascript';addscript.src='http://10.13.69.91:8080/CDITS/test.js';document.getElementsByTagName('body')[0].appendChild(addscript);");
//
//			     //wait for the js to be loaded to the DOM
//			     Thread.sleep(2000);
//			     System.out.println(((JavascriptExecutor) driver)
//			      .executeScript("return typeof(somefunc)").toString().equals("function"));
//
//
//			     //Now you call the JavaScript functions in the JS file
//
//			     System.out.println(((JavascriptExecutor) driver)
//			      .executeScript("somefunc();"));
		
	//	setConnection();
		
	}

}
