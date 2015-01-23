package Client;
import java.awt.*;

import javax.swing.*;

import edu.cmu.relativelayout.*;

public class HomeView extends JFrame{
	
	JButton btnCreate;
	JButton btnLogout;
	JLabel lblUsername;
	JPanel pnlChatrooms;
	JPanel pnlChatroomsDescription;
	JPanel pnlChatroomsList;
	JScrollPane pnlScrollList;
	
	public HomeView(){
		super ("Home");
		setLayout(new RelativeLayout());
		
		btnCreate = new JButton("Create Chatroom");
		lblUsername = new JLabel("Name");
		btnLogout = new JButton("Log out");
		
		pnlChatrooms = new JPanel();
		pnlChatrooms.setLayout(new BorderLayout());
		
		pnlChatroomsDescription = new JPanel();
		pnlChatroomsDescription.setLayout(new GridLayout(0,5));
				
		pnlChatroomsList = new JPanel();
		pnlChatroomsList.setLayout(new BorderLayout());
		
		pnlScrollList = new JScrollPane(pnlChatroomsList);
		
		BindingFactory bf = new BindingFactory();
		Binding leftEdge = bf.leftEdge();
		Binding topEdge = bf.topEdge();
		Binding rightEdge = bf.rightEdge();
		Binding leftOfLogout = bf.leftOf(btnLogout);
		Binding belowCreate = bf.below(btnCreate);
		Binding bottomEdge = bf.bottomEdge();
		
		RelativeConstraints btnCreateConstraints = new RelativeConstraints(leftEdge, topEdge);
		RelativeConstraints btnLogoutConstraint = new RelativeConstraints(topEdge, rightEdge);
		RelativeConstraints lblUsernameConstraint = new RelativeConstraints(topEdge, leftOfLogout);
		RelativeConstraints pnlChatroomsConstraint = new RelativeConstraints(belowCreate, rightEdge, leftEdge, bottomEdge);
		
		add(btnCreate, btnCreateConstraints);
		add(btnLogout, btnLogoutConstraint);
		add(lblUsername, lblUsernameConstraint);
		add(pnlChatrooms, pnlChatroomsConstraint);
		
		pnlChatrooms.add(pnlChatroomsDescription, BorderLayout.NORTH);
		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		pnlChatroomsDescription.add(lblName);
		JLabel lblPassword = new JLabel("Locked");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pnlChatroomsDescription.add(lblPassword);
		JLabel lblMembers = new JLabel("Members");
		lblMembers.setHorizontalAlignment(SwingConstants.CENTER);
		pnlChatroomsDescription.add(lblMembers);
		JLabel lblLanguage = new JLabel("Language");
		lblLanguage.setHorizontalAlignment(SwingConstants.CENTER);
		pnlChatroomsDescription.add(lblLanguage);
		
		pnlChatroomsDescription.add(new JLabel());
		
		pnlChatrooms.add(pnlScrollList, BorderLayout.CENTER);
		
	}

	public JButton getBtnCreate() {
		return btnCreate;
	}

	public void setBtnCreate(JButton btnCreate) {
		this.btnCreate = btnCreate;
	}

	public JButton getBtnLogout() {
		return btnLogout;
	}

	public void setBtnLogout(JButton btnLogout) {
		this.btnLogout = btnLogout;
	}

	public JLabel getLblUsername() {
		return lblUsername;
	}

	public void setLblUsername(JLabel lblUsername) {
		this.lblUsername = lblUsername;
	}

	public JPanel getPnlChatroomsList() {
		return pnlChatroomsList;
	}
	
}
