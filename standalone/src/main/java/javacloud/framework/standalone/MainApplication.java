package javacloud.framework.standalone;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.cdi.ServiceBootstrapper;
import javacloud.framework.config.internal.StandardConfigSource;
import javacloud.framework.config.internal.SystemConfigSource;
import javacloud.framework.util.Objects;
/**
 * Add services with start/stop method to run-list: META-INF/javacloud.cdi.services.runlist
 * 
 * @author tobiho
 *
 */
public class MainApplication {
	private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
	private final AtomicBoolean terminated = new AtomicBoolean(false);
	
	void terminateQuietly() {
		if (terminated.compareAndSet(false, true)) {
			Objects.closeQuietly(() -> ServiceBootstrapper.get().shutdown());
		}
	}
	
	public void run(String[] args) {
		// combine arguments configSource
		logger.fine("Parsing command lin arguments...");
		StandardConfigSource configSource = new StandardConfigSource(args);
		configSource.keySet().stream()
					.forEach(name -> SystemConfigSource.get().setProperty(name, configSource.getProperty(name)));
		
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
	
	public static void main(String[] args) {
		new MainApplication().run(args);
	}
}
