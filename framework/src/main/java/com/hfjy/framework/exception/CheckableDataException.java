package com.hfjy.framework.exception;

import com.hfjy.framework.exception.base.BaseException;

public class CheckableDataException extends BaseException {
	private static final long serialVersionUID = 1L;

	public CheckableDataException(String msg) {
		super(msg, null);
	}

	public CheckableDataException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
