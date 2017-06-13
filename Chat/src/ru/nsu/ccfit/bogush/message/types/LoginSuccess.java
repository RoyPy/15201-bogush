package ru.nsu.ccfit.bogush.message.types;

import ru.nsu.ccfit.bogush.message.Message;
import ru.nsu.ccfit.bogush.message.MessageHandler;

public class LoginSuccess implements Message {
	private String successMessage;

	public LoginSuccess(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	@Override
	public void handleBy(MessageHandler handler) {
		handler.handle(this);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(\"" + successMessage + "\")";
	}
}
