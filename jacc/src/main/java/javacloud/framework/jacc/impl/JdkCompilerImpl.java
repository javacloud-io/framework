package javacloud.framework.jacc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import javacloud.framework.i18n.CharsetContext;
import javacloud.framework.i18n.LocaleContext;
import javacloud.framework.jacc.ClassCollector;
import javacloud.framework.jacc.JavaCompiler;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;
/**
 * 1. Java native compiler only available in JDK 1.8+!!!
 * 2. By default, java.class.path will be visible unless override by complier
 * @author ho
 *
 */
@Singleton
public class JdkCompilerImpl implements JavaCompiler {
	private static final Logger logger = Logger.getLogger(JdkCompilerImpl.class.getName());
	
	private final LocaleContext localeContext;
	private final CharsetContext charsetContext;
	private final javax.tools.JavaCompiler javaCompiler;
	/**
	 * Manually construct Jdk compiler using locale & charset
	 * 
	 * @param localeContext
	 * @param charsetContext
	 */
	@Inject
	public JdkCompilerImpl(LocaleContext localeContext, CharsetContext charsetContext) {
		this.localeContext = localeContext;
		this.charsetContext= charsetContext;
		this.javaCompiler  = getSystemJavaCompiler();
	}
	
	/**
	 * COMPILE THE JAVA CODE USING NATIVE COMPILER
	 */
	@Override
	public boolean compile(Iterable<JavaCompiler.Source> sources, JavaCompiler.Options options, ClassCollector collector) {
		//COMPILATION UNITS
		List<JdkSourceFileAdapter> compilationUnits = new ArrayList<>();
		for(JavaCompiler.Source src: sources) {
			compilationUnits.add(new JdkSourceFileAdapter(src));
		}
		
		//DIAGNOSTIC LISTENER
		Locale locale = localeContext.get();
		JdkDiagnosticAdapter diagnosticAdapter = new JdkDiagnosticAdapter(collector, locale);
		
		//INVOKE NATIVE COMPILER
		StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnosticAdapter, locale, charsetContext.get());
		try {
			JdkClassFileManager jdkFileManager = new JdkClassFileManager(fileManager, collector);
			List<String> jdkopts = options == null? null : Objects.asList("-classpath", options.classpath());
			
			//FIXME: supporting compiling options
			return javaCompiler.getTask(null, jdkFileManager, diagnosticAdapter, jdkopts, null, compilationUnits).call();
		} finally {
			Objects.closeQuietly(fileManager);
		}
	}
	
	/**
	 * Correctly locate java compliler
	 * @return
	 */
	static javax.tools.JavaCompiler getSystemJavaCompiler() {
		javax.tools.JavaCompiler impl = ToolProvider.getSystemJavaCompiler();
		//JRE USING USER PACKAGED TOOLS COMPILER
		if(impl == null) {
			logger.fine("System java complier is not available, using user packaged compiler.");
			try {
				//impl = com.sun.tools.javac.api.JavacTool.create();
				Class<?> javaToolClass = ResourceLoader.getClassLoader().loadClass("com.sun.tools.javac.api.JavacTool");
				impl = (javax.tools.JavaCompiler)javaToolClass.getMethod("create").invoke(null);
			} catch(Exception ex) {
				throw Exceptions.wrap(ex);
			}
		}
		return impl;
	}
}
