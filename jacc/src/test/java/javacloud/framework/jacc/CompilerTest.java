package javacloud.framework.jacc;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import javacloud.framework.cdi.ServiceRunlist;
import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.jacc.JavaCompiler;
import javacloud.framework.jacc.JavaSource;
import javacloud.framework.jacc.internal.InMemoryClassCollector;
import javacloud.framework.jacc.internal.InMemoryClassLoader;
import javacloud.framework.jacc.internal.JavaSourceFile;
import javacloud.framework.jacc.util.JavaSourceTokenizer;
import javacloud.framework.util.Pair;

/**
 * 
 * @author ho
 *
 */
public class CompilerTest extends ServiceTest {
	static final String CODE = "package io.test; \npublic class HelloWorld {\n public static void sayHello() {\nSystem.out.println(\"Hello World!\");\n} \n}\n//13 3323 \r\n   //11111\n/* dsdsdsdsd \n * \n***/ //zzz"; 
	@Inject
	private JavaCompiler javaCompiler;
	
	@Test
	public void testTokenizer() throws Exception {
		JavaSourceTokenizer tokenizier = new JavaSourceTokenizer(CODE);
		System.out.println("SOURCE CODE TOKENS");
		while(tokenizier.hasMoreTokens()) {
			System.out.print(tokenizier.getLineNo() + ":" + tokenizier.getColumnNo() + "\t");
			
			Pair<JavaSourceTokenizer.Type, String> token = tokenizier.nextToken();
			System.out.println(token.getKey() + "\t" + token.getValue());
		}
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompile() throws Exception {
		long starts = System.currentTimeMillis();
		try {
			List<JavaSource> sources = new ArrayList<JavaSource>();
			String className = JavaSourceFile.resolveClassName(CODE);//"io.test.HelloWorld"
			sources.add(new JavaSourceFile(CODE, className));
			
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
				Class<?> helloClass = classLoader.loadClass(className);
				ServiceRunlist.get().runMethod(helloClass, "sayHello");
			}
		} finally {
			System.out.println("\nESLAPED: " + (System.currentTimeMillis() - starts));
		}
	}

}
