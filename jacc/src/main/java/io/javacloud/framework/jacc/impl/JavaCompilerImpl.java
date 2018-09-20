package io.javacloud.framework.jacc.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import io.javacloud.framework.i18n.LocaleContext;
import io.javacloud.framework.jacc.ClassCollector;
import io.javacloud.framework.jacc.JavaCompiler;
import io.javacloud.framework.jacc.JavaSource;
import io.javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
@SuppressWarnings("restriction")
@Singleton
public class JavaCompilerImpl implements JavaCompiler {
	private final Locale locale;
	private final Charset charset;
	private final javax.tools.JavaCompiler impl;
	
	/**
	 * Manyally construct the 
	 * @param locale
	 * @param charset
	 */
	public JavaCompilerImpl(Locale locale, Charset charset) {
		this.locale = locale;
		this.charset= charset;
		this.impl = ToolProvider.getSystemJavaCompiler();
	}
	
	/**
	 * Default locale and charset
	 */
	@Inject
	public JavaCompilerImpl(LocaleContext localeContext) {
		this(localeContext.get(), Charset.defaultCharset());
	}
	
	/**
	 * 
	 */
	@Override
	public boolean compile(Iterable<JavaSource> sources, ClassCollector collector) {
		//COMPILATION UNITS
		List<JavaSourceFileAdapter> compilationUnits = new ArrayList<>();
		for(JavaSource src: sources) {
			compilationUnits.add(new JavaSourceFileAdapter(src));
		}
		
		//DIAGNOSTIC LISTENER
		JavaDiagnosticAdapter diagnosticAdapter = new JavaDiagnosticAdapter(collector, locale);
		
		//INVOKE NATIVE COMPILER
		StandardJavaFileManager fileManager = impl.getStandardFileManager(diagnosticAdapter, locale, charset);
		try {
			JavaClassFileManager classFileManager = new JavaClassFileManager(fileManager, collector);
		
			//FIXME: supporting compiling options
			return impl.getTask(null, classFileManager, diagnosticAdapter, null, null, compilationUnits).call();
		} finally {
			Objects.closeQuietly(fileManager);
		}
	}
}
