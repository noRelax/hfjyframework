package com.hfjy.framework.net.http.entity;

import javax.servlet.http.HttpServletRequest;

public interface TypeProcessor<T> {

	T handle(String mark, HttpServletRequest httpRequest);
}
