package javacloud.framework.standalone;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.cdi.ServiceBootstrapper;
import javacloud.framework.util.Objects;
/**
 * Add services with start/stop method to run-list: META-INF/javacloud.cdi.services.runlist
 * 
 * @author tobiho
 *
 */
public class MainApplication implements Runnable {
	private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
	private final AtomicBoolean terminated = new AtomicBoolean(false);
	
	@Override
	public void run() {
		// ADD shutdown hook
		logger.fine("Registering shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				terminateQuietly();
			}
		});
				
		// start/shutdown
		terminated.set(false);
		try {
			logger.fine("Starting application...");
			ServiceBootstrapper.get().startup();
			
			// wait until finish then shutdown
			Thread.yield();
			
			// terminate
			logger.fine("Terminating application...");
			terminateQuietly();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Terminating application due to uncaught exception...", ex);
			System.exit(1);
		}
	}
	
	void terminateQuietly() {
		if (terminated.compareAndSet(false, true)) {
			Objects.closeQuietly(() -> ServiceBootstrapper.get().shutdown());
		}
	}
	
	public static void main(String[] args) {
		new MainApplication().run();
	}
}
