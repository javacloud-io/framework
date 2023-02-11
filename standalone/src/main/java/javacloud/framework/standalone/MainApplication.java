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
	private final boolean awaitTermination;
	
	protected MainApplication(boolean awaitTermination) {
		this.awaitTermination = awaitTermination;
	}
	
	@Override
	public void run() {
		// ADD shutdown hook
		logger.fine("Register shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (terminated.compareAndSet(false, true)) {
					terminateQuietly();
				}
			}
		});
				
		// start/shutdown
		terminated.set(false);
		try {
			logger.fine("Start application...");
			ServiceBootstrapper.get().startup(awaitTermination);
			
			// wait until finish then shutdown
			terminated.set(true);
			terminateQuietly();
			
			logger.fine("Application terminated!!!");
			System.exit(0);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Terminate application due to uncaught exception...", ex);
			System.exit(1);
		}
	}
	
	protected void terminateQuietly() {
		Objects.closeQuietly(() -> ServiceBootstrapper.get().shutdown());
	}
	
	public static void main(String[] args) {
		MainApplication runner = new MainApplication(true);
		runner.run();
	}
}
