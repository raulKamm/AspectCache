package com.rk.aspectCache.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this to annotate a method that you want to cache. Since annotations on methods are not inherited, you must annotate the method in 
 * the parent class and in the subclasses which override it. <br>
 * The elements' default values need not stay the same across all the overriding versions, therefore you can enforce different caching behaviors
 * for the same method by calling it from subtypes of the original version.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
	KeyStrategy keyType() default KeyStrategy.ARGS_IDENTITY;
	FieldMonitoringStrategy fieldMonitoringStrategy() default FieldMonitoringStrategy.ALL;
	boolean stable() default false; // if true delays propagation of invalidations to parent cache entries until a new value is calculated and only if newValue != oldValue
	boolean automaticRecalculation() default false; // cannot use mutable keys. Cannot be stable (or rather can but means nothing)
	boolean waitForFresh() default false; // during a cache look-up, if a calculation of a fresh value is already underway, whether to wait for the new one or start another calculation
}
