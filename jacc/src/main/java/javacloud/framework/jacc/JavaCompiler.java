package javacloud.framework.jacc;

/**
 * Compile java source code from a stream and emit class to collector
 * 
 * @author ho
 *
 */
public interface JavaCompiler {
	/**
	 * 
	 * @param sources
	 * @param collector
	 * @return
	 */
	public boolean compile(Iterable<JavaSource> sources, ClassCollector collector);
}
