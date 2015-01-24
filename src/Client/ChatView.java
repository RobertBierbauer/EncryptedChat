package Client;
import javax.swing.*;

import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.BindingFactory;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

import java.awt.*;

//creates the Chat view
public class ChatView extends JFrame{
	private JScrollPane scrOutput;
	private JTextPane txaOutput;
	private JTextField txtInput;
	private JScrollPane scrMemberlist;
	private DefaultListModel<String> model;
	private JList<String> lstMemberlist;
	private JButton btnSend;
	private JButton btnLeave;
	
	public ChatView() {
		super("Chatroom");
		setLayout (new RelativeLayout());
		
		txaOutput = new JTextPane();
		txaOutput.setEditable(false);
		txaOutput.setContentType("text/html");
		scrOutput = new JScrollPane(txaOutput);
		model = new DefaultListModel<String>();
		lstMemberlist = new JList<String>(model);
		scrMemberlist = new JScrollPane(lstMemberlist);
		scrMemberlist.setPreferredSize(new Dimension(150, 0));
		txtInput = new JTextField();
		btnSend = new JButton("Send");
		btnLeave = new JButton("Leave");
		
		//sets the location fo the components
		BindingFactory bf = new BindingFactory();
		Binding leftEdge = bf.leftEdge();
		Binding topEdge = bf.topEdge();
		Binding rightEdge = bf.rightEdge();
		Binding belowBtnLeave = bf.below(btnLeave);
		Binding leftOfScrMemberlist= bf.leftOf(scrMemberlist);
		Binding rightOfScrOutput= bf.rightOf(scrOutput);
		Binding leftOfBtnSend= bf.leftOf(btnSend);
		Binding aboveTxtInput = bf.above(txtInput);
		Binding aboveBtnSend = bf.above(btnSend);
		Binding bottomEdge = bf.bottomEdge();
		
		RelativeConstraints btnLeaveConstraints = new RelativeConstraints(topEdge, leftEdge);
		RelativeConstraints scrOutputConstraints = new RelativeConstraints(leftEdge, belowBtnLeave, leftOfScrMemberlist, aboveBtnSend);
		RelativeConstraints scrMemberlistConstraint = new RelativeConstraints(belowBtnLeave, rightEdge, aboveBtnSend);
		RelativeConstraints txtInputConstraint = new RelativeConstraints(bottomEdge, leftEdge, leftOfBtnSend);
		RelativeConstraints btnSendConstraint = new RelativeConstraints(bottomEdge, rightEdge);
		
		add(btnLeave, btnLeaveConstraints);
		add(scrOutput, scrOutputConstraints);
		add(scrMemberlist, scrMemberlistConstraint);
		add(txtInput, txtInputConstraint);
		add(btnSend, btnSendConstraint);
		txtInput.requestFocus();
	}

	public JTextPane getTxaOutput() {
		return txaOutput;
	}

	public void setTxaOutput(JTextPane txaOutput) {
		this.txaOutput = txaOutput;
	}

	public JTextField getTxtInput() {
		return txtInput;
	}

	public void setTxtInput(JTextField txtInput) {
		this.txtInput = txtInput;
	}
	
	public DefaultListModel<String> getModel() {
		return model;
	}

	public JScrollPane getScrMemberlist() {
		return scrMemberlist;
	}

	public void setModel(DefaultListModel<String> model) {
		this.model = model;
	}

	public JList<String> getLstMemberlist() {
		return lstMemberlist;
	}

	public void setLstMemberlist(JList<String> lstMemberlist) {
		this.lstMemberlist = lstMemberlist;
	}

	public JButton getBtnSend() {
		return btnSend;
	}

	public void setBtnSend(JButton btnSend) {
		this.btnSend = btnSend;
	}

	public JButton getBtnLeave() {
		return btnLeave;
	}

	public void setBtnLeave(JButton btnLeave) {
		this.btnLeave = btnLeave;
	}
}
