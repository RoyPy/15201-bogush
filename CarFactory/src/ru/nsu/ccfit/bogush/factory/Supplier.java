package ru.nsu.ccfit.bogush.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.bogush.factory.storage.Storage;
import ru.nsu.ccfit.bogush.factory.thing.Thing;
import ru.nsu.ccfit.bogush.factory.thing.periodical.SimplePeriodical;

public class Supplier<T extends Thing> extends SimplePeriodical implements Runnable {
	private Storage<T> storage;
	private static final long DEFAULT_PERIOD = 0;
	private Class<T> thingClass;
	private Thread thread;

	private static final String LOGGER_NAME = "Supplier";
	private static final Logger logger = LogManager.getLogger(LOGGER_NAME);

	public Supplier(Storage<T> storage, Class<T> thingClass) {
		this(storage, thingClass, DEFAULT_PERIOD);
	}

	public Supplier(Storage<T> storage, Class<T> thingClass, long period) {
		super(period);
		logger.trace("initialize with period " + period);
		this.storage = storage;
		this.thingClass = thingClass;
		this.thread = new Thread(this);
		thread.setName(toString());
	}

	@Override
	public void run() {
		while (true) {
			try {
				storage.store(thingClass.newInstance());
				Thread.sleep(getPeriod());
			} catch (InterruptedException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "-" + thread.getId();
	}

	public Thread getThread() {
		return thread;
	}
}