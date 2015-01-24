package Client;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class CreateChatroomView extends JFrame{
	
	private JTextField txtName;
	private JTextField txtPassword;
	private JTextField txtLanguage;
	private JButton btnCreate;
	
	//creates the view to create a chatroom
	public CreateChatroomView(){
		super("Create Chatroom");
		setLayout(new GridLayout(7,1));
		add(new JLabel("Chatroom Name"));
		txtName = new JTextField();
		add(txtName);
		add(new JLabel("Password (optional)"));
		txtPassword = new JTextField();
		add(txtPassword);
		add(new JLabel("Language (optional)"));
		txtLanguage = new JTextField();
		add(txtLanguage);
		btnCreate = new JButton("Create Chatroom");
		add(btnCreate);
	}

	public JTextField getTxtName() {
		return txtName;
	}

	public void setTxtName(JTextField txtName) {
		this.txtName = txtName;
	}

	public JTextField getTxtPassword() {
		return txtPassword;
	}

	public void setTxtPassword(JTextField txtPassword) {
		this.txtPassword = txtPassword;
	}

	public JTextField getTxtLanguage() {
		return txtLanguage;
	}

	public void setTxtLanguage(JTextField txtLanguage) {
		this.txtLanguage = txtLanguage;
	}

	public JButton getBtnCreate() {
		return btnCreate;
	}

	public void setBtnCreate(JButton btnCreate) {
		this.btnCreate = btnCreate;
	}

}
