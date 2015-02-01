package Client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class HomeController implements ActionListener{
	private HomeView view;
	private Client client;
	private CreateChatroomController ccc;
	private LoginController lc;
	private ChatController cc;
	private LinkedList<Chatroom> chatrooms;

	//manages the home view
	public HomeController(Client client) {
		this.view = new HomeView();
		this.client = client;
		view.getBtnCreate().addActionListener(this);
		view.getBtnLogout().addActionListener(this);
		view.getLblUsername().setText(client.getUsername());
	}

	public void activate(){
		client.setContentPane(view.getContentPane());
	}

	//joins a chatroom
	public void enterChatroom(String chatroomName){
		cc = client.getChatController();
		cc.setTitle(chatroomName);
		cc.activate();
	}
	
	public void logout(){
		lc = client.getLoginController();
		lc.activate();
	}
	
	//updates the list of chatrooms on the home view
	public void updateChatroomListPanel(){
		view.getPnlChatroomsList().removeAll();
		JPanel currentPanel = view.getPnlChatroomsList();
		for(Chatroom chatroom : chatrooms){
			final JPanel tmpPanel = new JPanel(new GridLayout(1, 5));
			tmpPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			final JLabel lblName = new JLabel(""+ chatroom.getName());
			lblName.setHorizontalAlignment(SwingConstants.CENTER);
			tmpPanel.add(lblName);
			final JLabel lblPassword = new JLabel("" + (chatroom.getPassword() != null ? "Locked" : "Open"));
			lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
			tmpPanel.add(lblPassword);
			final JLabel lblMembers = new JLabel(""+ chatroom.getMembers());
			lblMembers.setHorizontalAlignment(SwingConstants.CENTER);
			tmpPanel.add(lblMembers);
			final JLabel lblLanguage = new JLabel("" + (chatroom.getLanguage() != null ? "" + chatroom.getLanguage() : "Not specified"));
			lblLanguage.setHorizontalAlignment(SwingConstants.CENTER);
			tmpPanel.add(lblLanguage);
			JButton btnJoinChatroom = new JButton("Join");
			tmpPanel.add(btnJoinChatroom);


			btnJoinChatroom.addActionListener( new ActionListener( ) {
				public void actionPerformed( ActionEvent e )
				{
					String chatroomName = lblName.getText();
					if(lblPassword.getText().equals("Locked")){
						JPasswordField txtPassword = new JPasswordField();
						int option = JOptionPane.showConfirmDialog(null, txtPassword, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

						if (option == JOptionPane.OK_OPTION) {
							String password = new String(txtPassword.getPassword());
							client.write("join \"" + chatroomName + "\" " + password);
						}
					}
					else{
						client.write("join \"" + chatroomName + "\" null");
					}
				}
			});


			currentPanel.add(tmpPanel, BorderLayout.NORTH);
			JPanel newCurrentPanel = new JPanel(new BorderLayout());
			currentPanel.add(newCurrentPanel, BorderLayout.CENTER);
			currentPanel = newCurrentPanel;
		}
		view.getPnlChatroomsList().revalidate();
		view.getPnlChatroomsList().repaint();
	}
	
	//password to join the chatroom was wrong
	public void showPasswordFailedBox(){
		JOptionPane.showMessageDialog(null, "Password was not correct!");
	}

	//chatroom could not be joined
	public void showJoinFailedBox(){
		JOptionPane.showMessageDialog(null, "Chatroom does not exist anymore!");
	}
	
	public void setChatroomList(LinkedList<Chatroom> chatrooms){
		this.chatrooms = chatrooms;
		updateChatroomListPanel();
	}
	
	//increments the counter of members in the chatroom list
	public void addMemberInChatroom(String chatroomName){
		for(Chatroom chatroom : chatrooms){
			if(chatroom.getName().equals(chatroomName)){
				chatroom.incrementMembers();
				break;
			}
		}
		
		updateChatroomListPanel();
	}

	//decrements the counter of members in the chatroom list and removes the chatroom if empty
	public void removeMemberInChatroom(String chatroomName){
		for(Chatroom chatroom : chatrooms){
			if(chatroom.getName().equals(chatroomName)){
				chatroom.decrementMembers();
				if(chatroom.getMembers() == 0){
					chatrooms.remove(chatroom);
				}
				break;
			}
		}
		updateChatroomListPanel();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == view.getBtnCreate()){
			ccc = client.getCreateChatroomController();
			ccc.activate();
		}
		else if(e.getSource() == view.getBtnLogout()){
			client.write("logout " + client.getUsername());
		}
	}
}
