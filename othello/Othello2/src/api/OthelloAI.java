/**
 *
 */
package api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate the annotated ai class can be loaded.
 * @author bamboo3892
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OthelloAI {

	/**
	 * @return depending Othello AI Kit version
	 */
	String depend();

	String author();

	String version();

}
