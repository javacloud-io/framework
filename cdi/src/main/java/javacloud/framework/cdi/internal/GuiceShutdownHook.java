package javacloud.framework.cdi.internal;

import javacloud.framework.cdi.ServiceBootstrapper;
import javacloud.framework.util.Objects;

public final class GuiceShutdownHook extends Thread {
	private GuiceShutdownHook() {
	}
	
	@Override
	public void run() {
		Objects.closeQuietly(() -> ServiceBootstrapper.get().shutdown());
	}
	
	public static void register() {
		Runtime.getRuntime().addShutdownHook(new GuiceShutdownHook());
	}
}
