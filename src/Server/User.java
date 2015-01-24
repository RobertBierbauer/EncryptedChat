package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class User {
	private Socket socket;
	private String name;
	private DataInputStream i;
	private DataOutputStream o;
	private Chatroom chatroom;
	private byte[] publickey;
	
	public User(Socket socket, String name) {
		super();
		this.socket = socket;
		this.name = name;
		chatroom = null;
		try {
			i = new DataInputStream(socket.getInputStream());
			o = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	//getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public DataInputStream getDataInputStream(){
		return i;
	}
	
	public DataOutputStream getDataOutputStream(){
		return o;
	}

	public Chatroom getChatroom() {
		return chatroom;
	}

	public void setChatroom(Chatroom chatroom) {
		this.chatroom = chatroom;
	}

	public byte[] getPublickey() {
		return publickey;
	}

	public void setPublickey(byte[] publickey) {
		this.publickey = publickey;
	}
}
