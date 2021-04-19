package javacloud.framework.cdi.internal;

import javacloud.framework.cdi.ServiceBootstrapper;
import javacloud.framework.util.Objects;

public final class ServiceShutdownHook extends Thread {
	private ServiceShutdownHook() {
	}
	
	@Override
	public void run() {
		Objects.closeQuietly(() -> ServiceBootstrapper.get().shutdown());
	}
	
	public static void register() {
		Runtime.getRuntime().addShutdownHook(new ServiceShutdownHook());
	}
}
