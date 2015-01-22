package Server;
import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.*;

public class ServerCommunicationHandler extends Thread{
	private Socket socket;
	private Server server;
	private DataInputStream i;
	private DataOutputStream o;
	private Command command;
	private User user;
	public ServerCommunicationHandler (Server server, Socket socket) throws IOException {
		this.socket = socket;
		this.server = server;
		i = new DataInputStream (socket.getInputStream ());
		o = new DataOutputStream (socket.getOutputStream ());
	}
	
	private void write(DataOutputStream o, String message){
		try {	
			byte[] m = message.getBytes("ISO-8859-1");
			o.writeInt(m.length);
			o.write(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run () {
		try {
			while (true) {
				int length = i.readInt();
				byte[] input = new byte[length];
				i.read(input);
				String message = new String (input, "ISO-8859-1");
				System.out.println("Message: " + message);
				command = getCommand(message);
				message = message.substring(message.indexOf(" ")+1);
				String username, password, chatroomName, chatroomPassword;
				switch(command){
					case LOGIN:
						username = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						password =  message.substring(0);
						if(server.isUser(username, password)){
							write(o, "Accepted Login");
							user = server.userConnected(socket, username);
							if(server.getChatrooms().size() > 0){
								String chatroomString = getChatroomList();
								write(user.getDataOutputStream(), chatroomString);
							}
						}
						else{
							write(o, "Denied Login");
						}
						break;
					case REGISTER:
						username = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						password =  message.substring(0);
						if(server.addUser(username, password)){
							write(o, "Accepted Register");
							user = server.userConnected(socket, username);
							System.out.println(server.getChatrooms().size());
							if(server.getChatrooms().size() > 0){
								String chatroomString = getChatroomList();
								write(user.getDataOutputStream(), chatroomString);
							}
						}
						else{
							write(o,"Denied Register");
						}
						break;
					case CREATE:
						chatroomName = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						chatroomPassword = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						String chatroomLanguage =  message.substring(0);
						Chatroom chatroom = server.addChatroom(chatroomName, chatroomPassword, chatroomLanguage);
						if(chatroom != null){
							write(o, "Accepted Create");
							server.joinChatroom(user, chatroom);
							user.setChatroom(chatroom);
							write(o,"member joined " + user.getName());
							String chatroomString = getChatroomList();
							broadcast(chatroomString);
						}
						else{
							write(o,"Denied Create");
						}
						break;
					case JOIN:
						chatroomName = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						chatroomPassword = message.substring(0);
						Chatroom joinChatroom = server.isChatroom(chatroomName);
						if(joinChatroom != null){
							if(joinChatroom.getPassword().equals(chatroomPassword)){
								write(o,"Accepted Join " + chatroomName);
								LinkedList<User> usersFromChatroom = joinChatroom.getUsers();
								String newMember = "member joined " + user.getName();
								broadcastToList(newMember, usersFromChatroom);
								server.joinChatroom(user, joinChatroom);
								user.setChatroom(joinChatroom);
								User userWithChatroomPassword = joinChatroom.getUsers().getFirst();
								write(userWithChatroomPassword.getDataOutputStream(), "keyrequest " + user.getName() + " " + new String(user.getPublickey(), "ISO-8859-1"));
								String allMembers = "";
								for(User user : usersFromChatroom){
									allMembers += user.getName() + " ";
								}
								write(o,"member list " + allMembers);
								String chatroomMemberCount = "chatroom joined " + chatroomName;
								broadcast(chatroomMemberCount);
							}
							else{
								write(o,"Denied JoinPW");
							}
						}
						else{
							write(o,"Denied Join");
						}
						break;
					case CHAT:
						chatroomName = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						String chatMessage = "chat " + user.getName() + " " + message;
						Chatroom cr = server.isChatroom(chatroomName);
						if(cr != null){
							broadcastToList(chatMessage, cr.getUsers());
						}
						break;
					case LOGOUT:
						server.userDisconnected(user);
						user = null;
						write(o,"Accepted Logout");
						break;
					case PUBLICKEY:
						user.setPublickey(message.getBytes(Charset.forName("ISO-8859-1")));
						write(o,"Accepted PublicKey");
						break;
					case KEYSEND:
						username = message.substring(0, message.indexOf(" "));
						chatroomPassword = message.substring(message.indexOf(" ")+1);
						User u = server.getUserByName(username);
						write(u.getDataOutputStream(),"keyanswer " + chatroomPassword);
						break;
				}
			}
		} catch (IOException ex) {
			if(user != null){
				System.out.println("User " + user.getName() + " disconnected...");
			}
		} finally {
			try {
				socket.close ();
				if(user != null){
					if(user.getChatroom() != null){
						server.leftChatroom(user, user.getChatroom());
						for(User u : user.getChatroom().getUsers()){
							write(u.getDataOutputStream(), "member left " + user.getName());
						}
					}
					server.userDisconnected(user);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected Command getCommand(String message) {
		if(message.substring(0, message.indexOf(" ")).equals("login")){
			return Command.LOGIN;
		}
		else if(message.substring(0, message.indexOf(" ")).equals("register")){
			return Command.REGISTER;
		}
		else if(message.substring(0, message.indexOf(" ")).equals("join")){
			return Command.JOIN;
		}
		else if(message.substring(0, message.indexOf(" ")).equals("create")){
			return Command.CREATE;
		}
		else if(message.substring(0, message.indexOf(" ")).equals("logout")){
			return Command.LOGOUT;
		}
		else if(message.substring(0, message.indexOf(" ")).equals("publickey")){
			return Command.PUBLICKEY;
		}
		else if(message.substring(0, message.indexOf(" ")).equals("keysend")){
			return Command.KEYSEND;
		}
		else{
			return Command.CHAT;
		}
	}
	
	private String getChatroomList(){
		LinkedList<Chatroom> chatrooms = new LinkedList<Chatroom>();
		chatrooms = server.getChatrooms();
		String chatroomString = "chatroom list";
		for(Chatroom cr : chatrooms){
			chatroomString += " " + cr.getName() + " " + cr.getPassword() + " " + cr.getUsers().size() + " " + cr.getLanguage();
		}
		return chatroomString;
	}
	
	private void broadcastToList (String message, LinkedList<User> users) {
		for(User user : users){
			write(user.getDataOutputStream(), message);
		}
	}

	private void broadcast (String message) {
		LinkedList<User> users = server.getCurrentUsers();
		for(User user : users){
			write(user.getDataOutputStream(), message);
		}
	}
	
	private enum Command {
	    LOGIN, REGISTER, JOIN, CREATE, CHAT, LOGOUT, PUBLICKEY, KEYSEND
	}
	
}
