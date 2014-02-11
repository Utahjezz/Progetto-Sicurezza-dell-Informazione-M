package Server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import Controller.ServerController;

public class Server implements Runnable{
	
	public static final int PORT=3000; 
	private String output;
    private Socket socket;
    
    public Server(Socket s) 
    { 
        socket = s; 
    }
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
    	ServerSocketFactory factory; 
        
        System.out.println("Server avviato."); 
          
        /*Inizializzazione ServerSocketFactory 
          altrimenti in automatico è abilitata  
          l'autenticazione ma non la cifratura. 
         */
        SSLContext ctx; 
        KeyManagerFactory kmf; 
        KeyStore ks; 
        char[] password="sslserver".toCharArray(); 
        String keyfile="serverKeystore.jks"; 
        ctx=SSLContext.getInstance("TLS"); 
        kmf= KeyManagerFactory.getInstance("SUNX509"); 
        ks= KeyStore.getInstance("JKS"); 
        ks.load(new FileInputStream(keyfile),password); 
        kmf.init(ks,password); 
        ctx.init(kmf.getKeyManagers(), null, null); 
        factory=ctx.getServerSocketFactory(); 
          
        SSLServerSocket serv= (SSLServerSocket) factory.createServerSocket(PORT); 
        System.setProperty("javax.net.ssl.keyStore", "serverKeystore.jks"); 
        System.setProperty("javax.net.ssl.keyStorePassword", "sslserver"); 
        
          
        serv.setNeedClientAuth(true); //richiesta autenticazione client! Di default l'autenticazione è solo del server 
        serv.setWantClientAuth(true); //richiede autenticazione del client (non obbligatoria) 
          
        while(true)  
        { 
            SSLSocket sock=(SSLSocket) serv.accept(); 
            sock.setUseClientMode(false); 
            Server server=new Server(sock); 
            System.out.println("Richiesta di connessione ricevuta"); 
            Thread t=new Thread(server);
            t.start();
        }
    }

	@Override
	public void run() {
            System.out.println("Connessione avvenuta correttamente dopo aver verificato se il certificato del client è presente nel truststore."); 
            ServerController srvc = new ServerController();
            ServerReceiver sr = new ServerReceiver(socket, srvc);
            ServerSender ss = new ServerSender(socket, srvc);
            srvc.setServerSender(ss);
            Thread srt = new Thread(sr);
            Thread sst = new Thread(ss);
            srt.start();
            sst.start();
	}
	
	public class ServerReceiver implements Runnable{
		
		private Socket socket;
		private ServerController srvc;
		
		public ServerReceiver(Socket s, ServerController srvc) {
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
		            System.out.println(s+"\nMessaggio ricevuto correttamente. Rispondo al Client.");
		            srvc.receiveMessage(s);
				}catch (IOException exc)  
		        { 
		            System.out.println("Eccezione IO "+ exc); 
		            exc.printStackTrace(); 
		        } 
			}
		}
	}
	
	public class ServerSender implements Runnable {
		
		private Socket socket;
		private ServerController srvc;
		
		public ServerSender(Socket s, ServerController srvc) {
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