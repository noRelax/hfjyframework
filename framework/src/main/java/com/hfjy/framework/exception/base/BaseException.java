package com.hfjy.framework.exception.base;

public class BaseException extends Exception {
	private static final long serialVersionUID = 1L;

	public BaseException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}
}
