package com.hfjy.framework.net.http.entity;

public interface ControllerConfig {

	String getCharacterEncoding();

	String getPrefix();

	String getSuffix();

	String[] getControllersPackages();

	ControllerDataChecker getDataChecker();

	TypeProcessor<?>[] getTypeProcessor();
}
