package Client;
import javax.swing.*;

import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.BindingFactory;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

import java.awt.*;

public class LoginView extends JFrame{
	
	private JPanel pnlInputFields;
	private JPanel pnlButtons;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JButton btnLogin;
	private JButton btnRegister;
	
	public LoginView(){
		super ("Login");
		setLayout(new GridLayout(2,1,0,8));
		pnlInputFields = new JPanel();
		pnlInputFields.setLayout(new GridLayout(2,1));
		pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(2,1,0,5));
		add(pnlInputFields);
		add(pnlButtons);
		
		txtUsername = new JTextField ();
		txtPassword = new JPasswordField ();
		btnLogin = new JButton("Login");
		btnRegister = new JButton("Register");
		
		pnlInputFields.add (txtUsername);
		pnlInputFields.add (txtPassword);
		pnlButtons.add (btnLogin);
		pnlButtons.add (btnRegister);
		txtUsername.requestFocus();
	}

	public JTextField getTxtUsername() {
		return txtUsername;
	}

	public void setTxtUsername(JTextField txtUsername) {
		this.txtUsername = txtUsername;
	}

	public JTextField getTxtPassword() {
		return txtPassword;
	}

	public void setTxtPassword(JPasswordField txtPassword) {
		this.txtPassword = txtPassword;
	}

	public JButton getBtnLogin() {
		return btnLogin;
	}

	public void setBtnLogin(JButton btnLogin) {
		this.btnLogin = btnLogin;
	}

	public JButton getBtnRegister() {
		return btnRegister;
	}

	public void setBtnRegister(JButton btnRegister) {
		this.btnRegister = btnRegister;
	}
}
