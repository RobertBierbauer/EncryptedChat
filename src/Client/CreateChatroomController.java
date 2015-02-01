package Client;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateChatroomController implements ActionListener{
	private CreateChatroomView view;
	private Client client;
	private ChatController cc;
	
	//manages the create chatroom view
	public CreateChatroomController(Client client) {
		super();
		this.client = client;
		view = new CreateChatroomView();
		view.getBtnCreate().addActionListener(this);
		view.getBtnCancel().addActionListener(this);
		final Color txtDefault = view.getTxtName().getBackground();
		view.getTxtName().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				view.getTxtName().setBackground(txtDefault);
			}
		});
	}
	
	public void activate(){
		view.getTxtName().setText("");
		view.getTxtPassword().setText("");
		view.getTxtLanguage().setText("");
		client.setContentPane(view.getContentPane());
	}
	
	//chatroom was created successfully
	public void createSuccess(){
		String chatroomName = view.getTxtName().getText();
		client.createChatroomPassword();
		cc = client.getChatController();
		cc.setTitle(chatroomName);
		cc.activate();
	}
	
	//chatroom was not created
	public void createFail(){
		view.getTxtName().setText("");
		view.getTxtName().setBackground(Color.RED);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == view.getBtnCreate()){
			if(view.getTxtName().getText().equals("")){
				view.getTxtName().setBackground(Color.RED);
			}
			else{
				String password = new String(view.getTxtPassword().getPassword());
				client.write("create \"" + view.getTxtName().getText() + "\" " + (password.equals("") ? "null" : password) + " " + (view.getTxtLanguage().getText().equals("") ? "null" : view.getTxtLanguage().getText()));
			}
		}
		else if(e.getSource() == view.getBtnCancel()){
			view.getTxtName().setText("");
			view.getTxtPassword().setText("");
			view.getTxtLanguage().setText("");
			client.getHomeController().activate();
		}
	}
	
}
