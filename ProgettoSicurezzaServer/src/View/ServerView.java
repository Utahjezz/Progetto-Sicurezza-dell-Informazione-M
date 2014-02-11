package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Controller.ServerController;

public class ServerView extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField chatLine;
	JTextArea chatText = null;
	ServerController svrc;
	
	public ServerView(ServerController svrc) {
        	this.svrc = svrc;
	       setTitle("Chat: Server");
	       setSize(300, 200);
	       setLocationRelativeTo(null);
	       setDefaultCloseOperation(EXIT_ON_CLOSE);        
	    }
	
	public void initUI() {
		JPanel chatPane = new JPanel(new BorderLayout());
	      chatText = new JTextArea(10, 20);
	      chatText.setLineWrap(true);
	      chatText.setEditable(false);
	      chatText.setForeground(Color.blue);
	      JScrollPane chatTextPane = new JScrollPane(chatText,
	         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	      chatLine = new JTextField();
	      chatLine.setEnabled(true);
	      chatLine.addKeyListener(new KeyListener(){

		              public void keyPressed(KeyEvent e){
		
		                  if(e.getKeyCode() == KeyEvent.VK_ENTER){
		                	  chatText.setForeground(Color.blue);
		                	  String s = chatLine.getText();
		                      chatText.append("\nServer: "+s);
		                      chatLine.setText("");
		                      svrc.sendMessage(s);
		                  }       
		              }

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}
		          }
		      );
	      chatPane.add(chatLine, BorderLayout.SOUTH);
	      chatPane.add(chatTextPane, BorderLayout.CENTER);
	      chatPane.setPreferredSize(new Dimension(200, 200));
	      add(chatPane);
	}
	
	public void printReceivedMessage(String s) {
		chatText.setForeground(Color.red);
		chatText.append("\n"+s);
	}
	
	class ActionAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
}
