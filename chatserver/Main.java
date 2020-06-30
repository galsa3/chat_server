package chatserver;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException {
		ChatServer server = new ChatServer();
		server.addTcpConnection(ProtocolPort.CHAT_PROTOCOL_PORT.getPort());
		System.out.println("before: new Thread(server).start();");		
		new Thread(server).start();	
		System.out.println("after: new Thread(server).start();");
		Thread.sleep(2000);
		//server.stopServer();
		//System.out.println("bye");\\
	}
}
