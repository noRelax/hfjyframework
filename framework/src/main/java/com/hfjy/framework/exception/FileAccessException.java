package com.hfjy.framework.exception;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.exception.base.BaseException;

public class FileAccessException extends BaseException {
	private static final long serialVersionUID = 1L;
	private AccessResult<?> accessResult;

	public FileAccessException(String msg, Throwable cause, AccessResult<?> accessResult) {
		super(msg, cause);
		this.accessResult = accessResult;
	}

	public AccessResult<?> getAccessResult() {
		return accessResult;
	}
}
