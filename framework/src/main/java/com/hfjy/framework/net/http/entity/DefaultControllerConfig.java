package com.hfjy.framework.net.http.entity;

public class DefaultControllerConfig implements ControllerConfig {

	@Override
	public String getCharacterEncoding() {
		return "UTF-8";
	}

	@Override
	public String getPrefix() {
		return "";
	}

	@Override
	public String getSuffix() {
		return "";
	}

	@Override
	public String[] getControllersPackages() {
		return new String[0];
	}

	@Override
	public ControllerDataChecker getDataChecker() {
		return null;
	}

	@Override
	public TypeProcessor<?>[] getTypeProcessor() {
		return null;
	}
}
