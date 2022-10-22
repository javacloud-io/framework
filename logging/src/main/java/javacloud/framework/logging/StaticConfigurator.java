package javacloud.framework.logging;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

import org.slf4j.LoggerFactory;

import javacloud.framework.util.ResourceLoader;

/**
 * JUL has to be configured prior to use Log4J, SLF4j...using:
 * -Djava.util.logging.config.class=javacloud.framework.logging.StaticConfigurator
 * 
 * @author ho
 *
 */
public final class StaticConfigurator {
	private static final String loggingConfig = configureJUL();
	
	/**
	 * FOR RUNLIST LCM
	 */
	public void start() {
		log("Configure JUL logger: {}", loggingConfig);
	}
	
	/**
	 * FOR RUNLIST LCM
	 */
	public void stop() {
	}
	
	/**
	 * 
	 * @return true if success configure logger
	 */
	static String configureJUL() {
		String configClass = System.getProperty("java.util.logging.config.class");
		if (configClass != null && !configClass.equals(StaticConfigurator.class.getName())) {
			return configClass;
		}
		
		String configFile = System.getProperty("java.util.logging.config.file");
		if (configFile != null) {
			return configFile;
		}
		
		//READ FROM RESOURCE PROPERTIES
		try {
			URL url = ResourceLoader.getClassLoader().getResource("logging.properties");
			if (url == null) {
				log("Not found JUL resource logging.properties");
				return null;
			}
			try (InputStream ins = url.openStream()) {
				LogManager.getLogManager().readConfiguration(ins);
			}
			
			return url.toString();
		} catch (Exception ex) {
			log("Unable to load JUL resource logging.properties");
		}
		return null;
	}
	
	/**
	 * ONLY USING slf4j LOG PRIOR TO TERMINATION TO AVOID SIDE EFFECT
	 * 
	 * @param format
	 * @param args
	 */
	static void log(String format, Object...args) {
		LoggerFactory.getLogger(StaticConfigurator.class).debug(format, args);
	}
}
