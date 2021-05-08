package javacloud.framework.gson;

import javacloud.framework.gson.internal.BuilderFactory;
import org.junit.Assert;
import org.junit.Test;

public class BuilderFactoryTest {
	
	@Test
	public void testInstance() {
		Assert.assertNotNull(BuilderFactory.get());
	}
}
