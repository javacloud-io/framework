package javacloud.framework.cdi;

import javacloud.framework.cdi.TestModule.TestServiceImpl;
import javacloud.framework.cdi.test.ServiceTest;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author ho
 *
 */
public class ServiceRegistryTest extends ServiceTest {
	@Inject
	Provider<TestService> testProvider;
	
	@Inject
	TestService testService;
	
	@Inject
	TestLazyService lazyService;
	
	@Inject @Named("named")
	TestService namedService;
	@Test
	public void testInject() {
		Assert.assertNotNull(testService);
		Assert.assertNotNull(namedService);
		Assert.assertSame(testProvider.get(), testService);
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
		Assert.assertSame(ts, ServiceRegistry.get().getInstance(TestServiceImpl.class));
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
	
	/**
	 * 
	 * @throws InvocationTargetException
	 */
	@Test
	public void testRunlist() throws Exception {
		ServiceRunlist.get().runMethod(ServiceRegistryTest.class, "testInstances");
	}
}
