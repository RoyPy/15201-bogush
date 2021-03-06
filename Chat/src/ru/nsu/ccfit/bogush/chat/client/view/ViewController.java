package ru.nsu.ccfit.bogush.chat.client.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.bogush.chat.User;
import ru.nsu.ccfit.bogush.chat.client.Client;
import ru.nsu.ccfit.bogush.chat.client.UserListChangeListener;
import ru.nsu.ccfit.bogush.chat.client.view.handlers.*;
import ru.nsu.ccfit.bogush.chat.message.types.TextMessage;
import ru.nsu.ccfit.bogush.chat.network.LostConnectionListener;
import ru.nsu.ccfit.bogush.chat.network.ReceiveTextMessageListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ViewController implements UserListChangeListener, ReceiveTextMessageListener, LostConnectionListener {
	private static final Logger logger = LogManager.getLogger(ViewController.class.getSimpleName());

	private Client client;
	private final String ip;
	private final String port;
	private final String defaultNickname;
	private String nickname;

	private ConnectView connectView;
	private LoginView loginView;
	private ChatView chatView;

	private ArrayList<ChatEventHandler> chatEventHandlers = new ArrayList<>();

	public ViewController(Client client, String ip, String port, String defaultNickname) {
		this.client = client;
		this.ip = ip;
		this.port = port;
		this.defaultNickname = defaultNickname;
		showConnectView();
	}

	private void createConnectView() {
		logger.trace("Create connect window");
		connectView = new ConnectView(this, ip, port);
		connectView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				logger.trace("connect window closed");
				if (loginView != null) {
					loginView.dispose();
				}
			}
		});
	}

	private void createLoginView() {
		logger.trace("Create login window");
		loginView = new LoginView(this, defaultNickname);

		loginView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				logger.trace("Login window closed");
				showConnectView();
				disconnect();
			}
		});
	}

	private void createChatView() {
		logger.trace("Create chat window");
		chatView = new ChatView(this, nickname);
		chatView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				logger.trace("Chat window closed");
				logout();
			}
		});
	}

	private void showConnectView() {
		logger.trace("Show connect window");
		if (connectView == null) createConnectView();
		connectView.pack();
		connectView.setVisible(true);
	}

	private void hideConnectView() {
		logger.trace("Hide connect window");
		connectView.setVisible(false);
	}

	private void showLoginView() {
		logger.trace("Show login window");
		if (loginView == null) createLoginView();
		loginView.pack();
		loginView.setVisible(true);
	}

	private void hideLoginView() {
		logger.trace("Hide login window");
		loginView.setVisible(false);
	}

	private void showChatView() {
		logger.trace("Show chat window");
		if (chatView == null) createChatView();
		chatView.pack();
		chatView.setVisible(true);
	}

	private void hideChatView() {
		logger.trace("Hide chat window");
		if (chatView != null)
			chatView.setVisible(false);
	}

	void connect(String host, int port) {
//		logger.trace("Connecting to {}:{}", host, port);
		for (ConnectHandler handler : chatEventHandlers) {
			if (!handler.connect(host, port)) {
				new AlertDialog(connectView, "Connect", "Failed to connect to server");
				return;
			}
		}
//		logger.trace("Connected successfully");
		hideConnectView();
		showLoginView();
	}

	private void disconnect() {
//		logger.trace("Disconnecting");
		for (DisconnectHandler handler : chatEventHandlers) {
			handler.disconnect();
		}
	}

	void login(String nickname) {
//		logger.trace("Logging in with nickname \"{}\"", nickname);
		this.nickname = nickname;
		createChatView();
		for (LoginHandler loginHandler : chatEventHandlers) {
			loginHandler.login(nickname);
		}
		chatView.addUser(client.getUser());
		showChatView();
		hideLoginView();
	}

	private void logout() {
//		logger.trace("Logging out");
		for (LogoutHandler logoutHandler : chatEventHandlers) {
			logoutHandler.logout();
		}
		hideChatView();
		showLoginView();
	}

	@Override
	public void lostConnection() {
		hideChatView();
		hideLoginView();
		showConnectView();
	}

	@Override
	public void userEntered(User user) {
		if (chatView == null) {
			logger.warn("User entered but chat view was not created yet");
		} else {
			chatView.addUser(user);
			chatView.appendUserEntered(user.getName());
		}
	}

	@Override
	public void userLeft(User user) {
		chatView.removeUser(user);
		chatView.appendUserLeft(user.getName());
	}

	@Override
	public void userListReceived(User[] users) {
		chatView.removeAllUsers();
		for (User user : users) {
			chatView.addUser(user);
		}
	}

	void sendTextMessage(String text) {
		logger.trace("Sending text message \"{}\"", text);
		for (SendMessageHandler handler : chatEventHandlers) {
			handler.sendMessage(text);
		}
	}

	User getUser() {
		return client.getUser();
	}

	@Override
	public void textMessageReceived(User author, TextMessage msg) {
		chatView.appendTextMessage(author.getName(), msg.getText());
	}

	public void addChatEventHandler(ChatEventHandler handler) {
		chatEventHandlers.add(handler);
	}

	public ConnectView getConnectView() {
		return connectView;
	}

	public LoginView getLoginView() {
		return loginView;
	}

	public ChatView getChatView() {
		return chatView;
	}
}
