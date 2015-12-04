package com.hfjy.framework.checkable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Checkable {

	boolean isNull() default true;

	String regex() default "";

	String dateFormat() default "";

	int minLength() default -1;

	int maxLength() default -1;

	int decimalLength() default -1;
}