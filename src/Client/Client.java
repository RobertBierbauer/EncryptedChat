package Client;
import java.net.*;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

public class Client{
	private DataInputStream i;
	private DataOutputStream o;
	private LoginController lc;
	private ChatController cc;
	private CreateChatroomController ccc;
	private HomeController hc;
	private JFrame frame;
	private ChatHandler ch;
	private String username;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private byte[] chatPassword;
	
	//manages the frame and switches the content view
	public Client (InputStream i, OutputStream o) {
		this.i = new DataInputStream(i);
		this.o = new DataOutputStream(o);
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	            	System.exit(0);
	            }
		});
		frame.setLayout(new BorderLayout());
		frame.setSize(500, 400);
		frame.setMinimumSize(new Dimension(500, 400));
		frame.setVisible(true);
		
		lc = new LoginController(this);
		cc = new ChatController(this);
		hc = new HomeController(this);
		ccc = new CreateChatroomController(this);
		
		ch = new ChatHandler(this);
		ch.start();
		
		lc.activate();
	}
	
	//generates the RSA public key for this client
	public void generateRSA(){		
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			publicKey = keyPair.getPublic();
			write(new String("publickey " + new String(publicKey.getEncoded(), "ISO-8859-1")));			
			privateKey = keyPair.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	//method to write messages to the server
	public void write(String message){
		try {
			byte[] m = message.getBytes("ISO-8859-1");
			o.writeInt(m.length);
			o.write(m);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DataInputStream getInputStream(){
		return i;
	}
	
	public DataOutputStream getOutputStream(){
		return o;
	}
	
	//changes the content from the content pane
	public void setContentPane(Container contentPane){
		frame.setContentPane(contentPane);
		frame.revalidate();
		frame.repaint();
	}
	
	public LoginController getLoginController(){
		return lc;
	}
	
	public HomeController getHomeController(){
		return hc;
	}
	
	public ChatController getChatController(){
		return cc;
	}

	public CreateChatroomController getCreateChatroomController(){
		return ccc;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username){
		this.username = username;
	}
	
	//creates a random password for the chat
	public void createChatroomPassword(){
		SecureRandom random = new SecureRandom();
		chatPassword = new byte[16];
		random.nextBytes(chatPassword);
	}
	
	//encrypts a chat message
	public byte[] encryptMessage(String plaintext){
		SecretKeySpec keySpec = null;
		keySpec = new SecretKeySpec(chatPassword, "AES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			return cipher.doFinal(plaintext.getBytes("ISO-8859-1"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//decrypts a chat message
	public String decryptMessage(byte[] encryptedMessage){
		SecretKeySpec keySpec = null;
		keySpec = new SecretKeySpec(chatPassword, "AES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			return new String(cipher.doFinal(encryptedMessage), "ISO-8859-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] getChatroomPassword(){
		return chatPassword;
	}
	
	//decrypts the chat key with the private key
	public void setChatroomPassword(byte[] chatPassword){
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			chatPassword = cipher.doFinal(chatPassword);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		this.chatPassword = chatPassword;
	}
	
	public static void main (String args[]) throws IOException {
		Socket s = new Socket ("localhost", 8080);
		Client client = new Client (s.getInputStream (), s.getOutputStream ());
	}
	
}
