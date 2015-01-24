package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//handles the messages from the server
public class ChatHandler extends Thread{
	private Client client;
	private DataInputStream i;
	private DataOutputStream o;
	private LoginController lc;
	private ChatController cc;
	private CreateChatroomController ccc;
	private HomeController hc;
	
	public ChatHandler(Client client){
		this.client = client;
		i = client.getInputStream();
		o = client.getOutputStream();
		cc = client.getChatController();
	}
	
	public void run(){
		while(true){
			try {
				int length = i.readInt();
				byte[] input = new byte[length];
				i.read(input);
				String message = new String(input, "ISO-8859-1");
				String firstWord = message.substring(0, message.indexOf(" "));
				message = message.substring(message.indexOf(" ") +1);
				
				//manages accept messages from the server
				if(firstWord.equals("Accepted")){
					if(message.substring(0).equals("Login") || message.substring(0).equals("Register")){
						if(lc == null){
							lc = client.getLoginController();
						}
						lc.loginRegisterSuccess();
					}
					else if(message.substring(0).equals("Create")){
						if(ccc == null){
							ccc = client.getCreateChatroomController();
						}
						ccc.createSuccess();
					}
					else if(message.indexOf(" ") != -1 && message.substring(0, message.indexOf(" ")).equals("Join")){
						message = message.substring(message.indexOf(" ")+1);
						if(hc == null){
							hc = client.getHomeController();
						}
						hc.enterChatroom(message);
					}
					else if(message.substring(0).equals("Logout")){
						message = message.substring(message.indexOf(" ")+1);
						if(hc == null){
							hc = client.getHomeController();
						}
						hc.logout();
					}
					else if(message.substring(0).equals("Publickey")){}
				}
				//manages denied messages from the server
				else if(firstWord.equals("Denied")){
					if(message.substring(0).equals("Login")){
						if(lc == null){
							lc = client.getLoginController();
						}
						lc.loginFail();
					}
					else if(message.substring(0).equals("Register")){
						if(lc == null){
							lc = client.getLoginController();
						}
						lc.registerFail();
					}
					else if(message.substring(0).equals("Create")){
						if(ccc == null){
							ccc = client.getCreateChatroomController();
						}
						ccc.createFail();
					}
					else if(message.substring(0).equals("JoinPW")){
						hc.showPasswordFailedBox();
					}
					else if(message.substring(0).equals("Join")){
						hc.showJoinFailedBox();
					}
				}
				//manages other messages
				else{
					Command command = getCommand(firstWord);
					String operation = "";
					switch(command){
						//manages all member messages
						case MEMBER:
							operation = message.substring(0, message.indexOf(" "));
							message = message.substring(message.indexOf(" ") +1);
							//got a member list from the server
							if(operation.equals("list")){
								LinkedList<String> members = new LinkedList<String>();
								while(message.indexOf(" ") != -1){
									members.add(message.substring(0, message.indexOf(" ")));
									message = message.substring(message.indexOf(" ") +1);
								}
								members.add(message.substring(0));
								cc.setMemberList(members);
							}
							//a member joined the chat
							else if(operation.equals("joined")){
								String newMemberName = message.substring(0);
								cc.addMember(newMemberName);
							}
							//a member left the chat
							else if(operation.equals("left")){
								String newMemberName = message.substring(0);
								cc.removeMember(newMemberName);
							}
							break;
						//manages chatroom messages
						case CHATROOM:
							operation = message.substring(0, message.indexOf(" "));
							message = message.substring(message.indexOf(" ") +1);
							//got a list of chatrooms
							if(operation.equals("list")){
								LinkedList<Chatroom> chatrooms = new LinkedList<Chatroom>();
								while(message.indexOf(" ") != -1){
									String chatroomName = message.substring(0, message.indexOf(" "));
									message = message.substring(message.indexOf(" ") +1);
									String chatroomPassword = message.substring(0, message.indexOf(" "));
									message = message.substring(message.indexOf(" ") +1);
									int memberCount = Integer.parseInt(message.substring(0, message.indexOf(" ")));
									message = message.substring(message.indexOf(" ") +1);
									String chatroomLanguage = message.indexOf(" ") != -1 ? message.substring(0, message.indexOf(" ")) : message.substring(0);
									message = message.substring(message.indexOf(" ") +1);
									chatrooms.add(new Chatroom(chatroomName, chatroomPassword, chatroomLanguage, memberCount));
								}
								if(hc == null){
									hc = client.getHomeController();
								}
								hc.setChatroomList(chatrooms);
							}
							//joined a chatroom
							else if(operation.equals("joined")){
								String chatroomName = message.substring(0);
								message = message.substring(message.indexOf(" ") +1);
								if(hc == null){
									hc = client.getHomeController();
								}
								hc.addMemberInChatroom(chatroomName);
							}
							break;
						//manages the chat messages
						case CHAT:
							String username = message.substring(0, message.indexOf(" "));
							byte[] m = message.substring(message.indexOf(" ") +1).getBytes("ISO-8859-1");
							if(cc == null){
								cc = client.getChatController();
							}
							cc.newMessage(username, m);
							break;
						//manages the key request
						case KEYREQUEST:
							String user = message.substring(0, message.indexOf(" "));
							String userPubKey = message.substring(message.indexOf(" ") +1);
							byte[] password = client.getChatroomPassword();
							byte[] pubkey = userPubKey.getBytes(Charset.forName("ISO-8859-1"));

							//encrypts the chat key and sends it to the server
							try{
								Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
								PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubkey));
								cipher.init(Cipher.ENCRYPT_MODE, key);
								password = cipher.doFinal(password);
							}catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
								e.printStackTrace();
							} 
							client.write("keysend " + user + " " + new String(password, "ISO-8859-1"));
							break;
						//got key for the chat
						case KEYANSWER:
							String chatroomPassword = message.substring(0);
							client.setChatroomPassword(chatroomPassword.getBytes(Charset.forName("ISO-8859-1")));
							break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//filters the command from a message
	private Command getCommand(String command){
		if(command.equals("member")){
			return Command.MEMBER;
		}else if(command.equals("chatroom")){
			return Command.CHATROOM;
		}else if(command.equals("chat")){
			return Command.CHAT;
		}else if(command.equals("keyanswer")){
			return Command.KEYANSWER;
		}else{
			return Command.KEYREQUEST;
		}
	}
	
	private enum Command {
		MEMBER, CHATROOM, CHAT, KEYREQUEST, KEYANSWER
	}
}
