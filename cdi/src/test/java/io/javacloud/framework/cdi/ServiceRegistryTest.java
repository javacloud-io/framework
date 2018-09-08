package io.javacloud.framework.cdi;

import io.javacloud.framework.cdi.ServiceTest;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author ho
 *
 */
public class ServiceRegistryTest extends ServiceTest {
	@Inject
	TestService testService;
	
	@Inject @Named("named")
	TestService namedService;
	@Test
	public void testInject() {
		Assert.assertNotNull(testService);
		Assert.assertNotNull(namedService);
	}
	
	/**
	 * 
	 */
	@Test
	public void testInstance() {
		Assert.assertNotNull(ServiceRegistry.get());
		
		TestService ts = ServiceRegistry.get().getInstance(TestService.class);
		Assert.assertNotNull(ts);
		
		//ALWAYS NEW
		Assert.assertSame(ts, ServiceRegistry.get().getInstance(TestService.class));
		Assert.assertSame(ts, ServiceRegistry.get().getInstance(TestService.class, "named"));
		
		Assert.assertNotNull(ServiceRegistry.get().getInstance(TestInject.class).getService());
		Assert.assertNotNull(ServiceRegistry.get().getInstance(TestInjectNamed.class).getService());
	}
	
	/**
	 * 
	 */
	@Test
	public void testInstances() {
		List<TestService> services = ServiceRegistry.get().getInstances(TestService.class);
		Assert.assertEquals(2, services.size());
	}
}
