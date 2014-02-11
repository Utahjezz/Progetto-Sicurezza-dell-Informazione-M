package Controller;

import Client.Client.ClientSender;
import View.ClientView;


public class ClientController {
	ClientView cl;
	ClientSender clientSender;
	
	public ClientController() {
		 cl = new ClientView(this);
         cl.initUI();
         cl.setVisible(true);
	}
	
	public void setClientSender(ClientSender cs) {
		this.clientSender = cs;
	}
	
	public void sendMessage(String s) {
		clientSender.sendMessage(s);
	}
	
	public void receiveMessage(String s) {
		cl.printReceivedMessage("Server: "+s);
	}
}
