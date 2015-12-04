package com.hfjy.framework.exception;

import com.google.gson.JsonElement;
import com.hfjy.framework.exception.base.BaseException;

public class DateJsonHandlerException extends BaseException {
	private static final long serialVersionUID = 1L;

	public DateJsonHandlerException(JsonElement msg, Throwable cause) {
		super(msg.toString(), cause);
	}

}
