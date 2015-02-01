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
	
	//method to write on the output stream
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
				
				//get a message form the stream
				i.read(input);				
				String message = new String (input, "ISO-8859-1");
				command = getCommand(message);
				message = message.substring(message.indexOf(" ")+1);
				String username, password, chatroomName, chatroomPassword;
				
				//check the first keyword of the message
				switch(command){
					//manages a login request
					case LOGIN:
						username = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						password =  message.substring(0);
						if(server.isUser(username, password) && !server.isLoggedIn(username)){
							write(o, "Accepted Login");
							user = server.userConnected(socket, username);
							String chatroomString = getChatroomList();
							write(user.getDataOutputStream(), chatroomString);
						}
						else{
							write(o, "Denied Login");
						}
						break;
						
					//manages a register request
					case REGISTER:
						username = message.substring(0, message.indexOf(" "));
						message = message.substring(message.indexOf(" ")+1);
						password =  message.substring(0);
						if(server.addUser(username, password)){
							write(o, "Accepted Register");
							user = server.userConnected(socket, username);
							if(server.getChatrooms().size() > 0){
								String chatroomString = getChatroomList();
								write(user.getDataOutputStream(), chatroomString);
							}
						}
						else{
							write(o,"Denied Register");
						}
						break;
						
					//manages a create chatroom request
					case CREATE:
						message = message.substring(1);
						chatroomName = message.substring(0, message.indexOf("\" "));
						message = message.substring(message.indexOf("\" ")+2);
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
							broadcastToList(chatroomString, server.getUsersOnHomeView());
						}
						else{
							write(o,"Denied Create");
						}
						break;
						
					//manages a join chatroom request
					case JOIN:
						message = message.substring(1);
						chatroomName = message.substring(0, message.indexOf("\" "));
						message = message.substring(message.indexOf("\" ")+2);
						chatroomPassword = message.substring(0);
						Chatroom joinChatroom = server.isChatroom(chatroomName);
						if(joinChatroom != null){
							if(joinChatroom.getPassword().equals(chatroomPassword)){
								write(o,"Accepted Join \"" + chatroomName + "\"");
								
								//add the user to the chatroom
								LinkedList<User> usersFromChatroom = joinChatroom.getUsers();
								String newMember = "member joined " + user.getName();
								broadcastToList(newMember, usersFromChatroom);
								server.joinChatroom(user, joinChatroom);
								user.setChatroom(joinChatroom);
								
								//forwards a password request to a member of the chatroom
								User userWithChatroomPassword = joinChatroom.getUsers().getFirst();
								write(userWithChatroomPassword.getDataOutputStream(), "keyrequest " + user.getName() + " " + new String(user.getPublickey(), "ISO-8859-1"));
								
								//update the memberlist of the chatroom at all clients in the chatroom
								String allMembers = "";
								for(User user : usersFromChatroom){
									allMembers += user.getName() + " ";
								}
								write(o,"member list " + allMembers);
								String chatroomMemberCount = "chatroom joined \"" + chatroomName + "\"";
								broadcastToList(chatroomMemberCount, server.getUsersOnHomeView());
							}
							else{
								//password for the chatroom was not correct
								write(o,"Denied JoinPW");
							}
						}
						else{
							//chatroom does not exist anymore
							write(o,"Denied Join");
						}
						break;
					//manages the chat request and broadcasts it to the users in the chatroom
					case LEAVE:
						chatroomName = message.substring(1, message.length()-1);
						Chatroom cr = server.isChatroom(chatroomName);
						if(cr != null){
							server.leftChatroom(user, cr);
							String chatroomList = "chatroom left \"" + chatroomName + "\"";
							broadcastToList(chatroomList, server.getUsersOnHomeView());
							broadcastToList("member left " + user.getName(), cr.getUsers());
							write(o, "Accepted Leave");
							String chatroomString = getChatroomList();
							write(o, chatroomString);
						}
						break;
					//manages the chat request and broadcasts it to the users in the chatroom
					case CHAT:
						message = message.substring(1);
						chatroomName = message.substring(0, message.indexOf("\" "));
						message = message.substring(message.indexOf("\" ")+2);
						String chatMessage = "chat " + user.getName() + " " + message;
						cr = server.isChatroom(chatroomName);
						if(cr != null){
							broadcastToList(chatMessage, cr.getUsers());
						}
						break;
					//manages the logout request
					case LOGOUT:
						server.userDisconnected(user);
						user = null;
						write(o,"Accepted Logout");
						break;
					//stores the public key from a user
					case PUBLICKEY:
						user.setPublickey(message.getBytes(Charset.forName("ISO-8859-1")));
						write(o,"Accepted PublicKey");
						break;
					//got the encrypted chatroom key and forwards it to the new user in the chatroom
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
				if(user != null){
					if(server.userDisconnected(user)){
						if(user.getChatroom() != null){
							Chatroom chatroom = user.getChatroom();
							server.leftChatroom(user, chatroom);
							broadcastToList("member left " + user.getName(), chatroom.getUsers());
							broadcastToList("chatroom left \"" + chatroom.getName() + "\"", server.getUsersOnHomeView());
						}
					}
				}
				socket.close ();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	//filters the first word of the message and returns the corresponding command
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
		else if(message.substring(0, message.indexOf(" ")).equals("leave")){
			return Command.LEAVE;
		}
		else{
			return Command.CHAT;
		}
	}
	
	//sends a list of the chatrooms to the users
	private String getChatroomList(){
		LinkedList<Chatroom> chatrooms = new LinkedList<Chatroom>();
		chatrooms = server.getChatrooms();
		String chatroomString = "chatroom list";
		if(chatrooms.size() >0){
			for(Chatroom cr : chatrooms){
				chatroomString += " \"" + cr.getName() + "\" " + cr.getPassword() + " " + cr.getUsers().size() + " " + cr.getLanguage();
			}
		}
		else{
			chatroomString += " empty";
		}
		return chatroomString;
	}
	
	//broadcasts a message to a list of users
	private void broadcastToList (String message, LinkedList<User> users) {
		for(User user : users){
			write(user.getDataOutputStream(), message);
		}
	}
	
	private enum Command {
	    LOGIN, REGISTER, JOIN, CREATE, LEAVE, CHAT, LOGOUT, PUBLICKEY, KEYSEND
	}
	
}
