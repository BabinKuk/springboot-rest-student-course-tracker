package org.babinkuk.vo.diff;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A class that uses this annotation should use {@link DiffField} to specify which fields are to be included
 * when calculating the difference between objects of this type 
 * 
 * @author Nikola
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Diffable {
	String id() default "";
}
