package javacloud.framework.logging;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

import javax.inject.Singleton;

import org.slf4j.LoggerFactory;

import javacloud.framework.util.ResourceLoader;

/**
 * JUL has to be configured prior to use Log4J, SLF4j...using:
 * -Djava.util.logging.config.class=javacloud.framework.logging.StaticConfigurator
 * 
 * @author ho
 *
 */
@Singleton
public final class StaticConfigurator {
	private static volatile Boolean configured = false;
	/**
	 * JUL configure using constructor
	 */
	public StaticConfigurator() {
		start();
	}
	
	/**
	 * FOR RUNLIST LCM
	 */
	public static void start() {
		if(!configured) {
			synchronized(configured) {
				if(!configured) {
					configureJUL();
					configured = true;
				}
			}
		}
	}
	
	/**
	 * FOR RUNLIST LCM
	 */
	public static void stop() {
	}
	
	/**
	 * 
	 * @return true if success configure logger
	 */
	static boolean configureJUL() {
		String configClass = System.getProperty("java.util.logging.config.class");
		if(configClass != null && !configClass.equals(StaticConfigurator.class.getName())) {
			log("JUL already configured using class: {}", configClass);
			return false;
		}
		String configFile = System.getProperty("java.util.logging.config.file");
		if(configFile != null) {
			log("JUL already configured using file: {}", configFile);
			return false;
		}
		
		//READ FROM RESOURCE PROPERTIES
		try {
			URL url = ResourceLoader.getClassLoader().getResource("logging.properties");
			if(url == null) {
				log("Not found JUL resource logging.properties");
				return false;
			}
			try(InputStream ins = url.openStream()) {
				LogManager.getLogManager().readConfiguration(ins);
			}
			return true;
		} catch(Exception ex) {
			log("Unable to load JUL resource logging.properties");
		}
		return false;
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
