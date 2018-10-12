package io.javacloud.framework.jacc;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import io.javacloud.framework.cdi.ServiceRunner;
import io.javacloud.framework.cdi.test.ServiceTest;
import io.javacloud.framework.jacc.JavaCompiler;
import io.javacloud.framework.jacc.internal.InMemoryClassCollector;
import io.javacloud.framework.jacc.internal.InMemoryClassLoader;
import io.javacloud.framework.jacc.internal.JavaSourceFile;

/**
 * 
 * @author ho
 *
 */
public class CompilerTest extends ServiceTest {
	static final String CODE = "package io.test; \npublic class Hello {\n public static void sayHello() {\n\n} \n}"; 
	@Inject
	private JavaCompiler javaCompiler;
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompile() throws Exception {
		long starts = System.currentTimeMillis();
		try {
			List<JavaSource> sources = new ArrayList<JavaSource>();
			sources.add(new JavaSourceFile("io.test.Hello", CODE));
			
			InMemoryClassCollector collector = new InMemoryClassCollector();
			boolean success = javaCompiler.compile(sources, collector);
			if(!success) {
				System.out.println("COMPILATION ERROR:");
				for(URI file: collector.getFailures()) {
					for (InMemoryClassCollector.Metric metric: collector.getMetrics(file)) {
						System.out.println(file + ":" + metric);
					}
				}
			} else {
				System.out.println("COMPILATION SUCCESSFUL.");
				InMemoryClassLoader classLoader = new InMemoryClassLoader(collector, CompilerTest.class.getClassLoader());
				Class<?> helloClass = classLoader.loadClass("io.test.Hello");
				ServiceRunner.get().runMethod(helloClass, "sayHello");
			}
		} finally {
			System.out.println("\nESLAPED: " + (System.currentTimeMillis() - starts));
		}
	}

}
