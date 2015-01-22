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
				else{
					Command command = getCommand(firstWord);
					String operation = "";
					switch(command){
						case MEMBER:
							operation = message.substring(0, message.indexOf(" "));
							message = message.substring(message.indexOf(" ") +1);
							if(operation.equals("list")){
								LinkedList<String> members = new LinkedList<String>();
								while(message.indexOf(" ") != -1){
									members.add(message.substring(0, message.indexOf(" ")));
									message = message.substring(message.indexOf(" ") +1);
								}
								members.add(message.substring(0));
								cc.setMemberList(members);
							}
							else if(operation.equals("joined")){
								String newMemberName = message.substring(0);
								cc.addMember(newMemberName);
							}
							else if(operation.equals("left")){
								String newMemberName = message.substring(0);
								cc.removeMember(newMemberName);
							}
							break;
						case CHATROOM:
							operation = message.substring(0, message.indexOf(" "));
							message = message.substring(message.indexOf(" ") +1);
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
							else if(operation.equals("joined")){
								String chatroomName = message.substring(0);
								message = message.substring(message.indexOf(" ") +1);
								if(hc == null){
									hc = client.getHomeController();
								}
								hc.addMemberInChatroom(chatroomName);
							}
							break;
						case CHAT:
							String username = message.substring(0, message.indexOf(" "));
							byte[] m = message.substring(message.indexOf(" ") +1).getBytes("ISO-8859-1");
							if(cc == null){
								cc = client.getChatController();
							}
							cc.newMessage(username, m);
							break;
						case KEYREQUEST:
							String user = message.substring(0, message.indexOf(" "));
							String userPubKey = message.substring(message.indexOf(" ") +1);
							byte[] password = client.getChatroomPassword();
							byte[] pubkey = userPubKey.getBytes(Charset.forName("ISO-8859-1"));
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
