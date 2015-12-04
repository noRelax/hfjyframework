package com.hfjy.framework.transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

	String[] mark();

	TransactionalLevel level() default TransactionalLevel.LEVEL4;

	boolean autoCommit() default false;

	boolean insure() default false;

}