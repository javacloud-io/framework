package javacloud.framework.cdi.test;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.cdi.internal.GuiceBuilder;
import javacloud.framework.cdi.internal.GuiceRegistry;
import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.cdi.test.TestModule.TestServiceImpl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Injector;
/**
 * 
 * @author ho
 *
 */
public class ServiceRegistryIT extends IntegrationTest {
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
	
	@Test
	public void testInheritBuilder() {
		GuiceRegistry inheritRegistry = new GuiceRegistry() {
			@Override
			protected Injector createInjector() {
				GuiceBuilder builder = new GuiceBuilder.InheritBuilder(GuiceRegistry.getInjector());
				return builder.build(Collections.emptyList(), Collections.emptyList());
			}
		};
		
		// not same instance
		Assert.assertSame(inheritRegistry.getInstance(TestService.class),
						  ServiceRegistry.get().getInstance(TestService.class));
	}
}
