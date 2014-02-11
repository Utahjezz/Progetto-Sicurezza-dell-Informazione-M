package Controller;

import Server.Server;
import Server.Server.ServerSender;
import View.ServerView;

public class ServerController {
	
	ServerView sv;
	ServerSender serverSender;
	
	public ServerController() {
		 sv = new ServerView(this);
         sv.initUI();
         sv.setVisible(true);
	}
	
	public void setServerSender(ServerSender serverSender) {
		this.serverSender = serverSender;
	}
	
	public void sendMessage(String s) {
		serverSender.sendMessage(s);
	}
	
	public void receiveMessage(String s) {
		sv.printReceivedMessage("Client: "+s);
	}

}
