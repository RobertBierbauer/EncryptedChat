package Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

//Controller for the ChatView
public class ChatController implements ActionListener{
	private ChatView view;
	private Client client;
	private String chatroomName;
	private String smilingSmiley;
	private String unhappySmiley;
	private String heart;

	public ChatController(Client client) {
		super();
		this.view = new ChatView();
		this.client = client;
		
		view.getBtnSend().addActionListener(this);
		view.getBtnLeave().addActionListener(this);
		smilingSmiley = getClass().getResource("images/Smiley.png" ).toString();
		unhappySmiley = getClass().getResource("images/UnhappySmiley.png" ).toString();
		heart = getClass().getResource("images/Heart.png" ).toString();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == view.getBtnSend()){
			if(!view.getTxtInput().getText().equals("")){
				byte[] m = client.encryptMessage(view.getTxtInput().getText());
				try {
					client.write("chat \"" + chatroomName + "\" " + new String(m, "ISO-8859-1"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				view.getTxtInput().setText("");
			}
		}
		else if(e.getSource() == view.getBtnLeave()){
			client.write("leave \"" + chatroomName + "\"");
		}		
	}
	
	//sets the title of the chatroom
	public void setTitle(String chatroomName) {
		view.setTitle(chatroomName);
		this.chatroomName = chatroomName;
	}
	
	public void activate(){
		client.setContentPane(view.getContentPane());
		view.getTxaOutput().setText("");
		view.getTxtInput().setText("");
		view.getModel().removeAllElements();
	}
	
	//sets the memberlist in the chat
	public void setMemberList(LinkedList<String> members){
		view.getModel().removeAllElements();
		for(String member : members){
			view.getModel().addElement(member);
		}
		view.revalidate();
		view.repaint();
	}
	
	//adds a new message in the chat and replaces smileys
	public void newMessage(String username, byte[] encryptedMessage){
		String message = client.decryptMessage(encryptedMessage);
		String output = view.getTxaOutput().getText();
		String newMessage =  "<b>" + username + ":</b> ";
		while(message.indexOf(" :)") >= 0 || message.indexOf(" :-)") >= 0 || message.indexOf(" :(") >= 0 || message.indexOf(" :-(") >= 0 || message.indexOf(" <3") >= 0){
			if(message.indexOf(" :)") >= 0){
				newMessage += message.substring(0, message.indexOf(" :)"));
				message = message.substring(message.indexOf(" :)")+3);
				newMessage += " <img src='" + smilingSmiley + "'/>";
			}else if(message.indexOf(" :-)") >= 0){
				newMessage += message.substring(0, message.indexOf(" :-)"));
				message = message.substring(message.indexOf(" :-)")+4);
				newMessage += " <img src='" + smilingSmiley + "'/>";
			}else if(message.indexOf(" :(") >= 0){
				newMessage += message.substring(0, message.indexOf(" :("));
				message = message.substring(message.indexOf(" :(")+3);
				newMessage += " <img src='" + unhappySmiley + "'/>";
			}else if(message.indexOf(" :-(") >= 0){
				newMessage += message.substring(0, message.indexOf(" :-("));
				message = message.substring(message.indexOf(" :-(")+4);
				newMessage += " <img src='" + unhappySmiley + "'/>";
			}else if(message.indexOf(" <3") >= 0){
				newMessage += message.substring(0, message.indexOf(" <3"));
				message = message.substring(message.indexOf(" <3")+3);
				newMessage += " <img src='" + heart + "'/>";
			}
		}
		newMessage += message + "<br>";
		output = output.substring(0, output.indexOf("</body>")) + newMessage + output.substring(output.indexOf("</body>"));
		view.getTxaOutput().setText(output);
		view.getTxaOutput().setSelectionEnd(view.getTxaOutput().getText().length());
	}
	
	//adds a member to the list
	public void addMember(String member){
		view.getModel().addElement(member);
		view.revalidate();
		view.repaint();
	}
	
	//removes a member from the list
	public void removeMember(String member){
		view.getModel().removeElement(member);
		view.revalidate();
		view.repaint();
	}

}
