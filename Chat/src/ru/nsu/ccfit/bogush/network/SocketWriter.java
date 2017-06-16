package ru.nsu.ccfit.bogush.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.bogush.message.Message;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketWriter implements Runnable {
	private static final Logger logger = LogManager.getLogger(SocketWriter.class.getSimpleName());

	private Thread thread;
	private final MessageSender messageSender;
	private LinkedBlockingQueue<Message> messageQueue;

	public SocketWriter(MessageSender messageSender, int queueCapacity) {
		this.messageSender = messageSender;
		this.messageQueue = new LinkedBlockingQueue<>(queueCapacity);
		thread = new Thread(this, this.getClass().getSimpleName());
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				Message msg = messageQueue.take();
				messageSender.sendMessage(msg);
				logger.info("Send {}", msg);
			} catch (MessageSender.Exception e) {
				logger.error("Failed to send message");
			} catch (InterruptedException e) {
				logger.trace("Interrupted");
				break;
			}
		}
	}

	public void start() {
		logger.trace("Start {}", SocketWriter.class.getSimpleName());
		thread.start();
	}

	public void stop() {
		logger.trace("Stop {}", SocketWriter.class.getSimpleName());
		thread.interrupt();
	}

	public void write(Message message) throws InterruptedException {
		logger.trace("Put {} to message queue", message);
		messageQueue.put(message);
	}
}
