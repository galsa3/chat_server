package chatserver;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ChatClient {
	public static void main(String[] args) {
		ChatClient gal = new ChatClient("gal");
		try {
			gal.startClient();
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	private String username;

	public ChatClient(String username) {
		this.username = username;
	}

	public void startClient() throws IOException, ClassNotFoundException, InterruptedException {
		int portNumber = ProtocolPort.CHAT_PROTOCOL_PORT.getPort();
		System.out.println(username + " Connecting to chat server in port " + portNumber);
		ByteBuffer buffer = ByteBuffer.allocate(2048);
		SocketChannel clientSocket = null;

		ServerMessage registerMessage = new ServerMessage(ProtocolType.CHAT_SERVER,
				new ChatMessage(ChatProtocolKeys.REGISTRATION_REQUEST, username));
		ServerMessage receivedMssage = null;

		byte[] messageArray = ServerMessage.toByteArray(registerMessage);
		try {
			clientSocket = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// send register message
		messageArray = ServerMessage.toByteArray(registerMessage);				
		buffer.clear();
		buffer.put(messageArray);
		buffer.flip();
		clientSocket.write(buffer);

		// receive registration result
		buffer.clear();
		clientSocket.read(buffer);
		receivedMssage = (ServerMessage) ServerMessage.toObject(buffer.array());
		System.out.println("response:" + receivedMssage.getData().getKey());
		System.out.println("response: " + receivedMssage.getData().getData());
		Thread.sleep(1000);
		new Thread(new ReceieveMessageFromServer(clientSocket)).start();
		startGettingInput(clientSocket);
	}

	private class ReceieveMessageFromServer implements Runnable {
		private SocketChannel clientSocket;
		private ByteBuffer buffer = ByteBuffer.allocate(4096);
		private ServerMessage receivedMssage = null;

		public ReceieveMessageFromServer(SocketChannel clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			while (true) {
				buffer.clear();
				try {
					clientSocket.read(buffer);
					receivedMssage = (ServerMessage) ServerMessage.toObject(buffer.array());
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}

//				System.out.println(receivedMssage.getData().getKey());
				System.out.println(receivedMssage.innerMessage.getData());

			}
		}
	}

	private void startGettingInput(SocketChannel clientSocket) {
		try(Scanner sc = new Scanner(System.in)){
			ByteBuffer buffer = ByteBuffer.allocate(2048);

			String input = "";
			while (!input.equalsIgnoreCase("exit")) {
				System.out.print("Enter message: ");
				input = sc.nextLine();
				if (input.length() < 80) {
					sendMessage(input, buffer, clientSocket);
				}
				else {
					System.out.println("input cannot exced 80 chars");
				}
			}
		}		
	}

	private void sendMessage(String input, ByteBuffer buffer, SocketChannel clientSocket) {
		ServerMessage orginalMessage = new ServerMessage(ProtocolType.CHAT_SERVER, new ChatMessage(ChatProtocolKeys.MESSAGE, input));
		byte[] messageArray;
		try {
			System.out.println("client sending message");
			messageArray = ServerMessage.toByteArray(orginalMessage);			
			buffer.clear();
			buffer.put(messageArray);
			buffer.flip();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		

		try {
			clientSocket.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
