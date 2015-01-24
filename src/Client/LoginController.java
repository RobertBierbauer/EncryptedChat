package Client;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class LoginController implements ActionListener{
	private LoginView view;
	private Client client;
	private HomeController hc;
	
	//manages the login view
	public LoginController(Client client){
		super();
		view = new LoginView();
		this.client = client;
		
		view.getBtnLogin().addActionListener(this);
		view.getBtnRegister().addActionListener(this);
		
		final Color txtDefault = view.getTxtUsername().getBackground();
		view.getTxtUsername().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				view.getTxtUsername().setBackground(txtDefault);
			}
		});
		view.getTxtPassword().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				view.getTxtPassword().setBackground(txtDefault);
			}
		});
	}
	
	public void activate(){
		view.getTxtUsername().setText("");
		view.getTxtPassword().setText("");
		client.setContentPane(view.getContentPane());
	}
	
	//login or register was successful
	public void loginRegisterSuccess(){
		client.setUsername(view.getTxtUsername().getText());
		client.generateRSA();
		hc = client.getHomeController();
		hc.activate();
	}
	
	//login failed
	public void loginFail(){
		view.getTxtUsername().setText("");
		view.getTxtUsername().setBackground(Color.RED);
		view.getTxtPassword().setText("");
		view.getTxtPassword().setBackground(Color.RED);
	}

	//register failed
	public void registerFail(){
		view.getTxtUsername().setBackground(Color.RED);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == view.getBtnLogin()){
			boolean filled = true;
			if(view.getTxtUsername().getText().equals("")){
				view.getTxtUsername().setBackground(Color.RED);
				filled = false;
			}
			if(view.getTxtPassword().getText().equals("")){
				view.getTxtPassword().setBackground(Color.RED);
				filled = false;
			}
			if(filled){
				client.write("login " + view.getTxtUsername().getText() + " " + view.getTxtPassword().getText());
			}
		}
		else{
			boolean filled = true;
			if(view.getTxtUsername().getText().equals("")){
				view.getTxtUsername().setBackground(Color.RED);
				filled = false;
			}
			if(view.getTxtPassword().getText().equals("")){
				view.getTxtPassword().setBackground(Color.RED);
				filled = false;
			}
			if(filled){
				client.write("register " + view.getTxtUsername().getText() + " " + view.getTxtPassword().getText());
			}
		}
	}
	
	
}
