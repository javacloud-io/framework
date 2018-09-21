package io.javacloud.framework.jacc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import io.javacloud.framework.i18n.CharsetContext;
import io.javacloud.framework.i18n.LocaleContext;
import io.javacloud.framework.jacc.ClassCollector;
import io.javacloud.framework.jacc.JavaCompiler;
import io.javacloud.framework.jacc.JavaSource;
import io.javacloud.framework.util.Objects;
/**
 * Java native compiler only available in JDK 1.8+!!!
 * @author ho
 *
 */
@SuppressWarnings("restriction")
@Singleton
public class JdkCompilerImpl implements JavaCompiler {
	private final LocaleContext localeContext;
	private final CharsetContext charsetContext;
	private final javax.tools.JavaCompiler impl;
	
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
		this.impl 	= ToolProvider.getSystemJavaCompiler();
	}
	
	/**
	 * COMPILE THE JAVA CODE USING NATIVE COMPILER
	 */
	@Override
	public boolean compile(Iterable<JavaSource> sources, ClassCollector collector) {
		//COMPILATION UNITS
		List<JdkSourceFileAdapter> compilationUnits = new ArrayList<>();
		for(JavaSource src: sources) {
			compilationUnits.add(new JdkSourceFileAdapter(src));
		}
		
		//DIAGNOSTIC LISTENER
		Locale locale = localeContext.get();
		JdkDiagnosticAdapter diagnosticAdapter = new JdkDiagnosticAdapter(collector, locale);
		
		//INVOKE NATIVE COMPILER
		StandardJavaFileManager fileManager = impl.getStandardFileManager(diagnosticAdapter, locale, charsetContext.get());
		try {
			JdkClassFileManager jdkFileManager = new JdkClassFileManager(fileManager, collector);
		
			//FIXME: supporting compiling options
			return impl.getTask(null, jdkFileManager, diagnosticAdapter, null, null, compilationUnits).call();
		} finally {
			Objects.closeQuietly(fileManager);
		}
	}
}
