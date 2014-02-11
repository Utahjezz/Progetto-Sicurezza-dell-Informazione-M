package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


import Controller.ClientController;


public class Client implements Runnable{
	public static final int PORT=3000;
    public static final String host= "127.0.0.1";
    private String output;
    private SSLSocket socket;
    
    public Client(Socket socket) {
    	this.socket = (SSLSocket) socket;
    }
  
    public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException { 
        
        System.out.println("Client avviato."); 
        
        System.setProperty("javax.net.ssl.trustStore","clientTrustStore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","sslclient");
          
        SocketFactory factory= SSLSocketFactory.getDefault();  //di default solo autenticazione! 
        SSLSocket socket=(SSLSocket) factory.createSocket(host,PORT); 
          
        System.out.println("Connessione avvenuta correttamente dopo aver verificato se il certificato del server � presente nel truststore\n"); 
          
        //stabilisco che il client venga riconosciuto come tale nel protocollo 
        socket.setUseClientMode(true);    
        //faccio partire manualmente l'handshake, altrimenti viene eseguito in automatico alla prima lettura o scrittura 
        socket.startHandshake(); 
          
        System.out.println("Meccanismi di cifratura abilitati:"); 
        for(String s:socket.getSupportedCipherSuites()) 
        { 
            System.out.println(s); 
        } 
        System.out.println(""); 
        //setto la lista dei cifrari supportati come cifrari utilizzabili 
        socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
        Client c = new Client(socket);
        Thread t = new Thread(c);
        t.start();
    }
    
    public void run() {
    	System.out.println("Connessione avvenuta correttamente dopo aver verificato se il certificato del client � presente nel truststore.");
    	ClientController clc = new ClientController();
        ClientReceiver cr = new ClientReceiver(socket, clc);
        ClientSender cs = new ClientSender(socket, clc);
        clc.setClientSender(cs);
        Thread srt = new Thread(cr);
        Thread sst = new Thread(cs);
        srt.start();
        sst.start();
    }
    
    public void receiveMessage(String s) {
		output = s;
	}
    
    public class ClientReceiver implements Runnable{
		
		private Socket socket;
		private ClientController srvc;
		
		public ClientReceiver(Socket s, ClientController srvc) {
			this.socket = s;
			this.srvc = srvc;
		}

		@Override
		public void run() {
			while(true) {
				BufferedReader brd;
				try {
					brd = new BufferedReader( new InputStreamReader(socket.getInputStream(), "UTF-8"));
					String s = brd.readLine();
		            System.out.println(s+"\nMessaggio ricevuto correttamente.");
		            srvc.receiveMessage(s);
				}catch (IOException exc)  
		        { 
		            System.out.println("Eccezione IO "+ exc); 
		            exc.printStackTrace(); 
		        } 
			}
		}
	}
	
	public class ClientSender implements Runnable {
		
		private Socket socket;
		private ClientController srvc;
		
		public ClientSender(Socket s, ClientController srvc) {
			this.socket = s;
			this.srvc = srvc;
		}
		
		public void sendMessage(String s) {
			PrintWriter prw;
			try {
				prw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")); 
				String rep = s;
				prw.println(rep);
	            prw.flush();
			}
			catch (IOException exc)  
	        { 
	            System.out.println("Eccezione IO "+ exc); 
	            exc.printStackTrace(); 
	        } 
		}
		@Override
		public void run() {
		}
	}
}
