package ru.nsu.ccfit.bogush.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CarStorageController extends SimplyNamed implements Runnable {
	private CarStorage carStorage;
	private CarFactory carFactory;
	private final Thread thread;
	private boolean updateRequested = false;

	private static final Object lock = new Object();

	private static final String LOGGER_NAME = "CarStorageController";
	private static final Logger logger = LogManager.getLogger(LOGGER_NAME);

	public CarStorageController(CarFactory carFactory) {
		logger.traceEntry();
		this.carFactory = carFactory;
		this.thread = new Thread(this, toString());
		logger.traceExit();
	}

	public void setCarStorage(CarStorage carStorage) {
		logger.traceEntry();
		logger.trace("set car storage to " + carStorage);
		this.carStorage = carStorage;
		logger.traceExit();
	}

	public void update() {
		logger.traceEntry();
		logger.trace("update");
		synchronized (lock) {
			updateRequested = true;
			lock.notifyAll();
		}
		logger.traceExit();
	}

	private int carsNeeded() {
		logger.traceEntry();
		logger.trace("calculate amount of cars needed");
		int taskQueueSize = carFactory.getThreadPool().getAwaitingNumber();
		int carStorageSize = carStorage.size();
		int carStorageCapacity = carStorage.capacity();
		int result = carStorageCapacity - carStorageSize - taskQueueSize;
		logger.trace("taskQueueSize = " + taskQueueSize);
		logger.trace("carStorageSize = " + carStorageSize);
		logger.trace("carStorageCapacity = " + carStorageCapacity);
		logger.trace("carsNeeded = " + result);
		return logger.traceExit(result);
	}

	public void start() {
		logger.traceEntry();
		thread.start();
		logger.traceExit();
	}

	public void stop() {
		logger.traceEntry();
		thread.interrupt();
		logger.traceExit();
	}

	@Override
	public void run() {
		logger.traceEntry();
		try {
			while (!Thread.interrupted()) {
				carFactory.requestCars(carsNeeded());
				synchronized (lock) {
					while (!updateRequested) {
						lock.wait();
					}
					updateRequested = false;
				}
			}
		} catch (InterruptedException e) {
			logger.trace("interrupted");
		} finally {
			logger.trace("stopped");
			logger.traceExit();
		}
	}
}
