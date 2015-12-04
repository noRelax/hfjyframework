package com.hfjy.framework.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hfjy.framework.common.entity.DateType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
	boolean refresh() default true;

	boolean local() default true;

	long count() default 0;

	long max() default 0;

	DateType dateType() default DateType.SECOND;

	long outdate() default 0;
}
