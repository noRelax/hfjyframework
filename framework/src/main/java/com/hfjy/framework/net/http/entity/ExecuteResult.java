package com.hfjy.framework.net.http.entity;

public class ExecuteResult {
	private Object[] parameter;
	private Object result;
	private Exception exception;

	public Object[] getParameter() {
		return parameter;
	}

	public void setParameter(Object[] parameter) {
		this.parameter = parameter;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		return result == null ? null : result.toString();
	}
}