package com.hfjy.framework.common.entity;

import java.io.Serializable;

public class AccessResult<T> implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private boolean success = false;
	private String type;
	private String code;
	private String info;
	private T result;

	public boolean isSuccess() {
		return success;
	}

	public boolean isFailure() {
		return !success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public void copy(AccessResult<T> accessResult) {
		success = accessResult.success;
		code = accessResult.code;
		type = accessResult.type;
		info = accessResult.info;
		result = accessResult.result;
	}

	public static <T> AccessResult<T> initSuccess(T date) {
		AccessResult<T> result = new AccessResult<>();
		result.success = true;
		result.result = date;
		return result;
	}

	public static <T> AccessResult<T> initSuccess(T data, String code, String type) {
		AccessResult<T> result = new AccessResult<>();
		result.success = true;
		result.result = data;
		result.code = code;
		result.type = type;
		return result;
	}

	public static <T> AccessResult<T> initFailure(T data, String code) {
		AccessResult<T> result = new AccessResult<>();
		result.success = false;
		result.result = data;
		result.code = code;
		return result;
	}

	public static <T> AccessResult<T> initFailure(String info) {
		AccessResult<T> result = new AccessResult<>();
		result.success = false;
		result.info = info;
		return result;
	}

	public static <T> AccessResult<T> initFailure(String info, String code) {
		AccessResult<T> result = new AccessResult<>();
		result.success = false;
		result.info = info;
		result.code = code;
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AccessResult<T> clone() throws CloneNotSupportedException {
		return (AccessResult<T>) super.clone();
	}
}
