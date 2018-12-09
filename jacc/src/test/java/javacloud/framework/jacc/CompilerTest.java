package javacloud.framework.jacc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import javacloud.framework.cdi.ServiceRunlist;
import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.io.BytesChannelReader;
import javacloud.framework.jacc.JavaCompiler;
import javacloud.framework.jacc.JavaSource;
import javacloud.framework.jacc.internal.StandardClassCollector;
import javacloud.framework.jacc.internal.JavaSourceFile;
import javacloud.framework.util.ResourceLoader;

/**
 * 
 * @author ho
 *
 */
public class CompilerTest extends ServiceTest {
	@Inject
	private JavaCompiler javaCompiler;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	static String loadSource(String name) throws IOException {
		InputStream source = ResourceLoader.getClassLoader().getResourceAsStream(name);
		return BytesChannelReader.toBytesStream(source).toString();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompile() throws Exception {
		long starts = System.currentTimeMillis();
		try {
			String source = loadSource("hello.java.txt");
			
			List<JavaSource> sources = new ArrayList<JavaSource>();
			String className = JavaSourceFile.resolveMainClass(source);//"io.test.HelloWorld"
			sources.add(new JavaSourceFile(source, className));
			System.out.println("MAIN CLASS: " + className);
			
			StandardClassCollector collector = new StandardClassCollector();
			boolean success = javaCompiler.compile(sources, collector);
			if(!success) {
				System.out.println("COMPILATION ERROR:");
				for(URI file: collector.getFailures()) {
					for (StandardClassCollector.Metric metric: collector.getMetrics(file)) {
						System.out.println(file + ":" + metric);
					}
				}
			} else {
				System.out.println("COMPILATION SUCCESSFUL.");
				ClassLoader classLoader = collector.asClassLoader(CompilerTest.class.getClassLoader());
				Class<?> helloClass = classLoader.loadClass(className);
				ServiceRunlist.get().runMethod(helloClass, "sayHello");
			}
		} finally {
			System.out.println("\nESLAPED: " + (System.currentTimeMillis() - starts));
		}
	}

}
