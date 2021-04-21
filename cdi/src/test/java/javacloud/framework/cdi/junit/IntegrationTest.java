package javacloud.framework.cdi.junit;

import org.junit.runner.RunWith;

import junit.framework.TestCase;
/**
 * Place holder for subclass test case without annotate with RunWith. Subclass has to use annotation @Test for
 * any test case to execute
 * 
 * @author ho
 *
 */
@RunWith(ServiceJUnit4Runner.class)
public abstract class IntegrationTest extends TestCase {

}
