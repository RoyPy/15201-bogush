package ru.nsu.ccfit.bogush.message.types;

import ru.nsu.ccfit.bogush.message.Message;
import ru.nsu.ccfit.bogush.message.MessageHandler;
import ru.nsu.ccfit.bogush.network.Session;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "success")
@XmlType
public class LoginSuccess implements Message {
	@XmlElement(name = "session")
	private Session session;

	public LoginSuccess() {}

	public LoginSuccess(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	@Override
	public void handleBy(MessageHandler handler) {
		handler.handle(this);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + session +")";
	}
}
