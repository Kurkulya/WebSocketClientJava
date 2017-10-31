
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public class CFrame extends JFrame
{
	JTextField chat;
	JTextField message;
	WebSocketClient client = null;

	
	public CFrame() 
	{
			
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		client = new WebSocketClient();
		setTitle("CalcNB");
		setBounds(0,0,300,300);
		setLayout(new GridLayout(1,1));
		
		JPanel jp=new JPanel();
						
		add(jp);
		
		jp.setLayout(new GridLayout(4, 2));
			
		addFields(jp);
		addButton(jp);
		
		setVisible(true);
	}
	
	private void addFields(JPanel jp)
	{
	
		chat=new JTextField(200);
		chat.setName("chat");
		jp.add(chat);				
		
		message=new JTextField(100);
		message.setName("message");
		jp.add(message);
	}
	
	private void addButton(JPanel jp)
	{
		JButton send=new JButton("Send");
		send.setName("send");
		jp.add(send);
		
		send.addActionListener(new ActionListener() 
		{
			//@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String caption = ((JButton) arg0.getSource()).getText();
				client.sendMessage(caption);
				chat.setText(chat.getText() + "\nYou: " + caption);
			}
		});
		
		JButton connect=new JButton("Connect");
		connect.setName("connect");
		jp.add(connect);
		
		connect.addActionListener(new ActionListener() 
		{
			//@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				client.connect();
			}
		});
	}
	
	@ClientEndpoint
	class WebSocketClient 
	{
		WebSocketContainer container = null;
		Session userSession = null;
			
		public WebSocketClient() 
		{
			container = ContainerProvider.getWebSocketContainer();
		}
		
				
		public void connect()
		{		
			try
			{	            
				container.connectToServer(this, URI.create("ws://127.0.0.1:8888//ChatServer"));

	        } 
			catch (Exception e1)
			{
				chat.setText(chat.getText() + "\n" + e1.getMessage());
	        }
		}
			
	    @OnOpen
	    public void onOpen(Session userSession) {
	    	chat.setText(chat.getText() + "\nConnection open!");
	        this.userSession = userSession;
	    }
	
	    @OnClose
	    public void onClose(Session userSession, CloseReason reason) {
	    	chat.setText(chat.getText() + "\nConnection closed!");
	        this.userSession = null;
	    }
	
	    @OnMessage
	    public void onMessage(String message) {
	    	chat.setText(chat.getText() + "\nServer: " + message);
	    }
	
	    public void sendMessage(String message) {
	        this.userSession.getAsyncRemote().sendText(message);
	    }
	}
}
